package com.mobius.stream.Game.Event.Ui;

import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Object.TipBox;

public class ClearTipBoxEvent extends GameEvent {
    public final TipBox tipBox;

    public ClearTipBoxEvent(final TipBox tipBox) {
        this.tipBox = tipBox;
    }
}
