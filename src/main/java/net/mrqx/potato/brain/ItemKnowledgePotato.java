package net.mrqx.potato.brain;

import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ItemKnowledgePotato extends Item {
    public static final String KNOWLEDGE_POTATO_KEY = BigPotatoBrainMod.MODID + ".exp";

    public ItemKnowledgePotato() {
        super(new Properties().stacksTo(16).food(new FoodProperties.Builder().alwaysEat().build()).rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        if (stack.hasTag() && stack.getOrCreateTag().contains(KNOWLEDGE_POTATO_KEY, Tag.TAG_INT)) {
            tooltipComponents.add(Component.translatable("item.big_potato_brain.potato_of_knowledge.amount", stack.getOrCreateTag().getInt(KNOWLEDGE_POTATO_KEY)));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof Player player && stack.hasTag() && stack.getOrCreateTag().contains(KNOWLEDGE_POTATO_KEY, Tag.TAG_INT)) {

            int i = this.repairPlayerItems(player, stack.getOrCreateTag().getInt(KNOWLEDGE_POTATO_KEY), stack);
            if (i > 0) {
                player.giveExperiencePoints(i);
            }
            player.giveExperiencePoints(stack.getOrCreateTag().getInt(KNOWLEDGE_POTATO_KEY));
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }

    private int repairPlayerItems(Player player, int repairAmount, ItemStack stack) {
        Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomItemWith(Enchantments.MENDING, player, ItemStack::isDamaged);
        if (entry != null) {
            ItemStack itemstack = entry.getValue();
            int i = Math.min((int) (stack.getOrCreateTag().getInt(KNOWLEDGE_POTATO_KEY) * itemstack.getXpRepairRatio()), itemstack.getDamageValue());
            itemstack.setDamageValue(itemstack.getDamageValue() - i);
            int j = repairAmount - this.durabilityToXp(i);
            return j > 0 ? this.repairPlayerItems(player, j, stack) : 0;
        } else {
            return repairAmount;
        }
    }

    private int durabilityToXp(int durability) {
        return durability / 2;
    }
}
