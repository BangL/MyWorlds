package com.bergerkiller.bukkit.mw;

import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class MyWorldsLite extends PluginBase {
    
    public static int timeLockInterval;
    public static boolean calculateWorldSize;
    private static String mainWorld;
    public static boolean forceMainWorldSpawn;
    
    public static MyWorldsLite plugin;
    
    @Override
    public int getMinimumLibVersion() {
        return 7;
    }
    
    public String root() {
        return getDataFolder() + File.separator;
    }
    
    @Override
    public void enable() {
        plugin = this;
        
        // Event registering
        this.register(MWListener.class);
        
        FileConfiguration config = new FileConfiguration(this);
        config.load();
        
        config.setHeader("This is the configuration of MyWorldsLite");
        
        config.setHeader("timeLockInterval", "\nThe tick interval at which time is kept locked");
        timeLockInterval = config.get("timeLockInterval", 20);
        
        config.setHeader("calculateWorldSize", "\nWhether the world info command will calculate the world size on disk");
        config.addHeader("calculateWorldSize", "If this process takes too long, disable it to prevent possible server freezes");
        calculateWorldSize = config.get("calculateWorldSize", true);
        
        config.setHeader("mainWorld", "\nThe main world in which new players spawn");
        config.addHeader("mainWorld", "If left empty, the main world defined in the server.properties is used");
        mainWorld = config.get("mainWorld", "");
        
        config.setHeader("forceMainWorldSpawn", "\nWhether all players respawn on the main world at all times");
        forceMainWorldSpawn = config.get("forceMainWorldSpawn", false);
        
        config.save();
        
        // World info
        WorldConfigStore.init();
        
        // init chunk loader
        LoadChunksTask.init();
    }
    
    @Override
    public void disable() {
        // World info
        WorldConfigStore.deinit(root() + "worlds.yml");
        
        // Abort chunk loader
        LoadChunksTask.deinit();
        
        plugin = null;
    }
    
    @Override
    public boolean command(CommandSender sender, String cmdLabel, String[] args) {
        com.bergerkiller.bukkit.mw.commands.Command.execute(sender, cmdLabel, args);
        return true;
    }
    
    @Override
    public void localization() {
        this.loadLocales(Localization.class);
    }
    
    @Override
    public void permissions() {
        this.loadPermissions(Permission.class);
    }
    
    /**
     * Gets the main world
     * 
     * @return Main world
     */
    public static World getMainWorld() {
        if (!mainWorld.isEmpty()) {
            World world = Bukkit.getWorld(mainWorld);
            if (world != null) {
                return world;
            }
        }
        return WorldUtil.getWorlds().get(0).getWorld();
    }
}
