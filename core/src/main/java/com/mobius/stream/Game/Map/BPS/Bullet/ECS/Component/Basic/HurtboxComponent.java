package com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic;

import com.badlogic.ashley.core.Component;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;

public class HurtboxComponent implements Component {
    public float radius = 16f;
    public BulletModel.Team team;
}
