package xyz.kaleidiodev.kaleidiosguns.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import xyz.kaleidiodev.kaleidiosguns.config.KGConfig;
import xyz.kaleidiodev.kaleidiosguns.registry.ModEnchantments;

import javax.annotation.Nullable;
import java.util.List;

public class ShotgunItem extends GunItem {

	private final int bulletCount;
	protected boolean isVampire;
	private int vampireCount;

	public ShotgunItem(Properties properties, int bonusDamage, double damageMultiplier, int fireDelay, double inaccuracy, int enchantability, int bulletCount, double attackSpeed, double attackDamage) {
		super(properties, bonusDamage, damageMultiplier, fireDelay, inaccuracy, enchantability, attackSpeed, attackDamage);
		this.bulletCount = bulletCount;
	}

	@Override
	protected void fireWeapon(World world, PlayerEntity player, ItemStack gun, ItemStack ammo, IBullet bulletItem, boolean bulletFree) {
		//always fire vampire check first
		vampireBulletCount(player);
		//then fire bullet count check.  this will ensure that division only applies to bullets AFTER vampire check has added its own so damage scales accordingly
		for (int i = 0; i < getBulletCount(gun, player); i++) super.fireWeapon(world, player, gun, ammo, bulletItem, bulletFree);
	}

	@Override
	protected void addExtraStatsTooltip(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip) {
		tooltip.add(new TranslationTextComponent("tooltip.kaleidiosguns.shotgun.shots" + (isProjectileCountModified(stack) ? ".modified" : ""), getBulletCount(stack, null)));
		if (isVampire) tooltip.add(new TranslationTextComponent("tooltip.kaleidiosguns.vampire_shotgun"));
	}

	protected int getBulletCount(ItemStack stack, @Nullable PlayerEntity player) {
		int divisionFactor = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.division, stack) * KGConfig.divisionCountIncrease.get();

		return getBaseBulletCount() + divisionFactor;
	}

	protected void vampireBulletCount(@Nullable PlayerEntity player) {
		int entityCount = 0;
		vampireCount = 0;
		if (isVampire && player != null) {
			List<Entity> victims = player.level.getEntitiesOfClass(Entity.class, AxisAlignedBB.ofSize(KGConfig.netheriteShotgunEntityRadius.get() * 2, KGConfig.netheriteShotgunEntityRadius.get() * 2, KGConfig.netheriteShotgunEntityRadius.get() * 2).move(player.position()));

			for (Entity mob : victims) {
				if (mob instanceof LivingEntity) {
					LivingEntity creature = (LivingEntity) mob;
					//every creature in this 10 block box gets a heart sacrificed for a new bullet in the shotgun
					//cap at a certain amount of entities
					if ((creature.getUUID() != player.getUUID()) && (entityCount < KGConfig.netheriteShotgunEntityCap.get())) {
						if (!checkIsSameTeam(player, mob)) {
							creature.hurt((new EntityDamageSource("magic", (Entity) player)), (float) (double) KGConfig.netheriteShotgunEntityHurt.get()); //set value for vampire via config later
							vampireCount += KGConfig.netheriteShotgunBulletsPerEntity.get();
							entityCount++;
						}
					}
				}
			}
		}
	}

	protected boolean checkIsSameTeam(Entity player, Entity victim) {
		//check pet role first before team, as null team means they don't belong to any team in the first place
		if (victim instanceof TameableEntity) {
			return ((TameableEntity) victim).getOwner() == player;
		}
		if ((player.getTeam() == null) && (victim.getTeam() == null)) return false;
		return player.getTeam() == victim.getTeam();
	}

	protected boolean isProjectileCountModified(ItemStack stack) {
		return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.division, stack) >= 1;
	}

	public int getBaseBulletCount() {
		return this.bulletCount + this.vampireCount;
	}

	public boolean getIsVampire() { return isVampire; }

	public ShotgunItem setIsVampire(boolean vampire) {
		this.isVampire = vampire;
		return this;
	}

}
