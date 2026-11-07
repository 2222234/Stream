package com.mobius.stream.Stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mobius.stream.Resource.ResourcesRecord;
import com.mobius.stream.Resource.TextRecord;
import com.mobius.stream.Stage.Element.Fade;
import com.mobius.stream.Stream;

public class Logo extends ScreenAdapter {
    private final Stage stage;

    public Logo(Stream stream) {
        stage = new Stage(new ScreenViewport());
        Fade fade = new Fade(0.8f, 1, 0.8f, () -> stream.setScreen(new Title(stream)));
        fade.setFillParent(true);
        Label logo = new Label(stream.resources.getString(TextRecord.Title_Logo), stream.resources.getSkin(), ResourcesRecord.Large_Font);
        fade.add(logo).center();
        stage.addActor(fade);

        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                stream.setScreen(new Title(stream));
                return true;
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                stream.setScreen(new Title(stream));
                return true;
            }
        });
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
