package com.caedis.duradisplay.config;

import java.io.File;
import java.util.Arrays;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import crazypants.enderio.config.Config;
import gregtech.GT_Mod;

public class DuraDisplayConfig {

    public static final String CATEGORY_CHARGE = "charge";
    public static final String CATEGORY_DURABILITY = "durability";

    private static boolean configLoaded = false;
    public static boolean Durability_Enable = true;
    public static boolean Charge_Enable = true;

    public static boolean Durability_HideBar = true;
    public static boolean Charge_HideBar = true;
    public static int Durability_PercentageLocation = 2;
    public static int Charge_PercentageLocation = 8;
    public static boolean Durability_PercentageWhenFull = false;
    public static boolean Charge_PercentageWhenFull = false;

    public static boolean Durability_UseColorThresholds = false;
    public static double[] Durability_ColorThresholds = new double[] { 25.0, 75.0 };

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

        Durability_Enable = config
            .getBoolean("Enable", DuraDisplayConfig.CATEGORY_DURABILITY, Durability_Enable, "Enable durability module");

        Durability_HideBar = config
            .getBoolean("HideBar", DuraDisplayConfig.CATEGORY_DURABILITY, Durability_HideBar, "Hide durability bar");

        Charge_Enable = config
            .getBoolean("Enable", DuraDisplayConfig.CATEGORY_CHARGE, Charge_Enable, "Enable charge module");

        Charge_HideBar = config
            .getBoolean("HideBar", DuraDisplayConfig.CATEGORY_CHARGE, Charge_HideBar, "Hide charge bar");

        Durability_PercentageLocation = config.getInt(
            "PercentageLocation",
            DuraDisplayConfig.CATEGORY_DURABILITY,
            Durability_PercentageLocation,
            1,
            9,
            "Location in item where the durability percentage will be (numpad style)");

        Charge_PercentageLocation = config.getInt(
            "PercentageLocation",
            DuraDisplayConfig.CATEGORY_CHARGE,
            Charge_PercentageLocation,
            1,
            9,
            "Location in item where the charge percentage will be (numpad style)");

        Durability_PercentageWhenFull = config.getBoolean(
            "PercentageWhenFull",
            DuraDisplayConfig.CATEGORY_DURABILITY,
            Durability_PercentageWhenFull,
            "Show durability percentage when item is undamaged/full");

        Charge_PercentageWhenFull = config.getBoolean(
            "PercentageWhenFull",
            DuraDisplayConfig.CATEGORY_CHARGE,
            Charge_PercentageWhenFull,
            "Show charge percentage when item is full");

        Durability_UseColorThresholds = config.getBoolean(
            "UseColorThresholds",
            DuraDisplayConfig.CATEGORY_DURABILITY,
            Durability_UseColorThresholds,
            "Use the stated color thresholds instead of the default gradient coloring.");

        Property colorThresh = config.get(
            CATEGORY_DURABILITY,
            "ColorThresholds",
            Durability_ColorThresholds,
            "List of numbers in ascending order from 0-100 that set the thresholds for durability color mapping. "
                + "Colors are from Red -> Yellow -> Green with Red being less than or equal to the first value "
                + "and Green being greater than or equal to the last value");
        Durability_ColorThresholds = colorThresh.getDoubleList();

        // clean up whatever the user inputs
        Durability_ColorThresholds = Arrays.stream(Durability_ColorThresholds)
            .filter(num -> num >= 0.0 && num <= 100.0)
            .sorted()
            .toArray();
        colorThresh.set(Durability_ColorThresholds);

        if (config.hasChanged()) {
            config.save();
        }

        // Gregtech Bars
        GT_Mod.gregtechproxy.mRenderItemDurabilityBar = !(Durability_Enable && Durability_HideBar);
        GT_Mod.gregtechproxy.mRenderItemChargeBar = !(Charge_Enable && Charge_HideBar);

        // EnderIO Bars
        Config.renderChargeBar = !(Charge_Enable && Charge_HideBar);
        Config.renderDurabilityBar = !(Durability_Enable && Durability_HideBar);
    }
}
