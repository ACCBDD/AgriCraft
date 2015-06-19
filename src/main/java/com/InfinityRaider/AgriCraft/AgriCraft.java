package com.InfinityRaider.AgriCraft;

/*
    This is my first "real" mod, I've made this while learning to use Minecraft Forge to Mod Minecraft. The code might not be optimal but that wasn't the point of this project.

    Cheers to:
        - Pam for trusting me with her source code and support
        - Pahimar for making his code open source and for creating his Let's Mod Reboot Youtube series, I've learned a lot from this (also used some code, credits due where credits due)
        - VSWE for his "Forging a Minecraft Mod" summer courses
        - NealeGaming for his Minecraft modding tutorials on youtube

    I've annotated my code heavily, for myself and for possible others who might learn from it.

    Oh and keep on modding in the free world

        ~ InfinityRaider
*/

import com.InfinityRaider.AgriCraft.apiimpl.APISelector;
import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.init.*;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import com.InfinityRaider.AgriCraft.proxy.IProxy;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RenderLogger;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = Reference.MOD_ID,name = Reference.MOD_NAME,version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASS)
public class AgriCraft {
    @Mod.Instance(Reference.MOD_ID)
    public static AgriCraft instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS,serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        LogHelper.debug("Starting Pre-Initialization");
        LoadedMods.init();
        proxy.registerEventHandlers();
        NetworkWrapperAgriCraft.init();
        ConfigurationHandler.init(event);
        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
        if (ConfigurationHandler.debug) {
            FMLCommonHandler.instance().bus().register(new RenderLogger());
        }
        Blocks.init();
        Crops.init();
        Items.init();
        APISelector.init();
        LogHelper.debug("Pre-Initialization Complete");
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        LogHelper.debug("Starting Initialization");
        NetworkRegistry.INSTANCE.registerGuiHandler(instance , new GuiHandler());
        proxy.registerTileEntities();
        proxy.registerRenderers();
        LogHelper.debug("Initialization Complete");
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        LogHelper.debug("Starting Post-Initialization");
        //Have to do this in postInit because some mods don't register their items/blocks until init
        ResourceCrops.init();
        CustomCrops.init();
        ModHelper.initHelpers();
        Recipes.init();
        SeedHelper.init();
        GrowthRequirementHandler.init();
        CustomCrops.initGrassSeeds();
        CropPlantHandler.init();
        MutationHandler.init();
        CropProducts.init();
        if(!ConfigurationHandler.disableWorldGen) {WorldGen.init();}
        ModHelper.performPostTasks();
        proxy.initNEI();
        LogHelper.debug("Post-Initialization Complete");
    }
}
