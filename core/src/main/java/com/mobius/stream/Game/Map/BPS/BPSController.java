package com.mobius.stream.Game.Map.BPS;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Game.Entity.EntityActor;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Map.BPS.Bullet.BulletECS;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.HitEvent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.SpawnBulletRequest;
import com.mobius.stream.Game.Map.BPS.Particle.FxEvent;
import com.mobius.stream.Game.Map.BPS.Particle.ParticleSystem;
import com.mobius.stream.Game.Object.ParticleBox;
import com.mobius.stream.Resource.BP.Particle;
import com.mobius.stream.Resource.MapRecord;

public class BPSController {
    private final PoolManager signalPool;
    private final ParticleSystem particleSystem;
    private final BulletECS bulletECS;
    private final Array<ParticleBox> particleBoxes;

    public BPSController(PoolManager signalPool, Signal<GameEvent> signal, TextureAtlas textureAtlas) {
        this.signalPool = signalPool;
        signalPool.addPool(FxEvent.class, FxEvent::new);
        signalPool.addPool(SpawnBulletRequest.class, SpawnBulletRequest::new);
        signalPool.addPool(HitEvent.class, HitEvent::new);

        particleSystem = new ParticleSystem(signalPool, textureAtlas);
        bulletECS = new BulletECS(textureAtlas, signalPool, signal, particleSystem.getFxQueue());
        particleBoxes = new Array<>();
    }

    public void update(float delta, OrthographicCamera camera) {
        bulletECS.render(camera);

        particleSystem.update(delta);
        particleSystem.render(camera);

        particleBoxes.iterator().forEach(particleBox -> particleBox.update(delta));
    }

    public void setBounds(Rectangle bounds) {
        particleSystem.setBounds(bounds);
    }

    public void changeMap(TiledMap tiledMap) {
        bulletECS.changeMap(tiledMap);
        refresh(tiledMap);
    }

    private void refresh(TiledMap tiledMap) {
        particleBoxes.clear();
        MapLayer mapLayer = tiledMap.getLayers().get(MapRecord.Layer.Particle.name());
        if (mapLayer == null) return;
        MapObjects objects = mapLayer.getObjects();
        for (MapObject object : objects) {
            if (object instanceof PointMapObject pointMapObject) {
                Vector2 position = pointMapObject.getPoint();
                particleBoxes.add(new ParticleBox(signalPool, this, Particle.valueOf(pointMapObject.getName()),
                    MapRecord.toMeters(position.x), MapRecord.toMeters(position.y)));
            }
        }
    }

    public void dispose() {
        particleSystem.clear();
        bulletECS.dispose();
    }

    public void addParticleBox(Particle particle, float x, float y) {
        particleBoxes.add(new ParticleBox(signalPool, this, particle, MapRecord.toMeters(x), MapRecord.toMeters(y)));
    }

    public void removeParticleBox(float x, float y) {
        int index = 0;
        float mx = MapRecord.toMeters(x);
        float my = MapRecord.toMeters(y);
        for (ParticleBox particleBox : particleBoxes) {
            if (particleBox.x == mx && particleBox.y == my) {
                break;
            }
            index++;
        }
        particleBoxes.removeIndex(index);
    }

    public void pushSpawnBulletRequest(SpawnBulletRequest spawnBulletRequest) {
        bulletECS.getSpawnQueue().add(spawnBulletRequest);
    }

    public void pushFxEvent(FxEvent fxEvent) {
        particleSystem.getFxQueue().add(fxEvent);
    }

    public void createPlayer(EntityActor entityActor) {
        bulletECS.createPlayer(entityActor);
    }

    public void createEnemy(EntityActor entityActor) {
        bulletECS.createEnemy(entityActor);
    }

    public void destroyEntity(EntityActor entityActor) {
        this.bulletECS.destroyEntity(entityActor);
    }

}
