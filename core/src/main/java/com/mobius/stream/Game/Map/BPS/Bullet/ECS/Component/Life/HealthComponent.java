package com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {
    public float hp = 100f;
    public boolean dead = false;
}
