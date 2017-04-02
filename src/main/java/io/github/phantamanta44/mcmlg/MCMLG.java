package io.github.phantamanta44.mcmlg;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "mcmlg", version = "1.0.0")
public class MCMLG {

    @Mod.Instance("mcmlg")
    public static MCMLG INSTANCE;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new Airhorn());
        MinecraftForge.EVENT_BUS.register(new Hitmarker());
    }

}
