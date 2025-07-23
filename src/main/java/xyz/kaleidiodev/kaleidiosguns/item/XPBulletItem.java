package xyz.kaleidiodev.kaleidiosguns.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.kaleidiodev.kaleidiosguns.config.KGConfig;
import xyz.kaleidiodev.kaleidiosguns.entity.BulletEntity;

import javax.annotation.Nullable;
import java.util.List;

public class XPBulletItem extends BulletItem {

	public XPBulletItem(Properties properties, double damage, int durability) {
		super(properties, damage, durability);
	}

	@Override
	public void consume(ItemStack stack, PlayerEntity player, ItemStack gunItem) {
		player.giveExperiencePoints(-costToUse(gunItem) * KGConfig.xpPerShot.get());
		stack.hurtAndBreak(costToUse(gunItem), player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
	}

	@Override
	public boolean hasAmmo(ItemStack stack, PlayerEntity player, ItemStack gunItem) {
		return currentXP(player) >= costToUse(gunItem) * KGConfig.xpPerShot.get();
	}

	// Manual workaround since commands don't grant xp the way they should do
	private int currentXP(PlayerEntity player) {
		int totalXP = 0;

		for (int i = 0; i <= player.experienceLevel; i++)
		{
			if (i == player.experienceLevel) {
				totalXP += (int)(player.experienceProgress * player.getXpNeededForNextLevel());
			}
			else {
				if (i >= 30) {
					totalXP += 112 + (i - 30) * 9;
				} else {
					totalXP += i >= 15 ? 37 + (i - 15) * 5 : 7 + i * 2;
				}
			}
		}

		return totalXP;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("tooltip.kaleidiosguns.xp_bullet").withStyle(TextFormatting.GRAY));
	}

	public static boolean isShot(ItemStack stack) {
		return !stack.isEmpty() && stack.getOrCreateTag().contains("shot");
	}
}
