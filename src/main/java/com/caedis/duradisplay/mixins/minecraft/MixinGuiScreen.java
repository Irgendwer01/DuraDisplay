package com.caedis.duradisplay.mixins.minecraft;

import com.caedis.duradisplay.render.DurabilityRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiScreen.class)
public class MixinGuiScreen {

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "drawScreen(IIF)V", at = @At("HEAD"))
    private void drawScreenStart(CallbackInfo cbi){
        DurabilityRenderer.ShouldRun = false;
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "drawScreen(IIF)V", at = @At("RETURN"))
    private void drawScreenEnd(CallbackInfo cbi){
        DurabilityRenderer.ShouldRun = true;
    }
}
