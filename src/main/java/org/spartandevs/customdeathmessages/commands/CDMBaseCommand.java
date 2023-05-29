package org.spartandevs.customdeathmessages.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.Dependency;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

public class CDMBaseCommand extends BaseCommand {
    @Dependency
    protected CustomDeathMessages plugin;
    protected static final String PERMISSION_PREFIX = "customdeathmessages.";
    protected static final String PERMISSION_RELOAD = PERMISSION_PREFIX + "reload";
    protected static final String PERMISSION_MODIFY = PERMISSION_PREFIX + "modify";
    protected static final String PERMISSION_ADMIN = PERMISSION_PREFIX + "admin";
    private static final String MSG_PREFIX = "&8&l[&e&lCDM&8&l] ";

    protected static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MSG_PREFIX + message));
    }
}
