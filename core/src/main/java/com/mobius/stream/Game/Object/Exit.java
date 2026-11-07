package com.mobius.stream.Game.Object;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Event.Global.ExitGameEvent;
import com.mobius.stream.Game.Event.Global.ReturnTitleEvent;
import com.mobius.stream.Resource.RegionRecord;
import com.mobius.stream.Resource.Resources;
import com.mobius.stream.Resource.TextRecord;
import com.mobius.stream.Stage.Element.UnderLineButton;

public class Exit extends Table {
    public Exit(Resources resources, Signal<GameEvent> signal) {
        setFillParent(true);
        setVisible(false);
        setBackground(new TextureRegionDrawable(resources.getAtlas().findRegion(RegionRecord.White)).tint(new Color(0.5f, 0.5f, 0.5f, 0.5f)));

        UnderLineButton title = new UnderLineButton(resources, resources.getString(TextRecord.Title_Return), 0.15f, () -> {
            signal.dispatch(new ReturnTitleEvent());
        });

        add(title).row();
        UnderLineButton exit = new UnderLineButton(resources, resources.getString(TextRecord.Title_Exit), 0.15f, () -> {
            signal.dispatch(new ExitGameEvent());
        });
        add(exit);
    }
}
