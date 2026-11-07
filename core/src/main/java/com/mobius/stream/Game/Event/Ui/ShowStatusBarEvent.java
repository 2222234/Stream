package com.mobius.stream.Game.Event.Ui;

import com.mobius.stream.Game.Event.GameEvent;

public class ShowStatusBarEvent extends GameEvent {
    public final boolean visible;

    public ShowStatusBarEvent(boolean visible) {
        this.visible = visible;
    }
}
