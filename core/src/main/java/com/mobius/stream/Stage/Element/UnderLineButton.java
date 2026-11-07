package com.mobius.stream.Stage.Element;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Null;
import com.mobius.stream.Resource.RegionRecord;
import com.mobius.stream.Resource.Resources;
import com.mobius.stream.Resource.ResourcesRecord;

public class UnderLineButton extends Table {
    private final float changeTime;
    private final Image image;
    private final Label label;
    private State target = State.Waiting;
    private float start;
    private float current;
    private boolean cover;

    public UnderLineButton(Resources resources, String text, float changeTime, Runnable runnable) {
        this.changeTime = changeTime;
        label = new Label(text, resources.getSkin(), ResourcesRecord.Mid_Font);
        add(label).padBottom(1).row();
        image = new Image(resources.getSkin().getDrawable(RegionRecord.UnderLine));
        add(image);
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                setState(State.Select, false);
                cover = isOver();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, @Null Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                setState(State.Waiting, false);
                cover = isOver();
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setState(State.Click, true);
                runnable.run();
            }
        });
    }

    public void setState(State state, boolean isClick) {
        if (!isClick && target == State.Click) return;
        target = state;
        current = 0;
        start = image.getWidth();
    }

    @Override
    public void act(float delta) {
        current = Math.min(current + delta / changeTime, 1);
        image.setWidth(MathUtils.lerp(start, label.getWidth() * target.progress, Interpolation.smooth.apply(current)));
        image.setX(label.getWidth() / 2 - image.getWidth() / 2);
        if (current == 1 && target == State.Click) {
            setState(cover ? State.Select : State.Waiting, true);
        }
    }

    public enum State {
        Select(0.8f),
        Waiting(0.5f),
        Click(1.0f);
        final float progress;

        State(float progress) {
            this.progress = progress;
        }
    }
}
