package com.mobius.stream.Game.Animate;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mobius.stream.Resource.Anim;

import java.util.EnumMap;

public class AnimationCache {
    private final EnumMap<Anim, Animation<TextureRegion>> cache = new EnumMap<>(Anim.class);
    private final TextureAtlas atlas;

    public AnimationCache(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    /**
     * 按枚举取动画，第一次构建后缓存复用；key 就是枚举自身
     */
    public Animation<TextureRegion> get(Anim anim) {
        Animation<TextureRegion> a = cache.get(anim);
        if (a == null) {
            a = build(anim);
            cache.put(anim, a);
        }
        return a;
    }

    private Animation<TextureRegion> build(Anim anim) {
        TextureRegion[][] grid = atlas.findRegion(anim.regionName)
            .split(anim.frameW, anim.frameH);
        Array<TextureRegion> frames = new Array<>(anim.frameCount);
        for (int i = 0; i < anim.frameCount; i++) {
            frames.add(grid[anim.row][i]);
        }
        return new Animation<>(anim.frameDuration, frames, anim.playMode);
    }

    public void clear() {
        cache.clear();
    }
}
