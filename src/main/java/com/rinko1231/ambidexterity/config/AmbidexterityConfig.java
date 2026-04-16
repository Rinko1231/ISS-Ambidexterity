package com.rinko1231.ambidexterity.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;
@SuppressWarnings("removal")
public class AmbidexterityConfig {
    public static ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> itemBlacklist;


    static
    {
        BUILDER.push("Ambidexterity Config");

        itemBlacklist = BUILDER
                .comment("StaffItem whose attribute modifiers won't take effect in a single hand.")
                .defineList("StaffItem Blacklist", List.of("modA:mainhand_only_staff"),
                        element -> element instanceof String);


        SPEC = BUILDER.build();
    }

    public static void setup()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "AmbidexterityConfig.toml");
    }

}
