package com.rinko1231.ambidexterity.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
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
        if (changedEquipment == null) return;

        LivingEntity self = (LivingEntity) (Object) this;

        for (EquipmentSlot slot : irons_handSlots) {
            ItemStack currentStack = changedEquipment.get(slot);
            if (currentStack != null) {
                ItemStack oldStack = this.getLastHandItem(slot);

                // 只要是 StaffItem 就算作法杖
                boolean selected = currentStack.getItem() instanceof StaffItem;
                boolean deselected = oldStack.getItem() instanceof StaffItem;

                if (selected || deselected) {
                    // 情况 A：主手发生变化
                    if (slot == EquipmentSlot.MAINHAND) {
                        ItemStack offhandStack = self.getOffhandItem();
                        // 如果副手有法杖，且和主手新/旧法杖不是同一种
                        if (offhandStack.getItem() instanceof StaffItem && !ItemStack.isSameItem(offhandStack, currentStack)) {
                            if (selected) {
                                // 主手拿起了新法杖，移除副手可能冲突的属性
                                self.getAttributes().removeAttributeModifiers(irons_filterAttributes(offhandStack));
                            }
                            if (deselected) {
                                // 主手放下了法杖，恢复副手法杖的属性
                                self.getAttributes().addTransientAttributeModifiers(irons_filterAttributes(offhandStack));
                            }
                        }
                    }
                    // 情况 B：副手发生变化
                    else if (slot == EquipmentSlot.OFFHAND) {
                        ItemStack mainhandStack = self.getMainHandItem();

                        // 逻辑：如果副手拿起了法杖，且主手【没有拿法杖】或者主手拿的是【不同种类的法杖】
                        if (selected && (!(mainhandStack.getItem() instanceof StaffItem) || !ItemStack.isSameItem(mainhandStack, currentStack))) {
                            self.getAttributes().addTransientAttributeModifiers(irons_filterAttributes(currentStack));
                        }

                        // 如果副手放下了法杖
                        if (deselected && !ItemStack.isSameItem(mainhandStack, oldStack)) {
                            self.getAttributes().removeAttributeModifiers(irons_filterAttributes(oldStack));
                        }
                    }
                }
            }
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