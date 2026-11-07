package com.mobius.stream.Game.Event.Ui;

import com.mobius.stream.Game.Event.GameEvent;

public class ShowMapEvent extends GameEvent {
    public final boolean visible;

    public ShowMapEvent(boolean visible) {
        this.visible = visible;
    }
}
