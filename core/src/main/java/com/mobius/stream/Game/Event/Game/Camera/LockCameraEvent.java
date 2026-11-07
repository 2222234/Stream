package com.mobius.stream.Game.Event.Game.Camera;

import com.badlogic.gdx.math.Vector2;
import com.mobius.stream.Game.Event.GameEvent;

public class LockCameraEvent extends GameEvent {
    public final Vector2 target;

    public LockCameraEvent(Vector2 target) {
        this.target = target;
    }
}
