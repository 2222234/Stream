package com.mobius.stream.Game.Event.Ui;

import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Stage.Component.ToastOverlay;

public class ShowTitleEvent extends GameEvent {
    public final ToastOverlay.Type type;
    public final String title;
    public final float time;

    public ShowTitleEvent(ToastOverlay.Type type, String title, float time) {
        this.type = type;
        this.title = title;
        this.time = time;
    }
}
