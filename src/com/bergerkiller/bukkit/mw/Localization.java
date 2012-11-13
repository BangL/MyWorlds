package com.bergerkiller.bukkit.mw;

import com.bergerkiller.bukkit.common.localization.ILocalizationDefault;
import org.bukkit.command.CommandSender;

public enum Localization implements ILocalizationDefault {
    COMMAND_NOPERM("command.nopermission", "§4You do not have permission to use this command!"),
    WORLD_NOTFOUND("world.notfound", "§cWorld not found!"),
    WORLD_NOTLOADED("world.notloaded", "§eWorld '%0%' is not loaded!");
    
    private final String name;
    private final String defValue;
    
    private Localization(String name, String defValue) {
        this.name = name;
        this.defValue = defValue;
    }
    
    @Override
    public String getDefault() {
        return this.defValue;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    /**
     * Sends this Localization message to the sender specified
     * 
     * @param sender to send to
     * @param arguments for the node
     */
    public void message(CommandSender sender, String...arguments) {
        sender.sendMessage(get(arguments));
    }
    
    /**
     * Gets the locale value for this Localization node
     * 
     * @param arguments for the node
     * @return Locale value
     */
    public String get(String... arguments) {
        return MyWorldsLite.plugin.getLocale(this.getName(), arguments);
    }
}
