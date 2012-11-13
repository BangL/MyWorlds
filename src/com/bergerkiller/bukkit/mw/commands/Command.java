package com.bergerkiller.bukkit.mw.commands;

import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.mw.Localization;
import com.bergerkiller.bukkit.mw.MyWorldsLite;
import com.bergerkiller.bukkit.mw.Permission;
import com.bergerkiller.bukkit.mw.Util;
import com.bergerkiller.bukkit.mw.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command {
    
    public Permission permission;
    public String commandNode;
    public String command;
    public Player player;
    public CommandSender sender;
    public String[] args;
    public String worldname;
    
    private static String[] commandNodes = new String[] {
        "world.repair",
        "world.delete",
        "world.rename",
        "world.copy",
        "world.save",
        "world.load",
        "world.unload",
        "world.reloadwhenempty",
        "world.create",
        "world.listgenerators",
        "world.spawn",
        "world.setspawn",
        "world.list",
        "world.info",
        "world.gamemode",
        "tpp"
    };

    public Command(Permission permission, String commandNode) {
        this.permission = permission;
        this.commandNode = commandNode;
    }
    
    public void init(CommandSender sender, String[] args) {
        this.args = args;
        this.sender = sender;
        if (sender instanceof Player) {
            this.player = (Player) sender;
        }
    }
    
    public static boolean allowConsole(String node) {
        if (node.equals("world.setspawn")) return false;
        if (node.equals("world.spawn")) return false;
        return true;
    }
    
    public void removeArg(int index) {
        String[] newargs = new String[args.length - 1];
        int ni = 0;
        for (int i = 0; i < args.length; i++) {
            if (i == index) continue;
            newargs[ni] = args[i];
            ni++;
        }
        this.args = newargs;
    }
    
    public boolean hasPermission() {
        return this.hasPermission(this.permission.getName());
    }
    
    public boolean hasPermission(String node) {
        if (this.player == null) {
            return allowConsole(node);
        } else {
            return Permission.has(this.player, node);
        }
    }
    
    public boolean handleWorld() {
        if (this.worldname == null) {
            locmessage(Localization.WORLD_NOTFOUND);
        }
        return this.worldname != null;
    }
    
    public void message(String msg) {
        if (msg == null) return;
        CommonUtil.sendMessage(this.sender, msg);
    }
    
    public void locmessage(Localization node, String... arguments) {
        node.message(this.sender, arguments);
    }
    
    public void notifyConsole(String message) {
        Util.notifyConsole(sender, message);
    }
    
    public boolean showInv() {
        return this.showInv(this.commandNode);
    }
    
    public boolean showInv(String node) {
        message(ChatColor.RED + "Invalid arguments for this command!");
        return showUsage(node);
    }
    
    public boolean showUsage() {
        return showUsage(this.commandNode);
    }
    
    public boolean showUsage(String commandNode) {
        if (hasPermission()) {
            this.sender.sendMessage(MyWorldsLite.plugin.getCommandUsage(commandNode));
            return true;
        } else {
            return false;
        }
    }
    
    public void genWorldname(int argindex) {
        if (argindex >= 0 && argindex < this.args.length) {
            this.worldname = WorldManager.matchWorld(args[argindex]);
            if (this.worldname != null) return;
        }
        if (player != null) {
            this.worldname = player.getWorld().getName();
        } else {
            this.worldname = Bukkit.getServer().getWorlds().get(0).getName();
        }
    }
    
    public static void execute(CommandSender sender, String cmdLabel, String[] args) {
        //generate a node from this command
        Command rval = null;
        if (cmdLabel.equalsIgnoreCase("world")
                || cmdLabel.equalsIgnoreCase("myworlds")
                || cmdLabel.equalsIgnoreCase("worlds")
                || cmdLabel.equalsIgnoreCase("mw")) {
            if (args.length >= 1) {
                cmdLabel = args[0];
                args = StringUtil.remove(args, 0);
                if (cmdLabel.equalsIgnoreCase("list")) {
                    rval = new WorldList();
                } else if (cmdLabel.equalsIgnoreCase("info")) {
                    rval = new WorldInfo();
                } else if (cmdLabel.equalsIgnoreCase("i")) {
                    rval = new WorldInfo();
                } else if (cmdLabel.equalsIgnoreCase("load")) {
                    rval = new WorldLoad();
                } else if (cmdLabel.equalsIgnoreCase("unload")) {
                    rval = new WorldUnload();
                } else if (cmdLabel.equalsIgnoreCase("create")) {
                    rval = new WorldCreate();
                } else if (cmdLabel.equalsIgnoreCase("spawn")) {
                    rval = new WorldSpawn();
                } else if (cmdLabel.equalsIgnoreCase("repair")) {
                    rval = new WorldRepair();
                } else if (cmdLabel.equalsIgnoreCase("rep")) {
                    rval = new WorldRepair();
                } else if (cmdLabel.equalsIgnoreCase("save")) {
                    rval = new WorldSave();
                } else if (cmdLabel.equalsIgnoreCase("delete")) {
                    rval = new WorldDelete();
                } else if (cmdLabel.equalsIgnoreCase("del")) {
                    rval = new WorldDelete();
                } else if (cmdLabel.equalsIgnoreCase("copy")) {
                    rval = new WorldCopy();
                } else if (cmdLabel.equalsIgnoreCase("setspawn")) {
                    rval = new WorldSetSpawn();
                } else if (cmdLabel.equalsIgnoreCase("gamemode")) {
                    rval = new WorldGamemode();
                } else if (cmdLabel.equalsIgnoreCase("setgamemode")) {
                    rval = new WorldGamemode();
                } else if (cmdLabel.equalsIgnoreCase("gm")) {
                    rval = new WorldGamemode();
                } else if (cmdLabel.equalsIgnoreCase("setgm")) {
                    rval = new WorldGamemode();
                } else if (cmdLabel.equalsIgnoreCase("generators")) {
                    rval = new WorldListGenerators();
                } else if (cmdLabel.equalsIgnoreCase("gen")) {
                    rval = new WorldListGenerators();
                } else if (cmdLabel.equalsIgnoreCase("listgenerators")) {
                    rval = new WorldListGenerators();
                } else if (cmdLabel.equalsIgnoreCase("listgen")) {
                    rval = new WorldListGenerators();
                } else if (cmdLabel.equalsIgnoreCase("togglespawnloaded")) {
                    rval = new WorldToggleSpawnLoaded();
                } else if (cmdLabel.equalsIgnoreCase("spawnloaded")) {
                    rval = new WorldToggleSpawnLoaded();
                } else if (cmdLabel.equalsIgnoreCase("keepspawnloaded")) {
                    rval = new WorldToggleSpawnLoaded();
                } else if (cmdLabel.equalsIgnoreCase("difficulty")) {
                    rval = new WorldDifficulty();
                } else if (cmdLabel.equalsIgnoreCase("difficult")) {
                    rval = new WorldDifficulty();
                } else if (cmdLabel.equalsIgnoreCase("diff")) {
                    rval = new WorldDifficulty();
                } else if (cmdLabel.equalsIgnoreCase("setsave")) {
                    rval = new WorldSetSaving();
                } else if (cmdLabel.equalsIgnoreCase("setsaving")) {
                    rval = new WorldSetSaving();
                } else if (cmdLabel.equalsIgnoreCase("saving")) {
                    rval = new WorldSetSaving();
                } else if (cmdLabel.equalsIgnoreCase("autosave")) {
                    rval = new WorldSetSaving();
                } else if (cmdLabel.equalsIgnoreCase("config")) {
                    rval = new WorldConfig();
                } else if (cmdLabel.equalsIgnoreCase("cfg")) {
                    rval = new WorldConfig();
                } else if (cmdLabel.equalsIgnoreCase("reloadwhenempty")) {
                    rval = new WorldReloadWE();
                } else if (cmdLabel.equalsIgnoreCase("reloadwe")) {
                    rval = new WorldReloadWE();
                } else if (cmdLabel.equalsIgnoreCase("reloadempty")) {
                    rval = new WorldReloadWE();
                } else if (cmdLabel.equalsIgnoreCase("reloadnoplayers")) {
                    rval = new WorldReloadWE();
                } else if (cmdLabel.equalsIgnoreCase("togglerespawn")) {
                    rval = new WorldToggleRespawn();
                } else if (cmdLabel.equalsIgnoreCase("respawn")) {
                    rval = new WorldToggleRespawn();
                }
            }
        }
        if (rval == null) {
            rval = new Command(null, null);
            rval.init(sender, new String[] {cmdLabel});
            rval.execute();
        } else {
            rval.init(sender, args);
            if (!rval.hasPermission()) {
                if (rval.player == null) {
                    rval.sender.sendMessage("This command is only for players!");
                } else {
                    rval.locmessage(Localization.COMMAND_NOPERM);
                }
            } else {
                rval.execute();
            }
        }
    }		
    
    public void execute() {
        //This is executed when no command was found
        boolean hac = false; //has available commands
        for (String command : commandNodes) {
            hac |= showUsage(command);
        }
        if (hac) {
            message(ChatColor.RED + "Unknown command: " + args[0]);
        } else {
            locmessage(Localization.COMMAND_NOPERM);
        }
    }
}
