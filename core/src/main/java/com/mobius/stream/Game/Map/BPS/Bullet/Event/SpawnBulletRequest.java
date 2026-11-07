package com.mobius.stream.Game.Map.BPS.Bullet.Event;

import com.badlogic.gdx.utils.Pool;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.Bullet;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletChain;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;

public class SpawnBulletRequest implements Pool.Poolable {
    public BulletModel.Team team;
    public BulletChain bulletChain;
    public Bullet bullet;
    public float x, y;
    public float angle;
    public float speedMul = 1f;
    public float damageMul = 1f;

    public SpawnBulletRequest set(BulletModel.Team team, BulletChain bulletChain, Bullet bullet, float x, float y, float angle, float speedMul, float damageMul) {
        this.team = team;
        this.bulletChain = bulletChain;
        this.bullet = bullet;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speedMul = speedMul;
        this.damageMul = damageMul;
        return this;
    }

    @Override
    public void reset() {
        team = null;
        bullet = null;
        x = y = angle = 0f;
        speedMul = 1f;
        damageMul = 1f;
    }
}
