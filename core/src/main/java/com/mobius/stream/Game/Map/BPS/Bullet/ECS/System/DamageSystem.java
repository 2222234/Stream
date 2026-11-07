package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.utils.Array;
import com.mobius.stream.Game.Event.Game.Audio.PlaySoundEvent;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.HurtboxComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.HealthComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.HitEvent;
import com.mobius.stream.Resource.AudioRecord;

public class DamageSystem extends EntitySystem {
    private final Array<HitEvent> hitQueue;
    private final Signal<GameEvent> signal;

    private final ComponentMapper<HurtboxComponent> hbcm = ComponentMapper.getFor(HurtboxComponent.class);
    private final ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);

    public DamageSystem(Array<HitEvent> hitQueue, Signal<GameEvent> signal) {
        this.hitQueue = hitQueue;
        this.signal = signal;
    }

    @Override
    public void update(float delta) {
        hitQueue.iterator().forEach(hitEvent -> {
            Entity target = hitEvent.target;
            HealthComponent hc = hcm.get(target);
            HurtboxComponent hbc = hbcm.get(target);
            if (hc != null && !hc.dead) {
                hc.hp -= hitEvent.amount;
                if (hc.hp <= 0f) {
                    hc.hp = 0f;
                    hc.dead = true;
                }
                if (hbc.team == BulletModel.Team.Player) {
                    signal.dispatch(new PlaySoundEvent(AudioRecord.Hit, 0.5f, 0.5f, 0));
                }
            }
        });
    }
}
