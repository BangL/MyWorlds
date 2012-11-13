package com.bergerkiller.bukkit.mw;

import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IDataManager;
import net.minecraft.server.NBTCompressedStreamTools;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.PlayerFileData;
import net.minecraft.server.WorldNBTStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;

/**
 * A player file data implementation<br>
 * 
 * <b>When a player joins</b><br>
 * The main file is read to find out the save file. This save file is then read and 
 * applied on the player<br><br>
 * 
 * <b>When a player leaves</b><br>
 * The player data is written to the save file. If he was not on the main world, 
 * the main world file is updated with the current world the player is in<br><br>
 * 
 * <b>When a player teleports between worlds</b><br>
 * The old data is saved appropriately and the new data is applied again (not all data)
 */
public class PlayerData implements PlayerFileData {
    
    private Map<String, File> playerFileLoc = new HashMap<String, File>();
    
    public static void init() {
        CommonUtil.getServerConfig().playerFileData = new PlayerData();
    }
    
    @Override
    public String[] getSeenPlayers() {
        IDataManager man = WorldUtil.getWorlds().get(0).getDataManager();
        if (man instanceof WorldNBTStorage) {
            return ((WorldNBTStorage) man).getSeenPlayers();
        } else {
            return new String[0];
        }
    }
    
    /**
     * Gets the Main world save file for the playerName specified
     * 
     * @param playerName
     * @return Save file
     */
    public static File getMainFile(String playerName) {
        World world = MyWorldsLite.getMainWorld();
        return getPlayerData(world.getName(), world, playerName);
    }
    
    /**
     * Gets the save file for the player in the current world
     * 
     * @param player to get the save file for
     * @return save file
     */
    public static File getSaveFile(EntityHuman player) {
        return getSaveFile(player.name);
    }
    
    /**
     * Gets the save file for the player in a world
     * 
     * @param worldname
     * @return playername
     */
    public static File getSaveFile(String playerName) {
        World world = MyWorldsLite.getMainWorld();
        return getPlayerData(world.getName(), world, playerName);
    }
    
    /**
     * Gets the player data folder for a player in a certain world
     * 
     * @param worldName to use as backup
     * @param world to use as main goal (can be null)
     * @param playerName for the data
     * @return Player data file
     */
    private static File getPlayerData(String worldName, World world, String playerName) {
        File playersFolder = null;
        if (world != null) {
            IDataManager man = WorldUtil.getNative(world).getDataManager();
            if (man instanceof WorldNBTStorage) {
                playersFolder = ((WorldNBTStorage) man).getPlayerDir();
            }
        }
        if (playersFolder == null) {
            File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
            playersFolder = new File(worldFolder, "players");
        }
        return new File(playersFolder, playerName + ".dat");
    }
    
    /**
     * Writes the compound to the destination file specified
     * 
     * @param nbttagcompound to save
     * @param destFile to save to
     * @throws Exception on any type of failure
     */
    public static void write(NBTTagCompound nbttagcompound, File destFile) throws Exception {
        File tmpDest = new File(destFile.toString() + ".tmp");
        NBTCompressedStreamTools.a(nbttagcompound, new FileOutputStream(tmpDest));
        if (destFile.exists()) {
            destFile.delete();
        }
        tmpDest.renameTo(destFile);
    }
    
    /**
     * Tries to read the saved data from a source file
     * 
     * @param sourceFile to read from
     * @return the data in the file, or the empty data constant if the file does not exist
     * @throws Exception
     */
    public static NBTTagCompound read(File sourceFile, EntityHuman human) throws Exception {
        if (sourceFile.exists()) {
            return NBTCompressedStreamTools.a(new FileInputStream(sourceFile));
        } else {
            NBTTagCompound empty = new NBTTagCompound();
            empty.setShort("Health", (short) 20);
            empty.setShort("HurtTime", (short) 0);
            empty.setShort("DeathTime", (short) 0);
            empty.setShort("AttackTime", (short) 0);
            empty.set("Motion", Util.doubleArrayToList(human.motX, human.motY, human.motZ));
            setLocation(empty, human.getBukkitEntity().getLocation());
            empty.setInt("Dimension", human.dimension);
            empty.setString("SpawnWorld", human.spawnWorld);
            return empty;
        }
    }
    
    private static void setLocation(NBTTagCompound nbttagcompound, Location location) {
        nbttagcompound.set("Pos", Util.doubleArrayToList(location.getX(), location.getY(), location.getZ()));
        nbttagcompound.set("Rotation", Util.floatArrayToList(location.getYaw(), location.getPitch()));
        UUID worldUUID = location.getWorld().getUID();
        nbttagcompound.setLong("WorldUUIDLeast", worldUUID.getLeastSignificantBits());
        nbttagcompound.setLong("WorldUUIDMost", worldUUID.getMostSignificantBits());
    }
    
    @Override
    public void load(EntityHuman entityhuman) {
        try {
            File main;
            NBTTagCompound nbttagcompound;
            boolean hasPlayedBefore = false;
            main = getMainFile(entityhuman.name);
            hasPlayedBefore = main.exists();
            nbttagcompound = read(main, entityhuman);
            if (!hasPlayedBefore || MyWorldsLite.forceMainWorldSpawn) {
                // Alter saved data to point to the main world
                setLocation(nbttagcompound, WorldManager.getSpawnLocation(MyWorldsLite.getMainWorld()));
            }
            // Load the save file
            entityhuman.e(nbttagcompound);
            if (entityhuman instanceof EntityPlayer) {
                CraftPlayer player = (CraftPlayer) entityhuman.getBukkitEntity();
                if (hasPlayedBefore) {
                    player.setFirstPlayed(main.lastModified());
                } else {
                    // Bukkit bug: entityplayer.e(tag) -> b(tag) -> craft.readExtraData(tag) which instantly sets it
                    // Make sure the player is marked as being new
                    SafeField.set(player, "hasPlayedBefore", false);
                }
            }
        } catch (Exception exception) {
            Bukkit.getLogger().warning("Failed to load player data for " + entityhuman.name);
            exception.printStackTrace();
        }
    }
    
    @Override
    public void save(EntityHuman entityhuman) {
        try {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            entityhuman.d(nbttagcompound);
            File mainDest = getMainFile(entityhuman.name);
            File dest;
            dest = mainDest;
            // Set the saved file location to quicken loading the next time
            playerFileLoc.put(entityhuman.name, dest);
            // Write to the source
            write(nbttagcompound, dest);
            if (mainDest.equals(dest)) {
                return; // Do not update world if same file
            }
            // Update the world in the main file
            if (mainDest.exists()) {
                nbttagcompound = NBTCompressedStreamTools.a(new FileInputStream(mainDest));
            }
            UUID worldUUID = entityhuman.world.getWorld().getUID();
            nbttagcompound.setLong("WorldUUIDLeast", worldUUID.getLeastSignificantBits());
            nbttagcompound.setLong("WorldUUIDMost", worldUUID.getMostSignificantBits());
            write(nbttagcompound, mainDest);
        } catch (Exception exception) {
            Bukkit.getLogger().warning("Failed to save player data for " + entityhuman.name);
            exception.printStackTrace();
        }
    }
}
