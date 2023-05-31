package org.spartandevs.customdeathmessages.commands;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spartandevs.customdeathmessages.chat.ChatColor;
import org.spartandevs.customdeathmessages.util.DeathCause;

import java.util.List;
import java.util.Set;

@CommandAlias("customdeathmessages|deathmessages|cdm")
public class CDMCommand extends CDMBaseCommand {
    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @CommandPermission("customdeathmessages.reload")
    @Description("Reloads the config file and other resources.")
    public void onReload(CommandSender sender) {
        plugin.reload();
        sendMessage(sender, "&aConfig reloaded.");
    }

    @Subcommand("add")
    @Syntax("<path> <death message>")
    @CommandPermission("customdeathmessages.modify")
    @Description("Adds a custom death message.")
    @CommandCompletion("@deathMessagePaths @nothing")
    public void onAdd(CommandSender sender, DeathCause path, String deathMessage) {
        plugin.getConfigManager().addCustomMessage(path, deathMessage);
        sendMessage(sender, "&aAdded custom death message.");
    }

    @Subcommand("list")
    @Syntax("<path>")
    @CommandPermission("customdeathmessages.modify")
    @Description("Lists all custom death messages and their indices.")
    @CommandCompletion("@deathMessagePaths")
    public void onList(CommandSender sender, DeathCause path) {
        Set<DeathCause> causes = DeathCause.deathCauseWithPath(path);

        sendMessage(sender, "&aCustom death messages for &e&l" + setToString(causes));
        List<String> messages = plugin.getConfigManager().listDeathMessages(path);

        for (int i = 0; i < messages.size(); i++) {
            sendMessage(sender, "&e&l" + i + ": &f" + messages.get(i));
        }
    }

    private static String setToString(Set<?> set) {
        StringBuilder builder = new StringBuilder();

        for (Object o : set) {
            builder.append(ChatColor.capitalize(o.toString().toLowerCase().replace("_", " ")));
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }

    @Subcommand("remove")
    @Syntax("<path> <index>")
    @CommandPermission("customdeathmessages.modify")
    @Description("Removes a custom death message.")
    @CommandCompletion("@deathMessagePaths @deathMessageIndices")
    public void onRemove(CommandSender sender, DeathCause path, @Conditions("indexInBounds") int index) {
        plugin.getConfigManager().removeCustomMessage(path, index);
        sendMessage(sender, "&aRemoved death message.");
    }

    @Subcommand("set flag")
    @Syntax("<path> <true|false>")
    @CommandPermission("customdeathmessages.modify")
    @Description("Sets a config flag.")
    @CommandCompletion("@boolConfigPaths true|false")
    public void onSetBoolean(CommandSender sender, @Conditions("validBoolConfigPath") String configPath, boolean value) {
        plugin.getConfigManager().setBoolean(configPath, value);
        sendMessage(sender, "&aSet config value. You may need to reload the plugin for this to take effect.");
    }

    @Subcommand("set message")
    @Syntax("<path> <message>")
    @CommandPermission("customdeathmessages.modify")
    @Description("Sets a config message.")
    @CommandCompletion("@stringConfigPaths @nothing")
    public void onSetString(CommandSender sender, @Conditions("validStringConfigPath") String configPath, String value) {
        plugin.getConfigManager().setString(configPath, value);
        sendMessage(sender, "&aSet config value. You may need to reload the plugin for this to take effect.");
    }

    @Subcommand("set number")
    @Syntax("<path> <value>")
    @CommandPermission("customdeathmessages.modify")
    @Description("Sets a config number.")
    @CommandCompletion("@numConfigPaths @nothing")
    public void onSetNumber(CommandSender sender, @Conditions("validNumConfigPath") String configPath, double value) {
        plugin.getConfigManager().setDouble(configPath, value);
        sendMessage(sender, "&aSet config value. You may need to reload the plugin for this to take effect.");
    }

    @Subcommand("debug shoot")
    @Syntax("<player>")
    @CommandCompletion("@players")
    @CommandPermission("customdeathmessages.debug")
    @Description("Shoots the player with an instant-kill arrow. Used for debugging.")
    @Conditions("debugEnabled")
    public void onDebugShoot(Player sender, @Optional Player target) {
        if (target == null) {
            target = sender;
        }

        target.damage(1000, sender);
        sendMessage(sender, "&aShot " + target.getName() + " with an instant-kill arrow.");
    }

    @CatchUnknown
    public void onUnknown(CommandSender sender) {
        sendMessage(sender, "&cUnknown command. Type /cdm help for help.");
    }
}
