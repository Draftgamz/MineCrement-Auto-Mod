--- src/main/java/com/minecrement/utilities/features/AutoGG.java (原始)


+++ src/main/java/com/minecrement/utilities/features/AutoGG.java (修改后)
package com.minecrement.utilities.features;

import com.minecrement.utilities.config.ModConfig;
import com.minecrement.utilities.utils.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class AutoGG {
    private final ModConfig config;
    private final MinecraftClient client;

    public AutoGG(ModConfig config, MinecraftClient client) {
        this.config = config;
        this.client = client;
    }

    public void tick() {
        if (config.paused) return;

        if (client.player == null || client.world == null) return;

        config.ggTimer--;
        if (config.ggTimer < 0) {
            config.ggTimer = 0;
        }
    }

    public void onGameMessage(Text message) {
        if (config.paused || client.player == null) return;

        String messageText = message.getString().toLowerCase();

        // Check for GG triggers (server broadcasts only)
        boolean triggerFound = messageText.contains("gg wave has started") ||
                               messageText.equals("gg!") ||
                               messageText.trim().equals("gg");

        if (triggerFound && config.ggTimer <= 0) {
            sendGG();
        }
    }

    private void sendGG() {
        if (client.player == null || client.getNetworkHandler() == null) return;

        client.execute(() -> {
            String response = config.ggResponse;
            if (response != null && !response.isEmpty()) {
                client.getNetworkHandler().sendChatCommand("say " + response);

                config.ggTimer = config.ggCooldownTicks;

                Text msg = ChatUtil.createMessage("AutoGG sent: " + response, ChatUtil.MessageType.SUCCESS);
                client.player.sendMessage(msg, true);

                Text info = ChatUtil.createInfoLine("Cooldown: " + config.getGGCooldownSeconds() + "s");
                client.player.sendMessage(info, true);
            }
        });
    }

    public void togglePause() {
        config.paused = !config.paused;
        sendStatusMessage();
    }

    private void sendStatusMessage() {
        client.execute(() -> {
            if (client.player == null) return;

            String status = config.paused ? "paused" : "resumed";
            Text msg = ChatUtil.createMessage("AutoGG " + status, ChatUtil.MessageType.INFO);
            client.player.sendMessage(msg, true);

            Text info = ChatUtil.createInfoLine("Response: \"" + config.ggResponse + "\" | Cooldown: " + config.getGGCooldownSeconds() + "s");
            client.player.sendMessage(info, true);
        });
    }

    public boolean isActive() {
        return !config.paused;
    }

    public int getRemainingCooldown() {
        return Math.max(0, config.ggTimer / 20);
    }
}