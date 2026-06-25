package com.durabilityswap;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class DurabilitySwapMod implements ModInitializer {

    public static final String MOD_ID = "durabilityswap";
    // Threshold: 10% durability remaining
    private static final float DURABILITY_THRESHOLD = 0.10f;

    @Override
    public void onInitialize() {
        // Register a server tick event to check durability each tick
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        System.out.println("[DurabilitySwap] Mod initialized! Tools at 10% will move to offhand.");
    }

    private void onServerTick(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            for (PlayerEntity player : world.getPlayers()) {
                checkAndSwapTool(player);
            }
        }
    }

    private void checkAndSwapTool(PlayerEntity player) {
        PlayerInventory inventory = player.inventory;

        // Get the item currently in the main hand (selected hotbar slot)
        ItemStack mainHandStack = inventory.getMainHandStack();
        // Get the item currently in the offhand
        ItemStack offhandStack = inventory.offHand.get(0);

        boolean mainHandIsTool = isTool(mainHandStack);
        boolean mainHandLowDurability = mainHandIsTool && isLowDurability(mainHandStack);

        // --- SWAP TO OFFHAND when main hand tool reaches <= 10% durability ---
        if (mainHandLowDurability && offhandStack.isEmpty()) {
            // Move main hand tool to offhand
            inventory.offHand.set(0, mainHandStack.copy());
            inventory.main.set(inventory.selectedSlot, ItemStack.EMPTY);

            player.sendMessage(
                new LiteralText("⚠ ").formatted(Formatting.YELLOW)
                    .append(new LiteralText("Herramienta al 10%! Movida a la mano secundaria.").formatted(Formatting.GOLD)),
                true // action bar message (above hotbar)
            );
            return;
        }

        // --- RETURN TO MAIN HAND when offhand tool recovers above 10% ---
        boolean offhandIsTool = isTool(offhandStack);
        boolean offhandHighDurability = offhandIsTool && !isLowDurability(offhandStack);

        if (offhandHighDurability && mainHandStack.isEmpty()) {
            // Move offhand tool back to the selected main hand slot
            inventory.main.set(inventory.selectedSlot, offhandStack.copy());
            inventory.offHand.set(0, ItemStack.EMPTY);

            player.sendMessage(
                new LiteralText("✔ ").formatted(Formatting.GREEN)
                    .append(new LiteralText("Herramienta reparada, devuelta a la mano principal.").formatted(Formatting.GREEN)),
                true
            );
        }
    }

    /**
     * Returns true if the ItemStack is a tool (pickaxe, axe, shovel, sword).
     */
    private boolean isTool(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof ToolItem
            || stack.getItem() instanceof SwordItem;
    }

    /**
     * Returns true if the item's remaining durability is <= 10%.
     */
    private boolean isLowDurability(ItemStack stack) {
        if (!stack.isDamageable()) return false;
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) return false;
        // damage = damage taken; remaining = maxDamage - damage
        int remaining = maxDamage - stack.getDamage();
        float ratio = (float) remaining / (float) maxDamage;
        return ratio <= DURABILITY_THRESHOLD;
    }
}
