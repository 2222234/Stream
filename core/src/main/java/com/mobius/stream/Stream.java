package com.mobius.stream;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.mobius.stream.Resource.Resources;
import com.mobius.stream.Resource.Save;
import com.mobius.stream.Stage.Loading;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Stream extends Game {
    public Resources resources;

    @Override
    public void create() {
        System.out.println("LWJGL 运行版本: " + Gdx.graphics.getGLVersion().getVersionString());
        resources = new Resources();
        setScreen(new Loading(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        resources.dispose();
        Save.save();
    }
}
