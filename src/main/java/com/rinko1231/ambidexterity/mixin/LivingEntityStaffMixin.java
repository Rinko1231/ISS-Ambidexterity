package com.rinko1231.ambidexterity.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
@Mixin(LivingEntity.class)
public abstract class LivingEntityStaffMixin {
    @Unique
    private static final List<EquipmentSlot> irons_handSlots = List.of(EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND);

    @Shadow
    protected abstract ItemStack getLastHandItem(EquipmentSlot var1);


    @Inject(
            method = "collectEquipmentChanges",
            at = @At("RETURN")
    )
    public void irons_handleEquipmentChanges(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        Map<EquipmentSlot, ItemStack> changedEquipment = cir.getReturnValue();
        if (changedEquipment == null || changedEquipment.isEmpty()) return;

        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack mainhandStack = self.getMainHandItem();
        ItemStack offhandStack = self.getOffhandItem();

        // 检查主副手是否发生变化
        boolean mainhandChanged = changedEquipment.containsKey(EquipmentSlot.MAINHAND);
        boolean offhandChanged = changedEquipment.containsKey(EquipmentSlot.OFFHAND);

        if (!mainhandChanged && !offhandChanged) return;

        // 先清理所有手部法杖的属性
        for (EquipmentSlot slot : irons_handSlots) {
            ItemStack oldStack = this.getLastHandItem(slot);
            if (oldStack.getItem() instanceof StaffItem) {
                self.getAttributes().removeAttributeModifiers(irons_filterAttributes(oldStack));
            }
        }

        // 如果副手有法杖，且主手没有法杖或主手法杖与副手不同，则应用副手法杖的属性
        if (offhandStack.getItem() instanceof StaffItem) {
            boolean shouldApplyOffhand = true;
            if (mainhandStack.getItem() instanceof StaffItem) {
                // 如果主副手是同样的法杖，只应用一份
                if (ItemStack.isSameItem(mainhandStack, offhandStack)) {
                    shouldApplyOffhand = false;
                }
            }
            if (shouldApplyOffhand) {
                self.getAttributes().addTransientAttributeModifiers(irons_filterAttributes(offhandStack));
            }
        }

        // 如果主手有法杖，应用主手法杖的属性
        if (mainhandStack.getItem() instanceof StaffItem) {
            self.getAttributes().addTransientAttributeModifiers(irons_filterAttributes(mainhandStack));
        }
    }

    @Unique
    private static Multimap<Attribute, AttributeModifier> irons_filterAttributes(ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
        if (stack.getItem() instanceof StaffItem staffItem) {
            Multimap<Attribute, AttributeModifier> fullMap = ((StaffItemAccessor) staffItem).irons_getDefaultModifiers();

            for (Attribute attribute : fullMap.keySet()) {
                // 排除物理属性，只保留魔法属性
                if (attribute != Attributes.ATTACK_DAMAGE &&
                        attribute != Attributes.ATTACK_SPEED &&
                        attribute != Attributes.ATTACK_KNOCKBACK &&
                        attribute != ForgeMod.ENTITY_REACH.get()) {
                    map.putAll(attribute, fullMap.get(attribute));
                }
            }
        }
        return map;
    }
}