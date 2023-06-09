package com.caedis.duradisplay.config;

public abstract class OverlayConfig {

    public boolean Enabled = true;
    public int Position;
    public boolean ShowWhenFull = false;
    public boolean RenderBar = false;

    public boolean UseColorThreshold;
    public double[] ColorThreshold = new double[] { 15, 50 };
    public double VisibleThreshold = 100;

    public static class DurabilityOverlayConfig extends OverlayConfig {

        public DurabilityOverlayConfig() {
            Position = 2;
        }
    }

    public static class ChargeOverlayConfig extends OverlayConfig {

        public ChargeOverlayConfig() {
            Position = 8;
        }
    }
}
