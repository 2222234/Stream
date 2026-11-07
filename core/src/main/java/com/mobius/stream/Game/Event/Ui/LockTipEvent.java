package com.mobius.stream.Game.Event.Ui;

import com.mobius.stream.Game.Event.GameEvent;

public class LockTipEvent extends GameEvent {
    public final boolean lock;

    public LockTipEvent(boolean lock) {
        this.lock = lock;
    }
}
