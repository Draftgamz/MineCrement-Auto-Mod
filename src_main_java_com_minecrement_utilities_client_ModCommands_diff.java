--- src/main/java/com/minecrement/utilities/client/ModCommands.java (原始)


+++ src/main/java/com/minecrement/utilities/client/ModCommands.java (修改后)
package com.minecrement.utilities.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import com.minecrement.utilities.config.ModConfig;
import com.minecrement.utilities.utils.ChatUtil;

public class ModCommands {

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildCommands() {
        return ClientCommandManager.literal("mc")
            .then(ClientCommandManager.literal("enable")
                .executes(ModCommands::enableAll)
                .then(ClientCommandManager.argument("count", IntegerArgumentType.integer(1, 6))
                    .executes(ModCommands::enableWithCount)))
            .then(ClientCommandManager.literal("disable")
                .executes(ModCommands::disableAll))
            .then(ClientCommandManager.literal("reload")
                .executes(ModCommands::reloadConfig)
                .then(ClientCommandManager.argument("count", IntegerArgumentType.integer(1, 6))
                    .executes(ModCommands::reloadWithCount)))
            .then(ClientCommandManager.literal("status")
                .executes(ModCommands::showStatus))
            .then(ClientCommandManager.literal("alias")
                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                    .then(ClientCommandManager.argument("count", IntegerArgumentType.integer(1, 6))
                        .executes(ModCommands::setAlias))))
            .then(ClientCommandManager.literal("aliases")
                .executes(ModCommands::listAliases))
            .then(ClientCommandManager.literal("reconnect")
                .then(ClientCommandManager.literal("enable")
                    .executes(ModCommands::reconnectEnable))
                .then(ClientCommandManager.literal("disable")
                    .executes(ModCommands::reconnectDisable))
                .then(ClientCommandManager.literal("delay")
                    .then(ClientCommandManager.argument("seconds", IntegerArgumentType.integer(1, 30))
                        .executes(ModCommands::reconnectDelay))));
    }

    private static int enableAll(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        MineCrementUtilitiesClient.config.paused = false;

        Text msg = ChatUtil.createMessage("All features enabled", ChatUtil.MessageType.SUCCESS);
        source.sendFeedback(msg);

        Text info = ChatUtil.createInfoLine("Press J to pause | Press O for settings");
        source.sendFeedback(info);

        return 1;
    }

    private static int enableWithCount(CommandContext<FabricClientCommandSource> context) {
        int count = IntegerArgumentType.getInteger(context, "count");
        MineCrementUtilitiesClient.config.setSlotCount(count);
        MineCrementUtilitiesClient.config.paused = false;

        Text msg = ChatUtil.createMessage("Enabled with slot count: " + count, ChatUtil.MessageType.SUCCESS);
        context.getSource().sendFeedback(msg);

        return 1;
    }

    private static int disableAll(CommandContext<FabricClientCommandSource> context) {
        MineCrementUtilitiesClient.config.paused = true;

        Text msg = ChatUtil.createMessage("All features disabled", ChatUtil.MessageType.WARNING);
        context.getSource().sendFeedback(msg);

        return 1;
    }

    private static int reloadConfig(CommandContext<FabricClientCommandSource> context) {
        MineCrementUtilitiesClient.config = ModConfig.load();

        Text msg = ChatUtil.createMessage("Configuration reloaded", ChatUtil.MessageType.INFO);
        context.getSource().sendFeedback(msg);

        return 1;
    }

    private static int reloadWithCount(CommandContext<FabricClientCommandSource> context) {
        int count = IntegerArgumentType.getInteger(context, "count");
        MineCrementUtilitiesClient.config = ModConfig.load();
        MineCrementUtilitiesClient.config.setSlotCount(count);

        Text msg = ChatUtil.createMessage("Configuration reloaded with slot count: " + count, ChatUtil.MessageType.INFO);
        context.getSource().sendFeedback(msg);

        return 1;
    }

    private static int showStatus(CommandContext<FabricClientCommandSource> context) {
        ModConfig config = MineCrementUtilitiesClient.config;

        Text title = ChatUtil.createGradientMessage("=== MineCrement Status ===");
        context.getSource().sendFeedback(title);

        Text status = ChatUtil.createMessage(
            "Paused: " + (config.paused ? "Yes" : "No") +
            " | Slot: " + config.getSlotCount() +
            " | Interval: " + config.getIntervalSeconds() + "s" +
            " | Click: " + (config.rightClick ? "Right" : "Left"),
            ChatUtil.MessageType.INFO);
        context.getSource().sendFeedback(status);

        Text ggInfo = ChatUtil.createMessage(
            "GG Response: \"" + config.ggResponse + "\"" +
            " | Cooldown: " + config.getGGCooldownSeconds() + "s",
            ChatUtil.MessageType.INFO);
        context.getSource().sendFeedback(ggInfo);

        Text reconnectInfo = ChatUtil.createMessage(
            "AutoReconnect: " + (config.autoReconnectEnabled ? "Enabled" : "Disabled") +
            " | Delay: " + config.getReconnectDelaySeconds() + "s",
            ChatUtil.MessageType.INFO);
        context.getSource().sendFeedback(reconnectInfo);

        if (MineCrementUtilitiesClient.autoReconnect.isPending()) {
            Text pending = ChatUtil.createMessage(
                "Reconnecting in " + MineCrementUtilitiesClient.autoReconnect.getRemainingTime() + "s...",
                ChatUtil.MessageType.WARNING);
            context.getSource().sendFeedback(pending);
        }

        Text footer = ChatUtil.createInfoLine("Use /mc help for more commands");
        context.getSource().sendFeedback(footer);

        return 1;
    }

    private static int setAlias(CommandContext<FabricClientCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        int count = IntegerArgumentType.getInteger(context, "count");

        MineCrementUtilitiesClient.config.aliases.put(name, count);
        MineCrementUtilitiesClient.config.save();

        Text msg = ChatUtil.createMessage("Alias '" + name + "' set to slot count " + count, ChatUtil.MessageType.SUCCESS);
        context.getSource().sendFeedback(msg);

        return 1;
    }

    private static int listAliases(CommandContext<FabricClientCommandSource> context) {
        ModConfig config = MineCrementUtilitiesClient.config;

        if (config.aliases.isEmpty()) {
            Text msg = ChatUtil.createMessage("No aliases configured", ChatUtil.MessageType.INFO);
            context.getSource().sendFeedback(msg);
            return 1;
        }

        Text title = ChatUtil.createGradientMessage("=== Aliases ===");
        context.getSource().sendFeedback(title);

        for (var entry : config.aliases.entrySet()) {
            Text alias = ChatUtil.createMessage(
                entry.getKey() + " -> " + entry.getValue() + " slots",
                ChatUtil.MessageType.INFO);
            context.getSource().sendFeedback(alias);
        }

        return 1;
    }

    private static int reconnectEnable(CommandContext<FabricClientCommandSource> context) {
        MineCrementUtilitiesClient.autoReconnect.setEnabled(true);
        return 1;
    }

    private static int reconnectDisable(CommandContext<FabricClientCommandSource> context) {
        MineCrementUtilitiesClient.autoReconnect.setEnabled(false);
        MineCrementUtilitiesClient.autoReconnect.cancelReconnect();
        return 1;
    }

    private static int reconnectDelay(CommandContext<FabricClientCommandSource> context) {
        int seconds = IntegerArgumentType.getInteger(context, "seconds");
        MineCrementUtilitiesClient.config.setReconnectDelaySeconds(seconds);

        Text msg = ChatUtil.createMessage("Reconnect delay set to " + seconds + "s", ChatUtil.MessageType.INFO);
        context.getSource().sendFeedback(msg);

        return 1;
    }
}