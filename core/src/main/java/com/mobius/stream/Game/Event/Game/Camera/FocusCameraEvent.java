package com.mobius.stream.Game.Event.Game.Camera;

import com.mobius.stream.Game.Entity.Interface.Controllable;
import com.mobius.stream.Game.Event.GameEvent;

public class FocusCameraEvent extends GameEvent {
    public final Controllable controllable;
    public final float ratio;

    public FocusCameraEvent(Controllable controllable, float ratio) {
        this.controllable = controllable;
        this.ratio = ratio;
    }
}
