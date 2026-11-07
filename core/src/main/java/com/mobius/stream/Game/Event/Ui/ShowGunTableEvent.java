package com.mobius.stream.Game.Event.Ui;

import com.mobius.stream.Game.Event.GameEvent;

public class ShowGunTableEvent extends GameEvent {
    public final boolean visible;

    public ShowGunTableEvent(boolean visible) {
        this.visible = visible;
    }
}
