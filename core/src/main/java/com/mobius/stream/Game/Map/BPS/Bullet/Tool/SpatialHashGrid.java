package com.mobius.stream.Game.Map.BPS.Bullet.Tool;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * 空间哈希网格，用于加速附近实体查询。
 */
public class SpatialHashGrid<T> {

    private final float cellSize;
    private final ObjectMap<Long, Array<T>> cells = new ObjectMap<>();

    public SpatialHashGrid(float cellSize) {
        this.cellSize = cellSize;
    }

    private long key(int cx, int cy) {
        return ((long) cx << 32) ^ (cy & 0xffffffffL);
    }

    public void insert(float x, float y, T obj) {
        int cx = (int) Math.floor(x / cellSize);
        int cy = (int) Math.floor(y / cellSize);
        long k = key(cx, cy);
        Array<T> cell = cells.get(k);
        if (cell == null) {
            cell = new Array<>();
            cells.put(k, cell);
        }
        cell.add(obj);
    }

    public void query(float x, float y, float radius, Array<T> out) {
        int minX = (int) Math.floor((x - radius) / cellSize);
        int maxX = (int) Math.floor((x + radius) / cellSize);
        int minY = (int) Math.floor((y - radius) / cellSize);
        int maxY = (int) Math.floor((y + radius) / cellSize);

        for (int cx = minX; cx <= maxX; cx++) {
            for (int cy = minY; cy <= maxY; cy++) {
                Array<T> cell = cells.get(key(cx, cy));
                if (cell != null) out.addAll(cell);
            }
        }
    }

    public void clear() {
        cells.clear();
    }

    public int count() {
        return cells.size;
    }

    public void debugDrawGrid(ShapeRenderer sr,
                              float x, float y,
                              float width, float height) {
        int minCx = (int) Math.floor(x / cellSize);
        int maxCx = (int) Math.floor((x + width) / cellSize);
        int minCy = (int) Math.floor(y / cellSize);
        int maxCy = (int) Math.floor((y + height) / cellSize);

        float left = minCx * cellSize;
        float bottom = minCy * cellSize;
        float right = (maxCx + 1) * cellSize;
        float top = (maxCy + 1) * cellSize;

        // 竖线
        for (int cx = minCx; cx <= maxCx + 1; cx++) {
            float gx = cx * cellSize;
            sr.line(gx, bottom, gx, top);
        }

        // 横线
        for (int cy = minCy; cy <= maxCy + 1; cy++) {
            float gy = cy * cellSize;
            sr.line(left, gy, right, gy);
        }
    }

    /**
     * 绘制指定区域内“有对象”的格子。
     * 调用前需确保 sr 已经 begin(ShapeType.Line)。
     */
    public void debugDrawOccupied(ShapeRenderer sr,
                                  float x, float y,
                                  float width, float height) {
        for (ObjectMap.Entry<Long, Array<T>> entry : cells.entries()) {
            long k = entry.key;

            int cx = (int) (k >> 32);
            int cy = (int) k;

            float gx = cx * cellSize;
            float gy = cy * cellSize;

            // 只画与传入区域相交的格子
            if (gx + cellSize < x || gx > x + width ||
                gy + cellSize < y || gy > y + height) {
                continue;
            }

            sr.rect(gx, gy, cellSize, cellSize);
        }
    }
}
