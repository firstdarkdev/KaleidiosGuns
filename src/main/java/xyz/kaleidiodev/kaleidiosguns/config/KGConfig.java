package xyz.kaleidiodev.kaleidiosguns.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class KGConfig {

    public static ForgeConfigSpec spec;

    //Guns
    public static ForgeConfigSpec.DoubleValue ironPistolDamageMultiplier;
    public static ForgeConfigSpec.IntValue ironPistolFireDelay;
    public static ForgeConfigSpec.IntValue ironPistolEnchantability;
    public static ForgeConfigSpec.IntValue ironPistolDurability;
    public static ForgeConfigSpec.DoubleValue ironPistolInaccuracy;
    public static ForgeConfigSpec.DoubleValue ironPistolProjectileSpeed;

    public static ForgeConfigSpec.DoubleValue diamondShotgunDamageMultiplier;
    public static ForgeConfigSpec.IntValue diamondShotgunFireDelay;
    public static ForgeConfigSpec.IntValue diamondShotgunEnchantability;
    public static ForgeConfigSpec.IntValue diamondShotgunDurability;
    public static ForgeConfigSpec.DoubleValue diamondShotgunInaccuracy;
    public static ForgeConfigSpec.IntValue diamondShotgunBulletCount;
    public static ForgeConfigSpec.DoubleValue diamondShotgunProjectileSpeed;

    public static ForgeConfigSpec.DoubleValue goldDoubleShotgunDamageMultiplier;
    public static ForgeConfigSpec.IntValue goldDoubleShotgunFireDelay;
    public static ForgeConfigSpec.IntValue goldDoubleShotgunEnchantability;
    public static ForgeConfigSpec.IntValue goldDoubleShotgunDurability;
    public static ForgeConfigSpec.DoubleValue goldDoubleShotgunInaccuracy;
    public static ForgeConfigSpec.IntValue goldDoubleShotgunBulletCount;
    public static ForgeConfigSpec.DoubleValue goldDoubleShotgunProjectileSpeed;

    public static ForgeConfigSpec.DoubleValue diamondSniperDamageMultiplier;
    public static ForgeConfigSpec.IntValue diamondSniperFireDelay;
    public static ForgeConfigSpec.IntValue diamondSniperEnchantability;
    public static ForgeConfigSpec.IntValue diamondSniperDurability;
    public static ForgeConfigSpec.DoubleValue diamondSniperInaccuracy;
    public static ForgeConfigSpec.DoubleValue diamondSniperProjectileSpeed;

    public static ForgeConfigSpec.DoubleValue ironCarbineDamageMultiplier;
    public static ForgeConfigSpec.IntValue ironCarbineFireDelay;
    public static ForgeConfigSpec.IntValue ironCarbineEnchantability;
    public static ForgeConfigSpec.IntValue ironCarbineDurability;
    public static ForgeConfigSpec.DoubleValue ironCarbineInaccuracy;
    public static ForgeConfigSpec.DoubleValue ironCarbineProjectileSpeed;

    public static ForgeConfigSpec.DoubleValue diamondSmgDamageMultiplier;
    public static ForgeConfigSpec.IntValue diamondSmgFireDelay;
    public static ForgeConfigSpec.IntValue diamondSmgEnchantability;
    public static ForgeConfigSpec.IntValue diamondSmgDurability;
    public static ForgeConfigSpec.DoubleValue diamondSmgInaccuracy;
    public static ForgeConfigSpec.DoubleValue diamondSmgProjectileSpeed;
    public static ForgeConfigSpec.DoubleValue diamondSmgMineChance;

    public static ForgeConfigSpec.DoubleValue ironAssaultDamageMultiplier;
    public static ForgeConfigSpec.IntValue ironAssaultFireDelay;
    public static ForgeConfigSpec.IntValue ironAssaultEnchantability;
    public static ForgeConfigSpec.IntValue ironAssaultDurability;
    public static ForgeConfigSpec.DoubleValue ironAssaultInaccuracy;
    public static ForgeConfigSpec.DoubleValue ironAssaultProjectileSpeed;

    public static ForgeConfigSpec.DoubleValue goldStreamDamageMultiplier;
    public static ForgeConfigSpec.IntValue goldStreamFireDelay;
    public static ForgeConfigSpec.IntValue goldStreamEnchantability;
    public static ForgeConfigSpec.IntValue goldStreamDurability;
    public static ForgeConfigSpec.DoubleValue goldStreamInaccuracy;
    public static ForgeConfigSpec.DoubleValue goldStreamProjectileSpeed;

    //Bullets
    public static ForgeConfigSpec.IntValue flintBulletDamage;
    public static ForgeConfigSpec.IntValue ironBulletDamage;
    public static ForgeConfigSpec.IntValue blazeBulletDamage;
    public static ForgeConfigSpec.IntValue hungerBulletDamage;

    //Minegun balance curve
    public static ForgeConfigSpec.DoubleValue mineGunSecondLevel;
    public static ForgeConfigSpec.DoubleValue mineGunThirdLevel;
    public static ForgeConfigSpec.DoubleValue mineGunFourthLevel;
    public static ForgeConfigSpec.DoubleValue mineGunFifthLevel;

    public static ForgeConfigSpec.DoubleValue impactDamageIncrease;
    public static ForgeConfigSpec.DoubleValue bullseyeAccuracyIncrease;
    public static ForgeConfigSpec.DoubleValue sleightOfHandFireRateDecrease;
    public static ForgeConfigSpec.DoubleValue preservingRateIncrease;
    public static ForgeConfigSpec.DoubleValue acceleratorSpeedIncrease;
    public static ForgeConfigSpec.DoubleValue passionForBloodRateIncrease;
    public static ForgeConfigSpec.DoubleValue passionForBloodHealIncrease;
    public static ForgeConfigSpec.IntValue divisionCountIncrease;


    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Kaleidio's Guns Config");
        builder.comment("Please note that the below options may effect the balance, as usually values cap at 100%.  higher values may not work as expected, or the curve of balance may flatten at the top.");

        builder.push("pistols");
        ironPistolDamageMultiplier = builder
                .comment("Define the Damage multiplier for Pistols")
                .defineInRange("ironPistolDamageModifier", 1D, 0.1D, 5D);
        ironPistolFireDelay = builder
                .comment("Configure the Fire delay for Pistols")
                .defineInRange("ironPistolFireDelay", 12, 0, 72000);
        ironPistolEnchantability = builder
                .comment("Configure the Enchantability for Pistols")
                .defineInRange("ironPistolEnchantability", 14, 0, 30);
        ironPistolDurability = builder
                .comment("Configure the Durability for Pistols")
                .defineInRange("ironPistolDurability", 4000, 0, 32767);
        ironPistolInaccuracy = builder
                .comment("Configure the Inaccuracy for Pistols")
                .defineInRange("ironPistolInaccuracy", 2.5D, 0D, 90D);
        ironPistolProjectileSpeed = builder
                .comment("Configure the Projectile Speed for Pistols")
                .defineInRange("ironPistolProjectileSpeed", 8D, 0D, 64D);
        builder.pop();

        builder.push("shotguns");
        diamondShotgunDamageMultiplier = builder
                .comment("Define the Damage multiplier for Shotguns")
                .defineInRange("diamondShotgunDamageMultiplier", 0.4D, 0.1D, 5D);
        diamondShotgunFireDelay = builder
                .comment("Configure the Fire delay for Shotguns")
                .defineInRange("diamondShotgunFireDelay", 24, 0, 72000);
        diamondShotgunEnchantability = builder
                .comment("Configure the Enchantability for Shotguns")
                .defineInRange("diamondShotgunEnchantability", 10, 0, 30);
        diamondShotgunDurability = builder
                .comment("Configure the Durability for Shotguns")
                .defineInRange("diamondShotgunDurability", 667, 0, 32767);
        diamondShotgunInaccuracy = builder
                .comment("Configure the Inaccuracy for Shotguns")
                .defineInRange("diamondShotgunInaccuracy", 7.5D, 0D, 90D);
        diamondShotgunBulletCount = builder
                .comment("Configure the amount of Bullets at once for Shotguns")
                .defineInRange("diamondShotgunBulletCount", 5, 0, 50);
        diamondShotgunProjectileSpeed = builder
                .comment("Configure the Projectile Speed for Shotguns")
                .defineInRange("diamondShotgunProjectileSpeed", 8D, 0D, 64D);

        goldDoubleShotgunDamageMultiplier = builder
                .comment("Define the Damage multiplier for Double Barrel Shotguns")
                .defineInRange("goldDoubleShotgunDamageMultiplier", 0.4D, 0.1D, 5D);
        goldDoubleShotgunFireDelay = builder
                .comment("Configure the Fire delay for Double Barrel Shotguns")
                .defineInRange("goldDoubleShotgunFireDelay", 60, 0, 72000);
        goldDoubleShotgunEnchantability = builder
                .comment("Configure the Enchantability for Double Barrel Shotguns")
                .defineInRange("goldDoubleShotgunEnchantability", 23, 0, 30);
        goldDoubleShotgunDurability = builder
                .comment("Configure the Durability for Double Barrel Shotguns")
                .defineInRange("goldDoubleShotgunDurability", 267, 0, 32767);
        goldDoubleShotgunInaccuracy = builder
                .comment("Configure the Inaccuracy for Double Barrel Shotguns")
                .defineInRange("goldDoubleShotgunInaccuracy", 12.5D, 0D, 90D);
        goldDoubleShotgunBulletCount = builder
                .comment("Configure the amount of Bullets at once for Double Barrel Shotguns")
                .defineInRange("goldDoubleShotgunBulletCount", 5, 0, 100);
        goldDoubleShotgunProjectileSpeed = builder
                .comment("Configure the Projectile Speed for Double Barrel Shotguns")
                .defineInRange("goldDoubleShotgunProjectileSpeed", 8D, 0D, 64D);
        builder.pop();

        builder.push("rifles");
        diamondSniperDamageMultiplier = builder
                .comment("Define the Damage multiplier for Snipers")
                .defineInRange("diamondSniperDamageMultiplier", 2.5D, 0.1D, 5D);
        diamondSniperFireDelay = builder
                .comment("Configure the Fire delay for Snipers")
                .defineInRange("diamondSniperFireDelay", 48, 0, 72000);
        diamondSniperEnchantability = builder
                .comment("Configure the Enchantability for Snipers")
                .defineInRange("diamondSniperEnchantability", 10, 0, 30);
        diamondSniperDurability = builder
                .comment("Configure the Durability for Snipers")
                .defineInRange("diamondSniperDurability", 400, 0, 32767);
        diamondSniperInaccuracy = builder
                .comment("Configure the Inaccuracy for Snipers")
                .defineInRange("diamondSniperInaccuracy", 0D, 0D, 90D);
        diamondSniperProjectileSpeed = builder
                .comment("Configure the Projectile Speed for Snipers")
                .defineInRange("diamondSniperProjectileSpeed", 16D, 0D, 64D);

        ironCarbineDamageMultiplier = builder
                .comment("Define the Damage multiplier for Carbines")
                .defineInRange("ironCarbineDamageMultiplier", 1.5D, 0.5D, 5D);
        ironCarbineFireDelay = builder
                .comment("Configure the Fire delay for Carbines")
                .defineInRange("ironCarbineFireDelay", 20, 0, 72000);
        ironCarbineEnchantability = builder
                .comment("Configure the Enchantability for Carbines")
                .defineInRange("ironCarbineEnchantability", 14, 0, 30);
        ironCarbineDurability = builder
                .comment("Configure the Durability for Carbines")
                .defineInRange("ironCarbineDurability", 1600, 0, 32767);
        ironCarbineInaccuracy = builder
                .comment("Configure the Inaccuracy for Carbines")
                .defineInRange("ironCarbineInaccuracy", 0D, 0D, 90D);
        ironCarbineProjectileSpeed = builder
                .comment("Configure the Projectile Speed for Carbines")
                .defineInRange("ironCarbineProjectileSpeed", 12D, 0D, 64D);
        builder.pop();

        builder.push("automatics");
        diamondSmgDamageMultiplier = builder
                .comment("Define the Damage multiplier for Mineguns")
                .defineInRange("diamondSmgDamageMultiplier", 0.5D, 0.1D, 5D);
        diamondSmgFireDelay = builder
                .comment("Configure the Fire delay for Mineguns")
                .defineInRange("diamondSmgFireDelay", 10, 0, 72000);
        diamondSmgEnchantability = builder
                .comment("Configure the Enchantability for Mineguns")
                .defineInRange("diamondSmgEnchantability", 10, 0, 30);
        diamondSmgDurability = builder
                .comment("Configure the Durability for Mineguns")
                .defineInRange("diamondSmgDurability", 8000, 0, 32767);
        diamondSmgInaccuracy = builder
                .comment("Configure the Inaccuracy for Mineguns")
                .defineInRange("diamondSmgInaccuracy", 2D, 0D, 90D);
        diamondSmgProjectileSpeed = builder
                .comment("Configure the Projectile Speed for Mineguns")
                .defineInRange("diamondSmgProjectileSpeed", 2.5D, 0D, 64D);
        diamondSmgMineChance = builder
                .comment("Configure the Break Block Chance for Mineguns")
                .defineInRange("diamondSmgMineChance", 0.33D, 0D, 1D);

        ironAssaultDamageMultiplier = builder
                .comment("Define the Damage multiplier for Assault Rifles")
                .defineInRange("ironAssaultDamageMultiplier", 0.5D, 0.1D, 5D);
        ironAssaultFireDelay = builder
                .comment("Configure the Fire delay for Assault Rifles")
                .defineInRange("ironAssaultFireDelay", 4, 0, 72000);
        ironAssaultEnchantability = builder
                .comment("Configure the Enchantability for Assault Rifles")
                .defineInRange("ironAssaultEnchantability", 14, 0, 30);
        ironAssaultDurability = builder
                .comment("Configure the Durability for Assault Rifles")
                .defineInRange("ironAssaultDurability", 24000, 0, 32767);
        ironAssaultInaccuracy = builder
                .comment("Configure the Inaccuracy for Assault Rifles")
                .defineInRange("ironAssaultInaccuracy", 5.0D, 0D, 90D);
        ironAssaultProjectileSpeed = builder
                .comment("Configure the Projectile Speed for Assault Rifles")
                .defineInRange("ironAssaultProjectileSpeed", 10D, 0D, 64D);

        goldStreamDamageMultiplier = builder
                .comment("Define the Damage multiplier for Plasma Rifles")
                .defineInRange("goldPlasmaDamageMultiplier", 0.75D, 0.1D, 5D);
        goldStreamFireDelay = builder
                .comment("Configure the Fire delay for Plasma Rifles")
                .defineInRange("goldPlasmaFireDelay", 5, 0, 72000);
        goldStreamEnchantability = builder
                .comment("Configure the Enchantability for Plasma Rifles")
                .defineInRange("goldPlasmaEnchantability", 23, 0, 30);
        goldStreamDurability = builder
                .comment("Configure the Durability for Plasma Rifles")
                .defineInRange("goldPlasmaDurability", 8000, 0, 32767);
        goldStreamInaccuracy = builder
                .comment("Configure the Inaccuracy for Plasma Rifles")
                .defineInRange("goldPlasmaInaccuracy", 0D, 0D, 90D);
        goldStreamProjectileSpeed = builder
                .comment("Configure the Projectile Speed for Plasma Rifles")
                .defineInRange("goldPlasmaProjectileSpeed", 2D, 0D, 64D);
        builder.pop();

        builder.push("bullet_config");
        flintBulletDamage = builder
                .comment("Configure the damage of Flint Bullets")
                .defineInRange("flintBulletDamage", 5, 1, 20);
        ironBulletDamage = builder
                .comment("Configure the damage of Iron Bullets")
                .defineInRange("ironBulletDamage", 7, 1, 20);
        blazeBulletDamage = builder
                .comment("Configure the damage of Blaze Bullets")
                .defineInRange("blazeBulletDamage", 8, 1, 20);
        hungerBulletDamage = builder
                .comment("Configure the damage of Hunger Bullets")
                .defineInRange("hungerBulletDamage", 6, 1, 20);
        builder.pop();

        builder.push("minegun_config");
        builder.comment("Also note that the first level, which mines like a hand, is of any value not listed here.");
        mineGunSecondLevel = builder
                .comment("Damage required to mine like a wooden tool")
                .defineInRange("mineGunSecondLevel", 3D, 0.5D, 40D);
        mineGunThirdLevel = builder
                .comment("Damage required to mine like a stone tool")
                .defineInRange("mineGunThirdLevel", 4D, 0.5D, 40D);
        mineGunFourthLevel = builder
                .comment("Damage required to mine like an iron tool")
                .defineInRange("mineGunFourthLevel", 5D, 0.5D, 40D);
        mineGunFifthLevel = builder
                .comment("Damage required to mine like a diamond tool")
                .defineInRange("mineGunFifthLevel", 6D, 0.5D, 40D);
        builder.pop();

        builder.push("enchantments_config");
        preservingRateIncrease = builder
                .comment("How much percentage per preserving level, represented as floating point only")
                .defineInRange("preservingRateIncrease", 0.1D, 0.1D, 1D);
        impactDamageIncrease = builder
                .comment("How much damage increase per impact level")
                .defineInRange("impactDamageIncrease", 1D, 0.1D, 20D);
        bullseyeAccuracyIncrease = builder
                .comment("How much accuracy increase per bullseye level, represented as a division + 1 of base inaccuracy")
                .defineInRange("bullseyeAccuracyIncrease", 0.5D, 0.1D, 5D);
        sleightOfHandFireRateDecrease = builder
                .comment("How much percentage fire rate increase per sleight of hand level, represented as floating point only")
                .defineInRange("sleightOfHandFireRateIncrease", 0.25D, 0.1D, 1D);
        acceleratorSpeedIncrease = builder
                .comment("How much percentage speed increase per accelerator level, represented as multiplication + 1 of base speed")
                .defineInRange("acceleratorSpeedIncrease", 0.25D, 0.1D, 1D);
        passionForBloodRateIncrease = builder
                .comment("How much percentage chance increase per passion for blood level, represented as floating point only")
                .defineInRange("passionForBloodRateIncrease", 0.1D, 0.01D, 1.0D);
        passionForBloodHealIncrease = builder
                .comment("Multiplier for how much healing passion for blood should give in relation to damage delivered")
                .defineInRange("passionForBloodHealIncrease", 0.5D, 0.1D, 5.0D);
        divisionCountIncrease = builder
                .comment("Multiplier for how many extra bullets per division level on shotguns")
                .defineInRange("divisionCountIncrease", 1, 1, 5);
        builder.pop();

        spec = builder.build();
    }

}
