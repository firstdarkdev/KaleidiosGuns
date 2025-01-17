package xyz.kaleidiodev.kaleidiosguns.event;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.kaleidiodev.kaleidiosguns.KaleidiosGuns;
import xyz.kaleidiodev.kaleidiosguns.entity.BulletEntity;

public class ShootEvent {
    //Event to change add bullet inaccuracy if VivecraftForgeExtension is present
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof BulletEntity) {
            Random random = new Random();
            BulletEntity shot = (BulletEntity)event.getEntity();
            Vector3d direction = shot.getDeltaMovement();
            double velocity = direction.length();

            //TODO: hero wave shotgun mathematics from shot.heroStep goes here
            direction = direction.normalize().add(random.nextGaussian() * 0.0075 * shot.getInaccuracy(), random.nextGaussian() * 0.0075 * shot.getInaccuracy(), random.nextGaussian() * 0.0075 * shot.getInaccuracy()).scale(velocity);

            shot.setDeltaMovement(direction);
            float horizontalDistance = MathHelper.sqrt(direction.x * direction.x + direction.z * direction.z);
            shot.yRot = (float)(MathHelper.atan2(direction.x, direction.z) * (double)(180F / (float)Math.PI));
            shot.xRot = (float)(MathHelper.atan2(direction.y, horizontalDistance) * (double)(180F / (float)Math.PI));
            shot.yRotO = shot.yRot;
            shot.xRotO = shot.xRot;
        }
    }
}
