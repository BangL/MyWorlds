package com.bergerkiller.bukkit.mw.commands;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.mw.Permission;
import com.bergerkiller.bukkit.mw.WorldConfig;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;

public class WorldDifficulty extends Command {
    
    public WorldDifficulty() {
        super(Permission.COMMAND_DIFFICULTY, "world.difficulty");
    }
    
    public void execute() {
        this.genWorldname(1);
        if (this.handleWorld()) {
            WorldConfig wc = WorldConfig.get(worldname);
            if (args.length == 0) {
                String diff = wc.difficulty.toString().toLowerCase();
                message(ChatColor.YELLOW + "Difficulty of world '" + worldname + "' is set at " + ChatColor.WHITE + diff);
            } else {
                Difficulty diff = ParseUtil.parseEnum(args[0], Difficulty.NORMAL);
                if (diff != null) {
                    wc.difficulty = diff;
                    wc.updateDifficulty(wc.getWorld());
                    message(ChatColor.YELLOW + "Difficulty of world '" + worldname + "' set to " + ChatColor.WHITE + diff.toString().toLowerCase());
                } else {
                    message(ChatColor.RED + "Difficulty '" + args[0] + "' has not been recognized!");
                }
            }
        }
    }
}
