package com.mobius.stream.Game.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import static com.mobius.stream.Game.Physic.PhysicService.toMeters;

public class Render {
    private OrthogonalTiledMapRenderer renderer;

    public void changeMap(TiledMap tiledMap) {
        if (renderer == null) {
            renderer = new OrthogonalTiledMapRenderer(tiledMap, toMeters(1f));
        } else {
            renderer.setMap(tiledMap);
        }
    }

    public void render(OrthographicCamera camera) {
        if (renderer != null) {
            renderer.setView(camera);
            renderer.render();
        }
    }

    public void dispose() {
        renderer.dispose();
    }
}
