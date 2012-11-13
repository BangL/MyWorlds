package com.bergerkiller.bukkit.mw.commands;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.mw.Permission;
import com.bergerkiller.bukkit.mw.WorldConfig;
import org.bukkit.ChatColor;

public class WorldSetSaving extends Command {
    
    public WorldSetSaving() {
        super(Permission.COMMAND_SETSAVING, "world.setsaving");
    }
    
    public void execute() {
        if (args.length != 0) {
            this.genWorldname(1);
            if (this.handleWorld()) {
                WorldConfig wc = WorldConfig.get(worldname);
                if (ParseUtil.isBool(args[0])) {
                    boolean set = ParseUtil.parseBool(args[0]);
                    wc.autosave = set;
                    wc.updateAutoSave(wc.getWorld());
                    if (set) {
                        message(ChatColor.YELLOW + "Auto-saving on world '" + worldname + "' is now enabled!");
                    } else {
                        message(ChatColor.YELLOW + "Auto-saving on world '" + worldname + "' is now disabled!");
                    }
                } else {
                    if (wc.autosave) {
                        message(ChatColor.YELLOW + "Auto-saving on world '" + worldname + "' is enabled!");
                    } else {
                        message(ChatColor.YELLOW + "Auto-saving on world '" + worldname + "' is disabled!");
                    }
                }
            }
        } else {
            showInv();
        }
    }
}
