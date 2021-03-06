package com.bergerkiller.bukkit.mw.commands;

import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.mw.Permission;
import com.bergerkiller.bukkit.mw.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WorldList extends Command {
    
    public WorldList() {
        super(Permission.COMMAND_LIST, "world.list");
    }
    
    public void execute() {
        if (sender instanceof Player) {
            //perform some nice layout coloring
            MessageBuilder builder = new MessageBuilder();
            builder.newLine().green("[Loaded/Online] ").red("[Unloaded/Offline] ").dark_red("[Broken/Dead]");
            builder.newLine().yellow("Available worlds: ").newLine();
            builder.setIndent(2).setSeparator(ChatColor.WHITE, " / ");
            for (String world : WorldManager.getWorlds()) {
                if (WorldManager.isLoaded(world)) {
                    builder.green(world);
                } else if (WorldManager.getData(world) == null) {
                    builder.dark_red(world);
                } else {
                        builder.red(world);
                }
            }
            builder.send(sender);
        } else {
            //plain world per line
            sender.sendMessage("Available worlds:");
            for (String world : WorldManager.getWorlds()) {
                String status = "[Unloaded]";
                if (WorldManager.isLoaded(world)) {
                    status = "[Loaded]";
                } else if (WorldManager.getData(world) == null) {
                    status = "[Broken]";
                }
                sender.sendMessage("    " + world + " " + status);
            }
        }
    }
}
