package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;

public class AutoShop extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
        .name("command")
        .description("Lenh ban do gui len server.")
        .defaultValue("/sell all")
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Thoi gian cho giua cac lan gui lenh (tick).")
        .defaultValue(60)
        .min(20).max(300)
        .build()
    );

    private int timer = 0;

    public AutoShop() {
        super(AddonTemplate.SADPLUM, "Auto Shop", "Tu dong gui lenh ban do khi tui do day.");
    }

    @Override
    public void onActivate() {
        timer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        if (timer > 0) {
            timer--;
            return;
        }

        // Đếm số ô trống trong hòm đồ
        int emptySlots = 0;
        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).isEmpty()) {
                emptySlots++;
            }
        }

        // Nếu còn ít hơn hoặc bằng 1 ô trống -> gửi lệnh bán
        if (emptySlots <= 1) {
            ChatUtils.sendPlayerMsg(command.get());
            timer = delay.get();
        }
    }
}
