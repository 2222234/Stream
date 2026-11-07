package com.mobius.stream.Resource.Object.Show;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mobius.stream.Resource.RegionRecord;
import com.mobius.stream.Resource.Resources;

import static com.mobius.stream.Resource.MapRecord.toMeters;

public class ClockShow extends Actor {
    private final Resources resources;

    public ClockShow(Resources resources) {
        this.resources = resources;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frame = resources.getAtlas().findRegion(RegionRecord.Clock);
        if (frame == null) return;
        float width = toMeters(frame.getRegionWidth() / 4f);
        float height = toMeters(frame.getRegionHeight() / 4f);
        batch.draw(frame,
            getX() - width / 2f, getY() - width / 2f,
            0, 0,
            width, height,
            1f, 1f,
            getRotation());
    }
}
