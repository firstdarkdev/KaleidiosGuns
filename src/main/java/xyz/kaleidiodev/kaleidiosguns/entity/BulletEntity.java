package xyz.kaleidiodev.kaleidiosguns.entity;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeHooks;
import xyz.kaleidiodev.kaleidiosguns.config.KGConfig;
import xyz.kaleidiodev.kaleidiosguns.item.GatlingItem;
import xyz.kaleidiodev.kaleidiosguns.item.GunItem;
import xyz.kaleidiodev.kaleidiosguns.item.IBullet;
import xyz.kaleidiodev.kaleidiosguns.network.NetworkUtils;
import xyz.kaleidiodev.kaleidiosguns.registry.ModEntities;
import xyz.kaleidiodev.kaleidiosguns.registry.ModItems;
import xyz.kaleidiodev.kaleidiosguns.registry.ModSounds;

import java.util.*;

public class BulletEntity extends AbstractFireballEntity {
	protected double damage = 1;
	protected double inaccuracy = 0.0;
	protected boolean ignoreInvulnerability = false;
	protected double knockbackStrength = 0.0;
	protected int ticksSinceFired;
	protected double healthRewardChance = 0.0f;
	protected float healthOfVictim;
	protected boolean shouldBreakBlock;
	protected boolean shouldCollateral;
	protected double bulletSpeed;
	public boolean isTorpedo;
	protected boolean shouldGlow;
	protected GunItem shootingGun;
	protected Vector3d origin;
	public boolean isExplosive;
	public boolean isPlasma;
	public boolean isWither;
	public boolean wasRevenge;
	public boolean wasDark;
	public boolean shootsLights;
	public boolean isClean;
	public boolean isCorrupted;
	public boolean shouldBreakDoors;
	public boolean shouldShatterBlocks;
	public boolean healsFriendlies;
	public boolean juggle;
	public byte lavaMode; //bit 0 is player is on fire, bit 1 is enemy is on fire, bit 2 is is active
	public boolean isMeleeBonus;
	public int redstoneLevel;
	public double mineChance;

	public BulletEntity(EntityType<? extends BulletEntity> entityType, World worldIn) {
		super(entityType, worldIn);
	}

	public BulletEntity(World worldIn, LivingEntity shooter) {
		this(worldIn, shooter, 0, 0, 0);
		setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
	}

	public BulletEntity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
		super(ModEntities.BULLET, shooter, accelX, accelY, accelZ, worldIn);
		this.setNoGravity(true);
	}

	//change the particle type the projectile is going to emit
	@Override
	protected IParticleData getTrailParticle() {
		//seems that this method fires once on server and once on client.  something needs to be done in order to support multiple particle types
		if (isExplosive) return ParticleTypes.POOF;
		if (isPlasma) return ParticleTypes.INSTANT_EFFECT;
		if (wasRevenge || isMeleeBonus) return ParticleTypes.HAPPY_VILLAGER;
		if (wasDark) return ParticleTypes.SMOKE;
		if ((lavaMode & 0x04) != 0) return ParticleTypes.LANDING_LAVA; //if was a slag bullet in any mode
		return ParticleTypes.CRIT;
	}

	@Override
	public void tick() {
		//Using a thing I save so that bullets don't get clogged up on chunk borders
		ticksSinceFired++;
		if (ticksSinceFired > 100) {
			this.remove();
		}

		if (shouldGlow) {
			this.setGlowing(true);
		}

		//completely rewrite the entity code here
		Entity entity = this.getOwner();
		if (this.level.isClientSide || (entity == null || !entity.removed)) {
			this.setSharedFlag(6, this.isGlowing());
			//note that "is away from owner" is absolutely useless anyway, so it was not included
			this.baseTick();

			RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
			if (raytraceresult.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
				this.onHit(raytraceresult);
			}

			//why rotate toward movement?  that makes no sense
			//this needs rewritten so that:
			//1. the rotation is correct on spawn before first tick
			//2. the rotation is always correct at any rotation speed, like an arrow
			this.checkInsideBlocks();
			ProjectileHelper.rotateTowardsMovement(this, 1.0F);

			//add support for torpedo enchantment
			float f = this.getInertia();
			Vector3d vector3d = this.getDeltaMovement();

			if (this.isInWater() && this.level.isClientSide()) {
				this.level.addParticle(ParticleTypes.BUBBLE, true, this.getBoundingBox().getCenter().x, this.getBoundingBox().getCenter().y, this.getBoundingBox().getCenter().z, 0, 0, 0);
				//don't decrease inertia if the torpedo enchantment was on the gun
				if (!this.isTorpedo) f = 0.5f;
			}

			this.setDeltaMovement(vector3d.add(this.xPower, this.yPower, this.zPower).scale((double) f));
			//summon the particles in the center of the projectile instead of above it.
			//disable emitters when underwater, as otherwise it looks messy to have two emitters (bubble emitter happens elsewhere)
			if (!this.isInWater() && ticksSinceFired > 1 && this.level.isClientSide())
				this.level.addParticle(this.getTrailParticle(), true, this.getBoundingBox().getCenter().x, this.getBoundingBox().getCenter().y, this.getBoundingBox().getCenter().z, 0.0D, 0.0D, 0.0D);
			this.setPos(this.getX() + vector3d.x, this.getY() + vector3d.y, this.getZ() + vector3d.z);
		} else {
			this.remove();
		}
	}

	@Override
	protected void onHitEntity(EntityRayTraceResult raytrace) {
		super.onHitEntity(raytrace); //this seems to be on the right track, but we also need a manual raytrace to get a full list of entities in the next delta, just in case the projectile is moving too fast

		if (!level.isClientSide) {
			Entity target = raytrace.getEntity();
			entityHitProcess(target);
		}

		if (shouldCollateral) {
			//put some code here for the manual raytrace
			//the raytrace needs to be from current position to delta from last known position
			Set<Entity> entities = new HashSet<>();
			AxisAlignedBB bb = new AxisAlignedBB(this.blockPosition());
			bb.minmax(this.getBoundingBox());

			Vector3d incPosition = new Vector3d(this.getDeltaMovement().x / (bulletSpeed * 10), this.getDeltaMovement().y / (bulletSpeed * 10), this.getDeltaMovement().z / (bulletSpeed * 10));

			//the raytrace is really just a bunch of steps for boundary boxes.  this means accelerator makes sniper collateral further
			for (double i = 0; i < this.bulletSpeed; i += 0.1) {
				bb = bb.move(incPosition);

				List<Entity> nextEntities = this.level.getEntities(this, bb);

				//don't bother adding entities to the list that are already there.
				entities.addAll(nextEntities);

				//kill trace early if we hit a tile doing this, so it doesn't trace through walls.
				BlockPos someBlockPos = new BlockPos(bb.getCenter());
				BlockState someBlockState = this.level.getBlockState(someBlockPos);

				//stop processing if we hit a solid block
				if (someBlockState.getMaterial().blocksMotion()) break;
			}

			//because the sniper cannot have a projectile ignore invulnerability anyway, this is safe to do.
			for (Entity entity : entities) {
				if (!(entity instanceof PlayerEntity) && (entity instanceof LivingEntity)) entityHitProcess(entity);
			}
		}
		else if ((raytrace.getEntity() != this.getOwner()) && (this.getOwner() != null))
		{
			this.remove();
		}
	}

	@Override
	protected void onHitBlock(BlockRayTraceResult raytrace) {
		//make a spherical poof and a sound
		this.level.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.impact, SoundCategory.VOICE, 0.25f, (random.nextFloat() * 0.5f) + 0.75f);
		double d0 = raytrace.getLocation().x();
		double d1 = raytrace.getLocation().y() + (this.getBoundingBox().getYsize() / 2);
		double d2 = raytrace.getLocation().z();
		this.level.addParticle(ParticleTypes.POOF, d0, d1, d2, 0.0D, 0.0D, 0.0D);

		if (KGConfig.griefEnabled.get() && !level.isClientSide) {
			if (shouldBreakBlock) {
				//test if the block is of the right tool type to mine with.
				//we could not guarantee the projectile ended up inside the block on this tick, so let's add some mathematics to work around that

				BlockPos blockPositionToMine = raytrace.getBlockPos();
				ItemStack newTool;

				if (this.getDamage() > KGConfig.mineGunFifthLevel.get()) {
					newTool = new ItemStack(Items.DIAMOND_PICKAXE);
					tryBreakBlock(blockPositionToMine, newTool);
					newTool = new ItemStack(Items.DIAMOND_AXE);
					tryBreakBlock(blockPositionToMine, newTool);
					newTool = new ItemStack(Items.DIAMOND_SHOVEL);
					tryBreakBlock(blockPositionToMine, newTool);
					newTool = new ItemStack(Items.SHEARS);
					tryBreakBlock(blockPositionToMine, newTool);
					breakWeakBlocks(blockPositionToMine);
				} else if (this.getDamage() > KGConfig.mineGunFourthLevel.get()) {
					newTool = new ItemStack(Items.IRON_PICKAXE);
					tryBreakBlock(blockPositionToMine, newTool);
					newTool = new ItemStack(Items.IRON_AXE);
					tryBreakBlock(blockPositionToMine, newTool);
					newTool = new ItemStack(Items.IRON_SHOVEL);
					tryBreakBlock(blockPositionToMine, newTool);
					newTool = new ItemStack(Items.SHEARS);
					tryBreakBlock(blockPositionToMine, newTool);
					breakWeakBlocks(blockPositionToMine);
				} else if (this.getDamage() > KGConfig.mineGunThirdLevel.get()) {
					newTool = new ItemStack(Items.STONE_PICKAXE);
					tryBreakBlock(blockPositionToMine, newTool);
					newTool = new ItemStack(Items.STONE_AXE);
					tryBreakBlock(blockPositionToMine, newTool);
					newTool = new ItemStack(Items.STONE_SHOVEL);
					tryBreakBlock(blockPositionToMine, newTool);
					breakWeakBlocks(blockPositionToMine);
				} else if (this.getDamage() > KGConfig.mineGunSecondLevel.get()) {
					newTool = new ItemStack(Items.WOODEN_PICKAXE);
					tryBreakBlock(blockPositionToMine, newTool);
					newTool = new ItemStack(Items.WOODEN_AXE);
					tryBreakBlock(blockPositionToMine, newTool);
					newTool = new ItemStack(Items.WOODEN_SHOVEL);
					tryBreakBlock(blockPositionToMine, newTool);
					breakWeakBlocks(blockPositionToMine);
				} else {
					breakWeakBlocks(blockPositionToMine);
				}

				//don't do corruption stuff if the block wasn't successfully mined
				if ((isCorrupted) && (level.getBlockState(blockPositionToMine).getBlock() == Blocks.AIR) && KGConfig.netheriteMinegunCorruptBlock.get()) {
					level.setBlock(blockPositionToMine, Blocks.NETHERRACK.defaultBlockState(), 1);
					//don't place fire if something is above current block
					if (isOnFire() &&
							(random.nextDouble() < KGConfig.netheriteMinegunIgnitionChance.get()) &&
							(level.getBlockState(blockPositionToMine.above(1)).getBlock() == Blocks.AIR)) {
						level.setBlock(blockPositionToMine.above(1), Blocks.FIRE.defaultBlockState(), 3);
					}
				}
			}

			if (shootsLights) {
				Block blockToChange = level.getBlockState(raytrace.getBlockPos()).getBlock();
				if (blockToChange.getLightValue(level.getBlockState(raytrace.getBlockPos()), level, raytrace.getBlockPos()) > 0) {
					level.destroyBlock(raytrace.getBlockPos(), false);
				}
			}

			if (shouldShatterBlocks) {

			}

			if (shouldBreakDoors) {
				Block blockToChange = level.getBlockState(raytrace.getBlockPos()).getBlock();
				//break wooden doors
				if (BlockTags.WOODEN_DOORS.getValues().contains(blockToChange) ||
						BlockTags.WOODEN_TRAPDOORS.getValues().contains(blockToChange) ||
						BlockTags.FENCE_GATES.getValues().contains(blockToChange) ||
						BlockTags.FENCES.getValues().contains(blockToChange) ||
						BlockTags.ICE.getValues().contains(blockToChange) ||
						blockToChange instanceof GlassBlock ||
						blockToChange instanceof StainedGlassBlock ||
						blockToChange instanceof PaneBlock ||
						blockToChange == Blocks.IRON_DOOR ||
						blockToChange == Blocks.IRON_TRAPDOOR ||
						blockToChange == Blocks.HAY_BLOCK ||
						blockToChange == Blocks.HONEY_BLOCK ||
						blockToChange == Blocks.SLIME_BLOCK) {
					level.destroyBlock(raytrace.getBlockPos(), false);
				}
			}
		}

		this.remove();
	}

	protected void breakWeakBlocks(BlockPos blockPosToTest) {
		if (!level.getBlockState(blockPosToTest).requiresCorrectToolForDrops()) {
			breakBlock(blockPosToTest);
		}
	}

	protected void entityHitProcess(Entity entity) {
		Entity shooter = getOwner();
		IBullet bullet = (IBullet) getItemRaw().getItem();

		//get health of the victim before they get hit.
		if (entity instanceof LivingEntity) {
			LivingEntity victim = (LivingEntity) entity;
			healthOfVictim = victim.getHealth();

			if (shouldGlow) {
				victim.addEffect(new EffectInstance(Effects.GLOWING, KGConfig.enemyGlowTicks.get(), 0));
			}
		} else healthOfVictim = 0.0f;

		if (shooter != null) {
			if (healsFriendlies && (entity instanceof LivingEntity)) {
				if ((entity.getClassification(true) == EntityClassification.CREATURE) ||
						(entity.getClassification(true) == EntityClassification.WATER_CREATURE) ||
						checkIsSameTeam(shooter, entity)) {
					//heal by how much the bullet would end up as damage
					LivingEntity target = (LivingEntity) entity;
					target.heal((float)(bullet.modifyDamage(this.damage, this, target, shooter, this.level) * KGConfig.defenderRifleHealRatio.get()));
					//add some particle and sound effect here.
				}
				else giveDamage(shooter, entity, bullet);
			}
			else if (!checkIsSameTeam(shooter, entity)) giveDamage(shooter, entity, bullet);
		}
		else {
			giveDamage(null, entity, bullet);
		}
	}

	protected void giveDamage(Entity shooter, Entity victim, IBullet bullet) {
		if (victim.isOnFire()) lavaMode += 0x02;
		if (isOnFire()) victim.setSecondsOnFire(5);
		int lastHurtResistant = victim.invulnerableTime;
		if (ignoreInvulnerability) victim.invulnerableTime = 0;

		Vector3d previousDelta = victim.getDeltaMovement();
		if (victim instanceof LivingEntity) {
			healthOfVictim = ((LivingEntity)victim).getHealth();
		}

		boolean damaged = victim.hurt((new IndirectEntityDamageSource("arrow", this, shooter)).setProjectile(), (float) bullet.modifyDamage(damage, this, victim, shooter, level));

		if (isClean) victim.setDeltaMovement(previousDelta);
		else if (damaged && victim instanceof LivingEntity) {
			LivingEntity livingTarget = (LivingEntity) victim;

			double actualKnockback;
			if (this.shootingGun != null) {
				if (this.shootingGun.getItem() == ModItems.doubleBarrelShotgun) {
					actualKnockback = knockbackStrength / ticksSinceFired;

					Vector3d vec = getDeltaMovement().multiply(1, 0.25, 1).normalize().scale(actualKnockback);
					livingTarget.push(vec.x, vec.y, vec.z);
				}

				if (this.shootingGun.getItem() instanceof GatlingItem) {
					Vector3d vec = getDeltaMovement().multiply(1, 0, 1).normalize().scale(0.02);
					livingTarget.push(vec.x, vec.y, vec.z);

					Vector3d newMovement = livingTarget.getDeltaMovement();
					//this should cancel the vertical knockback done by vanilla's damage event by default
					if (livingTarget.isOnGround()) livingTarget.setDeltaMovement(newMovement.x, 0, newMovement.z);
				}
			}

			//force an overwrite of vertical velocity if the gun can juggle
			if (juggle) {
				Vector3d vel = livingTarget.getDeltaMovement();
				livingTarget.setDeltaMovement(new Vector3d(vel.x, 0.4, vel.z));
			}

			bullet.onLivingEntityHit(this, livingTarget, shooter, level);
		} else if (!damaged && ignoreInvulnerability) victim.invulnerableTime = lastHurtResistant;
	}

	@Override
	protected void onHit(RayTraceResult result) {
		//explode or damage?
		if (isExplosive && !level.isClientSide) {
			explode(result.getLocation());
		}
		//damage should try to hurt tiles and entities without using an explosion, so it will need to fire this super.
		else super.onHit(result);

		//remove will be present inside onHitBlock instead
		if (!level.isClientSide && !shouldCollateral) {
			remove();
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

	public void explode(Vector3d position) {
		float newRadius = (float)this.getShootingGun().damageMultiplier;

		level.explode(this, position.x, position.y, position.z, newRadius, isOnFire(), KGConfig.explosionsEnabled.get() ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
		if (isWither) {
			newRadius *= KGConfig.witherLauncherEffectRadiusMultiplier.get();
			AxisAlignedBB witherTrace = new AxisAlignedBB(position.x - newRadius, position.y - newRadius, position.z - newRadius, position.x + newRadius, position.y + newRadius, position.z + newRadius);
			List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class, witherTrace);

			for (LivingEntity mob : entities) {
				if (getOwner() != null) if (!checkIsSameTeam(getOwner(), mob)) mob.addEffect(new EffectInstance(Effects.WITHER, 200, 1));
			}
		}

		remove();
	}

	protected void tryBreakBlock(BlockPos blockPosToTest, ItemStack stack) {
		//test if the tool tier found works
		if (ForgeHooks.isToolEffective(this.level, blockPosToTest, stack)) {
			breakBlock(blockPosToTest);
		}
	}

	protected void breakBlock(BlockPos blockToBreak) {
		//drop the block in a fixed chance
		Random random = new Random();
		if (mineChance - random.nextDouble() > 0) this.level.destroyBlock(blockToBreak, true);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("tsf", ticksSinceFired);
		compound.putDouble("damage", damage);
		compound.putBoolean("explosive", isExplosive);
		compound.putBoolean("collateral", shouldCollateral);
		//compound.putBoolean("isPlasma", isPlasma);
		if (ignoreInvulnerability) compound.putBoolean("ignoreinv", ignoreInvulnerability);
		if (knockbackStrength != 0) compound.putDouble("knockback", knockbackStrength);
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		ticksSinceFired = compound.getInt("tsf");
		damage = compound.getDouble("damage");
		isExplosive = compound.getBoolean("explosive");
		shouldCollateral = compound.getBoolean("collateral");
		//isPlasma = compound.getBoolean("isPlasma");
		//The docs says if it's not here it's gonna be false/0 so it should be good
		ignoreInvulnerability = compound.getBoolean("ignoreinv");
		knockbackStrength = compound.getDouble("knockback");
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getDamage() {
		return damage;
	}

	public double getInaccuracy() {
		return inaccuracy;
	}

	public void setHealthRewardChance(double rewardChance) {
		this.healthRewardChance = rewardChance;
	}

	public float getPreviousHealthOfVictim() {
		return healthOfVictim;
	}

	public void setShouldBreakBlock(boolean breakBlock) {
		this.shouldBreakBlock = breakBlock;
	}

	public boolean rollRewardChance() {
		Random random = new Random();
		return (healthRewardChance - random.nextDouble()) > 0.0D;
	}

	public void setInaccuracy(double inaccuracy) {
		this.inaccuracy = inaccuracy;
	}

	public void setBulletSpeed(double speed) {
		this.bulletSpeed = speed;
	}

	public void setIgnoreInvulnerability(boolean ignoreInvulnerability) {
		//quick workaround, always make it ignore invulnerability.
		this.ignoreInvulnerability = ignoreInvulnerability;
	}

	public void setShouldCollateral(boolean collateral) {
		this.shouldCollateral = collateral;
	}

	public void setShouldGlow(boolean glow) {
		this.shouldGlow = glow;
	}

	public void setShootingGun(GunItem gun) {
		this.shootingGun = gun;
	}

	public void setExplosive(boolean explosive) {
		this.isExplosive = explosive;
	}

	public GunItem getShootingGun() {
		return this.shootingGun;
	}

	public void setOrigin(Vector3d shooterOrigin) { this.origin = shooterOrigin; }

	public Vector3d getOrigin() { return this.origin; }

	/**
	 * Knockback on impact, 0.6 is equivalent to Punch I.
	 */
	public void setKnockbackStrength(double knockbackStrength) {
		this.knockbackStrength = knockbackStrength;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	protected boolean shouldBurn() {
		return false;
	}

	@Override
	protected float getInertia() {
		return 1;
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkUtils.getProjectileSpawnPacket(this);
	}
}
