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

public class HungerBulletItem extends BulletItem {

	public HungerBulletItem(Properties properties, double damage) {
		super(properties, damage);
	}

	@Override
	public void consume(ItemStack stack, PlayerEntity player, ItemStack gunItem) {
		int cost = costToUse(gunItem);
		if (player.getFoodData().getFoodLevel() <= 0) player.hurt(DamageSource.STARVE, cost);
		player.causeFoodExhaustion(cost * 3);
	}

	//this bullet type allows for suicide kills
	@Override
	public boolean hasAmmo(ItemStack stack, PlayerEntity player, ItemStack gunItem) {
		return true;
	}

	@Override
	public BulletEntity createProjectile(World world, ItemStack stack, LivingEntity shooter, boolean isPlasma) {
		ItemStack fake = new ItemStack(this);
		fake.getOrCreateTag().putInt("shot", 1);
		return super.createProjectile(world, fake, shooter, isPlasma);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("tooltip.kaleidiosguns.hunger_bullet").withStyle(TextFormatting.GRAY));
	}

	public static boolean isShot(ItemStack stack) {
		return !stack.isEmpty() && stack.getOrCreateTag().contains("shot") && stack.getOrCreateTag().getBoolean("shot");
	}
}
