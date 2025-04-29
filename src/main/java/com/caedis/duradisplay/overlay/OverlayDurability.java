package com.caedis.duradisplay.overlay;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.NotNull;

import com.caedis.duradisplay.config.ConfigDurabilityLike;
import com.caedis.duradisplay.utils.ColorType;
import com.caedis.duradisplay.utils.DurabilityFormatter;
import com.caedis.duradisplay.utils.DurabilityLikeInfo;

import ic2.api.item.ICustomDamageItem;
import ic2.core.item.armor.ItemArmorFluidTank;
import mekanism.api.gas.IGasItem;

public class OverlayDurability extends OverlayDurabilityLike {

    public OverlayDurability() {
        super(
                new ConfigDurabilityLike(
                        true,
                        OverlayDurabilityLike.Style.Text,
                        DurabilityFormatter.Format.percent,
                        2,
                        false,
                        true,
                        0x00FF00,
                        ColorType.RYGDurability,
                        new double[] { 30, 70 },
                        new int[] { 0xFF0000, 0x55FF00, 0x00FF00 },
                        true,
                        0,
                        true) {

                    @Override
                    public void postLoadConfig() {
                        configCategory.setComment("""
                                Durability is the default module that shows durability of items
                                                                             """);
                    }

                    @Override
                    public @NotNull String category() {
                        return "durability";
                    }
                });
        addHandler("gregtech.api.items.toolitem.IGTTool", OverlayDurability::handleGregTech);
        addHandler("appeng.items.tools.powered.powersink.AEBasePoweredItem", i -> null);
        addHandler("ic2.api.item.IElectricItem", i -> null);
        addHandler("ic2.core.item.armor.ItemArmorFluidTank", OverlayDurability::handleItemArmorFluidTank);
        addHandler("ic2.api.item.ICustomDamageItem", OverlayDurability::handleICustomDamageItem);
        addHandler("vazkii.botania.common.item.brew.ItemBrewBase", i -> null);
        addHandler("vazkii.botania.common.item.interaction.thaumcraft.ItemManaInkwell", i -> null);
        addHandler("WayofTime.bloodmagic.item.ItemPotionFlask", i -> null);
        addHandler("WayofTime.bloodmagic.item.ItemInscriptionTool", i -> null);
        addHandler("buildcraft.core.ItemPaintbrush", i -> null);
        addHandler("ic2.core.item.tool.ItemToolPainter", i -> null);
        addHandler("thaumcraft.common.items.tools.ItemScribingTools", i -> null);
        addHandler("mekanism.common.item.ItemJetpack", OverlayDurability::handleIGasItem);
        addHandler("mekanism.common.item.ItemFlamethrower", OverlayDurability::handleIGasItem);
        addHandler("net.minecraft.item.Item", OverlayDurability::handleDefault);
    }

    @Override
    public @NotNull ConfigDurabilityLike config() {
        return config;
    }

    public static DurabilityLikeInfo handleDefault(@NotNull ItemStack stack) {
        Item item = stack.getItem();
        assert item != null;

        if (!item.isDamageable()) return null;

        // handled by OverlayGadgets
        if (OverlayGadgets.AllowListUnLocalized.contains(stack.getTranslationKey())) return null;

        double max = item.getMaxDamage(stack);
        double current = max - item.getDamage(stack);
        return new DurabilityLikeInfo(current, max);
    }

    public static DurabilityLikeInfo handleGregTech(@NotNull ItemStack stack) {
        if (!stack.hasTagCompound()) return null;
        NBTTagCompound nbt = stack.getTagCompound();

        if (nbt.hasKey("GT.Tool")) {
            NBTTagCompound ts = nbt.getCompoundTag("GT.Tool");
            double damage = ts.getLong("Durability");
            double max = ts.getLong("MaxDurability");
            double current = max - damage;
            return new DurabilityLikeInfo(current, max);
        }
        return null;
    }

    public static DurabilityLikeInfo handleItemArmorFluidTank(@NotNull ItemStack stack) {
        ItemArmorFluidTank bei = ((ItemArmorFluidTank) stack.getItem());
        assert bei != null;

        return new DurabilityLikeInfo(bei.getCharge(stack), bei.getMaxCharge(stack));
    }

    public static DurabilityLikeInfo handleICustomDamageItem(@NotNull ItemStack stack) {
        ICustomDamageItem bei = ((ICustomDamageItem) stack.getItem());
        assert bei != null;

        double damage = bei.getCustomDamage(stack);
        double max = bei.getMaxCustomDamage(stack);
        double current = max - damage;
        return new DurabilityLikeInfo(current, max);
    }

    public static DurabilityLikeInfo handleIGasItem(@NotNull ItemStack stack) {
        IGasItem iGasItem = ((IGasItem) stack.getItem());
        assert iGasItem != null;
        double max = iGasItem.getMaxGas(stack);
        double damage = iGasItem.getGas(stack) == null ? 0 : max - iGasItem.getGas(stack).amount;
        double current = max - damage;
        return new DurabilityLikeInfo(current, max);
    }
}
