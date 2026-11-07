package com.mobius.stream.Game.Object;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mobius.stream.Resource.RegionRecord;
import com.mobius.stream.Resource.Resources;
import com.mobius.stream.Resource.ResourcesRecord;

public class Tip extends Table {
    private final float changeTime = 0.5f;
    private final Image image;
    private final Label label;
    private final Stage gameStage;
    private final Vector2 gamePos = new Vector2();
    private final Vector2 temp = new Vector2();
    private final Resources resources;
    boolean lock;
    private float current;
    private TipBox tipBox;

    public Tip(Resources resources, Stage gameStage) {
        this.resources = resources;
        this.gameStage = gameStage;
        label = new Label("", resources.getSkin(), ResourcesRecord.Mid_Font);
        add(label).padBottom(1).row();
        image = new Image(resources.getSkin().getDrawable(RegionRecord.UnderLine));
        add(image);
        setVisible(false);
    }

    public void set(TipBox tipBox, Vector2 gamePos, boolean visible) {
        if (lock) return;
        if (visible) {
            this.tipBox = tipBox;
            this.gamePos.set(gamePos);
            setVisible(true);
            label.setText(resources.getString(tipBox.getShow()));
        } else {
            if (this.tipBox == tipBox) {
                setVisible(false);
            }
        }
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public void refresh() {
        if (tipBox != null) label.setText(resources.getString(tipBox.getShow()));
    }

    public void clear(TipBox tipBox) {
        if (tipBox == null || this.tipBox == tipBox) {
            this.tipBox = null;
            setVisible(false);
        }
    }

    @Override
    public void act(float delta) {
        temp.set(gamePos);
        gameStage.getViewport().project(temp);
        setPosition(temp.x, temp.y);
        current = Math.min(current + delta / changeTime, 1);
        image.setWidth(MathUtils.lerp(0, label.getWidth(), MathUtils.sin(current * MathUtils.PI)));
        image.setX(label.getWidth() / 2 - image.getWidth() / 2);
    }
}
