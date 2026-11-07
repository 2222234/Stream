package com.mobius.stream.Resource.Implement;

import com.mobius.stream.Game.Animate.AnimMap;
import com.mobius.stream.Game.Entity.Attribute.Attribute;
import com.mobius.stream.Game.Entity.Attribute.EntityType;
import com.mobius.stream.Game.Entity.Attribute.FireAttribute;
import com.mobius.stream.Game.Entity.Attribute.NameType;
import com.mobius.stream.Game.Entity.EntityState;
import com.mobius.stream.Game.Entity.Interface.FireMode;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletChain;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.SpawnBulletRequest;
import com.mobius.stream.Game.Object.GunFactory.ChainContext;
import com.mobius.stream.Resource.Anim;
import com.mobius.stream.Resource.Object.Bullet.DropperBullet;

import static com.mobius.stream.Game.Entity.EntityFactory.fixTarget;

public class ComputerAttribute {
    public static final Attribute attribute = new Attribute(NameType.Computer, EntityType.Enemy,
        2000, new FireAttribute(0.25f, 2f, 5f, 100f),
        72, 88, 144, 176);
    public static final AnimMap animMap = new AnimMap().put(EntityState.IDLE, Anim.Boss_STAND);
    public static final FireMode firemode1 = (context, x, y, targetRad) -> {
        SpawnBulletRequest spawnBulletRequest = context.signalPool().obtain(SpawnBulletRequest.class);
        BulletChain bulletChain = chainContext1().buildChain();
        spawnBulletRequest.set(BulletModel.Team.Enemy, bulletChain, bulletChain.bullet, x, y, targetRad, 1f, 1f);
        context.bpsController().pushSpawnBulletRequest(spawnBulletRequest);
    };
    public static final FireMode firemode2 = (context, x, y, targetRad) -> {
        SpawnBulletRequest spawnBulletRequest = context.signalPool().obtain(SpawnBulletRequest.class);
        BulletChain bulletChain = chainContext2().buildChain();
        spawnBulletRequest.set(BulletModel.Team.Enemy, bulletChain, bulletChain.bullet, x, y, fixTarget(targetRad), 1f, 1f);
        context.bpsController().pushSpawnBulletRequest(spawnBulletRequest);
    };

    public static ChainContext chainContext1() {
        ChainContext chainContext = new ChainContext(null, new SplitterNowBullet());
        chainContext.setChain(new ChainContext(chainContext, new SpirteBullet()), 0);
        chainContext.setChain(new ChainContext(chainContext, new SpirteBullet()), 1);
        chainContext.setChain(new ChainContext(chainContext, new SpirteBullet()), 2);
        chainContext.setChain(new ChainContext(chainContext, new SpirteBullet()), 3);
        chainContext.setChain(new ChainContext(chainContext, new SpirteBullet()), 4);
        chainContext.setChain(new ChainContext(chainContext, new SpirteBullet()), 5);
        chainContext.setChain(new ChainContext(chainContext, new SpirteBullet()), 6);
        chainContext.setChain(new ChainContext(chainContext, new SpirteBullet()), 7);
        return chainContext;
    }

    public static ChainContext chainContext2() {
        ChainContext chainContext = new ChainContext(null, new DropperBullet());
        chainContext.setChain(new ChainContext(chainContext, new LaserBullet()), 0);
        chainContext.setChain(new ChainContext(chainContext, new LaserBullet()), 1);
        chainContext.setChain(new ChainContext(chainContext, new LaserBullet()), 2);
        chainContext.setChain(new ChainContext(chainContext, new LaserBullet()), 3);
        chainContext.setChain(new ChainContext(chainContext, new LaserBullet()), 4);
        chainContext.setChain(new ChainContext(chainContext, new LaserBullet()), 5);
        chainContext.setChain(new ChainContext(chainContext, new LaserBullet()), 6);
        chainContext.setChain(new ChainContext(chainContext, new LaserBullet()), 7);
        return chainContext;
    }
}
