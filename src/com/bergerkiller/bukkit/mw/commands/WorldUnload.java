package com.bergerkiller.bukkit.mw.commands;

import com.bergerkiller.bukkit.mw.Localization;
import com.bergerkiller.bukkit.mw.Permission;
import com.bergerkiller.bukkit.mw.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

public class WorldUnload extends Command {
    
    public WorldUnload() {
        super(Permission.COMMAND_UNLOAD, "world.unload");
    }
    
    public void execute() {
        if (args.length != 0) {
            worldname = WorldManager.matchWorld(args[0]);
            if (this.handleWorld()) {
                World w = Bukkit.getServer().getWorld(worldname);
                if (w != null) {
                    notifyConsole("Issued an unload command for world: " + worldname);
                    if (WorldManager.unload(w)) {
                        message(ChatColor.GREEN + "World '" + worldname + "' has been unloaded!");
                    } else {
                        message(ChatColor.RED + "Failed to unload the world (main world or online players?)");
                    }
                } else {
                    Localization.WORLD_NOTLOADED.message(sender, worldname);
                }
            }
        } else {
            showInv();
        }
    }
}
