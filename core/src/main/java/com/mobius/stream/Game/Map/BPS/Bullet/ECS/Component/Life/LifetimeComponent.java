package com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life;

import com.badlogic.ashley.core.Component;

public class LifetimeComponent implements Component {
    public float remaining;

    public LifetimeComponent(float remaining) {
        this.remaining = remaining;
    }
}
