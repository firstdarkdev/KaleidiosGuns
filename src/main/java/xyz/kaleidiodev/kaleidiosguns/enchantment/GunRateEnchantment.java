package xyz.kaleidiodev.kaleidiosguns.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;

public class GunRateEnchantment extends GunEnchantment {

    public GunRateEnchantment(Rarity rarityIn, int maxLevel, int minCost, int levelCost, int levelCostSpan, EnchantmentType enchantmentType) {
        super(rarityIn, maxLevel, minCost, levelCost, levelCostSpan, enchantmentType);
    }

    @Override
    protected boolean checkCompatibility(Enchantment pEnchant) {
        return !(pEnchant instanceof GunDamageEnchantment);
    }
}