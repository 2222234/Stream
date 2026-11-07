package com.mobius.stream.Stage.Component;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mobius.stream.Resource.Anim;
import com.mobius.stream.Resource.Resources;

public class FadeAnimation extends Table {

    private final Animation<TextureRegion> animation;
    boolean playing = false;
    float time;

    public FadeAnimation(Resources resources) {
        animation = resources.getAnimationCache().get(Anim.GOTO);
        setFillParent(true);
        setTouchable(Touchable.disabled);
        setVisible(false);
    }

    public void play(float fadeIn, float fadeOut, Runnable onComplete) {
        clearActions();

        setVisible(true);
        time = 0;
        addAction(Actions.sequence(
            Actions.run(() -> playing = true),
            Actions.fadeIn(fadeIn, Interpolation.fade),
            Actions.delay(animation.getAnimationDuration()),
            Actions.fadeOut(fadeOut, Interpolation.fade),
            Actions.run(() -> {
                setVisible(false);
                if (onComplete != null) onComplete.run();
            })
        ));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (playing) {
            time += delta;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        TextureRegion region = animation.getKeyFrame(time);
        batch.draw(region, getWidth() / 2f, getHeight() / 2f, region.getRegionWidth(), region.getRegionHeight());
    }
}
