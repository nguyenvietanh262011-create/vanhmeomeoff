package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Miner extends Module {
    private final SettingGroup sgNuker = settings.createGroup("Nuker");
    private final SettingGroup sgSafety = settings.createGroup("Safety & Notify");

    private final Setting<Double> range = sgNuker.add(new DoubleSetting.Builder()
        .name("range")
        .description("Khoang cach quet khoi.")
        .defaultValue(4.0)
        .min(1.0).max(6.0)
        .build()
    );

    private final Setting<Integer> delay = sgNuker.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay giua cac lan dao (tick).")
        .defaultValue(0)
        .min(0).max(20)
        .build()
    );

    private final Setting<Integer> maxBlocksPerTick = sgNuker.add(new IntSetting.Builder()
        .name("max-blocks-per-tick")
        .description("So khoi toi da dao trong 1 tick.")
        .defaultValue(10)
        .min(1).max(20)
        .build()
    );

    private final Setting<Boolean> swing = sgNuker.add(new BoolSetting.Builder()
        .name("swing")
        .description("Vung tay khi dao de tranh loi NoSwing.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> stuckDetection = sgSafety.add(new BoolSetting.Builder()
        .name("stuck-detection")
        .description("Bo qua khoi bi ket (ghost block).")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> stuckTimeout = sgSafety.add(new IntSetting.Builder()
        .name("stuck-timeout")
        .description("So tick toi da truoc khi bo qua khoi ket.")
        .defaultValue(25)
        .min(5).max(100)
        .visible(stuckDetection::get)
        .build()
    );

    private int timer = 0;
    private BlockPos currentTarget = null;
    private int stuckTicks = 0;
    private final List<BlockPos> blacklistedBlocks = new ArrayList<>();

    public Miner() {
        super(AddonTemplate.SADPLUM, "Miner", "Tu dong dao cat kem bo dem chong ket.");
    }

    @Override
    public void onActivate() {
        timer = 0;
        stuckTicks = 0;
        currentTarget = null;
        blacklistedBlocks.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        if (timer > 0) {
            timer--;
            return;
        }

        List<BlockPos> blocks = getTargetBlocks();
        if (blocks.isEmpty()) return;

        int blocksBroken = 0;
        for (BlockPos pos : blocks) {
            if (blocksBroken >= maxBlocksPerTick.get()) break;

            if (stuckDetection.get()) {
                if (pos.equals(currentTarget)) {
                    stuckTicks++;
                    if (stuckTicks >= stuckTimeout.get()) {
                        blacklistedBlocks.add(pos);
                        currentTarget = null;
                        stuckTicks = 0;
                        continue;
                    }
                } else {
                    currentTarget = pos;
                    stuckTicks = 0;
                }
            }

            mc.interactionManager.updateBlockBreakingProgress(pos, Direction.UP);
            if (swing.get()) mc.player.swingHand(Hand.MAIN_HAND);
            blocksBroken++;
        }

        timer = delay.get();
    }

    private List<BlockPos> getTargetBlocks() {
        List<BlockPos> list = new ArrayList<>();
        int r = (int) Math.ceil(range.get());
        BlockPos playerPos = mc.player.getBlockPos();

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (mc.player.getEyePos().distanceTo(Vec3d.ofCenter(pos)) <= range.get()) {
                        if (mc.world.getBlockState(pos).isOf(Blocks.SAND) && !blacklistedBlocks.contains(pos)) {
                            list.add(pos);
                        }
                    }
                }
            }
        }

        list.sort(Comparator.comparingDouble(p -> mc.player.squaredDistanceTo(Vec3d.ofCenter(p))));
        return list;
    }
}
