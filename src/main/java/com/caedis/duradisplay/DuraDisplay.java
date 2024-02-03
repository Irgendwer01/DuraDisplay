package com.caedis.duradisplay;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.caedis.duradisplay.config.DuraDisplayConfig;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

@Mod(
    modid = Tags.MODID,
    version = Tags.VERSION,
    name = Tags.MODNAME,
    acceptedMinecraftVersions = "[1.12.2]",
    guiFactory = "com.caedis.duradisplay.config.GuiFactory",
    acceptableRemoteVersions = "*")
public class DuraDisplay implements ILateMixinLoader {

    public static final Logger LOG = LogManager.getLogger(Tags.MODID);

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (FMLCommonHandler.instance()
            .getSide()
            .isClient()) {
            DuraDisplayConfig.loadConfig();
            FMLCommonHandler.instance()
                .bus()
                .register(this);
        }
        /*
        try {
            AppEngItemRenderHook.init();
        } catch (NoClassDefFoundError e) {
            DuraDisplay.LOG.info("AE2 not found, skipping AppEngItemRenderHook");
        }

         */
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Tags.MODID)) {
            DuraDisplayConfig.config.save();
            DuraDisplayConfig.reloadConfigObject();
        }
    }

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.duradisplay-late.json");
    }
}
