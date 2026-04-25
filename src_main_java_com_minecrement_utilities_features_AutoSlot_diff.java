--- src/main/java/com/minecrement/utilities/features/AutoSlot.java (原始)


+++ src/main/java/com/minecrement/utilities/features/AutoSlot.java (修改后)
package com.minecrement.utilities.features;

import com.minecrement.utilities.config.ModConfig;
import com.minecrement.utilities.utils.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class AutoSlot {
    private final ModConfig config;
    private final MinecraftClient client;

    public AutoSlot(ModConfig config, MinecraftClient client) {
        this.config = config;
        this.client = client;
    }

    public void tick() {
        if (config.paused || config.smartCooldownActive) return;

        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        config.slotTimer++;
        if (config.slotTimer >= config.intervalTicks) {
            config.slotTimer = 0;

            // Cycle to next slot (2 -> N)
            int minSlot = 1; // Hotbar starts at 0, but we use 1-9 for display
            int maxSlot = config.getSlotCount();

            config.currentSlot++;
            if (config.currentSlot > maxSlot) {
                config.currentSlot = minSlot;
            }

            // Select the slot (0-indexed for actual selection)
            int slotIndex = config.currentSlot - 1;
            if (slotIndex >= 0 && slotIndex < 9) {
                client.player.getInventory().selectedSlot = slotIndex;

                // Simulate click
                simulateClick();
            }
        }
    }

    private void simulateClick() {
        if (client.interactionManager == null || client.player == null) return;

        // Use client.execute for thread safety
        client.execute(() -> {
            if (config.rightClick) {
                // Right click
                client.interactionManager.interactItem(client.player,
                    client.player.getActiveHand());
            } else {
                // Left click - attack air
                client.player.swingHand(client.player.getActiveHand());
            }
        });
    }

    public void togglePause() {
        config.paused = !config.paused;
        sendStatusMessage();
    }

    public void setSmartCooldown(int seconds) {
        config.smartCooldownActive = true;
        config.smartCooldownTicks = seconds * 20;
        client.execute(() -> {
            Text msg = ChatUtil.createInfoLine("Smart cooldown active for " + seconds + "s");
            client.player.sendMessage(msg, true);
        });
    }

    public void updateSmartCooldown() {
        if (config.smartCooldownActive) {
            config.smartCooldownTicks--;
            if (config.smartCooldownTicks <= 0) {
                config.smartCooldownActive = false;
            }
        }
    }

    private void sendStatusMessage() {
        client.execute(() -> {
            if (client.player == null) return;

            String status = config.paused ? "paused" : "resumed";
            Text msg = ChatUtil.createMessage("AutoSlot " + status, ChatUtil.MessageType.INFO);
            client.player.sendMessage(msg, true);

            Text info = ChatUtil.createInfoLine("Slot: " + config.currentSlot + "/" + config.getSlotCount() +
                " | Interval: " + config.getIntervalSeconds() + "s | Click: " + (config.rightClick ? "Right" : "Left"));
            client.player.sendMessage(info, true);
        });
    }

    public boolean isActive() {
        return !config.paused && !config.smartCooldownActive;
    }
}