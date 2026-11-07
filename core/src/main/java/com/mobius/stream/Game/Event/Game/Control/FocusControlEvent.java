package com.mobius.stream.Game.Event.Game.Control;

import com.mobius.stream.Game.Entity.Interface.Controllable;
import com.mobius.stream.Game.Event.GameEvent;

public class FocusControlEvent extends GameEvent {
    public final Controllable controllable;

    public FocusControlEvent(Controllable controllable) {
        this.controllable = controllable;
    }
}
