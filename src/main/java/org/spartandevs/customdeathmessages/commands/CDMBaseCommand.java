package org.spartandevs.customdeathmessages.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.Dependency;
import org.bukkit.command.CommandSender;
import org.spartandevs.customdeathmessages.CustomDeathMessages;
import org.spartandevs.customdeathmessages.chat.ChatColor;

public class CDMBaseCommand extends BaseCommand {
    @Dependency
    protected CustomDeathMessages plugin;
    private static final String MSG_PREFIX = "&8&l[&e&lCDM&8&l] ";

    protected void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translate(MSG_PREFIX + message));
    }

    /**
     * Sends text without translating color codes, e.g. raw config values or error messages.
     */
    protected void sendRaw(CommandSender sender, String text) {
        for (String line : text.split("\\R")) {
            sender.sendMessage(ChatColor.translate(MSG_PREFIX + "&f") + line);
        }
    }

    /**
     * Reports the outcome of a config change: {@code error} is {@code null} on success.
     */
    protected void report(CommandSender sender, String error, String success) {
        if (error == null) {
            sendMessage(sender, success);
            return;
        }

        sendMessage(sender, "&cCould not save the change, config.yml was left untouched:");
        sendRaw(sender, error);
    }
}
