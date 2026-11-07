package com.mobius.stream.Game.Event.Ui;

import com.mobius.stream.Game.Event.GameEvent;

public class ShowGoToEvent extends GameEvent {
    public final float fadeIn;
    public final float fadeOut;
    public final Runnable afterShow;

    public ShowGoToEvent(float fadeIn, float fadeOut, Runnable afterShow) {
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.afterShow = afterShow;
    }
}
