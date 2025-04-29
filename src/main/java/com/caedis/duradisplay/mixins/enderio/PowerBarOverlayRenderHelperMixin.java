package com.caedis.duradisplay.mixins.enderio;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.caedis.duradisplay.overlay.OverlayCharge;
import com.caedis.duradisplay.render.DurabilityRenderer;

import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;

@Mixin(value = PowerBarOverlayRenderHelper.class)
public class PowerBarOverlayRenderHelperMixin {

    @Inject(method = "render(Lnet/minecraft/item/ItemStack;IIZ)Z", at = @At("HEAD"), remap = false, cancellable = true)
    private void render(ItemStack stack, int xPosition, int yPosition, boolean alwaysShow,
                        CallbackInfoReturnable<Boolean> cir) {
        if (DurabilityRenderer.Execute && OverlayCharge.enabled) {
            cir.setReturnValue(false);
        }
    }
}
