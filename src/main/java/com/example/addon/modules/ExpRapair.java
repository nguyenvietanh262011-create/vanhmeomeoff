package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;

public class ExpRepair extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> minDurability = sgGeneral.add(new IntSetting.Builder()
        .name("min-durability")
        .description("Nguong do ben toi thieu truoc khi ngat dao.")
        .defaultValue(30)
        .min(5).max(200)
        .build()
    );
  @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;

        ItemStack mainHand = mc.player.getMainHandStack();
        if (mainHand.isDamageable()) {
            int remainingDurability = mainHand.getMaxDamage() - mainHand.getDamage();

            // Nếu độ bền dưới ngưỡng an toàn
            if (remainingDurability <= minDurability.get()) {
                Module miner = Modules.get().get("Miner");
                if (miner != null && miner.isActive()) {
                    miner.toggle(); // Tự động ngắt Miner để tránh gãy xẻng
                    info("Da ngat Miner vi cong cu sap gay!");
                }
            }
        }
    }
}
