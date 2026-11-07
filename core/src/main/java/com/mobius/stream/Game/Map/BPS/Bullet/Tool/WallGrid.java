package com.mobius.stream.Game.Map.BPS.Bullet.Tool;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mobius.stream.Resource.MapRecord;

public class WallGrid {

    private static final float STEP = 0.01f;

    private boolean[][] solid;
    private int width, height;

    public void loadMap(TiledMap tiledMap) {
        MapProperties p = tiledMap.getProperties();
        width = p.get(MapRecord.MapProperties.Width.name(), Integer.class);
        height = p.get(MapRecord.MapProperties.Height.name(), Integer.class);
        solid = new boolean[width][height];

        MapObjects objects = tiledMap.getLayers().get(MapRecord.Layer.Physic.name()).getObjects();
        int ts = MapRecord.tileSize;
        for (MapObject obj : objects) {
            if (!(obj instanceof RectangleMapObject)) continue;
            Rectangle r = ((RectangleMapObject) obj).getRectangle();

            int sx = Math.max(0, (int) Math.floor(r.x / ts));
            int sy = Math.max(0, (int) Math.floor(r.y / ts));
            int ex = Math.min(width, (int) Math.ceil((r.x + r.width) / ts));
            int ey = Math.min(height, (int) Math.ceil((r.y + r.height) / ts));

            for (int x = sx; x < ex; x++)
                for (int y = sy; y < ey; y++)
                    solid[x][y] = true;
        }
    }

    public boolean isSolid(float worldX, float worldY) {
        int tx = (int) Math.floor(worldX);
        int ty = (int) Math.floor(worldY);
        if (tx < 0 || ty < 0 || tx >= width || ty >= height) return true;
        return solid[tx][ty];
    }

    public float raycastT(float x0, float y0, float x1, float y1,
                          Vector2 outHit, Vector2 outLastFree) {
        float dx = x1 - x0, dy = y1 - y0;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist < 1e-6f) {
            if (!isSolid(x0, y0)) return -1f;
            if (outHit != null) outHit.set(x0, y0);
            if (outLastFree != null) outLastFree.set(x0, y0);
            return 0f;
        }

        int steps = Math.max(1, (int) Math.ceil(dist / STEP));
        float lastFreeT = 0f;

        for (int i = 1; i <= steps; i++) {
            float t = i / (float) steps;
            float px = x0 + dx * t;
            float py = y0 + dy * t;
            if (!isSolid(px, py)) {
                lastFreeT = t;
                continue;
            }

            if (outHit != null) outHit.set(px, py);
            if (outLastFree != null) outLastFree.set(x0 + dx * lastFreeT, y0 + dy * lastFreeT);
            return t;
        }
        return -1f;
    }

    public boolean raycast(float x0, float y0, float x1, float y1, Vector2 outHit) {
        return raycastT(x0, y0, x1, y1, outHit, null) >= 0f;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileWidth() {
        return MapRecord.tileSize;
    }

    public int getTileHeight() {
        return MapRecord.tileSize;
    }

    public boolean[][] getSolid() {
        return solid;
    }
}
