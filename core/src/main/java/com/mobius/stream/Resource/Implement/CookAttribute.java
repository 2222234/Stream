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

import static com.mobius.stream.Game.Entity.EntityFactory.fixTarget;

public class CookAttribute {
    public static final Attribute attribute = new Attribute(NameType.Cook, EntityType.Enemy,
        1000, new FireAttribute(0.25f, 2f, 5f, 100f),
        48, 48, 96, 96);
    public static final AnimMap animMap = new AnimMap().put(EntityState.IDLE, Anim.Cook_STAND);
    public static final FireMode firemode = (context, x, y, targetRad) -> {
        SpawnBulletRequest spawnBulletRequest = context.signalPool().obtain(SpawnBulletRequest.class);
        BulletChain bulletChain = chainContext().buildChain();
        spawnBulletRequest.set(BulletModel.Team.Enemy, bulletChain, bulletChain.bullet, x, y, fixTarget(targetRad), 1f, 1f);
        context.bpsController().pushSpawnBulletRequest(spawnBulletRequest);
    };

    public static ChainContext chainContext() {
        ChainContext chainContext = new ChainContext(null, new FlourBullet());
        chainContext.setChain(new ChainContext(chainContextIndex(chainContext), new FlourBullet()), 0);
        chainContext.setChain(new ChainContext(chainContextIndex(chainContext), new FlourBullet()), 1);
        chainContext.setChain(new ChainContext(chainContextIndex(chainContext), new FlourBullet()), 2);
        chainContext.setChain(new ChainContext(chainContextIndex(chainContext), new FlourBullet()), 3);
        chainContext.setChain(new ChainContext(chainContextIndex(chainContext), new FlourBullet()), 4);
        chainContext.setChain(new ChainContext(chainContextIndex(chainContext), new FlourBullet()), 5);
        chainContext.setChain(new ChainContext(chainContextIndex(chainContext), new FlourBullet()), 6);
        chainContext.setChain(new ChainContext(chainContextIndex(chainContext), new FlourBullet()), 7);
        return chainContext;
    }

    private static ChainContext chainContextIndex(ChainContext upper) {
        ChainContext chainContext = new ChainContext(upper, new FlourBullet());
        chainContext.setChain(new ChainContext(chainContext, new FlourBullet()), 0);
        chainContext.setChain(new ChainContext(chainContext, new FlourBullet()), 1);
        chainContext.setChain(new ChainContext(chainContext, new FlourBullet()), 2);
        chainContext.setChain(new ChainContext(chainContext, new FlourBullet()), 3);
        chainContext.setChain(new ChainContext(chainContext, new FlourBullet()), 4);
        chainContext.setChain(new ChainContext(chainContext, new FlourBullet()), 5);
        chainContext.setChain(new ChainContext(chainContext, new FlourBullet()), 6);
        chainContext.setChain(new ChainContext(chainContext, new FlourBullet()), 7);
        return chainContext;
    }
}
