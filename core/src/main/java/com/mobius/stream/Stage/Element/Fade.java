package com.mobius.stream.Stage.Element;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Fade extends Table {
    public Fade(float fadeIn, float hold, float fadeOut, Runnable onFinished) {
        getColor().a = 0f;

        SequenceAction seq = Actions.sequence();
        if (fadeIn > 0) seq.addAction(Actions.fadeIn(fadeIn, Interpolation.fade));
        if (hold > 0) seq.addAction(Actions.delay(hold));
        if (fadeOut > 0) seq.addAction(Actions.fadeOut(fadeOut, Interpolation.fade));

        if (onFinished != null) {
            seq.addAction(Actions.run(onFinished));
        }
        addAction(seq);
    }

    public void fadeOut(float fadeOut, Runnable onFinished) {
        clearActions();
        addAction(Actions.sequence(
            Actions.fadeOut(fadeOut, Interpolation.fade),
            Actions.run(onFinished)
        ));
    }
}
