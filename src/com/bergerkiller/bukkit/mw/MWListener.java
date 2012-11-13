package com.bergerkiller.bukkit.mw;

import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class MWListener implements Listener {
    
    // World to disable keepspawnloaded for
    private static HashSet<String> initIgnoreWorlds = new HashSet<String>();
    
    public static void ignoreWorld(String worldname) {
        initIgnoreWorlds.add(worldname);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldUnload(WorldUnloadEvent event) {
        if (!event.isCancelled()) {
            WorldConfig config = WorldConfig.get(event.getWorld());
            WorldManager.clearWorldReference(event.getWorld());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldInit(WorldInitEvent event) {
        if (initIgnoreWorlds.remove(event.getWorld().getName())) {
            event.getWorld().setKeepSpawnInMemory(false);
        } else {
            WorldConfig.get(event.getWorld()).update(event.getWorld());
        }
    }
    
    public static void setWeather(World w, boolean storm) {
        w.setStorm(storm);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeatherChange(WeatherChangeEvent event) {
        if (WorldConfig.get(event.getWorld()).holdWeather) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        WorldConfig.get(event.getPlayer()).update(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.isBedSpawn() && !WorldConfig.get(event.getPlayer()).forcedRespawn) {
            return; // Ignore bed spawns that are not overrided
        }
        Location loc = WorldManager.getRespawnLocation(event.getPlayer().getWorld());
        if (loc != null) {
            event.setRespawnLocation(loc);
        }
        WorldConfig.get(event.getRespawnLocation()).update(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        WorldConfig.updateReload(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        WorldConfig.updateReload(event.getFrom());
        WorldConfig.get(event.getPlayer()).update(event.getPlayer());
    }
}
