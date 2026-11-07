package com.mobius.stream.Game.Map.BPS.Bullet;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Game.Entity.EntityActor;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.HurtboxComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.TransformComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.EntityActorComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.HealthComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.System.*;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.HitEvent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.SpawnBulletRequest;
import com.mobius.stream.Game.Map.BPS.Bullet.Tool.SpatialHashGrid;
import com.mobius.stream.Game.Map.BPS.Bullet.Tool.WallGrid;
import com.mobius.stream.Game.Map.BPS.Particle.FxEvent;

import java.util.HashMap;

public class BulletECS {
    private final Engine engine;
    private final WallGrid wallGrid;
    private final SpatialHashGrid<Entity> spatialGrid;
    private final Array<SpawnBulletRequest> spawnQueue;
    private final SpriteBatch batch;
    private final ShapeRenderer debug;
    private final Rectangle rectangle = new Rectangle();
    private final HashMap<EntityActor, Entity> entityMap;

    public BulletECS(TextureAtlas textureAtlas, PoolManager signalPool, Signal<GameEvent> signal, Array<FxEvent> fxQueue) {
        batch = new SpriteBatch();
        debug = new ShapeRenderer();
        engine = new Engine();
        entityMap = new HashMap<>();

        wallGrid = new WallGrid();

        spatialGrid = new SpatialHashGrid<>(1f);

        spawnQueue = new Array<>();
        Array<HitEvent> hitQueue = new Array<>();

        engine.addSystem(new SyncBeforeSystem());
        engine.addSystem(new LifetimeSystem());

        engine.addSystem(new BulletMovementSystem(wallGrid, signalPool, fxQueue));
        engine.addSystem(new ExplosionSystem(spatialGrid, signalPool, hitQueue, fxQueue));
        engine.addSystem(new LaserSystem(spatialGrid, wallGrid, signalPool, hitQueue));
        engine.addSystem(new BulletSystem(spatialGrid, signalPool, hitQueue));
        engine.addSystem(new DamageSystem(hitQueue, signal));
        engine.addSystem(new BulletChainSystem(signalPool, spawnQueue));
        engine.addSystem(new BulletSpawnSystem(spawnQueue));

        engine.addSystem(new BulletParticleSystem(signalPool, fxQueue));

        engine.addSystem(new RenderSystem(batch, textureAtlas));
//        engine.addSystem(new DebugRenderSystem(debug, rectangle, wallGrid, spatialGrid));

        engine.addSystem(new SyncAfterSystem(signalPool, hitQueue));
        engine.addSystem(new DeathSystem());
        engine.addSystem(new PoolSystem(signalPool, spawnQueue, hitQueue));
    }

    private void updateVisible(OrthographicCamera camera) {
        float x1 = camera.frustum.planePoints[0].x;
        float y1 = camera.frustum.planePoints[0].y;
        float x2 = camera.frustum.planePoints[2].x;
        float y2 = camera.frustum.planePoints[2].y;
        rectangle.set(x1, y1, x2 - x1, y2 - y1);
    }

    public void changeMap(TiledMap tiledMap) {
        wallGrid.loadMap(tiledMap);
    }

    public Array<SpawnBulletRequest> getSpawnQueue() {
        return spawnQueue;
    }

    public void render(OrthographicCamera camera) {
        float delta = Gdx.graphics.getDeltaTime();

        spatialGrid.clear();
        for (Entity e : engine.getEntitiesFor(Family.all(HurtboxComponent.class, TransformComponent.class).get())) {
            TransformComponent t = e.getComponent(TransformComponent.class);
            spatialGrid.insert(t.pos.x, t.pos.y, e);
        }

        updateVisible(camera);
        batch.setProjectionMatrix(camera.combined);
        debug.setProjectionMatrix(camera.combined);

        engine.update(delta);
    }

    public Entity createPlayer(EntityActor entityActor) {
        Entity entity = createEntity(entityActor);
        HurtboxComponent hbc = new HurtboxComponent();
        hbc.radius = 0.5f;
        hbc.team = BulletModel.Team.Player;
        entity.add(hbc);
        engine.addEntity(entity);
        return entity;
    }

    public Entity createEnemy(EntityActor entityActor) {
        Entity entity = createEntity(entityActor);
        HurtboxComponent hbc = new HurtboxComponent();
        hbc.radius = 0.5f;
        hbc.team = BulletModel.Team.Enemy;
        entity.add(hbc);
        engine.addEntity(entity);
        return entity;
    }

    private Entity createEntity(EntityActor entityActor) {
        Entity entity = engine.createEntity();
        EntityActorComponent eac = new EntityActorComponent();
        eac.entityActor = entityActor;
        entity.add(eac);
        HealthComponent hc = new HealthComponent();
        hc.hp = entityActor.getAttribute().blood();
        entity.add(hc);
        TransformComponent tc = new TransformComponent();
        tc.pos.set(entityActor.getPosition());
        entity.add(tc);
        entityMap.put(entityActor, entity);
        return entity;
    }

    public void destroyEntity(EntityActor entityActor) {
        engine.removeEntity(entityMap.get(entityActor));
        entityMap.remove(entityActor);
    }

    public void dispose() {
        batch.dispose();
        debug.dispose();
    }
}
