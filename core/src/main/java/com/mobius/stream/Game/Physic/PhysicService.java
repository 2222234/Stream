package com.mobius.stream.Game.Physic;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mobius.stream.Game.Context;
import com.mobius.stream.Game.Object.RoadSign;
import com.mobius.stream.Game.Object.TipBox;
import com.mobius.stream.Resource.MapRecord;
import com.mobius.stream.Resource.Object.Box.Interaction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PhysicService {
    public static final int PPM = 16;
    private final World world;
    private final Array<Body> terrain;
    private final Array<Body> destroyQuene;
    private TiledMap nextMap;
    private Context context;

    public PhysicService() {
        world = new World(new Vector2(0, -10f), false);
        world.setContactListener(new ContactDispatcher());
        terrain = new Array<>();
        destroyQuene = new Array<>();
    }

    public static float toMeters(float pixels) {
        return pixels / PPM;
    }

    public static float toPixels(float meters) {
        return meters * PPM;
    }

    public World getWorld() {
        return world;
    }

    public void changeMap(Context context, TiledMap tiledMap) {
        this.context = context;
        destroyQuene.addAll(terrain);
        terrain.clear();
        nextMap = tiledMap;
    }

    private void buildTerrain(TiledMap map) {
        MapObjects objects = map.getLayers().get(MapRecord.Layer.Physic.name()).getObjects();
        for (MapObject object : objects) {
            createRect(object);
        }
    }

    private void buildRoadSign(TiledMap map) {
        MapLayer mapLayer = map.getLayers().get(MapRecord.Layer.RoadSign.name());
        if (mapLayer == null) return;
        MapObjects objects = mapLayer.getObjects();
        for (MapObject object : objects) {
            Fixture fixture = createRect(object);
            if (fixture != null) {
                fixture.setSensor(true);
                fixture.getBody().setUserData(new RoadSign(context.signal(), object.getName()));
            }
        }
    }

    private void buildTip(TiledMap map) {
        MapLayer mapLayer = map.getLayers().get(MapRecord.Layer.Interaction.name());
        if (mapLayer == null) return;
        MapObjects objects = mapLayer.getObjects();
        try {
            for (MapObject object : objects) {
                Interaction interaction = Interaction.valueOf(object.getName());
                Fixture fixture = createRect(object);
                if (fixture != null) {
                    fixture.setSensor(true);
                    Constructor<? extends TipBox> constructor = interaction.tipBox.getDeclaredConstructor(Context.class, Rectangle.class);
                    TipBox tipBox = constructor.newInstance(context, getRect(object));
                    if (tipBox.create()) {
                        fixture.getBody().setUserData(tipBox);
                        context.stage().addActor(tipBox);
                        tipBox.toBack();
                        tipBox.setBody(fixture.getBody());
                    }
                }
            }
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Rectangle getRect(MapObject object) {
        if (object instanceof RectangleMapObject rectangleMapObject) {
            return rectangleMapObject.getRectangle();
        }
        return null;
    }

    private Fixture createRect(MapObject object) {
        if (object instanceof RectangleMapObject rectangleMapObject) {
            Rectangle rect = rectangleMapObject.getRectangle();
            PolygonShape poly = new PolygonShape();
            Vector2 center = new Vector2(toMeters(rect.x + rect.width * 0.5f), toMeters(rect.y + rect.height * 0.5f));
            poly.setAsBox(toMeters(rect.width * 0.5f), toMeters(rect.height * 0.5f));
            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(center);
            bodyDef.type = BodyDef.BodyType.StaticBody;
            Body body = world.createBody(bodyDef);
            Fixture fixture = body.createFixture(poly, 1);
            terrain.add(body);
            poly.dispose();
            return fixture;
        }
        return null;
    }

    public void update(float delta) {
        destroyQuene.iterator().forEach(world::destroyBody);
        destroyQuene.clear();
        if (nextMap != null) {
            buildTerrain(nextMap);
            buildRoadSign(nextMap);
            buildTip(nextMap);
            nextMap = null;
        }
        world.step(delta, 6, 2);
    }

    public List<RayPoint> raycast(Vector2 start, Vector2 end, int loop) {
        Ray ray = new Ray(loop);
        world.rayCast(ray, start, end);
        return ray.rayPoints;
    }

    public void destroy(Body body) {
        if (terrain.contains(body, true)) {
            terrain.removeValue(body, true);
        }
        destroyQuene.add(body);
    }

    static class Ray implements RayCastCallback {
        final List<RayPoint> rayPoints = new ArrayList<>();
        int loop;

        Ray(int loop) {
            this.loop = loop;
        }

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            rayPoints.add(new RayPoint(fixture, point.cpy(), normal.cpy(), fraction));
            return loop -= 1;
        }
    }
}
