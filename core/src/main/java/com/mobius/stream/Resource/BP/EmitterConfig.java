package com.mobius.stream.Resource.BP;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.mobius.stream.Game.Physic.PhysicService;

import static com.mobius.stream.Game.Physic.PhysicService.toMeters;

public enum EmitterConfig {

    Hit(4, 10,
        toMeters(500f), toMeters(750f),
        15f, 20f,
        toMeters(3f), toMeters(8f),
        toMeters(0f), toMeters(2f),
        toMeters(-400f), 2f,
        MathUtils.PI / 2,
        -180, 180,
        new Color(1f, 1f, 1f, 0.5f), new Color(1f, 1f, 1f, 0f),
        ParticleRegion.Particle_Death.ordinal()),
    Collision(5, 10,
        10f, 20f,
        0.05f, 0.1f,
        PhysicService.toMeters(2f), PhysicService.toMeters(3f),
        PhysicService.toMeters(0f), PhysicService.toMeters(1f),
        0, 2f, MathUtils.PI / 2,
        -180, 180,
        new Color(1f, 0.0625f, 0.0625f, 1f), new Color(1f, 0.5f, 0.0625f, 0f),
        ParticleRegion.Particle_White.ordinal()),
    FireSpark(0, 2,
        1, 5,
        0.2f, 0.5f,
        toMeters(3f), toMeters(5f),
        toMeters(0f), toMeters(2f),
        0, 2f,
        MathUtils.PI / 4,
        -180, 180,
        new Color(1f, 1f, 1f, 1f), new Color(1f, 1f, 1f, 0f),
        ParticleRegion.Particles.ordinal()),
    Explosion(10, 20,
        1, 5,
        0.5f, 1f,
        toMeters(5f), toMeters(10f),
        toMeters(3f), toMeters(5f),
        0, 2f,
        MathUtils.PI * 2,
        -180, 180,
        new Color(1f, 1f, 1f, 1f), new Color(1f, 1f, 1f, 0f),
        ParticleRegion.Particles.ordinal()),
    Death(10, 20,
        1f, 5f,
        0.5f, 1.0f,
        PhysicService.toMeters(5f), PhysicService.toMeters(10f),
        PhysicService.toMeters(3f), PhysicService.toMeters(5f),
        0, 2f, MathUtils.PI * 2,
        -180, 180,
        new Color(1f, 1f, 1f, 1f), new Color(1f, 1f, 1f, 0f),
        ParticleRegion.Particle_Death.ordinal()),
    Tip(0, 1,
        1f, 2f,
        0.5f, 1.0f,
        PhysicService.toMeters(2f), PhysicService.toMeters(5f),
        PhysicService.toMeters(0f), PhysicService.toMeters(2f),
        0, 2f, MathUtils.PI * 2,
        -180, 180,
        new Color(1f, 1f, 1f, 1f), new Color(1f, 1f, 1f, 0f),
        ParticleRegion.Particle_Tip.ordinal()),
    Fountain(5, 10,
        2f, 5f,
        5f, 10f,
        PhysicService.toMeters(1f), PhysicService.toMeters(1f),
        PhysicService.toMeters(0f), PhysicService.toMeters(1f),
        -10, 2f, MathUtils.PI / 2,
        -180, 180,
        new Color(0.5f, 0.5f, 1f, 1f), new Color(0.25f, 0.25f, 1f, 0f),
        ParticleRegion.Particle_White.ordinal()),
    Spark(5, 10,
        5f, 10f,
        1f, 2f,
        PhysicService.toMeters(1f), PhysicService.toMeters(1f),
        PhysicService.toMeters(0f), PhysicService.toMeters(1f),
        -10, 2f, MathUtils.PI / 2,
        -180, 180,
        new Color(1f, 0f, 0f, 1f), new Color(1f, 0.75f, 0f, 0f),
        ParticleRegion.Particle_White.ordinal());

    public final int minCount;
    public final int maxCount;

    public final float minSpeed;
    public final float maxSpeed;

    public final float minLife;
    public final float maxLife;

    public final float minSize;
    public final float maxSize;
    public final float minEndSize;
    public final float maxEndSize;

    public final float gravity;       // 世界单位/秒²，向上为正
    public final float drag;          // 0~1，每秒衰减比例
    public final float spread; // 扩散角度范围，PI2 = 全向
    public final float minRotSpeed;
    public final float maxRotSpeed;
    public final Color startColor;
    public final Color endColor;
    public final int regionIndex;      // 纹理图集索引
    public float direction = 0;     // 基准角度（弧度）

    EmitterConfig(int minCount, int maxCount, float minSpeed, float maxSpeed, float minLife, float maxLife, float minSize, float maxSize, float minEndSize, float maxEndSize, float gravity, float drag, float spread, float minRotSpeed, float maxRotSpeed, Color startColor, Color endColor, int regionIndex) {
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.minLife = minLife;
        this.maxLife = maxLife;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.minEndSize = minEndSize;
        this.maxEndSize = maxEndSize;
        this.gravity = gravity;
        this.drag = drag;
        this.spread = spread;
        this.minRotSpeed = minRotSpeed;
        this.maxRotSpeed = maxRotSpeed;
        this.startColor = startColor;
        this.endColor = endColor;
        this.regionIndex = regionIndex;
    }

    /**
     * 按范围随机采样
     */
    public float randSpeed() {
        return MathUtils.random(minSpeed, maxSpeed);
    }

    public float randLife() {
        return MathUtils.random(minLife, maxLife);
    }

    public float randSize() {
        return MathUtils.random(minSize, maxSize);
    }

    public float randEndSize() {
        return MathUtils.random(minEndSize, maxEndSize);
    }

    public float randRotSpeed() {
        return MathUtils.random(minRotSpeed, maxRotSpeed);
    }

    public int randCount() {
        return MathUtils.random(minCount, maxCount);
    }
}
