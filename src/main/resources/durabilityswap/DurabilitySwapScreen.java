package com.durabilityswap;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class DurabilitySwapScreen extends Screen {

    private final Screen parent;
    private float threshold;
    private boolean enabled;
    private boolean offhand;

    public DurabilitySwapScreen(Screen parent) {
        super(new LiteralText("DurabilitySwap"));
        this.parent = parent;
        this.threshold = DurabilitySwapMod.getDurabilityThreshold();
        this.enabled = DurabilitySwapMod.isModEnabled();
        this.offhand = DurabilitySwapMod.isUseOffhand();
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        this.addButton(new ButtonWidget(cx - 100, cy - 80, 200, 20,
            new LiteralText("Mod: " + (enabled ? "ACTIVADO" : "DESACTIVADO")),
            button -> {
                enabled = !enabled;
                DurabilitySwapMod.setModEnabled(enabled);
                button.setMessage(new LiteralText("Mod: " + (enabled ? "ACTIVADO" : "DESACTIVADO")));
            }
        ));

        this.addButton(new ButtonWidget(cx - 100, cy - 50, 200, 20,
            new LiteralText("Modo: " + (offhand ? "Segunda mano" : "Cambiar herramienta")),
            button -> {
                offhand = !offhand;
                DurabilitySwapMod.setUseOffhand(offhand);
                button.setMessage(new LiteralText("Modo: " + (offhand ? "Segunda mano" : "Cambiar herramienta")));
            }
        ));

        this.addButton(new ButtonWidget(cx - 100, cy - 10, 90, 20,
            new LiteralText("- 5"),
            button -> {
                threshold = Math.max(0.05f, threshold - 0.05f);
                DurabilitySwapMod.setDurabilityThreshold(threshold);
            }
        ));

        this.addButton(new ButtonWidget(cx + 10, cy - 10, 90, 20,
            new LiteralText("+ 5"),
            button -> {
                threshold = Math.min(0.50f, threshold + 0.05f);
                DurabilitySwapMod.setDurabilityThreshold(threshold);
            }
        ));

        this.addButton(new ButtonWidget(cx - 100, cy + 50, 200, 20,
            new LiteralText("Cerrar"),
            button -> this.client.openScreen(parent)
        ));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        int pct = (int)(threshold * 100);
        drawCenteredString(matrices, this.textRenderer, "DurabilitySwap", this.width / 2, this.height / 2 - 110, 0xFFFFFF);
        drawCenteredString(matrices, this.textRenderer, "Umbral: " + pct, this.width / 2, this.height / 2 - 25, 0xFFFF55);
        drawCenteredString(matrices, this.textRenderer, "Creado por CokiMc", this.width / 2, this.height / 2 + 80, 0xAAAAAA);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
