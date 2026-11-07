package com.mobius.stream.Game.Map.NavigationSystem;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mobius.stream.Game.Entity.EntityActor;
import com.mobius.stream.Game.Event.Game.Map.NavigationTargetEvent;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Resource.MapRecord;

import static com.mobius.stream.Resource.MapRecord.toMeters;

public class Navigation implements Listener<GameEvent> {
    private final NavDebugRenderer navDebugRenderer = new NavDebugRenderer();
    private final Array<AIController> ais = new Array<>();
    private NavGraph graph;
    private FlowFieldManager flowManager;

    public static NavGraph buildFromTiled(TiledMap map, NavGraphBuilder builder) {
        MapLayer mapLayer = map.getLayers().get(MapRecord.Layer.Platform.name());
        if (mapLayer == null) return builder.build();
        MapObjects objects = mapLayer.getObjects();
        for (MapObject obj : objects) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                NavGraphBuilder.Platform p = new NavGraphBuilder.Platform();
                p.x = toMeters(r.x);
                p.y = toMeters(r.y);
                p.width = toMeters(r.width);
                p.passThrough = obj.getProperties().get("passThrough", false, Boolean.class);
                builder.addPlatform(p);
            }
        }
        return builder.build();
    }

    public void init(TiledMap tiledMap) {
        NavGraphBuilder builder = new NavGraphBuilder();
        builder.jumpVelocity = 12f;
        builder.gravity = 10f;
        builder.moveSpeed = 5f;

        graph = buildFromTiled(tiledMap, builder);

        flowManager = new FlowFieldManager(graph);
        flowManager.setTarget(0, 0);
        flowManager.forceRebuild();
    }

    public void addAIController(EntityActor entityActor) {
        ais.add(entityActor.getAiController());
    }

    public NavGraph getGraph() {
        return graph;
    }

    public FlowFieldManager getFlowManager() {
        return flowManager;
    }

    public void setTarget(float x, float y) {
        flowManager.setTarget(x, y);
    }

    public void update(float delta, OrthographicCamera camera) {
        flowManager.update(delta);
        ais.iterator().forEachRemaining(AIController::update);
//        navDebugRenderer.render(camera,
//            graph,
//            flowManager,
//            ais,
//            graph.nodes
//        );
    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent object) {
        if (object instanceof NavigationTargetEvent navigationTargetEvent) {
            setTarget(navigationTargetEvent.x, navigationTargetEvent.y);
        }
    }

    public void dispose() {
        navDebugRenderer.dispose();
    }

    public void removeAIController(EntityActor entityActor) {
        ais.removeValue(entityActor.getAiController(), true);
    }
}
