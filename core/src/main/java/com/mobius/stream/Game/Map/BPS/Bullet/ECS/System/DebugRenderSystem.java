package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.HurtboxComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.TransformComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.Tool.SpatialHashGrid;
import com.mobius.stream.Game.Map.BPS.Bullet.Tool.WallGrid;

public class DebugRenderSystem extends IteratingSystem {
    private final ShapeRenderer shapeRenderer;
    private final Rectangle rectangle;
    private final WallGrid wallGrid;
    private final SpatialHashGrid<Entity> spatialGrid;

    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<HurtboxComponent> hm = ComponentMapper.getFor(HurtboxComponent.class);

    public DebugRenderSystem(ShapeRenderer shapeRenderer, Rectangle rectangle, WallGrid wallGrid, SpatialHashGrid<Entity> spatialGrid) {
        super(Family.all(TransformComponent.class, HurtboxComponent.class).get());
        this.shapeRenderer = shapeRenderer;
        this.rectangle = rectangle;
        this.wallGrid = wallGrid;
        this.spatialGrid = spatialGrid;
    }

    @Override
    public void update(float deltaTime) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        super.update(deltaTime);

        shapeRenderer.setColor(Color.BLUE);
        boolean[][] wall = wallGrid.getSolid();
        for (int x = 0; x < wallGrid.getWidth(); x++) {
            for (int y = 0; y < wallGrid.getHeight(); y++) {
                if (wall[x][y]) {
                    shapeRenderer.rect(x, y, 1, 1);
                }
            }
        }

        spatialGrid.debugDrawGrid(shapeRenderer, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        shapeRenderer.setColor(Color.YELLOW);
        spatialGrid.debugDrawOccupied(shapeRenderer, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        shapeRenderer.end();
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        TransformComponent tc = tm.get(entity);
        HurtboxComponent hc = hm.get(entity);

        shapeRenderer.circle(tc.pos.x, tc.pos.y, hc.radius);
    }
}
