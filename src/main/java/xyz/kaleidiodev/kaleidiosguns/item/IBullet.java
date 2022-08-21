package xyz.kaleidiodev.kaleidiosguns.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import xyz.kaleidiodev.kaleidiosguns.config.KGConfig;
import xyz.kaleidiodev.kaleidiosguns.entity.BulletEntity;

import javax.annotation.Nullable;

public interface IBullet {
	/**
	 * Creates a projectile and set its stats and stuff. The gun will give it velocity and spawn it in the world.
	 */
	BulletEntity createProjectile(World world, ItemStack stack, LivingEntity shooter, boolean isPlasma);

	/**
	 * Uses up 1 item worth of ammo. Can be used for RF or magic based bullet pouches or something.
	 */
	void consume(ItemStack stack, PlayerEntity player);

	/**
	 * Whether that stack can be fired as ammo. Can be used for RF bullet pouches or something so that they get skipped if they're out of juice.
	 */
	default boolean hasAmmo(ItemStack stack) {
		return !stack.isEmpty();
	}

	/**
	 * Called on server only when a default projectile (or one that extends it) sucessfully damages a LivingEntity (so after damage).
	 * <br/>May change that later.
	 */
	default void onLivingEntityHit(BulletEntity projectile, LivingEntity target, @Nullable Entity shooter, World world) {
		//if the chance to heal rolls successfully
		if (projectile.rollRewardChance()) {
			//calculate the damage the enemy recieved.
			float damageDelta = projectile.getHealthOfVictim() - target.getHealth();

			//heal the shooter by a fraction of what damage the enemy recieved.
			LivingEntity shooterEntity = (LivingEntity)shooter;
			//cast to primitive first before casting to float.  thanks forge.
			shooterEntity.setHealth(shooterEntity.getHealth() + (damageDelta * (float)(double) KGConfig.passionForBloodHealIncrease.get()));
		}
	}

	/**
	 * Called on server only as damage is being applied when a bullet carrying this item hits. The target may not be a LivingEntity.
	 * <br/>May change that later.
	 */
	default double modifyDamage(double damage, BulletEntity projectile, Entity target, @Nullable Entity shooter, World world) {
		//if puncturing enchantment is present.
		double newDamage = damage;

		//apply chance based critical
		if (projectile.isCritical()) newDamage *= KGConfig.criticalDamage.get();

		//multiply all this by combo
		if (projectile.shouldCombo) {
			if ((target instanceof LivingEntity) && (shooter instanceof PlayerEntity)) {
				LivingEntity victim = (LivingEntity)target;
				PlayerEntity assailant = (PlayerEntity) shooter;
				//first remove the old multiplier, then add the new one.
				newDamage /= projectile.getShootingGun().getDamageMultiplier(new ItemStack(projectile.getShootingGun().getItem()));
				newDamage *= projectile.getShootingGun().tryComboCalculate(victim.getUUID(), assailant);
			}
		}

		//if the bullet is a plasma type, deal very high damage to a shield if one is in use.
		//this way we let the vanilla mechanic of a shield taking damage as durability into effect
		if ((projectile.isPlasma) && (target instanceof LivingEntity)) {
			LivingEntity livingTarget = (LivingEntity)target;
			Iterable<ItemStack> armor = livingTarget.getArmorSlots();

			for (ItemStack armorpiece : armor) {
				armorpiece.setDamageValue(armorpiece.getDamageValue() + KGConfig.goldStreamArmorAdditional.get());
			}

			if (target instanceof PlayerEntity) {
				PlayerEntity victim = (PlayerEntity) target;
				if (victim.getUseItem().isShield(victim)) {
					victim.stopUsingItem();
					victim.getCooldowns().addCooldown(victim.getUseItem().getItem(), 100);
					world.playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.SHIELD_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
				}
			}
		}

		//revenge shots should multiply their damage.
		if (projectile.wasRevenge) newDamage *= KGConfig.emeraldBlessedBlessingMultiplier.get();

		//shooting shadow in the dark multiplies damage.
		if (projectile.wasDark) {
			newDamage /= projectile.getShootingGun().getDamageMultiplier(new ItemStack(projectile.getShootingGun().getItem()));
			newDamage *= (projectile.getShootingGun().getDamageMultiplier(new ItemStack(projectile.getShootingGun().getItem())) + KGConfig.shadowRevolverShadowAdditionalMultiplier.get());
		}

		//apply damage multipier based on distance from target to origin, using max and min
		if (projectile.frostyDistance > 0) {
			double distanceTravelledToTarget = target.position().distanceTo(projectile.getOrigin());
			double multiplierPerBlock = (KGConfig.frostyMaxMultiplier.get() - KGConfig.frostyMinMultiplier.get()) / projectile.frostyDistance; //get a fraction that can be multiplied by the amount of blocks travelled
			double newMultiplier = (projectile.frostyDistance - distanceTravelledToTarget) * multiplierPerBlock; //multiply by amount of blocks until we reach the maximum travel cap

			if (newMultiplier < 0) newMultiplier = 0; //don't go below zero.
			newMultiplier += KGConfig.frostyMinMultiplier.get(); //add minimum multiplier back, it was removed before so block multiplier would be correct.
			newDamage *= newMultiplier;
		}

		return newDamage;
	}

}
