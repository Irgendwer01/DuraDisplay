package com.caedis.duradisplay.overlay;

import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.caedis.duradisplay.config.ConfigDurabilityLike;
import com.caedis.duradisplay.utils.ColorType;
import com.caedis.duradisplay.utils.DurabilityFormatter;
import com.caedis.duradisplay.utils.DurabilityLikeInfo;
import com.google.common.collect.Sets;

// Gadgets are items to show UseCount(remain) as default
// GT Lighter and GT Paint Sprayer for example
public class OverlayGadgets extends OverlayDurabilityLike {

    public OverlayGadgets() {
        super(
                new ConfigDurabilityLike(
                        true,
                        OverlayDurabilityLike.Style.Text,
                        DurabilityFormatter.Format.remaining,
                        2,
                        true,
                        true,
                        0xFFFFFF,
                        ColorType.Single,
                        new double[] { 30, 70 },
                        new int[] { 0xFF0000, 0xFFBDC8, 0xFFFFFF },
                        true,
                        2,
                        true) {

                    @Override
                    public void postLoadConfig() {
                        configCategory.setComment("""
                                Gadgets are items that show UseCount(remain) as default
                                including some items whose maxDurability<100
                                and GregTech lighters and Paint Sprayer
                                                                                """);
                    }

                    @Override
                    public @NotNull String category() {
                        return "gadgets";
                    }
                });
        addHandler("gregtech.api.items.metaitem.MetaItem", OverlayGadgets::handleGregtech);
        addHandler("buildcraft.core.item.ItemPaintbrush_BC8", OverlayGadgets::handleBCPaintBrush);
        addHandler("ic2.core.item.tool.ItemToolPainter", OverlayDurability::handleDefault);
        addHandler("WayofTime.bloodmagic.item.ItemInscriptionTool", OverlayGadgets::handleBMInscriptionTools);
        addHandler("thaumcraft.common.items.tools.ItemScribingTools", OverlayDurability::handleDefault);
        addHandler("vazkii.botania.common.item.interaction.thaumcraft.ItemManaInkwell",
                OverlayDurability::handleDefault);
        addHandler("net.minecraft.item.Item", OverlayGadgets::handleByAllowList);
    }

    public static final Set<String> AllowListUnLocalized = Sets.newHashSet(
            "item.flintAndSteel",
            "ic2.itemWeedEx",
            "item.for.waxCast",
            "item.for.solderingIron",
            "ic2.itemTreetap",
            "item.appliedenergistics2.ToolCertusQuartzCuttingKnife",
            "item.appliedenergistics2.ToolNetherQuartzCuttingKnife",
            "ic2.itemToolForgeHammer",
            "item.spellCloth",
            "item.WoodenBrickForm");

    @Override
    @NotNull
    ConfigDurabilityLike config() {
        return config;
    }

    @Nullable
    public static DurabilityLikeInfo handleBMInscriptionTools(@NotNull ItemStack stack) {
        int max = 10;
        int current = 0;
        if (stack.hasTagCompound()) {
            current = stack.getTagCompound().getInteger("uses");
            return new DurabilityLikeInfo(current, max);
        }
        return null;
    }

    public static DurabilityLikeInfo handleBCPaintBrush(@NotNull ItemStack stack) {
        int max = 64;
        int current = max;
        if (stack.hasTagCompound()) {
            current = max - stack.getTagCompound().getInteger("damage");
        }
        return new DurabilityLikeInfo(current, max);
    }

    @Nullable
    public static DurabilityLikeInfo handleByAllowList(@NotNull ItemStack stack) {
        if (!AllowListUnLocalized.contains(stack.getTranslationKey())) return null;
        Item item = stack.getItem();
        assert item != null;

        if (!item.isDamageable()) return null;

        double max = item.getMaxDamage();
        double current = max - item.getDamage(stack);
        return new DurabilityLikeInfo(current, max);
    }

    @Nullable
    public static DurabilityLikeInfo handleGregtech(@NotNull ItemStack stack) {
        if (!stack.getItem().getRegistryName().getPath().equals("meta_item_1")) return null;
        long max = 0;
        long current = 0;

        var tag = stack.getTagCompound();

        switch (stack.getItemDamage()) {
            case 91: {
                if (tag != null) {
                    max = 100;
                    current = tag.getCompoundTag("Fluid").getInteger("Amount");
                    return new DurabilityLikeInfo(current, max);
                }
            }
            case 92: {
                if (tag != null) {
                    max = 1000;
                    current = tag.getCompoundTag("Fluid").getInteger("Amount");
                    return new DurabilityLikeInfo(current, max);
                }
            }
            case 90: {
                if (tag != null) {
                    max = 16;
                    current = tag.getInteger("usesLeft");
                    return new DurabilityLikeInfo(current, max);
                }
            }
            case 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77: {
                max = 512;

                if (tag != null) {
                    current = tag.getInteger("GT.UsesLeft");
                } else {
                    current = max;
                }
                return new DurabilityLikeInfo(current, max);
            }
            case 60: {
                max = 1024;

                if (tag != null) {
                    current = tag.getInteger("GT.UsesLeft");
                } else {
                    current = max;
                }
                return new DurabilityLikeInfo(current, max);
            }
            default:
                return null;
        }
    }
}
