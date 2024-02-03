package com.caedis.duradisplay.render;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class BarRenderer extends OverlayRenderer {

    private final int color;
    private final double durabilityPercent;
    private final boolean smoothBar;
    private final int offset;
    private final boolean showBackground;

    public BarRenderer(int color, double durabilityPercent, boolean smoothBar, int offset, boolean showBackground) {
        this.color = color;
        this.durabilityPercent = durabilityPercent;
        this.smoothBar = smoothBar;
        this.offset = offset;
        this.showBackground = showBackground;
    }

    private static final Tessellator tessellator = Tessellator.getInstance();

    @Override
    public void Render(FontRenderer fontRenderer, int xPosition, int yPosition) {
        double length;
        if (smoothBar) length = durabilityPercent * 13.0;
        else length = Math.round(durabilityPercent * 13.0);
        final int k = (int) Math.round(durabilityPercent * 255.0);

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        final int i1 = (255 - k) / 4 << 16 | 16128;

        if (showBackground) {
            renderQuad(xPosition + 2, yPosition + 14 - offset, 13, 2, 0);
            renderQuad(xPosition + 2, yPosition + 14 - offset, 12, 1, i1);
        }
        renderQuad(xPosition + 2, yPosition + 14 - offset, length, 1, color);

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
    }

    private static void renderQuad(final double xPosition, final double yPosition, final double width,
        final double height, final int color) {
        int red = color >> 16 & 255, green = color >> 8 & 255, blue = color & 255;
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR);
        tessellator.getBuffer().pos(xPosition, yPosition, 0.0D).color(red, green, blue, 255).endVertex();
        tessellator.getBuffer().pos(xPosition, yPosition + height, 0.0D).color(red, green, blue, 255).endVertex();
        tessellator.getBuffer().pos(xPosition + width, yPosition + height, 0.0D).color(red, green, blue, 255).endVertex();
        tessellator.getBuffer().pos(xPosition + width, yPosition, 0.0D).color(red, green, blue, 255).endVertex();
        tessellator.draw();
    }
}
