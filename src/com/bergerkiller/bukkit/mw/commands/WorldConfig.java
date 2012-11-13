package com.bergerkiller.bukkit.mw.commands;

import com.bergerkiller.bukkit.mw.MyWorldsLite;
import com.bergerkiller.bukkit.mw.Permission;
import org.bukkit.ChatColor;

public class WorldConfig extends Command {
    
    public WorldConfig() {
        super(Permission.COMMAND_CONFIG, "world.config");
    }
    
    public void execute() {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("load")) {
                com.bergerkiller.bukkit.mw.WorldConfig.init();
                message(ChatColor.GREEN + "World configuration has been loaded!");
            } else if (args[0].equalsIgnoreCase("save")) {
                com.bergerkiller.bukkit.mw.WorldConfig.saveAll(MyWorldsLite.plugin.root() + "worlds.yml");
                message(ChatColor.GREEN + "World configuration has been saved!");
            } else {
                this.showInv();
            }
        } else {
            this.showInv();
        }
    }
}
