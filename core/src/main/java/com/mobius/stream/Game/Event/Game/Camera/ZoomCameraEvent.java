package com.mobius.stream.Game.Event.Game.Camera;

import com.mobius.stream.Game.Event.GameEvent;

public class ZoomCameraEvent extends GameEvent {
    public final float zoom;

    public ZoomCameraEvent(float zoom) {
        this.zoom = zoom;
    }
}
