--- src/main/java/com/minecrement/utilities/config/ModConfig.java (原始)


+++ src/main/java/com/minecrement/utilities/config/ModConfig.java (修改后)
package com.minecrement.utilities.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final Path CONFIG_DIR = Path.of("config", "minecrement");
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("state.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // AutoSlot settings
    @SerializedName("slot_count")
    public int slotCount = 4;
    @SerializedName("interval_ticks")
    public int intervalTicks = 40; // 2 seconds at 20 TPS
    @SerializedName("click_type")
    public boolean rightClick = true; // true = right, false = left

    // AutoGG settings
    @SerializedName("gg_response")
    public String ggResponse = "Good game!";
    @SerializedName("gg_cooldown_ticks")
    public int ggCooldownTicks = 100; // 5 seconds at 20 TPS

    // AutoReconnect settings
    @SerializedName("auto_reconnect_enabled")
    public boolean autoReconnectEnabled = false;
    @SerializedName("reconnect_delay_ticks")
    public int reconnectDelayTicks = 60; // 3 seconds at 20 TPS

    // Aliases
    @SerializedName("aliases")
    public java.util.Map<String, Integer> aliases = new java.util.HashMap<>();

    // Runtime state (not saved)
    @SerializedName(value = "paused", alternate = {"_paused"})
    public transient boolean paused = false;
    @SerializedName(value = "last_server", alternate = {"_last_server"})
    public transient String lastServer = null;
    @SerializedName(value = "reconnect_pending", alternate = {"_reconnect_pending"})
    public transient boolean reconnectPending = false;
    @SerializedName(value = "reconnect_timer", alternate = {"_reconnect_timer"})
    public transient int reconnectTimer = 0;
    @SerializedName(value = "slot_timer", alternate = {"_slot_timer"})
    public transient int slotTimer = 0;
    @SerializedName(value = "current_slot", alternate = {"_current_slot"})
    public transient int currentSlot = 1;
    @SerializedName(value = "gg_timer", alternate = {"_gg_timer"})
    public transient int ggTimer = 0;
    @SerializedName(value = "smart_cooldown_active", alternate = {"_smart_cooldown_active"})
    public transient boolean smartCooldownActive = false;
    @SerializedName(value = "smart_cooldown_ticks", alternate = {"_smart_cooldown_ticks"})
    public transient int smartCooldownTicks = 0;

    public static ModConfig load() {
        if (!Files.exists(CONFIG_FILE)) {
            ModConfig config = new ModConfig();
            config.save();
            return config;
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_FILE)) {
            return GSON.fromJson(reader, ModConfig.class);
        } catch (Exception e) {
            System.err.println("[MineCrement] Failed to load config: " + e.getMessage());
            return new ModConfig();
        }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            try (Writer writer = Files.newBufferedWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            System.err.println("[MineCrement] Failed to save config: " + e.getMessage());
        }
    }

    public int getSlotCount() {
        return Math.max(1, Math.min(6, slotCount));
    }

    public void setSlotCount(int count) {
        this.slotCount = Math.max(1, Math.min(6, count));
    }

    public int getIntervalSeconds() {
        return intervalTicks / 20;
    }

    public void setIntervalSeconds(int seconds) {
        this.intervalTicks = Math.max(10, Math.min(120, seconds)) * 20;
    }

    public int getGGCooldownSeconds() {
        return ggCooldownTicks / 20;
    }

    public void setGGCooldownSeconds(int seconds) {
        this.ggCooldownTicks = Math.max(1, Math.min(10, seconds)) * 20;
    }

    public int getReconnectDelaySeconds() {
        return reconnectDelayTicks / 20;
    }

    public void setReconnectDelaySeconds(int seconds) {
        this.reconnectDelayTicks = Math.max(1, Math.min(30, seconds)) * 20;
    }
}