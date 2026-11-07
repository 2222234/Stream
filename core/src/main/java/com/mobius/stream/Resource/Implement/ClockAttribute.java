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

public class ClockAttribute {
    public static final Attribute attribute = new Attribute(NameType.Clock, EntityType.Enemy,
        1000, new FireAttribute(0.25f, 2f, 5f, 100f),
        48, 40, 96, 80);
    public static final AnimMap animMap = new AnimMap().put(EntityState.IDLE, Anim.Clock_STAND);
    public static final FireMode firemode = (context, x, y, targetRad) -> {
        SpawnBulletRequest spawnBulletRequest = context.signalPool().obtain(SpawnBulletRequest.class);
        BulletChain bulletChain = chainContext().buildChain();
        spawnBulletRequest.set(BulletModel.Team.Enemy, bulletChain, bulletChain.bullet, x, y, targetRad, 1f, 1f);
        context.bpsController().pushSpawnBulletRequest(spawnBulletRequest);
    };

    public static ChainContext chainContext() {
        ChainContext chainContext = new ChainContext(null, new SplitterNowBullet());
        chainContext.setChain(new ChainContext(chainContext, new PointerBullet()), 0);
        chainContext.setChain(new ChainContext(chainContext, new PointerBullet()), 1);
        chainContext.setChain(new ChainContext(chainContext, new PointerBullet()), 2);
        chainContext.setChain(new ChainContext(chainContext, new PointerBullet()), 3);
        chainContext.setChain(new ChainContext(chainContext, new PointerBullet()), 4);
        chainContext.setChain(new ChainContext(chainContext, new PointerBullet()), 5);
        chainContext.setChain(new ChainContext(chainContext, new PointerBullet()), 6);
        chainContext.setChain(new ChainContext(chainContext, new PointerBullet()), 7);
        return chainContext;
    }
}
