package com.mobius.stream.Resource.Object.Bullet;

import com.badlogic.gdx.math.Vector2;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.Bullet;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletChain;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.Trigger;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.BulletComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.LifetimeComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.SpawnBulletRequest;
import com.mobius.stream.Game.Object.GunFactory.BulletContext;
import com.mobius.stream.Resource.BP.BulletRegion;
import com.mobius.stream.Resource.BP.EmitterConfig;

public class DropperBullet extends BulletContext {
    public DropperBullet() {
        super(BulletEnum.Dropper);
    }

    @Override
    public Bullet getBullet() {
        Bullet bullet = new Bullet(BulletRegion.Bullet_Dropper, BulletModel.Type.Bullet);
        bullet.gravity = true;
        bullet.life = 5f;
        bullet.speed = 25f;
        bullet.radius = 0.5f;
        bullet.emit = (fxEvent, tc, bc, lc) -> {
            fxEvent.emitterConfig = EmitterConfig.FireSpark;
            fxEvent.x = tc.pos.x;
            fxEvent.y = tc.pos.y;
            fxEvent.direction = (float) Math.PI + tc.vel.angleRad();
            bc.emitElapsed = 0;
            return fxEvent;
        };
        bullet.collision = bc -> null;
        return bullet;
    }

    @Override
    public Trigger getTrigger(int index) {
        return new Trigger() {
            boolean fired = false;

            @Override
            public boolean fire(BulletComponent bc, LifetimeComponent lc) {
                if (!fired) {
                    if (bc.time > (index + 1) * 0.1f) {
                        fired = true;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void spawn(BulletModel.Team team, BulletChain bulletChain, Vector2 pos, Vector2 vel, SpawnBulletRequest sbr) {
                sbr.set(team, bulletChain, bulletChain.bullet, pos.x, pos.y, vel.angleRad(), 1, 1);
            }
        };
    }
}
