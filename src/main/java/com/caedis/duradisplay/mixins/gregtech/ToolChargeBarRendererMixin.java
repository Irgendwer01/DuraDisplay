package com.caedis.duradisplay.mixins.gregtech;

import com.caedis.duradisplay.overlay.OverlayCharge;
import com.caedis.duradisplay.render.DurabilityRenderer;
import gregtech.client.utils.ToolChargeBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = ToolChargeBarRenderer.class)
public class ToolChargeBarRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false)
    private static void render(double level, int xPosition, int yPosition, int offset, boolean shadow, Color left, Color right, boolean doDepletedColor, CallbackInfo ci) {
        if (DurabilityRenderer.Execute) {
            ci.cancel();
        }
    }
}

