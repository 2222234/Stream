package com.mobius.stream.Resource.Object.Box;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mobius.stream.Game.Context;
import com.mobius.stream.Game.Event.Ui.ClearTipBoxEvent;
import com.mobius.stream.Game.Map.BPS.Particle.FxEvent;
import com.mobius.stream.Game.Object.TipBox;
import com.mobius.stream.Resource.BP.EmitterConfig;
import com.mobius.stream.Resource.RegionRecord;
import com.mobius.stream.Resource.TextRecord;

import static com.mobius.stream.Resource.MapRecord.toMeters;

public class EarPhone1 extends TipBox {
    private final float duration = 0.2f;
    private float time;

    public EarPhone1(Context context, Rectangle rect) {
        super(context, rect);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        time += delta;
        if (time >= duration) {
            FxEvent fxEvent = context.signalPool().obtain(FxEvent.class);
            fxEvent.emitterConfig = EmitterConfig.Tip;
            fxEvent.x = getX();
            fxEvent.y = getY();
            context.bpsController().pushFxEvent(fxEvent);
            time = 0;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion region = context.resources().getAtlas().findRegion(RegionRecord.Headphones);
        float width = toMeters(region.getRegionWidth());
        float height = toMeters(region.getRegionHeight());
        batch.draw(region, getX(), getY(),
            0, 0,
            width, height,
            1f, 1f,
            getRotation());
    }

    @Override
    public String getShow() {
        return TextRecord.Text_Pick;
    }

    @Override
    public boolean create() {
        return true;
    }

    @Override
    public void interact(Type type) {
        if (type == Type.Use) {
            context.signal().dispatch(new ClearTipBoxEvent(this));
            context.physicService().destroy(body);
            remove();
        }
    }
}
