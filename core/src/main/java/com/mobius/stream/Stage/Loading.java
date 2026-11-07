package com.mobius.stream.Stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mobius.stream.Resource.Resources;
import com.mobius.stream.Stage.Element.CometActor;
import com.mobius.stream.Stream;

public class Loading extends ScreenAdapter {
    private final Stream stream;
    private final Resources resources;
    private final Stage stage;
    private final Table table;
    private final CometActor cometActor;

    public Loading(Stream stream) {
        this.stream = stream;
        resources = stream.resources;

        stage = new Stage(new ScreenViewport());

        table = new Table();
        table.setFillParent(true);
        table.right().bottom();
        cometActor = new CometActor();
        cometActor.setOnEndComplete(() -> stream.setScreen(new Logo(stream)));
        table.add(cometActor);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        resources.start();
        cometActor.start();
    }

    @Override
    public void hide() {
        cometActor.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        float pad = width * 0.05f;
        table.padRight(pad).padBottom(pad);
        table.invalidate();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
        if (resources.update()) {
            cometActor.end();
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
