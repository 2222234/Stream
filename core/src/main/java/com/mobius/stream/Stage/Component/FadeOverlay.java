package com.mobius.stream.Stage.Component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mobius.stream.Resource.RegionRecord;
import com.mobius.stream.Resource.Resources;

public class FadeOverlay {
    private final TextureRegion region;

    private float alpha;
    private float from;
    private float to;
    private float elapsed;
    private float duration;
    private boolean animating;

    public FadeOverlay(Resources resources) {
        region = resources.getAtlas().findRegion(RegionRecord.White);

        alpha = 0f;
        from = 0f;
        to = 0f;
    }

    public void fadeIn(float duration) {
        startAnimation(0f, duration);
    }

    public void fadeOut(float duration) {
        startAnimation(1f, duration);
    }

    public void update(float delta) {
        if (!animating) return;

        elapsed += delta;
        if (elapsed >= duration) {
            alpha = to;
            animating = false;
        } else {
            float t = elapsed / duration;
            alpha = from + (to - from) * t;
        }
    }

    public void render(Batch batch, OrthographicCamera camera) {
        if (alpha <= 0f) return;

        batch.begin();
        batch.setColor(0f, 0f, 0f, alpha);
        batch.draw(region, 0f, 0f, camera.viewportWidth, camera.viewportHeight);
        batch.end();
        batch.setColor(Color.WHITE);
    }

    private void startAnimation(float target, float duration) {
        this.from = this.alpha;
        this.to = target;
        this.elapsed = 0f;
        this.duration = Math.max(duration, 0.0001f);
        this.animating = true;
    }

    public enum FadeState {
        FadeIn,
        FadeOut,
    }
}
