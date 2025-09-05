package net.mrqx.potato.brain;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.DoubleValue RANGE;
    public static final ForgeConfigSpec.IntValue BRAIN_MAX;
    public static final ForgeConfigSpec.BooleanValue ABSORB_EXP;

    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();
        commonBuilder.push("BigPotatoBrain settings");

        RANGE = commonBuilder
                .comment("Set the range of Big Brain. (default: 15)")
                .defineInRange("big_brain_range", 15, 0, Double.MAX_VALUE);
        BRAIN_MAX = commonBuilder
                .comment("Set the max exp of Big Brain.(default: 10000)")
                .defineInRange("big_brain_max", 10000, 0, Integer.MAX_VALUE);
        ABSORB_EXP = commonBuilder
                .comment("Does Big Brain directly absorb Experience Orb? (default: false)")
                .define("absorb_exp", false);

        commonBuilder.pop();
        COMMON_CONFIG = commonBuilder.build();
    }
}
