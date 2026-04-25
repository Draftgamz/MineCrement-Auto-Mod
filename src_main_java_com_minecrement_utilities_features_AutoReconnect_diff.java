--- src/main/java/com/minecrement/utilities/features/AutoReconnect.java (原始)


+++ src/main/java/com/minecrement/utilities/features/AutoReconnect.java (修改后)
package com.minecrement.utilities.features;

import com.minecrement.utilities.config.ModConfig;
import com.minecrement.utilities.utils.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.text.Text;

public class AutoReconnect {
    private final ModConfig config;
    private final MinecraftClient client;

    public AutoReconnect(ModConfig config, MinecraftClient client) {
        this.config = config;
        this.client = client;
    }

    public void tick() {
        if (!config.autoReconnectEnabled || !config.reconnectPending) return;

        config.reconnectTimer--;

        if (config.reconnectTimer <= 0) {
            attemptReconnect();
        }
    }

    public void onDisconnect(String serverAddress, String serverName) {
        if (!config.autoReconnectEnabled) return;

        // Only in multiplayer
        if (client.isInSingleplayer()) return;

        config.lastServer = serverAddress;
        config.reconnectPending = true;
        config.reconnectTimer = config.reconnectDelayTicks;

        client.execute(() -> {
            if (client.player == null) return;

            Text msg = ChatUtil.createMessage("Disconnected from " + (serverName != null ? serverName : serverAddress), ChatUtil.MessageType.WARNING);
            client.player.sendMessage(msg, true);

            Text info = ChatUtil.createInfoLine("Auto-reconnect in " + config.getReconnectDelaySeconds() + "s...");
            client.player.sendMessage(info, true);
        });
    }

    private void attemptReconnect() {
        if (config.lastServer == null || client.world != null) {
            config.reconnectPending = false;
            return;
        }

        client.execute(() -> {
            if (client.player == null) return;

            try {
                ServerAddress address = ServerAddress.parse(config.lastServer);

                Text msg = ChatUtil.createMessage("Reconnecting...", ChatUtil.MessageType.INFO);
                client.player.sendMessage(msg, true);

                ConnectScreen.connect(
                    new TitleScreen(),
                    client,
                    address,
                    false
                );

                config.reconnectPending = false;

                Text successMsg = ChatUtil.createMessage("Reconnection initiated", ChatUtil.MessageType.SUCCESS);
                client.player.sendMessage(successMsg, true);
            } catch (Exception e) {
                Text errorMsg = ChatUtil.createMessage("Reconnection failed: " + e.getMessage(), ChatUtil.MessageType.WARNING);
                client.player.sendMessage(errorMsg, true);
                config.reconnectPending = false;
            }
        });
    }

    public void cancelReconnect() {
        config.reconnectPending = false;
        config.reconnectTimer = 0;

        client.execute(() -> {
            if (client.player == null) return;

            Text msg = ChatUtil.createMessage("Auto-reconnect cancelled", ChatUtil.MessageType.INFO);
            client.player.sendMessage(msg, true);
        });
    }

    public void toggleEnabled() {
        config.autoReconnectEnabled = !config.autoReconnectEnabled;
        sendStatusMessage();
    }

    public void setEnabled(boolean enabled) {
        config.autoReconnectEnabled = enabled;
        sendStatusMessage();
    }

    private void sendStatusMessage() {
        client.execute(() -> {
            if (client.player == null) return;

            String status = config.autoReconnectEnabled ? "enabled" : "disabled";
            Text msg = ChatUtil.createMessage("AutoReconnect " + status, ChatUtil.MessageType.INFO);
            client.player.sendMessage(msg, true);

            Text info = ChatUtil.createInfoLine("Delay: " + config.getReconnectDelaySeconds() + "s");
            client.player.sendMessage(info, true);
        });
    }

    public int getRemainingTime() {
        return config.reconnectPending ? Math.max(0, config.reconnectTimer / 20) : 0;
    }

    public boolean isPending() {
        return config.reconnectPending;
    }

    public boolean isEnabled() {
        return config.autoReconnectEnabled;
    }
}