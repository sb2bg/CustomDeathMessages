package org.spartandevs.customdeathmessages.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.Dependency;
import org.bukkit.command.CommandSender;
import org.spartandevs.customdeathmessages.CustomDeathMessages;

public class CDMBaseCommand extends BaseCommand {
    @Dependency
    protected CustomDeathMessages plugin;
    private static final String MSG_PREFIX = "&8&l[&e&lCDM&8&l] ";

    protected void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(plugin.translateColorCodes(MSG_PREFIX + message));
    }
}
