package com.caedis.duradisplay.core;

import java.util.*;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1005)
@IFMLLoadingPlugin.DependsOn("cofh.asm.LoadingPlugin")
public class DuraDisplayCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.duradisplay.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return Arrays.asList("minecraft.MixinRenderItem");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
