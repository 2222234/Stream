package com.mobius.stream.Resource.Player;

import com.mobius.stream.Game.Animate.AnimMap;
import com.mobius.stream.Game.Animate.StateLogic;
import com.mobius.stream.Game.Entity.Attribute.Attribute;
import com.mobius.stream.Game.Entity.Attribute.EntityType;
import com.mobius.stream.Game.Entity.Attribute.FireAttribute;
import com.mobius.stream.Game.Entity.Attribute.NameType;
import com.mobius.stream.Game.Entity.EntityState;
import com.mobius.stream.Game.Entity.Interface.FireMode;
import com.mobius.stream.Game.Event.Game.Audio.PlaySoundEvent;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.Bullet;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletChain;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.SpawnBulletRequest;
import com.mobius.stream.Resource.Anim;
import com.mobius.stream.Resource.AudioRecord;
import com.mobius.stream.Resource.Object.Bullet.NormalBullet;

public class PlayerAttribute {
    public static final Attribute attribute = new Attribute(
        NameType.Dude, EntityType.Player, 100, new FireAttribute(0.1f, 1f, -1f, -1f), 16, 27, 32, 54);

    public static final AnimMap animMap = new AnimMap()
        .put(EntityState.IDLE, Anim.DUDE_STAND_RIGHT, Anim.DUDE_STAND_LEFT)
        .put(EntityState.MOVE, Anim.DUDE_MOVE_RIGHT, Anim.DUDE_MOVE_LEFT)
        .put(EntityState.HOLDING, Anim.DUDE_HOLDING_RIGHT, Anim.DUDE_HOLDING_LEFT);

    public static final StateLogic logic = (delta, self, current) -> {
        EntityState entityState = EntityState.MOVE;
        if (self.getHolding() != 0) entityState = EntityState.HOLDING;
        if (self.getMoveInput().x == 0) entityState = EntityState.IDLE;
        return entityState;
    };

    public static final FireMode fireMode1 = (context, x, y, targetRad) -> {
        SpawnBulletRequest spawnBulletRequest = context.signalPool().obtain(SpawnBulletRequest.class);
        BulletChain bulletChain = context.playerData().bulletChain1;
        Bullet bullet = null;
        if (context.playerData().weapon1 != null) {
            bulletChain = context.playerData().weapon1.buildChain();
            context.playerData().bulletChain1 = bulletChain;
            bullet = bulletChain.bullet;
        } else {
            bullet = new NormalBullet().getBullet();
        }
        if (bullet.type == BulletModel.Type.Bullet) {
            context.signal().dispatch(new PlaySoundEvent(AudioRecord.Bullet, 0.5f, 0.5f, 0));
        }
        if (bullet.type == BulletModel.Type.Laser) {
            context.signal().dispatch(new PlaySoundEvent(AudioRecord.Laser, 0.5f, 0.5f, 0));
        }
        spawnBulletRequest.set(BulletModel.Team.Player, bulletChain, bullet, x, y, targetRad, 1f, 1f);
        context.bpsController().pushSpawnBulletRequest(spawnBulletRequest);
    };

    public static final FireMode fireMode2 = (context, x, y, targetRad) -> {
        SpawnBulletRequest spawnBulletRequest = context.signalPool().obtain(SpawnBulletRequest.class);
        BulletChain bulletChain = context.playerData().bulletChain2;
        Bullet bullet = null;
        if (context.playerData().weapon2 != null) {
            bulletChain = context.playerData().weapon2.buildChain();
            context.playerData().bulletChain2 = bulletChain;
            bullet = bulletChain.bullet;
        } else {
            bullet = new NormalBullet().getBullet();
        }
        if (bullet.type == BulletModel.Type.Bullet) {
            context.signal().dispatch(new PlaySoundEvent(AudioRecord.Bullet, 0.5f, 0.5f, 0));
        }
        if (bullet.type == BulletModel.Type.Laser) {
            context.signal().dispatch(new PlaySoundEvent(AudioRecord.Laser, 0.5f, 0.5f, 0));
        }
        spawnBulletRequest.set(BulletModel.Team.Player, bulletChain, bullet, x, y, targetRad, 1f, 1f);
        context.bpsController().pushSpawnBulletRequest(spawnBulletRequest);
    };
}
