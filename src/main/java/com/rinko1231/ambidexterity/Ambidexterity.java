package com.rinko1231.ambidexterity;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("removal")
@Mod(Ambidexterity.MOD_ID)
public class Ambidexterity {
    public static final String MOD_ID = "ambidexterity";
    public static final String MODID = "ambidexterity"; //下划线很烦

    public Ambidexterity() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);



    }



}
