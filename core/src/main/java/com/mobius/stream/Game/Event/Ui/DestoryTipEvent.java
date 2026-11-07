package com.mobius.stream.Game.Event.Ui;

import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Object.TipBox;

public class DestoryTipEvent extends GameEvent {
    public final TipBox tipBox;

    public DestoryTipEvent(TipBox tipBox) {
        this.tipBox = tipBox;
    }
}
