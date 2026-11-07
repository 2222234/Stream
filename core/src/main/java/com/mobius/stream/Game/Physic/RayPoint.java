package com.mobius.stream.Game.Physic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;

public record RayPoint(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
}
