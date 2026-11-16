package org.spartandevs.customdeathmessages.commands;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spartandevs.customdeathmessages.chat.ChatColor;
import org.spartandevs.customdeathmessages.chat.DeathMessage;
import org.spartandevs.customdeathmessages.chat.HoverTransforms;
import org.spartandevs.customdeathmessages.chat.PlaceholderPopulator;
import org.spartandevs.customdeathmessages.util.ConfigManager;

import java.util.List;

@CommandAlias("cdm|customdeathmessages|customdeathmessage|deathmessages|deathmessage")
public class CDMCommand extends CDMBaseCommand {
    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload|rl")
    @CommandPermission("cdm.reload")
    @Description("Reloads the config file and other resources.")
    public void onReload(CommandSender sender) {
        String error = plugin.reload();

        if (error == null) {
            sendMessage(sender, "&aPlugin reloaded.");
            return;
        }

        sendMessage(sender, "&cReload failed, the previous configuration is still active:");
        sendRaw(sender, error);
    }

    @Subcommand("add")
    @Syntax("<path> <death message>")
    @CommandPermission("cdm.modify")
    @Description("Adds a custom death message.")
    @CommandCompletion("@messagePaths @nothing")
    public void onAdd(CommandSender sender, @Conditions("newMessagePath") String path, String deathMessage) {
        report(sender, plugin.getConfigManager().addCustomMessage(path, deathMessage), "&aAdded custom death message.");
    }

    @Subcommand("list")
    @Syntax("<path>")
    @CommandPermission("cdm.modify")
    @Description("Lists all custom death messages and their indices.")
    @CommandCompletion("@messagePaths")
    public void onList(CommandSender sender, @Conditions("messagePath") String path) {
        List<String> messages = plugin.getConfigManager().listDeathMessages(path);

        if (messages.isEmpty()) {
            sendMessage(sender, "&e" + path + " &ais empty, so the vanilla death message is used.");
            return;
        }

        sendMessage(sender, "&aDeath messages in &e" + path + "&a:");

        // show the raw text (with & codes) so it can be copied and edited
        for (int i = 0; i < messages.size(); i++) {
            String message = messages.get(i);
            sendRaw(sender, ChatColor.translate("&e" + i + ": &r") + (message.isEmpty() ? ChatColor.translate("&7(blank: no message is shown)") : message));
        }
    }

    @Subcommand("remove")
    @Syntax("<path> <index>")
    @CommandPermission("cdm.modify")
    @Description("Removes a death message. Removing the last one makes this death use the vanilla message.")
    @CommandCompletion("@messagePaths @deathMessageIndices")
    public void onRemove(CommandSender sender, @Conditions("messagePath") String path, @Conditions("indexInBounds") int index) {
        report(sender, plugin.getConfigManager().removeCustomMessage(path, index), "&aRemoved death message.");
    }

    @Subcommand("set flag")
    @Syntax("<path> <true|false>")
    @CommandPermission("cdm.modify")
    @Description("Sets a config flag.")
    @CommandCompletion("@boolConfigPaths true|false")
    public void onSetBoolean(CommandSender sender, @Conditions("validBoolConfigPath") String configPath, boolean value) {
        report(sender, plugin.getConfigManager().set(configPath, value), "&aSet &e" + configPath + " &ato &e" + value + "&a.");
    }

    @Subcommand("set message")
    @Syntax("<path> <message>")
    @CommandPermission("cdm.modify")
    @Description("Sets a config message.")
    @CommandCompletion("@stringConfigPaths @nothing")
    public void onSetString(CommandSender sender, @Conditions("validStringConfigPath") String configPath, String value) {
        report(sender, plugin.getConfigManager().set(configPath, value), "&aSet &e" + configPath + "&a.");
    }

    @Subcommand("set number")
    @Syntax("<path> <value>")
    @CommandPermission("cdm.modify")
    @Description("Sets a config number.")
    @CommandCompletion("@numConfigPaths @nothing")
    public void onSetNumber(CommandSender sender, @Conditions("validNumConfigPath") String configPath, double value) {
        String invalid = ConfigManager.validateNumber(configPath, value);

        if (invalid != null) {
            sendMessage(sender, "&c" + invalid);
            return;
        }

        report(sender, plugin.getConfigManager().set(configPath, value), "&aSet &e" + configPath + " &ato &e" + value + "&a.");
    }

    @Subcommand("debug shoot")
    @Syntax("<player>")
    @CommandCompletion("@players")
    @CommandPermission("cdm.debug")
    @Description("Kills the player with an instant-kill melee hit from you. Used for debugging.")
    @Conditions("debugEnabled")
    public void onDebugShoot(Player sender, @Optional Player target) {
        if (target == null) {
            target = sender;
        }

        target.damage(1000, sender);
        sendMessage(sender, "&aHit " + target.getName() + " with an instant-kill melee attack.");
    }

    @Subcommand("debug prime")
    @Syntax("<player>")
    @CommandCompletion("@players")
    @CommandPermission("cdm.debug")
    @Description("Sets the player's health to 1. Used for debugging.")
    @Conditions("debugEnabled")
    public void onDebugPrime(Player sender, @Optional Player target) {
        if (target == null) {
            target = sender;
        }

        target.setHealth(1);
        target.setSaturation(0);
        target.setFoodLevel(14);
        sendMessage(sender, "&aPrimed " + target.getName() + ".");
    }

    @Subcommand("debug hover")
    @Syntax("<player>")
    @CommandCompletion("@players")
    @CommandPermission("cdm.debug")
    @Description("Sends a test death message with your held item so you can verify hover rendering.")
    @Conditions("debugEnabled")
    public void onDebugHover(Player sender, @Optional Player target) {
        if (target == null) {
            target = sender;
        }

        ItemStack weapon = getHeldItem(sender);

        if (weapon == null || weapon.getType() == Material.AIR) {
            sendMessage(sender, "&cHold the item you want to test first.");
            return;
        }

        ConfigManager config = plugin.getConfigManager();
        boolean useHover = config.isItemOnHoverEnabled() || config.isOriginalOnHoverEnabled();
        ItemStack placeholderItem = config.isItemOnHoverEnabled() ? null : weapon;
        Entity killer = sender;

        PlaceholderPopulator populator = new PlaceholderPopulator(plugin, target, killer, placeholderItem);
        String originalMessage = new PlaceholderPopulator(plugin, target, killer, null).replace(target, "%victim% was slain by %killer%");
        HoverTransforms hoverTransforms = new HoverTransforms(plugin, originalMessage, weapon);
        DeathMessage deathMessage = new DeathMessage(
                "&c%victim% &ewas debugged by &c%killer% &eusing &7[&b%kill-weapon%&7]",
                populator,
                useHover ? DeathMessage.MessageType.JSON : DeathMessage.MessageType.STRING
        );

        if (deathMessage.getMessageType() == DeathMessage.MessageType.JSON) {
            sender.spigot().sendMessage(deathMessage.getTextComponent(hoverTransforms));
        } else {
            sendMessage(sender, deathMessage.getStringMessage());
        }
    }

    @CatchUnknown
    public void onUnknown(CommandSender sender) {
        sendMessage(sender, "&cUnknown command. Type /cdm help for help.");
    }

    @SuppressWarnings("deprecation")
    private static ItemStack getHeldItem(Player player) {
        try {
            return player.getInventory().getItemInMainHand();
        } catch (NoSuchMethodError ignored) {
            return player.getItemInHand();
        }
    }
}
