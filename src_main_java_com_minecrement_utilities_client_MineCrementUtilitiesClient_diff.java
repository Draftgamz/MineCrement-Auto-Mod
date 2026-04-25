--- src/main/java/com/minecrement/utilities/client/MineCrementUtilitiesClient.java (原始)


+++ src/main/java/com/minecrement/utilities/client/MineCrementUtilitiesClient.java (修改后)
package com.minecrement.utilities.client;

import com.minecrement.utilities.config.ModConfig;
import com.minecrement.utilities.features.AutoSlot;
import com.minecrement.utilities.features.AutoGG;
import com.minecrement.utilities.features.AutoReconnect;
import com.minecrement.utilities.gui.SettingsScreen;
import com.minecrement.utilities.utils.ChatUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class MineCrementUtilitiesClient implements ClientModInitializer {
    public static ModConfig config;
    public static AutoSlot autoSlot;
    public static AutoGG autoGG;
    public static AutoReconnect autoReconnect;

    private static KeyBinding pauseKey;
    private static KeyBinding settingsKey;

    @Override
    public void onInitializeClient() {
        // Load config
        config = ModConfig.load();

        MinecraftClient client = MinecraftClient.getInstance();

        // Initialize features
        autoSlot = new AutoSlot(config, client);
        autoGG = new AutoGG(config, client);
        autoReconnect = new AutoReconnect(config, client);

        // Register commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ModCommands.buildCommands());
        });

        // Register keybindings
        pauseKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.minecrement.pause",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "category.minecrement"
        ));

        settingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.minecrement.settings",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "category.minecrement"
        ));

        // Register tick event
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

        // Register chat message event (GAME type for server broadcasts)
        ClientReceiveMessageEvents.GAME.register(this::onGameMessage);

        // Register disconnect event
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client1) -> {
            if (client1.getServer() == null && handler.getConnectionInfo() != null) {
                String address = handler.getConnectionInfo().address().toString();
                autoReconnect.onDisconnect(address, null);
            }
        });

        System.out.println("[MineCrement Utilities] Initialized!");
    }

    private void onClientTick(MinecraftClient client) {
        // Handle keybindings
        while (pauseKey.wasPressed()) {
            config.paused = !config.paused;

            if (client.player != null) {
                String status = config.paused ? "paused" : "resumed";
                Text msg = ChatUtil.createMessage("All features " + status, ChatUtil.MessageType.INFO);
                client.player.sendMessage(msg, true);

                Text info = ChatUtil.createInfoLine("AutoSlot | AutoGG | AutoReconnect");
                client.player.sendMessage(info, true);
            }
        }

        while (settingsKey.wasPressed()) {
            if (client.player != null) {
                client.setScreen(new SettingsScreen(config, client));
            }
        }

        // Tick features
        autoSlot.tick();
        autoSlot.updateSmartCooldown();
        autoGG.tick();
        autoReconnect.tick();
    }

    private void onGameMessage(Text message, boolean overlay) {
        autoGG.onGameMessage(message);

        // Parse smart cooldown from chat: "for Xs" pattern
        String text = message.getString();
        if (config.smartCooldownActive) return;

        // Look for patterns like "for 5s" or "for 10s"
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("for\\s+(\\d+)s", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            int seconds = Integer.parseInt(matcher.group(1));
            autoSlot.setSmartCooldown(seconds);
        }
    }
}