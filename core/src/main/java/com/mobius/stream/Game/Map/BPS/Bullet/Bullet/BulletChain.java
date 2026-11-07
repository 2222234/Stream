package com.mobius.stream.Game.Map.BPS.Bullet.Bullet;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.TransformComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.BulletComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.LifetimeComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.SpawnBulletRequest;

public class BulletChain {
    public final Bullet bullet;
    private final Array<BulletChain> bulletChains = new Array<>();
    private final Array<Trigger> triggers = new Array<>();

    public BulletChain(Bullet bullet) {
        this.bullet = bullet;
    }

    public void addBullet(BulletChain bulletChain, Trigger trigger) {
        bulletChains.add(bulletChain);
        triggers.add(trigger);
    }

    public void update(PoolManager signalPool, Array<SpawnBulletRequest> spawnQueue, BulletComponent bc, TransformComponent tc, LifetimeComponent lc) {
        for (int i = 0; i < triggers.size; i++) {
            Trigger trigger = triggers.get(i);
            if (trigger.fire(bc, lc)) {
                SpawnBulletRequest sbr = signalPool.obtain(SpawnBulletRequest.class);
                trigger.spawn(bc.team, bc.bulletChain.bulletChains.get(i), tc.pos, tc.vel, sbr);
                spawnQueue.add(sbr);
            }
        }
    }
}
