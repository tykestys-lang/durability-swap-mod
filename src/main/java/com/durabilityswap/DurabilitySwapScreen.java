package com.durabilityswap;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class DurabilitySwapScreen extends Screen {

    private final Screen parent;
    private float threshold;
    private boolean enabled;

    public DurabilitySwapScreen(Screen parent) {
        super(new LiteralText("DurabilitySwap Config"));
        this.parent = parent;
        this.threshold = DurabilitySwapMod.getDurabilityThreshold();
        this.enabled = DurabilitySwapMod.isModEnabled();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addButton(new ButtonWidget(centerX - 100, centerY - 60, 200, 20,
            new LiteralText("Mod: " + (enabled ? "ACTIVADO" : "DESACTIVADO")),
            button -> {
                enabled = !enabled;
                DurabilitySwapMod.setModEnabled(enabled);
                button.setMessage(new LiteralText("Mod: " + (enabled ? "ACTIVADO" : "DESACTIVADO")));
            }
        ));

        this.addButton(new ButtonWidget(centerX - 100, centerY - 20, 90, 20,
            new LiteralText("- 5%"),
            button -> {
                threshold = Math.max(0.05f, threshold - 0.05f);
                DurabilitySwapMod.setDurabilityThreshold(threshold);
            }
        ));

        this.addButton(new ButtonWidget(centerX + 10, centerY - 20, 90, 20,
            new LiteralText("+ 5%"),
            button -> {
                threshold = Math.min(0.50f, threshold + 0.05f);
                DurabilitySwapMod.setDurabilityThreshold(threshold);
            }
        ));

        this.addButton(new ButtonWidget(centerX - 100, centerY + 40, 200, 20,
            new LiteralText("Cerrar"),
            button -> this.client.openScreen(parent)
        ));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredString(matrices, this.textRenderer, "DurabilitySwap
