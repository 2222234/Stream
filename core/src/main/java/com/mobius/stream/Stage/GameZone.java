package com.mobius.stream.Stage;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mobius.stream.Game.Controller.CameraController;
import com.mobius.stream.Game.Controller.ControlController;
import com.mobius.stream.Game.Controller.MusicController;
import com.mobius.stream.Game.Controller.ScriptController;
import com.mobius.stream.Game.Dialogue.DialogueBox;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Event.Global.ExitGameEvent;
import com.mobius.stream.Game.Event.Global.ReturnTitleEvent;
import com.mobius.stream.Game.Event.Global.StartGameEvent;
import com.mobius.stream.Game.Event.Ui.*;
import com.mobius.stream.Game.Map.MapController;
import com.mobius.stream.Game.Object.Exit;
import com.mobius.stream.Game.Object.GunFactory.GunTable;
import com.mobius.stream.Game.Object.Map;
import com.mobius.stream.Game.Object.PlayerStatusBar;
import com.mobius.stream.Game.Object.Tip;
import com.mobius.stream.Resource.Player.PlayerData;
import com.mobius.stream.Resource.Resources;
import com.mobius.stream.Resource.Save;
import com.mobius.stream.Stage.Component.FadeAnimation;
import com.mobius.stream.Stage.Component.FadeOverlay;
import com.mobius.stream.Stage.Component.ToastOverlay;
import com.mobius.stream.Stream;

public class GameZone extends ScreenAdapter implements Listener<GameEvent> {
    private final Stream stream;
    private final Stage uiStage;
    private final Stage gameStage;

    private final DialogueBox dialogueBox;
    private final ToastOverlay toastOverlay;
    private final FadeAnimation fadeAnimation;
    private final PlayerStatusBar playerStatusBar;
    private final GunTable gunTable;
    private final Map map;
    private final Exit exit;
    private final Tip tip;

    private final ScriptController scriptController;
    private final CameraController cameraController;
    private final MapController mapController;
    private final ControlController controlController;
    private final MusicController musicController;

    private final FadeOverlay fadeOverlay;

    private final Signal<GameEvent> signal;
    private final Resources resources;

    public GameZone(Stream stream, MusicController musicController, PlayerData playerData) {
        this.stream = stream;
        signal = new Signal<>();
        signal.add(this);

        signal.add(musicController);

        resources = stream.resources;

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        uiStage = new Stage(new ScreenViewport());

        playerStatusBar = new PlayerStatusBar(resources, playerData);
        uiStage.addActor(playerStatusBar);

        gunTable = new GunTable(resources, signal, playerData);
        uiStage.addActor(gunTable);

        dialogueBox = new DialogueBox(resources);
        uiStage.addActor(dialogueBox);

        toastOverlay = new ToastOverlay(resources);
        uiStage.addActor(toastOverlay);

        fadeAnimation = new FadeAnimation(resources);
        uiStage.addActor(fadeAnimation);

        map = new Map(resources, signal);
        uiStage.addActor(map);

        exit = new Exit(resources, signal);
        uiStage.addActor(exit);

        uiStage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    if (gunTable.isVisible()) {
                        signal.dispatch(new ShowGunTableEvent(false));
                        return true;
                    }
                    if (map.isVisible()) {
                        signal.dispatch(new ShowMapEvent(false));
                        return true;
                    }
                    exit.setVisible(!exit.isVisible());
                }
                return false;
            }
        });

        gameStage = new Stage(new ScreenViewport());

        tip = new Tip(resources, gameStage);
        uiStage.addActor(tip);

        mapController = new MapController(resources, signal, gameStage, playerData);
        signal.add(mapController);

        scriptController = new ScriptController(resources, signal, playerData, gameStage);
        signal.add(scriptController);

        cameraController = new CameraController((OrthographicCamera) gameStage.getCamera());
        signal.add(cameraController);

        controlController = new ControlController(gameStage, signal);
        signal.add(controlController);

        this.musicController = musicController;
        signal.add(musicController);

        inputMultiplexer.addProcessor(uiStage);
        inputMultiplexer.addProcessor(gameStage);
        inputMultiplexer.addProcessor(controlController);

        fadeOverlay = new FadeOverlay(resources);
    }

    @Override
    public void show() {
        signal.dispatch(new StartGameEvent());
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
        gameStage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        musicController.update(delta);
        scriptController.update(false, delta);
        cameraController.update(delta);
        mapController.update(delta);
        controlController.update(delta);

        fadeOverlay.update(delta);
        fadeOverlay.render(uiStage.getBatch(), (OrthographicCamera) uiStage.getCamera());

        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent object) {
        if (object instanceof ShowTitleEvent showTitleEvent) {
            if (showTitleEvent.type == ToastOverlay.Type.Main)
                toastOverlay.showMain(showTitleEvent.title, showTitleEvent.time);
            if (showTitleEvent.type == ToastOverlay.Type.Sub)
                toastOverlay.showSub(showTitleEvent.title, showTitleEvent.time);
            if (showTitleEvent.type == ToastOverlay.Type.Center)
                toastOverlay.showCenterTop(showTitleEvent.title, showTitleEvent.time);
            return;
        }
        if (object instanceof StartDiologuesEvent startDiologuesEvent) {
            dialogueBox.start(startDiologuesEvent.dialoguesRecord, startDiologuesEvent.callback);
            return;
        }
        if (object instanceof FadeOverlayEvent fadeOverlayEvent) {
            if (fadeOverlayEvent.fadeState == FadeOverlay.FadeState.FadeIn) fadeOverlay.fadeIn(fadeOverlayEvent.time);
            if (fadeOverlayEvent.fadeState == FadeOverlay.FadeState.FadeOut) fadeOverlay.fadeOut(fadeOverlayEvent.time);
            return;
        }
        if (object instanceof TipEvent tipEvent) {
            tip.set(tipEvent.tipBox, tipEvent.gamePos, tipEvent.visible);
            return;
        }
        if (object instanceof ClearTipBoxEvent clearTipBoxEvent) {
            tip.clear(clearTipBoxEvent.tipBox);
            return;
        }
        if (object instanceof LockTipEvent lockTipEvent) {
            tip.setLock(lockTipEvent.lock);
            return;
        }
        if (object instanceof RefreshTipEvent) {
            tip.refresh();
            return;
        }
        if (object instanceof ShowGunTableEvent showGunTableEvent) {
            gunTable.setVisible(showGunTableEvent.visible);
            lock(showGunTableEvent.visible);
            return;
        }
        if (object instanceof ShowMapEvent showMapEvent) {
            map.setVisible(showMapEvent.visible);
            lock(showMapEvent.visible);
            return;
        }
        if (object instanceof ShowStatusBarEvent showStatusBarEvent) {
            playerStatusBar.setVisible(showStatusBarEvent.visible);
            return;
        }
        if (object instanceof ShowGoToEvent showGoToEvent) {
            fadeAnimation.play(showGoToEvent.fadeIn, showGoToEvent.fadeOut, showGoToEvent.afterShow);
            return;
        }
        if (object instanceof ReturnTitleEvent) {
            Save.save();
            dispose();
            stream.setScreen(new Title(stream));
            return;
        }
        if (object instanceof ExitGameEvent) {
            Gdx.app.exit();
        }
    }

    private void lock(boolean lock) {
        controlController.setLock(lock);
        cameraController.setLock(lock);
        if (tip != null) {
            tip.setVisible(!lock);
        }
    }

    @Override
    public void dispose() {
        uiStage.dispose();
        gameStage.dispose();
        scriptController.dispose();
        mapController.dispose();
        musicController.dispose();
    }
}
