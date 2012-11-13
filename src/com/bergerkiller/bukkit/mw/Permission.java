package com.bergerkiller.bukkit.mw;

import com.bergerkiller.bukkit.common.permissions.IPermissionDefault;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public enum Permission implements IPermissionDefault {
    COMMAND_LIST("world.list", PermissionDefault.OP, "Sets if the player can list all worlds on the server"),
    COMMAND_INFO("world.info", PermissionDefault.OP, "Sets if the player can see world information, such as the seed and size"),
    COMMAND_CONFIG("world.config", PermissionDefault.OP, "Sets if the player can manually load and save the world configuration"),
    COMMAND_LISTGEN("world.listgenerators", PermissionDefault.OP, "Sets if the player can list all chunk generators on the server"),
    COMMAND_LOAD("world.load", PermissionDefault.OP, "Sets if the player can load unloaded worlds (not create)"),
    COMMAND_UNLOAD("world.unload", PermissionDefault.OP, "Sets if the player can unload loaded worlds (not create)"),
    COMMAND_CREATE("world.create", PermissionDefault.OP, "Sets if the player can create worlds (not replace)"),
    COMMAND_SPAWN("world.spawn", PermissionDefault.OP, "Sets if the player can teleport to world spawn points"),
    COMMAND_EVACUATE("world.evacuate", PermissionDefault.OP, "Sets if the player can clear a world from its players"),
    COMMAND_REPAIR("world.repair", PermissionDefault.OP, "Sets if the player can repair damaged worlds (only if broken)"),
    COMMAND_SAVE("world.save", PermissionDefault.OP, "Sets if the player can save worlds"),
    COMMAND_SETSAVING("world.setsaving", PermissionDefault.OP, "Sets if the player can toggle world auto-saving on or off"),
    COMMAND_DELETE("world.delete", PermissionDefault.OP, "Sets if the player can permanently delete worlds"),
    COMMAND_COPY("world.copy", PermissionDefault.FALSE, "Sets if the player can clone worlds"),
    COMMAND_DIFFICULTY("world.difficulty", PermissionDefault.OP, "Sets if the player can change the difficulty setting of worlds"),
    COMMAND_TOGGLESPAWNLOADED("world.togglespawnloaded", PermissionDefault.OP, "Sets if the player can toggle spawn chunk loading on or off"),
    COMMAND_GAMEMODE("world.gamemode", PermissionDefault.OP, "Sets if the player can change the gamemode of a world"),
    COMMAND_SETSPAWN("world.setspawn", PermissionDefault.OP, "Sets if the player can change the spawn point of a world"),
    COMMAND_TOGGLERESPAWN("world.togglerespawn", PermissionDefault.OP, "Sets if the player can toggle the forced respawn to the world spawn"),
    COMMAND_RELOADWE("world.reloadwe", PermissionDefault.OP, "Sets if players can toggle if worlds reload when empty"),
    GENERAL_TELEPORTALL("world.teleport.*", PermissionDefault.OP, "Sets the worlds a player can teleport to using /tpp and /world spawn"),
    GENERAL_IGNOREGM("world.ignoregamemode", PermissionDefault.FALSE, "Sets if the player game mode is not changed by the world game mode"),
    COMMAND_TPP("tpp", PermissionDefault.OP, "Sets if the player can teleport to worlds");
    
    private final String name;
    private final PermissionDefault def;
    private final String desc;
    private Permission(final String name, final PermissionDefault def, final String desc) {
        this.name = name;
        this.def = def;
        this.desc = desc;
    }
    
    @Override
    public String getName() {
        return "myworlds." + this.name;
    }
    
    @Override
    public PermissionDefault getDefault() {
        return this.def;
    }
    
    @Override
    public String getDescription() {
        return this.desc;
    }
    
    public boolean has(Player player) {
        return has(player, this.name);
    }
    
    public String toString() {
        return this.name;
    }
    
    public static boolean has(Player player, String command) {
        return player.hasPermission("myworlds." + command);
    }
    
    public static boolean hasGlobal(Player player, String node, String name) {
        return has(player, node + name) || has(player, node + "*");
    }
}
