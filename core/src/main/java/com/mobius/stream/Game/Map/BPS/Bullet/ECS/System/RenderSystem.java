package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.TransformComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.BulletComponent;
import com.mobius.stream.Resource.BP.BulletRegion;

import static com.mobius.stream.Resource.MapRecord.toMeters;

public class RenderSystem extends IteratingSystem {
    private final ComponentMapper<TransformComponent> tcm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<BulletComponent> bcm = ComponentMapper.getFor(BulletComponent.class);

    private final SpriteBatch batch;
    private final TransformDrawable[] drawables;
    private int lastRegion = -1;
    private TransformDrawable currentDrawable;

    public RenderSystem(SpriteBatch batch, TextureAtlas atlas) {
        super(Family.all(BulletComponent.class, TransformComponent.class).get());
        this.batch = batch;
        drawables = new TransformDrawable[BulletRegion.values().length];
        for (int i = 0; i < drawables.length; i++) {
            TextureRegion region = atlas.findRegion(BulletRegion.values()[i].name());
            if (!BulletRegion.values()[i].isLaser) {
                drawables[i] = new TextureRegionDrawable(region);
            } else {
                NinePatch ninePatch = new NinePatch(region, 1, 1, 1, 1);
                ninePatch.scale(1 / 16f, 1 / 16f);
                drawables[i] = new NinePatchDrawable(ninePatch);
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity bullet, float delta) {
        BulletComponent bc = bcm.get(bullet);
        TransformComponent tc = tcm.get(bullet);

        int regionIdx = bc.bullet.region.ordinal();
        if (regionIdx != lastRegion) {
            currentDrawable = drawables[regionIdx];
            lastRegion = regionIdx;
        }

        boolean isLaser = bc.bullet.type == BulletModel.Type.Laser;

        float width = isLaser ? bc.endLength : toMeters(currentDrawable.getMinWidth());
        float height = isLaser ? bc.bullet.radius * 2f : toMeters(currentDrawable.getMinHeight());
        float halfHeight = height * 0.5f;
        float originX = isLaser ? 0f : width * 0.5f;

        currentDrawable.draw(batch, tc.pos.x - originX, tc.pos.y - halfHeight, originX, halfHeight, width, height, 1f, 1f, tc.vel.angleDeg());
    }
}
