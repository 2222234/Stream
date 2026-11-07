package com.mobius.stream.Game.Event.Ui;

import com.badlogic.gdx.math.Vector2;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Object.TipBox;

public class TipEvent extends GameEvent {
    public final TipBox tipBox;
    public final Vector2 gamePos = new Vector2();
    public final boolean visible;

    public TipEvent(TipBox tipBox, Vector2 gamePos, boolean visible) {
        this.tipBox = tipBox;
        this.gamePos.set(gamePos);
        this.visible = visible;
    }
}
