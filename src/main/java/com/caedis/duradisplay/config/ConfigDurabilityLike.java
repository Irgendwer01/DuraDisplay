package com.caedis.duradisplay.config;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.caedis.duradisplay.overlay.OverlayDurabilityLike;
import com.caedis.duradisplay.utils.ColorType;
import com.caedis.duradisplay.utils.ConfigLoad;
import com.caedis.duradisplay.utils.DurabilityFormatter;

public abstract class ConfigDurabilityLike extends Config {

    public boolean enabled;
    public OverlayDurabilityLike.Style style;
    public DurabilityFormatter.Format textFormat;
    public int numPadPosition;
    public boolean showWhenFull;
    public boolean showWhenEmpty;
    public int color;
    public ColorType colorType;
    public double[] colorThreshold;
    public int[] threeColors;
    public boolean smoothBar;
    public int barOffset;
    public boolean showBackground;

    protected ConfigDurabilityLike(boolean enabled, OverlayDurabilityLike.Style style,
        DurabilityFormatter.Format textFormat, int numPadPosition, boolean showWhenFull, boolean showWhenEmpty,
        int color, ColorType colorType, double[] colorThreshold, int[] threeColors, boolean smoothBar, int barOffset,
        boolean showBackground) {
        this.enabled = enabled;
        this.style = style;
        this.textFormat = textFormat;
        this.numPadPosition = numPadPosition;
        this.showWhenFull = showWhenFull;
        this.showWhenEmpty = showWhenEmpty;
        this.color = color;
        this.colorType = colorType;
        this.colorThreshold = colorThreshold;
        this.threeColors = threeColors;
        this.smoothBar = smoothBar;
        this.barOffset = barOffset;
        this.showBackground = showBackground;
    }

    @Override
    public void loadConfig() {

        enabled = config.getBoolean("Enable", category(), enabled, String.format("Enable %s module", category()));

        style = ConfigLoad
            .loadEnum(category(), "Style", style, "Style of the Overlay, can be Text, Bar, or VerticalBar");

        numPadPosition = config.getInt(
            "Position",
            category() + ".StyleConfig.Text",
            numPadPosition,
            1,
            9,
            String.format("Location in item where the %s percentage will be (numpad style)", category()));

        textFormat = ConfigLoad
            .loadEnum(category() + ".StyleConfig.Text", "TextFormat", textFormat, "Format of the text");

        showWhenFull = config.getBoolean(
            "ShowWhenFull",
            category(),
            showWhenFull,
            String.format("Show %s percentage when item is undamaged/full", category()));

        showWhenEmpty = config.getBoolean(
            "ShowWhenEmpty",
            category(),
            showWhenEmpty,
            String.format("Show %s percentage when empty", category()));

        colorType = ConfigLoad.loadEnum(
            category() + ".Color",
            "ColorType",
            colorType,
            "ColorType of the Overlay, can be RYGDurability, Threshold, Vanilla, Single, Smooth");

        colorThreshold = Arrays.stream(
            config.get(
                category() + ".Color",
                "ColorThresholds",
                colorThreshold,
                "List of numbers in ascending order from 0-100 that set the thresholds for durability color mapping. "
                    + "Colors are the list of colors in ThreeColors\n"
                    + "By default from Red -> Yellow -> Green by default",
                0.0,
                100.0,
                true,
                2)
                .getDoubleList())
            .sorted()
            .toArray();

        Pattern hexPattern = Pattern.compile("^(?:[0-9a-fA-F]{3}){1,2}$");

        String colorCheck = config.getString(
            "Color",
            category() + ".Color",
            Integer.toHexString(color),
            "Color of the Overlay (hex code, no prefix)",
            hexPattern);
        color = Integer.parseInt(colorCheck.replace("#", ""), 16);

        String[] threeColorsCheck = config.get(
            category() + ".Color",
            "ThreeColors",
            Arrays.stream(threeColors)
                .mapToObj(Integer::toHexString)
                .toArray(String[]::new),
            "Colors used in Threshold/Smooth color mode (hex code, no prefix)",
            true,
            3,
            hexPattern)
            .getStringList();
        threeColors = Arrays.stream(threeColorsCheck)
            .mapToInt(s -> Integer.parseInt(s.replace("#", ""), 16))
            .toArray();

        smoothBar = config
            .getBoolean("SmoothBar", category() + ".StyleConfig.Bars", smoothBar, "Smooth the bar length");

        barOffset = config
            .getInt("BarOffset", category() + ".StyleConfig.Bars", barOffset, 0, 16, "Offset the bar by this amount");

        showBackground = config
            .getBoolean("ShowBackground", category() + ".StyleConfig.Bars", showBackground, "Show bar background");

        postLoadConfig();
    }

    public abstract void postLoadConfig();
}
