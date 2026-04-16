package com.rinko1231.ambidexterity.mixin;

import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = StaffItem.class, remap = false)
public interface StaffItemAccessor {
    @Accessor("defaultModifiers")
    Multimap<Attribute, AttributeModifier> getDefaultModifiers();
}