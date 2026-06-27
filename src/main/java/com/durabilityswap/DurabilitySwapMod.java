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
    public static final String CREDITS = "CokiMc";
    private static float durabilityThreshold = 0.10f;
    private static boolean modEnabled = true;
    private static boolean useOffhand = false;
    private static KeyBinding menuKey;

    @Override
    public void onInitializeClient() {
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.durabilityswap.menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "DurabilitySwap"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        if (client.player == null) return;

        while (menuKey.wasPressed()) {
            client.openScreen(new DurabilitySwapScreen(client.currentScreen));
        }

        if (!modEnabled) return;
        checkAndSwapTool(client.player);
    }

    private void checkAndSwapTool(PlayerEntity player) {
        PlayerInventory inventory = player.inventory;
        ItemStack mainHandStack = inventory.getMainHandStack();

        if (!isTool(mainHandStack)) return;
        if (!isLowDurability(mainHandStack)) return;

        if (useOffhand) {
            ItemStack offhand = inventory.offHand.get(0);
            if (offhand.isEmpty()) {
                inventory.offHand.set(0, mainHandStack.copy());
                inventory.main.set(inventory.selectedSlot, ItemStack.EMPTY);
                player.sendMessage(new LiteralText("Herramienta movida al offhand!").formatted(Formatting.GOLD), true);
            } else {
                player.sendMessage(new LiteralText("Offhand ocupado!").formatted(Formatting.RED), true);
            }
        } else {
            for (int i = 0; i < 36; i++) {
                if (i == inventory.selectedSlot) continue;
                ItemStack candidate = inventory.main.get(i);
                if (candidate.isEmpty()) continue;
                if (candidate.getItem() != mainHandStack.getItem()) continue;
                if (isLowDurability(candidate)) continue;

                ItemStack temp = mainHandStack.copy();
                inventory.main.set(inventory.selectedSlot, candidate.copy());
                inventory.main.set(i, temp);
                player.sendMessage(new LiteralText("Herramienta cambiada automaticamente!").formatted(Formatting.GOLD), true);
                return;
            }
            player.sendMessage(new LiteralText("Sin herramienta de repuesto!").formatted(Formatting.RED), true);
        }
    }

    public static float getDurabilityThreshold() { return durabilityThreshold; }
    public static void setDurabilityThreshold(float value) { durabilityThreshold = value; }
    public static boolean isModEnabled() { return modEnabled; }
    public static void setModEnabled(boolean value) { modEnabled = value; }
    public static boolean isUseOffhand() { return useOffhand; }
    public static void setUseOffhand(boolean value) { useOffhand = value; }

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
        return ratio <= durabilityThreshold;
    }
}
