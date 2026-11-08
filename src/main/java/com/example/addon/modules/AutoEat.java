package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class AutoEat extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> hunger = sgGeneral.add(new IntSetting.Builder()
        .name("hunger")
        .description("Nguong do doi de bat dau an.")
        .defaultValue(14)
        .min(1).max(19)
        .build()
    );

    private int prevSlot = -1;
    private boolean isEating = false;

    public AutoEat() {
        super(AddonTemplate.SADPLUM, "Auto Eat", "Tu dong an khi thanh do doi giam xuong.");
    }

    @Override
    public void onDeactivate() {
        stopEating();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        // Nếu đã no thì ngừng ăn và đổi lại slot cũ
        if (mc.player.getHungerManager().getFoodLevel() >= 20) {
            if (isEating) stopEating();
            return;
        }

        // Kiểm tra nếu đói dưới ngưỡng cài đặt
        if (mc.player.getHungerManager().getFoodLevel() <= hunger.get() && !isEating) {
            int foodSlot = findFoodSlot();
            if (foodSlot != -1) {
                prevSlot = mc.player.getInventory().selectedSlot;
                mc.player.getInventory().selectedSlot = foodSlot;
                mc.options.useKey.setPressed(true);
                isEating = true;
            }
        }
    }

    private void stopEating() {
        if (mc.player == null) return;
        mc.options.useKey.setPressed(false);
        if (prevSlot != -1) {
            mc.player.getInventory().selectedSlot = prevSlot;
            prevSlot = -1;
        }
        isEating = false;
    }

    private int findFoodSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getComponents().contains(DataComponentTypes.FOOD)) {
                return i;
            }
        }
        return -1;
    }
}
