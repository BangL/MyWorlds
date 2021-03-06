package com.bergerkiller.bukkit.mw;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class WorldConfigStore {
    
    protected static HashMap<String, WorldConfig> worldConfigs = new HashMap<String, WorldConfig>();
    private static FileConfiguration defaultProperties;
    
    public static WorldConfig get(String worldname) {
        WorldConfig c = worldConfigs.get(worldname.toLowerCase());
        if (c == null) {
            c = new WorldConfig(worldname);
            if (defaultProperties != null) {
                c.load(defaultProperties);
            }
        }
        return c;
    }
    
    public static WorldConfig get(World world) {
        return get(world.getName());
    }
    
    public static WorldConfig get(Entity entity) {
        return get(entity.getWorld());
    }
    
    public static WorldConfig get(Location location) {
        return get(location.getWorld());
    }
    
    public static WorldConfig get(Block block) {
        return get(block.getWorld());
    }
    
    public static Collection<WorldConfig> all() {
        return worldConfigs.values();
    }
    
    public static boolean exists(String worldname) {
        return worldConfigs.containsKey(worldname.toLowerCase());
    }
    
    public static void init() {
        // Default configuration
        defaultProperties = new FileConfiguration(MyWorldsLite.plugin, "defaultproperties.yml");
        defaultProperties.setHeader("This file contains the default world properties applied when loading or creating completely new worlds");
        defaultProperties.addHeader("All the nodes found in the worlds.yml can be set here");
        if (defaultProperties.exists()) {
            defaultProperties.load();
        } else {
            // Generate new properties
            new WorldConfig(null).save(defaultProperties);
            defaultProperties.save();
        }
        
        // Worlds configuration
        FileConfiguration config = new FileConfiguration(MyWorldsLite.plugin, "worlds.yml");
        config.load();
        for (ConfigurationNode node : config.getNodes()) {
            String worldname = node.get("name", node.getName());
            if (WorldManager.worldExists(worldname)) {
                WorldConfig wc = get(worldname);
                wc.load(node);
                if (node.get("loaded", false)) {
                    wc.loadWorld();
                }
            } else {
                MyWorldsLite.plugin.log(Level.WARNING, "World: " + node.getName() + " no longer exists, data will be wiped when disabling!");
            }
        }
        for (World world : Bukkit.getServer().getWorlds()) {
            get(world).update(world);
        }
    }
    
    public static void saveAll(String filename) {
        FileConfiguration cfg = new FileConfiguration(filename);
        for (WorldConfig wc : all()) {
            wc.save(cfg.getNode(wc.getConfigName()));
        }
        cfg.save();
    }
    
    public static void deinit(String filename) {
        saveAll(filename);
        defaultProperties = null;
    }
    
    public static void remove(String worldname) {
        worldConfigs.remove(worldname.toLowerCase());
    }
}
