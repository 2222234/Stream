package com.mobius.stream.Game.Map.BPS.Bullet.Bullet;

import com.mobius.stream.Resource.BP.BulletRegion;

public class Bullet {
    public final BulletRegion region;
    public final BulletModel.Type type;

    public float speed = 100f;
    public float damage = 10f;
    public float radius = 0.5f;
    public float life = 2f;
    public float knockbackForce = 2f;

    public Emit emit;
    public Collision collision;

    public float laserLength = 20f;
    public float hitInterval = 0.2f;
    public boolean laserBlockedByWall = true;

    public boolean move = true;
    public boolean gravity = false;

    public Bullet(BulletRegion region, BulletModel.Type type) {
        this.region = region;
        this.type = type;
    }
}
