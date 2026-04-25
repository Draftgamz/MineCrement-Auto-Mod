--- src/main/java/com/minecrement/utilities/utils/ChatUtil.java (原始)


+++ src/main/java/com/minecrement/utilities/utils/ChatUtil.java (修改后)
package com.minecrement.utilities.utils;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatUtil {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static Text createMessage(String text, MessageType type) {
        MutableText prefix = Text.literal("[MC]").formatted(Formatting.BOLD);

        switch (type) {
            case INFO -> prefix.append(Text.literal(" ").formatted(Formatting.RESET));
            case SUCCESS -> prefix.append(Text.literal(" ").formatted(Formatting.RESET));
            case WARNING -> prefix.append(Text.literal(" ").formatted(Formatting.RESET));
        }

        MutableText content = Text.literal(text);
        switch (type) {
            case INFO -> content.formatted(Formatting.GRAY);
            case SUCCESS -> content.formatted(Formatting.GREEN);
            case WARNING -> content.formatted(Formatting.YELLOW);
        }

        return prefix.append(content);
    }

    public static Text createGradientMessage(String text) {
        MutableText result = Text.empty();

        // Gradient colors: Aqua -> Light Purple
        Formatting[] gradient = {
            Formatting.AQUA,
            Formatting.BLUE,
            Formatting.DARK_PURPLE,
            Formatting.LIGHT_PURPLE
        };

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int colorIndex = (i * (gradient.length - 1)) / Math.max(1, text.length() - 1);
            result.append(Text.literal(String.valueOf(c)).formatted(gradient[colorIndex]));
        }

        return result;
    }

    public static Text createInfoLine(String info) {
        String timestamp = LocalTime.now().format(TIME_FORMAT);
        return Text.literal("↳ " + timestamp + " • " + info).formatted(Formatting.ITALIC, Formatting.DARK_GRAY);
    }

    public enum MessageType {
        INFO,
        SUCCESS,
        WARNING
    }
}