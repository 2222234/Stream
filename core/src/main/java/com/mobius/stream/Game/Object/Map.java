package com.mobius.stream.Game.Object;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Event.Global.GoToMapEvent;
import com.mobius.stream.Game.Event.Ui.ShowMapEvent;
import com.mobius.stream.Resource.MapRecord;
import com.mobius.stream.Resource.RegionRecord;
import com.mobius.stream.Resource.Resources;

public class Map extends Table {
    public Map(Resources resources, Signal<GameEvent> signal) {
        setFillParent(true);
        setVisible(false);
        setBackground(new TextureRegionDrawable(resources.getAtlas().findRegion(RegionRecord.White)).tint(new Color(0.5f, 0.5f, 0.5f, 0.5f)));

        Image station = new Image(resources.getAtlas().findRegion(RegionRecord.Station));
        station.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                signal.dispatch(new GoToMapEvent(MapRecord.Map.Station));
                signal.dispatch(new ShowMapEvent(false));
            }
        });
        Image mall = new Image(resources.getAtlas().findRegion(RegionRecord.Mall));
        mall.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                signal.dispatch(new GoToMapEvent(MapRecord.Map.Mall));
                signal.dispatch(new ShowMapEvent(false));
            }
        });
        Image school = new Image(resources.getAtlas().findRegion(RegionRecord.School));
        school.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                signal.dispatch(new GoToMapEvent(MapRecord.Map.School));
                signal.dispatch(new ShowMapEvent(false));
            }
        });

        add(station);
        add(mall);
        add(school);
    }
}
