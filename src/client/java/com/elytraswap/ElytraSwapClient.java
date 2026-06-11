package com.elytraswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ElytraSwapClient implements ClientModInitializer {

    private static KeyBinding swapKey;

    @Override
    public void onInitializeClient() {
        // Регистрируем клавишу (по умолчанию — R, можно сменить в настройках управления)
        swapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.elytraswap.swap",           // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,                  // клавиша по умолчанию: R
                "category.elytraswap.general"     // категория в меню управления
        ));

        // Регистрируем тип пакета на клиенте (S2C реестр не нужен, только C2S)
        // Отправляем пакет серверу при нажатии клавиши
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // wasPressed() сбрасывает флаг нажатия — вызываем в цикле тиков
            while (swapKey.wasPressed()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(new ElytraSwapMod.ElytraSwapPayload());
                }
            }
        });
    }
}
