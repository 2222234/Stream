package com.mobius.stream.Game.Entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.mobius.stream.Game.Animate.AnimMap;
import com.mobius.stream.Game.Animate.StateLogic;
import com.mobius.stream.Game.Context;
import com.mobius.stream.Game.Entity.Attribute.Attribute;
import com.mobius.stream.Game.Entity.Attribute.EntityType;
import com.mobius.stream.Game.Entity.Attribute.FireAttribute;
import com.mobius.stream.Game.Entity.Attribute.NameType;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.Bullet;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.SpawnBulletRequest;
import com.mobius.stream.Game.Physic.BodyFactory;
import com.mobius.stream.Resource.Anim;
import com.mobius.stream.Resource.BP.BulletRegion;
import com.mobius.stream.Resource.BP.EmitterConfig;
import com.mobius.stream.Resource.Implement.ClockAttribute;
import com.mobius.stream.Resource.Implement.ComputerAttribute;
import com.mobius.stream.Resource.Implement.CookAttribute;
import com.mobius.stream.Resource.Implement.HatAttribute;
import com.mobius.stream.Resource.Player.PlayerAttribute;
import com.mobius.stream.Resource.RegionRecord;

public class EntityFactory {
    public static EntityActor createEntity(Context context, Attribute attribute, StateLogic stateLogic, AnimMap animMap, EntityState entityState, float x, float y) {
        Body body = BodyFactory.buildBody(attribute, context.physicService().getWorld(), x, y);
        return new EntityActor(context, attribute, stateLogic, animMap, entityState, body);
    }

    public static EntityActor createPlayer(Context context, float x, float y) {
        EntityActor entityActor = createEntity(context, PlayerAttribute.attribute, PlayerAttribute.logic, PlayerAttribute.animMap, EntityState.MOVE, x, y);
        entityActor.addFireMode(null, null);
        entityActor.addFireMode(PlayerAttribute.fireMode1, context.resources().getAtlas().findRegion(RegionRecord.Gun1));
        entityActor.addFireMode(PlayerAttribute.fireMode2, context.resources().getAtlas().findRegion(RegionRecord.Gun2));
        return entityActor;
    }

    public static EntityActor createClock(Context context, float x, float y) {
        EntityActor entityActor = createEntity(context, ClockAttribute.attribute,
            (delta, self, current) -> EntityState.IDLE,
            ClockAttribute.animMap, EntityState.MOVE
            , x, y);
        entityActor.addFireMode(ClockAttribute.firemode, null);
        return entityActor;
    }

    public static EntityActor createHat(Context context, float x, float y) {
        EntityActor entityActor = createEntity(context, HatAttribute.attribute,
            (delta, self, current) -> EntityState.IDLE,
            HatAttribute.animMap, EntityState.MOVE
            , x, y);
        entityActor.addFireMode(HatAttribute.firemode1, null);
        entityActor.addFireMode(HatAttribute.firemode2, null);
        return entityActor;
    }

    public static EntityActor createCook(Context context, float x, float y) {
        EntityActor entityActor = createEntity(context, CookAttribute.attribute,
            (delta, self, current) -> EntityState.IDLE,
            CookAttribute.animMap, EntityState.MOVE
            , x, y);
        entityActor.addFireMode(CookAttribute.firemode, null);
        return entityActor;
    }

    public static EntityActor createComputer(Context context, float x, float y) {
        EntityActor entityActor = createEntity(context, ComputerAttribute.attribute,
            (delta, self, current) -> EntityState.IDLE,
            ComputerAttribute.animMap, EntityState.MOVE
            , x, y);
        entityActor.addFireMode(ComputerAttribute.firemode1, null);
        entityActor.addFireMode(ComputerAttribute.firemode2, null);
        return entityActor;
    }

    public static Bullet bulletReflect() {
        Bullet bullet = new Bullet(BulletRegion.Bullet, BulletModel.Type.Bullet);
        bullet.gravity = true;
        bullet.speed = 16f;
        bullet.radius = 0.5f;
        bullet.emit = (fxEvent, tc, bc, lc) -> {
            fxEvent.emitterConfig = EmitterConfig.FireSpark;
            fxEvent.x = tc.pos.x;
            fxEvent.y = tc.pos.y;
            fxEvent.direction = (float) Math.PI + tc.vel.angleRad();
            bc.emitElapsed = 0;
            return fxEvent;
        };
        bullet.collision = (bulletComponent) -> BulletModel.WallMode.Reflect;
        return bullet;
    }

    public static Bullet bulletTicket() {
        Bullet bullet = new Bullet(BulletRegion.Ticket_Bullet, BulletModel.Type.Bullet);
        bullet.speed = 8f;
        bullet.radius = 0.5f;
        bullet.damage = 1f;
        bullet.collision = (bulletComponent) -> null;
        return bullet;
    }

    public static EntityActor createTicket(Context context, float x, float y) {
        EntityActor entityActor = createEntity(context,
            new Attribute(NameType.Ticket, EntityType.Enemy, 100, new FireAttribute(0.5f, 2f, 5f, 10f), 16, 16, 32, 64),
            (delta, self, current) -> EntityState.IDLE,
            new AnimMap().put(EntityState.IDLE, Anim.Ticket_STAND), EntityState.IDLE
            , x, y);
        entityActor.addFireMode((context1, x1, y1, targetRad) -> {
            SpawnBulletRequest spawnBulletRequest = context.signalPool().obtain(SpawnBulletRequest.class);
            spawnBulletRequest.set(BulletModel.Team.Enemy, null, bulletTicket(), x1, y1, targetRad, 1f, 1f);
            context.bpsController().pushSpawnBulletRequest(spawnBulletRequest);
        }, null);
        return entityActor;
    }

    public static Bullet bulletWheel() {
        Bullet bullet = new Bullet(BulletRegion.Wheel_Bullet, BulletModel.Type.Bullet);
        bullet.gravity = true;
        bullet.speed = 8f;
        bullet.radius = 0.5f;
        bullet.damage = 1f;
        bullet.collision = (bulletComponent) -> BulletModel.WallMode.Reflect;
        return bullet;
    }

    public static EntityActor createTrain(Context context, float x, float y) {
        EntityActor entityActor = createEntity(context,
            new Attribute(NameType.Train, EntityType.Enemy, 100, new FireAttribute(0.5f, 2f, 5f, 10f), 48, 16, 96, 32),
            (delta, self, current) -> EntityState.IDLE,
            new AnimMap().put(EntityState.IDLE, Anim.Train_STAND), EntityState.IDLE
            , x, y);
        entityActor.addFireMode((context1, x1, y1, targetRad) -> {
            SpawnBulletRequest spawnBulletRequest = context.signalPool().obtain(SpawnBulletRequest.class);
            spawnBulletRequest.set(BulletModel.Team.Enemy, null, bulletWheel(), x1, y1, fixTarget(targetRad), 1f, 1f);
            context.bpsController().pushSpawnBulletRequest(spawnBulletRequest);
        }, null);
        return entityActor;
    }

    public static Bullet bulletGear() {
        Bullet bullet = new Bullet(BulletRegion.Gear_Bullet, BulletModel.Type.Bullet);
        bullet.gravity = true;
        bullet.speed = 8f;
        bullet.radius = 0.5f;
        bullet.damage = 1f;
        bullet.collision = (bulletComponent) -> BulletModel.WallMode.Reflect;
        return bullet;
    }

    public static EntityActor createStair(Context context, float x, float y) {
        EntityActor entityActor = createEntity(context,
            new Attribute(NameType.Stair, EntityType.Enemy, 150, new FireAttribute(0.5f, 2f, 5f, 10f), 16, 16, 64, 64),
            (delta, self, current) -> EntityState.IDLE,
            new AnimMap().put(EntityState.IDLE, Anim.Stair_STAND), EntityState.IDLE
            , x, y);
        entityActor.addFireMode((context1, x1, y1, targetRad) -> {
            SpawnBulletRequest spawnBulletRequest = context.signalPool().obtain(SpawnBulletRequest.class);
            spawnBulletRequest.set(BulletModel.Team.Enemy, null, bulletGear(), x1, y1, fixTarget(targetRad), 1f, 1f);
            context.bpsController().pushSpawnBulletRequest(spawnBulletRequest);
        }, null);
        return entityActor;
    }

    public static EntityActor createPos(Context context, float x, float y) {
        EntityActor entityActor = createEntity(context,
            new Attribute(NameType.Pos, EntityType.Enemy, 150, new FireAttribute(0.5f, 2f, 5f, 10f), 16, 16, 64, 64),
            (delta, self, current) -> EntityState.IDLE,
            new AnimMap().put(EntityState.IDLE, Anim.Pos_STAND), EntityState.IDLE
            , x, y);
        entityActor.addFireMode((context1, x1, y1, targetRad) -> {
            SpawnBulletRequest spawnBulletRequest = context.signalPool().obtain(SpawnBulletRequest.class);
            spawnBulletRequest.set(BulletModel.Team.Enemy, null, bulletTicket(), x1, y1, targetRad, 1f, 1f);
            context.bpsController().pushSpawnBulletRequest(spawnBulletRequest);
        }, null);
        return entityActor;
    }

    public static Bullet bulletTrash() {
        Bullet bullet = new Bullet(BulletRegion.Trash_Bullet, BulletModel.Type.Bomb);
        bullet.gravity = true;
        bullet.speed = 8f;
        bullet.radius = 2f;
        bullet.damage = 5f;
        bullet.collision = (bulletComponent) -> BulletModel.WallMode.Reflect;
        return bullet;
    }

    public static EntityActor createCan(Context context, float x, float y) {
        EntityActor entityActor = createEntity(context,
            new Attribute(NameType.Can, EntityType.Enemy, 200, new FireAttribute(0.5f, 2f, 5f, 10f), 16, 16, 64, 64),
            (delta, self, current) -> EntityState.IDLE,
            new AnimMap().put(EntityState.IDLE, Anim.Can_STAND), EntityState.IDLE
            , x, y);
        entityActor.addFireMode((context1, x1, y1, targetRad) -> {
            SpawnBulletRequest spawnBulletRequest = context.signalPool().obtain(SpawnBulletRequest.class);
            spawnBulletRequest.set(BulletModel.Team.Enemy, null, bulletTrash(), x1, y1, fixTarget(targetRad), 1f, 1f);
            context.bpsController().pushSpawnBulletRequest(spawnBulletRequest);
        }, null);
        return entityActor;
    }

    public static Bullet bulletMeat() {
        Bullet bullet = new Bullet(BulletRegion.Meat_Bullet, BulletModel.Type.Bomb);
        bullet.gravity = true;
        bullet.speed = 8f;
        bullet.radius = 2f;
        bullet.damage = 5f;
        bullet.collision = (bulletComponent) -> BulletModel.WallMode.Reflect;
        return bullet;
    }

    public static EntityActor createTray(Context context, float x, float y) {
        EntityActor entityActor = createEntity(context,
            new Attribute(NameType.Tray, EntityType.Enemy, 200, new FireAttribute(0.5f, 2f, 5f, 10f), 16, 16, 64, 64),
            (delta, self, current) -> EntityState.IDLE,
            new AnimMap().put(EntityState.IDLE, Anim.Tray_STAND), EntityState.IDLE
            , x, y);
        entityActor.addFireMode((context1, x1, y1, targetRad) -> {
            SpawnBulletRequest spawnBulletRequest = context.signalPool().obtain(SpawnBulletRequest.class);
            spawnBulletRequest.set(BulletModel.Team.Enemy, null, bulletMeat(), x1, y1, fixTarget(targetRad), 1f, 1f);
            context.bpsController().pushSpawnBulletRequest(spawnBulletRequest);
        }, null);
        return entityActor;
    }

    public static float fixTarget(float targetRad) {
        return MathUtils.lerpAngle(targetRad, MathUtils.HALF_PI, 0.5f);
    }
}
