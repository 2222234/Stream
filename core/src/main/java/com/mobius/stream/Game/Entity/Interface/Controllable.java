package com.mobius.stream.Game.Entity.Interface;

import com.badlogic.gdx.math.Vector2;

public interface Controllable {
    void setMoveInput(Vector2 moveInput);

    void setTargetInput(float targetRad);

    void fire();

    void setHold(int flag);

    Vector2 getPosition();
}
