package com.mobius.stream.Game.Event.Game.Map;

import com.badlogic.gdx.math.Rectangle;
import com.mobius.stream.Game.Event.GameEvent;

public class MapUpdated extends GameEvent {
    public final Rectangle bounds;
    public final boolean hasBounds;

    public MapUpdated(Rectangle bounds, boolean hasBounds) {
        this.bounds = bounds;
        this.hasBounds = hasBounds;
    }
}
