package com.caedis.duradisplay.render;

import java.awt.*;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.NotNull;

import com.caedis.duradisplay.config.DuraDisplayConfig;
import com.caedis.duradisplay.utils.GTToolsInfo;
import com.caedis.duradisplay.utils.NBTUtils;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.item.darksteel.IDarkSteelItem;
import gregtech.api.items.GT_MetaBase_Item;
import ic2.api.item.ElectricItem;
import ic2.core.item.tool.ItemElectricTool;

public class DurabilityRenderer {

    private static final Map<Class<?>, Function<ItemStack, List<ItemStackOverlay>>> itemHandlers;
    private static final NumberFormat nf = NumberFormat.getNumberInstance();

    //public static

    static {
        nf.setRoundingMode(RoundingMode.FLOOR);
        nf.setMaximumFractionDigits(0);

        itemHandlers = new LinkedHashMap<>();
        itemHandlers.put(GT_MetaBase_Item.class, DurabilityRenderer::handleGregTech);
        itemHandlers.put(IDarkSteelItem.class, DurabilityRenderer::handleDarkSteelItems);
        itemHandlers.put(ItemElectricTool.class, DurabilityRenderer::handleItemElectricTool);
        itemHandlers.put(IEnergyContainerItem.class, DurabilityRenderer::handleEnergyContainer);
        itemHandlers.put(Item.class, DurabilityRenderer::handleDefault);
    }

    public static void Render(FontRenderer fontRenderer, ItemStack stack, int xPosition, int yPosition, float zLevel) {
        assert stack.getItem() != null;
        ItemStackOverlay.DurabilityOverlay durabilityOverlay = null;
        ItemStackOverlay.ChargeOverlay chargeOverlay = null;

        Optional<Class<?>> key = itemHandlers.keySet().stream().filter(clazz -> clazz.isInstance(stack.getItem())).findFirst();

        if (!key.isPresent()) return;

        List<ItemStackOverlay> list = itemHandlers.get(key.get()).apply(stack);
        if (list != null) {
            for (ItemStackOverlay overlay : list) {
                if (overlay instanceof ItemStackOverlay.DurabilityOverlay dOverlay) {
                    durabilityOverlay = dOverlay;
                } else if (overlay instanceof ItemStackOverlay.ChargeOverlay cOverlay) {
                    chargeOverlay = cOverlay;
                }
            }
        }

        if (durabilityOverlay != null) durabilityOverlay.Render(fontRenderer, xPosition, yPosition, zLevel);
        if (chargeOverlay != null) chargeOverlay.Render(fontRenderer, xPosition, yPosition, zLevel);
    }

    public static int getRGBDurabilityForDisplay(double dur) {
        if (!DuraDisplayConfig.Durability_UseColorThresholds)
            return Color.HSBtoRGB(Math.max(0.0F, (float) dur) / 3.0F, 1.0F, 1.0F);
        else {
            double durability = dur * 100;
            if (durability <= DuraDisplayConfig.Durability_ColorThresholds[0]) {
                return 0xFF0000;
            } else if (durability
                >= DuraDisplayConfig.Durability_ColorThresholds[DuraDisplayConfig.Durability_ColorThresholds.length
                    - 1]) {
                        return 0x55FF00;
                    } else {
                        return 0XFFD500;
                    }
        }
    }

    private static List<ItemStackOverlay> handleDefault(@NotNull ItemStack stack) {
        if (!DuraDisplayConfig.Durability_Enable || !(stack.isItemStackDamageable()
            && (DuraDisplayConfig.Durability_PercentageWhenFull || stack.isItemDamaged()))) return null;
        assert stack.getItem() != null;

        List<ItemStackOverlay> overlays = new ArrayList<>();

        ItemStackOverlay durabilityOverlay = new ItemStackOverlay.DurabilityOverlay();
        double durability = (1 - stack.getItem()
            .getDurabilityForDisplay(stack));
        durabilityOverlay.color = getRGBDurabilityForDisplay(durability);
        durability *= 100;
        durabilityOverlay.isFull = durability == 100.0;
        durabilityOverlay.value = nf.format(durability) + "%";
        overlays.add(durabilityOverlay);

        return overlays;
    }

    private static List<ItemStackOverlay> handleGregTech(@NotNull ItemStack stack) {
        if (!stack.hasTagCompound()) return null;
        GT_MetaBase_Item gtItem = ((GT_MetaBase_Item) stack.getItem());
        assert gtItem != null;

        List<ItemStackOverlay> overlays = new ArrayList<>();

        if (DuraDisplayConfig.Charge_Enable) {
            Long[] elecStats = gtItem.getElectricStats(stack);
            if (elecStats != null) {
                ItemStackOverlay chargeOverlay = new ItemStackOverlay.ChargeOverlay();
                double charge = ((double) gtItem.getRealCharge(stack) / Math.abs(elecStats[0])) * 100;
                chargeOverlay.isFull = charge == 100.0;
                chargeOverlay.value = nf.format(charge) + "%";
                overlays.add(chargeOverlay);
            }
        }

        if (DuraDisplayConfig.Durability_Enable) {
            ItemStackOverlay durabilityOverlay = new ItemStackOverlay.DurabilityOverlay();
            GTToolsInfo gti = NBTUtils.getToolInfo(stack);
            if (gti.getRemainingPaint() > 0) {
                durabilityOverlay.color = 0xFFFFFF;
                durabilityOverlay.value = nf.format(gti.getRemainingPaint());
                durabilityOverlay.isFull = (double) gti.getRemainingPaint() / gti.getMaxPaint() == 100.0;
                overlays.add(durabilityOverlay);
            } else if (gti.getMaxDamage() > 0) {
                double durability = (1 - (double) gti.getDamage() / gti.getMaxDamage());
                durabilityOverlay.color = getRGBDurabilityForDisplay(durability);
                durability *= 100;
                durabilityOverlay.isFull = durability == 100.0;
                durabilityOverlay.value = nf.format(durability) + "%";
                overlays.add(durabilityOverlay);
            }
        }

        return overlays;
    }

    private static List<ItemStackOverlay> handleItemElectricTool(@NotNull ItemStack stack) {
        if (!DuraDisplayConfig.Charge_Enable) return null; // because TiCon tools have the interface
        ItemElectricTool bei = ((ItemElectricTool) stack.getItem());
        assert bei != null;

        List<ItemStackOverlay> overlays = new ArrayList<>();

        ItemStackOverlay chargeOverlay = new ItemStackOverlay.ChargeOverlay();
        double charge = ((double) ElectricItem.manager.getCharge(stack) / bei.getMaxCharge(stack)) * 100;
        chargeOverlay.isFull = charge == 100.0;
        chargeOverlay.value = nf.format(charge) + "%";
        overlays.add(chargeOverlay);

        return overlays;
    }

    private static List<ItemStackOverlay> handleEnergyContainer(@NotNull ItemStack stack) {
        List<ItemStackOverlay> overlays = new ArrayList<>();
        List<ItemStackOverlay> defaultOverlays = handleDefault(stack);
        if (defaultOverlays != null) {
            overlays.addAll(defaultOverlays);
        }

        if (!DuraDisplayConfig.Charge_Enable || !(stack.hasTagCompound() && stack.getTagCompound()
            .hasKey("Energy"))) return overlays; // because TiCon tools have the interface
        IEnergyContainerItem eci = ((IEnergyContainerItem) stack.getItem());
        assert eci != null;

        ItemStackOverlay chargeOverlay = new ItemStackOverlay.ChargeOverlay();
        double durability = ((double) eci.getEnergyStored(stack) / eci.getMaxEnergyStored(stack)) * 100;
        chargeOverlay.isFull = durability == 100.0;
        chargeOverlay.value = nf.format(durability) + "%";
        overlays.add(chargeOverlay);

        return overlays;
    }

    // handles all other EIO items
    private static List<ItemStackOverlay> handleDarkSteelItems(@NotNull ItemStack stack) {
        List<ItemStackOverlay> overlays = new ArrayList<>();
        List<ItemStackOverlay> defaultOverlays = handleDefault(stack);
        if (defaultOverlays != null) {
            overlays.addAll(defaultOverlays);
        }
        if (!DuraDisplayConfig.Charge_Enable || !stack.hasTagCompound()) return null;

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt.hasKey("enderio.darksteel.upgrade.energyUpgrade")) {
            NBTTagCompound upgrade = nbt.getCompoundTag("enderio.darksteel.upgrade.energyUpgrade");
            int capacity = upgrade.getInteger("capacity");
            int energy = upgrade.getInteger("energy");

            ItemStackOverlay chargeOverlay = new ItemStackOverlay.ChargeOverlay();
            double durability = ((double) energy / capacity) * 100;
            chargeOverlay.isFull = durability == 100.0;
            chargeOverlay.value = nf.format(durability) + "%";

            overlays.add(chargeOverlay);
        }

        // normal item durability is handled in default case

        return overlays;
    }
}
