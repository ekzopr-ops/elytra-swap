package com.elytraswap;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import io.netty.buffer.ByteBuf;

public class ElytraSwapMod implements ModInitializer {

    public static final String MOD_ID = "elytraswap";

    // Обычный класс вместо record — совместимо со всеми версиями Gradle/Groovy
    public static final class ElytraSwapPayload implements CustomPayload {
        public static final CustomPayload.Id<ElytraSwapPayload> ID =
                new CustomPayload.Id<>(Identifier.of(MOD_ID, "swap"));
        public static final PacketCodec<ByteBuf, ElytraSwapPayload> CODEC =
                PacketCodec.unit(new ElytraSwapPayload());

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(ElytraSwapPayload.ID, ElytraSwapPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ElytraSwapPayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    context.server().execute(() -> performSwap(player));
                });
    }

    private void performSwap(ServerPlayerEntity player) {
        ItemStack chestSlot = player.getEquippedStack(EquipmentSlot.CHEST);

        if (!chestSlot.isEmpty() && chestSlot.getItem() instanceof ElytraItem) {
            unequipToInventory(player, chestSlot.copy(), EquipmentSlot.CHEST);
        } else if (!chestSlot.isEmpty()) {
            ItemStack elytra = findElytraInInventory(player);
            if (elytra.isEmpty()) {
                player.sendMessage(Text.translatable("elytraswap.no_elytra"), true);
                return;
            }
            unequipToInventory(player, chestSlot.copy(), EquipmentSlot.CHEST);
            equipElytraFromInventory(player, elytra);
        } else {
            ItemStack elytra = findElytraInInventory(player);
            if (elytra.isEmpty()) {
                player.sendMessage(Text.translatable("elytraswap.no_elytra"), true);
                return;
            }
            equipElytraFromInventory(player, elytra);
        }
    }

    private void unequipToInventory(ServerPlayerEntity player, ItemStack stack, EquipmentSlot slot) {
        player.equipStack(slot, ItemStack.EMPTY);
        if (!player.getInventory().insertStack(stack)) {
            player.dropItem(stack, false);
        }
    }

    private ItemStack findElytraInInventory(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ElytraItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private void equipElytraFromInventory(ServerPlayerEntity player, ItemStack elytraStack) {
        ItemStack toEquip = elytraStack.copy();
        toEquip.setCount(1);
        elytraStack.decrement(1);
        player.equipStack(EquipmentSlot.CHEST, toEquip);
    }
}
