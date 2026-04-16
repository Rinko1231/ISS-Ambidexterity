package com.rinko1231.ambidexterity;

import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientPlayerEvent {

    @SubscribeEvent
    public static void imbuedWeaponTooltips(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof Scroll) return;


        MinecraftInstanceHelper.ifPlayerPresent((player1) -> {

            var lines = event.getToolTip();


            if (stack.getItem() instanceof StaffItem) {
                {
                    int i = TooltipsUtils.indexOfComponent(lines, "item.modifiers.mainhand");
                    if (i >= 0) {
                        lines.set(i, Component.translatable("tooltip.irons_spellbooks.modifiers.multihand").withStyle(lines.get(i).getStyle()));
                    }
                }
            }
        });
    }

}
