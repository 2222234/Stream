package com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class TransformComponent implements Component {
    public final Vector2 pos = new Vector2();
    public final Vector2 vel = new Vector2();
}
