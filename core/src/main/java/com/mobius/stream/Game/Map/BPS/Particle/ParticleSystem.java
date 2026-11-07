package com.mobius.stream.Game.Map.BPS.Particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Resource.BP.EmitterConfig;
import com.mobius.stream.Resource.BP.ParticleRegion;

public class ParticleSystem {
    private final SpriteBatch batch;
    private final Array<FxEvent> fxQueue;
    private final PoolManager signalPool;
    private final ParticlePool pool;
    private final TextureRegion[] regions;

    private final Rectangle bounds = new Rectangle();
    Rectangle cameraRect = new Rectangle();

    public ParticleSystem(PoolManager signalPool, TextureAtlas atlas) {
        this.signalPool = signalPool;
        regions = new TextureRegion[ParticleRegion.values().length];
        for (int i = 0; i < regions.length; i++) {
            regions[i] = atlas.findRegion(ParticleRegion.values()[i].name());
        }
        fxQueue = new Array<>();
        pool = new ParticlePool();
        batch = new SpriteBatch();
    }

    public ParticlePool getPool() {
        return pool;
    }

    public Array<FxEvent> getFxQueue() {
        return fxQueue;
    }

    public void emit(EmitterConfig cfg, float x, float y) {
        int n = cfg.randCount();
        for (int i = 0; i < n; i++) {
            int idx = pool.obtain();
            if (idx < 0) return;

            float angle = cfg.direction + MathUtils.random(-cfg.spread * 0.5f,
                cfg.spread * 0.5f);
            float speed = cfg.randSpeed();
            float life = cfg.randLife();

            pool.x[idx] = x;
            pool.y[idx] = y;
            pool.vx[idx] = MathUtils.cos(angle) * speed;
            pool.vy[idx] = MathUtils.sin(angle) * speed;
            pool.gravity[idx] = cfg.gravity;
            pool.drag[idx] = cfg.drag;

            pool.life[idx] = life;
            pool.maxLife[idx] = life;

            pool.startSize[idx] = cfg.randSize();
            pool.endSize[idx] = cfg.randEndSize();
            pool.size[idx] = pool.startSize[idx];

            pool.rotation[idx] = MathUtils.random(0f, 360f);
            pool.rotSpeed[idx] = cfg.randRotSpeed();

            pool.startR[idx] = cfg.startColor.r;
            pool.startG[idx] = cfg.startColor.g;
            pool.startB[idx] = cfg.startColor.b;
            pool.startA[idx] = cfg.startColor.a;

            pool.endR[idx] = cfg.endColor.r;
            pool.endG[idx] = cfg.endColor.g;
            pool.endB[idx] = cfg.endColor.b;
            pool.endA[idx] = cfg.endColor.a;

            pool.r[idx] = cfg.startColor.r;
            pool.g[idx] = cfg.startColor.g;
            pool.b[idx] = cfg.startColor.b;
            pool.a[idx] = cfg.startColor.a;

            pool.region[idx] = cfg.regionIndex;
        }
    }

    public void emitDirected(EmitterConfig cfg, float x, float y, float directionRad) {
        float saved = cfg.direction;
        cfg.direction = directionRad;
        emit(cfg, x, y);
        cfg.direction = saved;
    }

    private void processQueue() {
        for (int i = 0; i < fxQueue.size; i++) {
            FxEvent fxEvent = fxQueue.get(i);
            emitDirected(fxEvent.emitterConfig, fxEvent.x, fxEvent.y, fxEvent.direction);
            signalPool.free(fxEvent);
        }
        fxQueue.clear();
    }

    public void update(float delta) {
        processQueue();

        for (int i = 0; i < pool.count; i++) {
            pool.life[i] -= delta;
            if (pool.life[i] <= 0f || !bounds.contains(pool.x[i], pool.y[i])) {
                pool.free(i);
                i--;
                continue;
            }

            if (pool.drag[i] > 0f) {
                float d = 1f - pool.drag[i] * delta;
                if (d < 0f) d = 0f;
                pool.vx[i] *= d;
                pool.vy[i] *= d;
            }

            if (pool.gravity[i] != 0f) {
                pool.vy[i] += pool.gravity[i] * delta;
            }

            pool.x[i] += pool.vx[i] * delta;
            pool.y[i] += pool.vy[i] * delta;

            if (pool.rotSpeed[i] != 0f) {
                pool.rotation[i] += pool.rotSpeed[i] * delta;
            }

            float t = 1f - pool.life[i] / pool.maxLife[i];

            pool.size[i] = pool.startSize[i] + (pool.endSize[i] - pool.startSize[i]) * t;

            pool.r[i] = pool.startR[i] + (pool.endR[i] - pool.startR[i]) * t;
            pool.g[i] = pool.startG[i] + (pool.endG[i] - pool.startG[i]) * t;
            pool.b[i] = pool.startB[i] + (pool.endB[i] - pool.startB[i]) * t;
            pool.a[i] = pool.startA[i] + (pool.endA[i] - pool.startA[i]) * t;
        }
    }

    public void render(OrthographicCamera camera) {
        if (pool.count == 0) return;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        updateVisible(camera);
        renderParticles();
        batch.end();
    }

    private void updateVisible(OrthographicCamera camera) {
        float x1 = camera.frustum.planePoints[0].x;
        float y1 = camera.frustum.planePoints[0].y;
        float x2 = camera.frustum.planePoints[2].x;
        float y2 = camera.frustum.planePoints[2].y;
        cameraRect.set(x1, y1, x2 - x1, y2 - y1);
    }

    public void setBounds(Rectangle bounds) {
        this.bounds.set(bounds);
    }

    public void renderParticles() {
        int lastRegion = -1;
        TextureRegion currentRegion = null;

        for (int i = 0; i < pool.count; i++) {
            float px = pool.x[i];
            float py = pool.y[i];
            if (!cameraRect.contains(px, py)) continue;

            int regionIdx = pool.region[i];
            if (regionIdx != lastRegion) {
                currentRegion = regions[regionIdx];
                lastRegion = regionIdx;
            }

            float s = pool.size[i];
            if (s <= 0f) continue;

            float half = s * 0.5f;

            batch.setColor(pool.r[i], pool.g[i], pool.b[i], pool.a[i]);
            batch.draw(currentRegion,
                px - half, py - half,
                half, half,
                s, s,
                1f, 1f,
                pool.rotation[i]);
        }

        batch.setColor(Color.WHITE);
    }

    public void clear() {
        pool.clear();
    }

    public int getActiveCount() {
        return pool.count;
    }
}
