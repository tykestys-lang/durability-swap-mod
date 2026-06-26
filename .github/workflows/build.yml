package com.durabilityswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class DurabilitySwapMod implements ClientModInitializer {

    public static final String MOD_ID = "durabilityswap";
    private static final float DURABILITY_THRESHOLD = 0.10f;
    private static boolean modEnabled = true;
    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.durabilityswap.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.durabilityswap"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        System.out.println("[DurabilitySwap] Mod initialized!");
    }

    private void onClientTick(MinecraftClient client) {
        if (client.player == null) return;

        while (toggleKey.wasPressed()) {
            modEnabled = !modEnabled;
            client.player.sendMessage(
                new LiteralText(modEnabled ? "✔ DurabilitySwap ACTIVADO" : "✖ DurabilitySwap DESACTIVADO")
                    .formatted(modEnabled ? Formatting.GREEN : Formatting.RED),
                true
            );
        }

        if (!modEnabled) return;
        checkAndSwapTool(client.player);
    }

    private void checkAndSwapTool(PlayerEntity player) {
        PlayerInventory inventory = player.inventory;
        ItemStack mainHandStack = inventory.getMainHandStack();
        ItemStack offhandStack = inventory.offHand.get(0);

        boolean mainHandIsTool = isTool(mainHandStack);
        boolean mainHandLowDurability = mainHandIsTool && isLowDurability(mainHandStack);

        if (mainHandLowDurability && offhandStack.isEmpty()) {
            inventory.offHand.set(0, mainHandStack.copy());
            inventory.main.set(inventory.selectedSlot, ItemStack.EMPTY);
            player.sendMessage(
                new LiteralText("⚠ ").formatted(Formatting.YELLOW)
                    .append(new LiteralText("Herramienta al 10%! Movida a la mano secundaria.").formatted(Formatting.GOLD)),
                true
            );
            return;
        }

        boolean offhandIsTool = isTool(offhandStack);
        boolean offhandHighDurability = offhandIsTool && !isLowDurability(offhandStack);

        if (offhandHighDurability && mainHandStack.isEmpty()) {
            inventory.main.set(inventory.selectedSlot, offhandStack.copy());
            inventory.offHand.set(0, ItemStack.EMPTY);
            player.sendMessage(
                new LiteralText("✔ ").formatted(Formatting.GREEN)
                    .append(new LiteralText("Herramienta reparada, devuelta a la mano principal.").formatted(Formatting.GREEN)),
                true
            );
        }
    }

    private boolean isTool(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof ToolItem || stack.getItem() instanceof SwordItem;
    }

    private boolean isLowDurability(ItemStack stack) {
        if (!stack.isDamageable()) return false;
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) return false;
        int remaining = maxDamage - stack.getDamage();
        float ratio = (float) remaining / (float) maxDamage;
        return ratio <= DURABILITY_THRESHOLD;
    }
}
