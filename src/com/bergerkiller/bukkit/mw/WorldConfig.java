package com.bergerkiller.bukkit.mw;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

public class WorldConfig extends WorldConfigStore {	
    
    public String worldname;
    public boolean keepSpawnInMemory = true;
    public WorldMode worldmode;
    public String chunkGeneratorName;
    public Difficulty difficulty = Difficulty.NORMAL;
    public Position spawnPoint;
    public GameMode gameMode;
    public boolean holdWeather = false;
    public boolean autosave = true;
    public boolean reloadWhenEmpty = false;
    public boolean forcedRespawn = true;
    
    public WorldConfig(String worldname) {
        this.gameMode = Bukkit.getServer().getDefaultGameMode();
        this.worldname = worldname;
        if (worldname == null) {
            return;
        }
        worldname = worldname.toLowerCase();
        worldConfigs.put(worldname, this);
        World world = this.getWorld();
        if (world != null) {
            this.keepSpawnInMemory = world.getKeepSpawnInMemory();
            this.worldmode = WorldMode.get(world);
            this.difficulty = world.getDifficulty();
            this.spawnPoint = new Position(world.getSpawnLocation());
            this.autosave = world.isAutoSave();
        } else {
            this.worldmode = WorldMode.get(worldname);
            this.spawnPoint = new Position(worldname, 0, 64, 0);
        }
    }
    
    public void load(ConfigurationNode node) {
        this.keepSpawnInMemory = node.get("keepSpawnLoaded", this.keepSpawnInMemory);
        this.worldmode = node.get("environment", this.worldmode);
        this.chunkGeneratorName = node.get("chunkGenerator", String.class, this.chunkGeneratorName);
        this.difficulty = node.get("difficulty", Difficulty.class, this.difficulty);
        this.gameMode = node.get("gamemode", GameMode.class, this.gameMode);
        String worldspawn = node.get("spawn.world", String.class);
        if (worldspawn != null) {
            double x = node.get("spawn.x", 0.0);
            double y = node.get("spawn.y", 64.0);
            double z = node.get("spawn.z", 0.0);
            double yaw = node.get("spawn.yaw", 0.0);
            double pitch = node.get("spawn.pitch", 0.0);
            this.spawnPoint = new Position(worldspawn, x, y, z, (float) yaw, (float) pitch);
        }
        this.holdWeather = node.get("holdWeather", this.holdWeather);
        this.forcedRespawn = node.get("forcedRespawn", this.forcedRespawn);
        this.reloadWhenEmpty = node.get("reloadWhenEmpty", this.reloadWhenEmpty);
    }
    
    public void save(ConfigurationNode node) {
        //Set if the world can be directly accessed
        World w = this.getWorld();
        if (w != null) {
            this.difficulty = w.getDifficulty();
            this.keepSpawnInMemory = w.getKeepSpawnInMemory();
            this.autosave = w.isAutoSave();
            if (this.chunkGeneratorName == null) {
                ChunkGenerator gen = ((org.bukkit.craftbukkit.CraftWorld) w).getHandle().generator;
                if (gen != null) {
                    String name = gen.getClass().getName();
                    if (name.equals("bukkit.techguard.christmas.world.ChristmasGenerator")) {
                        this.chunkGeneratorName = "Christmas";
                    }
                }
            }
        }
        if (this.worldname == null || this.worldname.equals(this.getConfigName())) {
            node.remove("name");
        } else {
            node.set("name", this.worldname);
        }
        node.set("loaded", w != null);
        node.set("keepSpawnLoaded", this.keepSpawnInMemory);
        node.set("environment", this.worldmode == null ? null : this.worldmode.toString());
        node.set("chunkGenerator", this.chunkGeneratorName);
        if (this.gameMode == null) {
            node.set("gamemode", "NONE");
        } else {
            node.set("gamemode", this.gameMode.toString());
        }
        node.set("forcedRespawn", this.forcedRespawn);
        node.set("holdWeather", this.holdWeather);
        node.set("difficulty", this.difficulty.toString());
        node.set("reloadWhenEmpty", this.reloadWhenEmpty);
        if (this.spawnPoint == null) {
            node.remove("spawn");
        } else {
            node.set("spawn.world", this.spawnPoint.getWorldName());
            node.set("spawn.x", this.spawnPoint.getX());
            node.set("spawn.y", this.spawnPoint.getY());
            node.set("spawn.z", this.spawnPoint.getZ());
            node.set("spawn.yaw", (double) this.spawnPoint.getYaw());
            node.set("spawn.pitch", (double) this.spawnPoint.getPitch());
        }
    }
    
    public World loadWorld() {
        if (WorldManager.worldExists(this.worldname)) {
            World w = WorldManager.getOrCreateWorld(this.worldname);
            if (w == null) {
                MyWorldsLite.plugin.log(Level.SEVERE, "Failed to (pre)load world: " + worldname);
            }
            return w;
        } else {
            MyWorldsLite.plugin.log(Level.WARNING, "World: " + worldname + " could not be loaded because it no longer exists!");
        }
        return null;
    }
    
    public boolean unloadWorld() {
        return WorldManager.unload(this.getWorld());
    }
    
    public static void updateReload(Player player) {
        updateReload(player.getWorld());
    }
    
    public static void updateReload(Location loc) {
        updateReload(loc.getWorld());
    }
    
    public static void updateReload(World world) {
        updateReload(world.getName());
    }
    
    public static void updateReload(final String worldname) {
        new Task(MyWorldsLite.plugin) {
            public void run() {
                get(worldname).updateReload();
            }
        }.start(1);
    }
    
    public void updateReload() {
        World world = this.getWorld();
        if (world == null) return;
        if (!this.reloadWhenEmpty) return;
        if (world.getPlayers().size() > 0) return;
        //reload world
        MyWorldsLite.plugin.log(Level.INFO, "Reloading world '" + worldname + "' - world became empty");
        if (!this.unloadWorld()) {
            MyWorldsLite.plugin.log(Level.WARNING, "Failed to unload world: " + worldname + " for reload purposes");
        } else if (this.loadWorld() == null) {
            MyWorldsLite.plugin.log(Level.WARNING, "Failed to load world: " + worldname + " for reload purposes");
        } else {
            MyWorldsLite.plugin.log(Level.INFO, "World reloaded successfully");
        }
    }
    
    public void updateAutoSave(World world) {
        if (world != null && world.isAutoSave() != this.autosave) {
            world.setAutoSave(this.autosave);
        }
    }
    
    public void updateGamemode(Player player) {
        if (this.gameMode != null && !Permission.has(player, "world.ignoregamemode")) {
            player.setGameMode(this.gameMode);
        }
    }
    
    public void updateKeepSpawnInMemory(World world) { 
        if (world != null && world.getKeepSpawnInMemory() != this.keepSpawnInMemory) {
            world.setKeepSpawnInMemory(this.keepSpawnInMemory);
        }
    }
    
    public void updateDifficulty(World world) {
        if (world != null && world.getDifficulty() != this.difficulty) {
            world.setDifficulty(this.difficulty);
        }
    }
    
    public void update(World world) {
        if (world == null) return;
        updateKeepSpawnInMemory(world);
        updateDifficulty(world);
        updateAutoSave(world);
    }
    
    public void update(Player player) {
        updateGamemode(player);
    }
    
    /**
     * Gets a safe configuration name for this World Configuration<br>
     * Unsafe characters, such as dots, are replaced
     * 
     * @return Safe config world name
     */
    public String getConfigName() {
        if (this.worldname == null) {
            return "";
        }
        return this.worldname.replace('.', '_').replace(':', '_');
    }
    
    /**
     * Gets the loaded World of this world configuration<br>
     * If the world is not loaded, null is returned
     * 
     * @return the World
     */
    public World getWorld() {
        return this.worldname == null ? null : WorldManager.getWorld(this.worldname);
    }
    
    public void setGameMode(GameMode mode) {
        if (this.gameMode != mode) {
            this.gameMode = mode;
            if (mode != null) {
                World world = this.getWorld();
                if (world != null) {
                    for (Player p : world.getPlayers()) {
                        this.updateGamemode(p);
                    }
                }
            }
        }
    }
}
