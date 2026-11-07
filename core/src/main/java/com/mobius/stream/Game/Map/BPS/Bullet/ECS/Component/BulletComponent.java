package com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectSet;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.Bullet;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletChain;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;

public class BulletComponent implements Component {
    public final ObjectSet<Entity> alreadyHit = new ObjectSet<>();
    public Bullet bullet;
    public BulletChain bulletChain;
    public BulletModel.Team team;
    public float damage;
    public float time;

    public float timer;
    public boolean damageApplied;

    public float emitElapsed = 0f;

    public BulletModel.HitType lastHitType;
    public float lastHitNormalX;
    public float lastHitNormalY;

    public float startX, startY;
    public float endLength;
}
