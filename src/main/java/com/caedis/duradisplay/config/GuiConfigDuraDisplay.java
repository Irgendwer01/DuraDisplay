package com.caedis.duradisplay.config;

import static net.minecraftforge.fml.client.config.GuiConfig.getAbridgedConfigPath;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import com.caedis.duradisplay.Tags;
import com.caedis.duradisplay.overlay.OverlayInfo;

@SuppressWarnings({ "unused", "rawtypes" })
public class GuiConfigDuraDisplay extends GuiConfig {

    public GuiConfigDuraDisplay(GuiScreen parent) {
        super(
                parent,
                getCategories(),
                Tags.MODID,
                "general",
                false,
                false,
                getAbridgedConfigPath(DuraDisplayConfig.config.toString()));
    }

    private static List<IConfigElement> getCategories() {
        var list = new ArrayList<IConfigElement>();
        list.add(new ConfigElement(DuraDisplayConfig.config.getCategory(Configuration.CATEGORY_GENERAL)));
        for (var c : OverlayInfo.getConfigs()) {
            if (c != null) list.add(new ConfigElement(DuraDisplayConfig.config.getCategory(c.category())));
        }
        return list;
    }
}
