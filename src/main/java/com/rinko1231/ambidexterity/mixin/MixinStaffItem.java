package com.rinko1231.ambidexterity.mixin;

import com.google.common.collect.Multimap;
import com.rinko1231.ambidexterity.config.AmbidexterityConfig;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value = StaffItem.class)
public class MixinStaffItem {

    @Inject(
            method = "getDefaultAttributeModifiers",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectGetDefaultAttributeModifiers(EquipmentSlot slot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir) {
        StaffItem self = (StaffItem) (Object) this;
        String itemId = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(self)).toString();

        if ((slot == EquipmentSlot.MAINHAND|| slot == EquipmentSlot.OFFHAND)
                && !AmbidexterityConfig.itemBlacklist.get().contains(itemId)
        ) {
            cir.setReturnValue(((StaffItemAccessor)this).getDefaultModifiers());
        }
    }

}