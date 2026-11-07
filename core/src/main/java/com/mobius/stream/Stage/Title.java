package com.mobius.stream.Stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mobius.stream.Game.Controller.MusicController;
import com.mobius.stream.Game.Event.Game.Audio.PlayMusicEvent;
import com.mobius.stream.Resource.*;
import com.mobius.stream.Resource.Player.PlayerData;
import com.mobius.stream.Stage.Element.Fade;
import com.mobius.stream.Stage.Element.UnderLineButton;
import com.mobius.stream.Stream;

public class Title extends ScreenAdapter {
    private final Stage stage;
    private final Fade button;
    private final MusicController musicController;

    public Title(Stream stream) {
        Resources resources = stream.resources;

        musicController = new MusicController(resources);

        stage = new Stage(new ScreenViewport());
        Fade fade = new Fade(0.8f, 1, -1, null);
        fade.setFillParent(true);
        Image background = new Image(resources.getAtlas().findRegion(RegionRecord.Background));
        background.setScaling(Scaling.fill);
        fade.add(background).grow();
        stage.addActor(fade);

        button = new Fade(2f, 1, -1, null);
        button.setFillParent(true);
        button.right();
        Label title = new Label(resources.getString(TextRecord.Title_Title), resources.getSkin(), ResourcesRecord.Large_Font);
        button.add(title).padBottom(50).row();
        PlayerData playerData = Save.load();
        button.add(new UnderLineButton(resources, resources.getString(TextRecord.Title_Start), 0.15f, () -> {
            button.fadeOut(0.5f, () -> {
            });
            fade.fadeOut(0.5f, () -> stream.setScreen(new GameZone(stream, musicController, Save.createNew())));
        })).padBottom(10).row();

        if (playerData != null) {
            button.add(new UnderLineButton(resources, resources.getString("Title.Continue"), 0.15f, () -> {
                button.fadeOut(0.5F, () -> {
                });
                fade.fadeOut(0.5F, () -> stream.setScreen(new GameZone(stream, musicController, playerData)));
            })).padBottom(10.0F).row();
        }

        button.add(new UnderLineButton(resources, resources.getString(TextRecord.Title_Exit), 0.15f, () ->
                Gdx.app.exit()))
            .padBottom(10).row();
        stage.addActor(button);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        musicController.onPlayMusic(new PlayMusicEvent(AudioRecord.Title, true, 1f, 0.2f, false));
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        float pad = width * 0.15f;
        button.padLeft(pad).padRight(pad);
        button.invalidate();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();

        musicController.update(delta);
    }

    @Override
    public void hide() {
        musicController.stopMusic(0.5f);
    }

    @Override
    public void dispose() {
        stage.dispose();
        musicController.dispose();
    }
}
