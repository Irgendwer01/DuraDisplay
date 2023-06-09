package com.caedis.duradisplay.config;

import java.io.File;
import java.util.Arrays;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import crazypants.enderio.config.Config;
import gregtech.GT_Mod;

public class DuraDisplayConfig {

    public static OverlayConfig.DurabilityOverlayConfig DurabilityConfig = new OverlayConfig.DurabilityOverlayConfig();
    public static OverlayConfig.ChargeOverlayConfig ChargeConfig = new OverlayConfig.ChargeOverlayConfig();
    public static final String CATEGORY_CHARGE = "charge";
    public static final String CATEGORY_DURABILITY = "durability";

    private static boolean configLoaded = false;

    public static boolean Enable = true;

    public static Configuration config = null;

    public static void loadConfig() {
        if (configLoaded) {
            return;
        }
        configLoaded = true;
        final File configDir = new File(Launch.minecraftHome, "config");
        if (!configDir.isDirectory()) {
            configDir.mkdirs();
        }
        final File configFile = new File(configDir, "duradisplay.cfg");
        config = new Configuration(configFile);

        reloadConfigObject();

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void reloadConfigObject() {

        Enable = config.getBoolean("Enable", Configuration.CATEGORY_GENERAL, Enable, "Enable/disable the entire mod");

        DurabilityConfig.Enabled = config.getBoolean(
            "Enable",
            DuraDisplayConfig.CATEGORY_DURABILITY,
            DurabilityConfig.Enabled,
            "Enable durability module");

        DurabilityConfig.RenderBar = config.getBoolean(
            "RenderBar",
            DuraDisplayConfig.CATEGORY_DURABILITY,
            DurabilityConfig.RenderBar,
            "Render durability bar");

        ChargeConfig.Enabled = config
            .getBoolean("Enable", DuraDisplayConfig.CATEGORY_CHARGE, ChargeConfig.Enabled, "Enable charge module");

        ChargeConfig.RenderBar = config
            .getBoolean("RenderBar", DuraDisplayConfig.CATEGORY_CHARGE, ChargeConfig.RenderBar, "Render charge bar");

        DurabilityConfig.Position = config.getInt(
            "Position",
            DuraDisplayConfig.CATEGORY_DURABILITY,
            DurabilityConfig.Position,
            1,
            9,
            "Location in item where the durability percentage will be (numpad style)");

        ChargeConfig.Position = config.getInt(
            "Position",
            DuraDisplayConfig.CATEGORY_CHARGE,
            ChargeConfig.Position,
            1,
            9,
            "Location in item where the charge percentage will be (numpad style)");

        DurabilityConfig.ShowWhenFull = config.getBoolean(
            "ShowWhenFull",
            DuraDisplayConfig.CATEGORY_DURABILITY,
            DurabilityConfig.ShowWhenFull,
            "Show durability percentage when item is undamaged/full");

        ChargeConfig.ShowWhenFull = config.getBoolean(
            "ShowWhenFull",
            DuraDisplayConfig.CATEGORY_CHARGE,
            ChargeConfig.ShowWhenFull,
            "Show charge percentage when item is full");

        DurabilityConfig.UseColorThreshold = config.getBoolean(
            "UseColorThreshold",
            DuraDisplayConfig.CATEGORY_DURABILITY,
            DurabilityConfig.UseColorThreshold,
            "Use the color thresholds instead of the default gradient coloring.");

        ChargeConfig.UseColorThreshold = config.getBoolean(
            "UseColorThreshold",
            DuraDisplayConfig.CATEGORY_CHARGE,
            ChargeConfig.UseColorThreshold,
            "Use the color thresholds instead of the static blue color.");

        Property dura_colorThresh = config.get(
            CATEGORY_DURABILITY,
            "ColorThresholds",
            DurabilityConfig.ColorThreshold,
            "List of numbers in ascending order from 0-100 that set the thresholds for durability color mapping. "
                + "Colors are from Red -> Yellow -> Green with Red being less than or equal to the first value "
                + "and Green being greater than or equal to the last value");
        DurabilityConfig.ColorThreshold = dura_colorThresh.getDoubleList();

        // clean up whatever the user inputs
        DurabilityConfig.ColorThreshold = Arrays.stream(DurabilityConfig.ColorThreshold)
            .filter(num -> num >= 0.0 && num <= 100.0)
            .sorted()
            .toArray();
        dura_colorThresh.set(DurabilityConfig.ColorThreshold);

        Property charge_colorThresh = config.get(
            CATEGORY_CHARGE,
            "ColorThresholds",
            ChargeConfig.ColorThreshold,
            "List of numbers in ascending order from 0-100 that set the thresholds for charge color mapping. "
                + "Colors are from Red -> Orange -> Blue with Red being less than or equal to the first value "
                + "and Blue being greater than or equal to the last value");
        ChargeConfig.ColorThreshold = charge_colorThresh.getDoubleList();

        // clean up whatever the user inputs
        ChargeConfig.ColorThreshold = Arrays.stream(ChargeConfig.ColorThreshold)
            .filter(num -> num >= 0.0 && num <= 100.0)
            .sorted()
            .toArray();
        charge_colorThresh.set(ChargeConfig.ColorThreshold);

        if (config.hasChanged()) {
            config.save();
        }

        // Gregtech Bars
        GT_Mod.gregtechproxy.mRenderItemDurabilityBar = Enable
            && !(DurabilityConfig.Enabled && !DurabilityConfig.RenderBar);
        GT_Mod.gregtechproxy.mRenderItemChargeBar = Enable && !(ChargeConfig.Enabled && !ChargeConfig.RenderBar);

        // EnderIO Bars
        Config.renderChargeBar = Enable && !(ChargeConfig.Enabled && !ChargeConfig.RenderBar);
        Config.renderDurabilityBar = Enable && !(DurabilityConfig.Enabled && !DurabilityConfig.RenderBar);
    }
}
