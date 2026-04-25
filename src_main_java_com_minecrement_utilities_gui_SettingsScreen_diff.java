--- src/main/java/com/minecrement/utilities/gui/SettingsScreen.java (原始)


+++ src/main/java/com/minecrement/utilities/gui/SettingsScreen.java (修改后)
package com.minecrement.utilities.gui;

import com.minecrement.utilities.config.ModConfig;
import com.minecrement.utilities.utils.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class SettingsScreen extends Screen {
    private final ModConfig config;
    private final MinecraftClient client;

    private SliderWidget slotCountSlider;
    private SliderWidget intervalSlider;
    private SliderWidget ggCooldownSlider;
    private SliderWidget reconnectDelaySlider;
    private TextFieldWidget ggResponseField;
    private ButtonWidget clickTypeButton;
    private CheckboxWidget autoReconnectCheckbox;

    private List<Element> children = new ArrayList<>();
    private List<Selectable> selectables = new ArrayList<>();

    public SettingsScreen(ModConfig config, MinecraftClient client) {
        super(Text.literal("MineCrement Utilities Settings"));
        this.config = config;
        this.client = client;
    }

    @Override
    protected void init() {
        children.clear();
        selectables.clear();

        int y = 30;
        int labelWidth = 180;
        int sliderWidth = 200;
        int x = (width - labelWidth - sliderWidth - 20) / 2;

        // Title
        Text titleText = ChatUtil.createGradientMessage("MineCrement Utilities");
        addDrawableChild(new TextWidget(width / 2 - 100, 10, 200, 20, titleText, textRenderer));

        // Slot Count Slider
        addLabel(x, y, "AutoSlot - Slot Count (1-6):");
        slotCountSlider = new SliderWidget(x + labelWidth + 10, y - 3, sliderWidth, 20,
            Text.literal(String.valueOf(config.getSlotCount())),
            value -> {
                int val = (int) Math.round(value * 5) + 1;
                config.setSlotCount(val);
                slotCountSlider.setMessage(Text.literal(String.valueOf(val)));
            });
        slotCountSlider.setValue((config.getSlotCount() - 1) / 5.0);
        addElement(slotCountSlider);

        y += 30;

        // Interval Slider
        addLabel(x, y, "AutoSlot - Interval (10-120s):");
        intervalSlider = new SliderWidget(x + labelWidth + 10, y - 3, sliderWidth, 20,
            Text.literal(String.valueOf(config.getIntervalSeconds()) + "s"),
            value -> {
                int val = (int) Math.round(value * 110) + 10;
                config.setIntervalSeconds(val);
                intervalSlider.setMessage(Text.literal(val + "s"));
            });
        intervalSlider.setValue((config.getIntervalSeconds() - 10) / 110.0);
        addElement(intervalSlider);

        y += 30;

        // GG Cooldown Slider
        addLabel(x, y, "AutoGG - Cooldown (1-10s):");
        ggCooldownSlider = new SliderWidget(x + labelWidth + 10, y - 3, sliderWidth, 20,
            Text.literal(String.valueOf(config.getGGCooldownSeconds()) + "s"),
            value -> {
                int val = (int) Math.round(value * 9) + 1;
                config.setGGCooldownSeconds(val);
                ggCooldownSlider.setMessage(Text.literal(val + "s"));
            });
        ggCooldownSlider.setValue((config.getGGCooldownSeconds() - 1) / 9.0);
        addElement(ggCooldownSlider);

        y += 30;

        // Reconnect Delay Slider
        addLabel(x, y, "AutoReconnect - Delay (1-30s):");
        reconnectDelaySlider = new SliderWidget(x + labelWidth + 10, y - 3, sliderWidth, 20,
            Text.literal(String.valueOf(config.getReconnectDelaySeconds()) + "s"),
            value -> {
                int val = (int) Math.round(value * 29) + 1;
                config.setReconnectDelaySeconds(val);
                reconnectDelaySlider.setMessage(Text.literal(val + "s"));
            });
        reconnectDelaySlider.setValue((config.getReconnectDelaySeconds() - 1) / 29.0);
        addElement(reconnectDelaySlider);

        y += 35;

        // GG Response Text Field
        addLabel(x, y, "AutoGG - Response Message:");
        ggResponseField = new TextFieldWidget(textRenderer, x + labelWidth + 10, y - 3, sliderWidth, 20, Text.literal("GG Response"));
        ggResponseField.setText(config.ggResponse);
        ggResponseField.setChangedListener(text -> config.ggResponse = text);
        addElement(ggResponseField);

        y += 35;

        // Click Type Button
        addLabel(x, y, "AutoSlot - Click Type:");
        clickTypeButton = ButtonWidget.builder(
            Text.literal(config.rightClick ? "Right Click" : "Left Click"),
            btn -> {
                config.rightClick = !config.rightClick;
                clickTypeButton.setMessage(Text.literal(config.rightClick ? "Right Click" : "Left Click"));
            })
            .dimensions(x + labelWidth + 10, y - 3, sliderWidth, 20)
            .build();
        addElement(clickTypeButton);

        y += 35;

        // AutoReconnect Checkbox
        autoReconnectCheckbox = new CheckboxWidget(x + labelWidth + 10, y - 3, 20, 20,
            Text.literal(""), config.autoReconnectEnabled);
        autoReconnectCheckbox.setOnChanged(newValue -> config.autoReconnectEnabled = newValue);
        addElement(autoReconnectCheckbox);
        addLabel(x, y + 3, "Enable AutoReconnect");

        y += 40;

        // Save Button
        ButtonWidget saveButton = ButtonWidget.builder(
            Text.literal("Save & Close").formatted(Formatting.GREEN),
            btn -> {
                config.save();
                close();
            })
            .dimensions(width / 2 - 75, height - 50, 150, 20)
            .build();
        addElement(saveButton);

        // Status info at bottom
        Text statusInfo = ChatUtil.createInfoLine("Press J to toggle pause | Press O to reopen settings");
        addDrawable(new TextWidget(width / 2 - 150, height - 25, 300, 20, statusInfo, textRenderer));
    }

    private void addLabel(int x, int y, String text) {
        TextWidget label = new TextWidget(x, y + 4, 180, 20, Text.literal(text), textRenderer);
        addDrawable(label);
    }

    private void addElement(Element element) {
        children.add(element);
        if (element instanceof Selectable) {
            selectables.add((Selectable) element);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        // Draw background panel
        context.fillGradient(0, 0, width, height, 0xC0101010, 0xD0181818);

        super.render(context, mouseX, mouseY, delta);

        // Draw custom gradient title
        Text titleText = ChatUtil.createGradientMessage("MineCrement Utilities Settings");
        context.drawTextWithShadow(textRenderer, titleText, width / 2 - textRenderer.getWidth(titleText) / 2, 10, 0xFFFFFF);
    }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    @Override
    public List<? extends Selectable> narratables() {
        return selectables;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}