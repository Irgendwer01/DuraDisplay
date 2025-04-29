package com.caedis.duradisplay.mixins.gregtech;

import com.caedis.duradisplay.overlay.OverlayCharge;
import com.caedis.duradisplay.render.DurabilityRenderer;
import gregtech.api.items.metaitem.stats.IItemDurabilityManager;
import gregtech.client.utils.ToolChargeBarRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(value = ToolChargeBarRenderer.class)
public class ToolChargeBarRendererMixin {

    @Inject(method = "renderDurabilityBar(DII)Z", at = @At("HEAD"), cancellable = true)
    private static void renderDurabilityBar(double level, int xPosition, int yPosition, CallbackInfoReturnable<Boolean> cir) {
        if (DurabilityRenderer.Execute) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "renderDurabilityBar(Lnet/minecraft/item/ItemStack;Lgregtech/api/items/metaitem/stats/IItemDurabilityManager;II)Z", at = @At("HEAD"), cancellable = true)
    private static void renderDurabilityBar(ItemStack stack, IItemDurabilityManager manager, int xPosition, int yPosition, CallbackInfoReturnable<Boolean> cir) {
        if (DurabilityRenderer.Execute) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "renderElectricBar", at = @At("HEAD"), cancellable = true)
    private static void renderElectricBar(long charge, long maxCharge, int xPosition, int yPosition, boolean renderedDurability, CallbackInfo ci) {
        if (DurabilityRenderer.Execute && OverlayCharge.enabled) {
            ci.cancel();
        }
    }
}

