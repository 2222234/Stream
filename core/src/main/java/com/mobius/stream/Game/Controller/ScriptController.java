package com.mobius.stream.Game.Controller;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.mobius.stream.Game.Entity.Attribute.NameType;
import com.mobius.stream.Game.Event.Game.Audio.PlayMusicEvent;
import com.mobius.stream.Game.Event.Game.Audio.PlaySoundEvent;
import com.mobius.stream.Game.Event.Game.Audio.StopMusicEvent;
import com.mobius.stream.Game.Event.Game.Control.FocusControlEvent;
import com.mobius.stream.Game.Event.Game.Entity.*;
import com.mobius.stream.Game.Event.Game.Map.ChangeMapEvent;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Event.Global.*;
import com.mobius.stream.Game.Event.Ui.*;
import com.mobius.stream.Game.Timeline;
import com.mobius.stream.Resource.*;
import com.mobius.stream.Resource.BP.Particle;
import com.mobius.stream.Resource.Object.Show.ClockShow;
import com.mobius.stream.Resource.Object.Show.CookShow;
import com.mobius.stream.Resource.Object.Show.HatShow;
import com.mobius.stream.Resource.Player.PlayerData;
import com.mobius.stream.Stage.Component.FadeOverlay;
import com.mobius.stream.Stage.Component.ToastOverlay;

import static com.mobius.stream.Game.Physic.PhysicService.toMeters;

public class ScriptController implements Listener<GameEvent> {
    private final Array<Timeline> timelines = new Array<>();
    private final Resources resources;
    private final Signal<GameEvent> signal;
    private final PlayerData playerData;
    private final Stage stage;

    public ScriptController(Resources resources, Signal<GameEvent> signal, PlayerData playerData, Stage stage) {
        this.resources = resources;
        this.signal = signal;
        this.playerData = playerData;
        this.stage = stage;
    }

    public Timeline createTimeline() {
        Timeline tl = new Timeline();
        timelines.add(tl);
        return tl;
    }

    public void update(boolean paused, float delta) {
        float dt = paused ? 0f : delta;

        for (int i = timelines.size - 1; i >= 0; i--) {
            Timeline tl = timelines.get(i);
            tl.update(dt);
            if (tl.isFinished()) timelines.removeIndex(i);
        }
    }

    public void dispose() {
        timelines.clear();
    }

    public void gameStart() {
        Vector2 position = getPosition(MapRecord.Map.Home, MapRecord.Point.Computer);
        signal.dispatch(new ChangeMapEvent(MapRecord.Map.Home));
        signal.dispatch(new SpawnEntityEvent(NameType.Dude, position.x, position.y));
        signal.dispatch(new FadeOverlayEvent(FadeOverlay.FadeState.FadeOut, 0f));
        signal.dispatch(new LockTipEvent(true));
        if (!playerData.start) {
            createTimeline().after(0.5f, timeline -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.START_1,
                (box, dialogueId) -> {
                    Vector2 spark = getPosition(MapRecord.Map.Home, MapRecord.Point.Computer);
                    signal.dispatch(new CreateParticleBoxEvent(Particle.Spark, spark.x, spark.y));
                    createTimeline()
                        .after(0, timeline1 -> signal.dispatch(new FadeOverlayEvent(FadeOverlay.FadeState.FadeIn, 0.5f)))
                        .after(0.5f, timeline1 -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.START_2,
                            (box1, dialogueId1) ->
                                createTimeline()
                                    .after(0f, timeline2 -> signal.dispatch(new FadeOverlayEvent(FadeOverlay.FadeState.FadeOut, 0.5f)))
                                    .after(0.5f, timeline2 -> signal.dispatch(new RemoveParticleBoxEvent(position.x, position.y)))
                                    .after(0f, timeline2 -> signal.dispatch(new FadeOverlayEvent(FadeOverlay.FadeState.FadeIn, 0.5f)))
                                    .after(0.5f, timeline2 -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.START_3,
                                        (box2, dialogueId2) -> {
                                            signal.dispatch(new FocusControlEvent(playerData.player));
                                            signal.dispatch(new LockTipEvent(false));
                                        })))
                        )));
                })));
            playerData.start = true;
        } else {
            createTimeline()
                .after(0.5f, timeline -> signal.dispatch(new FadeOverlayEvent(FadeOverlay.FadeState.FadeIn, 0.5f)))
                .after(0f, timeline -> signal.dispatch(new FocusControlEvent(playerData.player)))
                .after(0f, timeline -> signal.dispatch(new LockTipEvent(false)));
        }
    }

    private Vector2 getPosition(MapRecord.Map map, MapRecord.Point point) {
        TiledMap tiledMap = resources.getMap(map);
        for (Object o : tiledMap.getLayers().get(MapRecord.Layer.Point.name()).getObjects()) {
            PointMapObject po = (PointMapObject) o;
            if (point.name().equals(po.getName())) {
                return po.getPoint().cpy();
            }
        }
        return new Vector2();
    }

    public void goTo(MapRecord.Map map) {
        signal.dispatch(new GenerateEnemyEvent(false));
        signal.dispatch(new ShowStatusBarEvent(false));
        switch (map) {
            case MapRecord.Map.Home -> goToHome();
            case MapRecord.Map.Station -> goToStation();
            case MapRecord.Map.Mall -> goToMall();
            case MapRecord.Map.School -> goToSchool();
        }
    }

    private void goToHome() {
        signal.dispatch(new StopMusicEvent(0.5f));
        Vector2 position = getPosition(MapRecord.Map.Home, MapRecord.Point.Player);
        createTimeline()
            .after(0f, timeline -> signal.dispatch(new ClearTipBoxEvent(null)))
            .after(0f, timeline -> signal.dispatch(new FadeOverlayEvent(FadeOverlay.FadeState.FadeOut, 0.5f)))
            .after(0.5f, timeline -> signal.dispatch(new ShowGoToEvent(0.5f, 0.5f, () -> createTimeline()
                .after(0f, timeline1 -> signal.dispatch(new ClearEnemyEvent(false)))
                .after(0f, timeline1 -> playerData.player.resetBlood())
                .after(0f, timeline1 -> signal.dispatch(new ChangeMapEvent(MapRecord.Map.Home)))
                .after(0f, timeline1 -> playerData.player.getBody().setTransform(toMeters(position.x), toMeters(position.y), 0))
                .after(0.5f, timeline1 -> signal.dispatch(new FadeOverlayEvent(FadeOverlay.FadeState.FadeIn, 0.5f))))));
    }

    private void goToStation() {
        Vector2 position = getPosition(MapRecord.Map.Station, MapRecord.Point.Player);
        createTimeline()
            .after(0f, timeline -> signal.dispatch(new ClearTipBoxEvent(null)))
            .after(0f, timeline -> signal.dispatch(new FadeOverlayEvent(FadeOverlay.FadeState.FadeOut, 0.5f)))
            .after(0.5f, timeline1 -> signal.dispatch(new ShowGoToEvent(0.5f, 0.5f, () -> createTimeline()
                .after(0f, timeline -> signal.dispatch(new ClearEnemyEvent(false)))
                .after(0f, timeline -> signal.dispatch(new ChangeMapEvent(MapRecord.Map.Station)))
                .after(0f, timeline -> playerData.player.getBody().setTransform(toMeters(position.x), toMeters(position.y), 0))
                .after(0.5f, timeline2 -> signal.dispatch(new FadeOverlayEvent(FadeOverlay.FadeState.FadeIn, 0.5f)))
                .after(0f, timeline ->
                {
                    if (!playerData.stage1) {
                        Vector2 bossShow = getPosition(MapRecord.Map.Station, MapRecord.Point.BossShow);
                        ClockShow clockShow = new ClockShow(resources);
                        clockShow.setPosition(toMeters(bossShow.x), toMeters(bossShow.y));
                        createTimeline()
                            .after(0f, timeline2 -> signal.dispatch(new FocusControlEvent(null)))
                            .after(0f, timeline2 -> stage.addActor(clockShow))
                            .after(0f, timeline2 -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.STATION_1, (box, dialogueId) -> {
                                clockShow.remove();
                                signal.dispatch(new FocusControlEvent(playerData.player));
                                signal.dispatch(new ShowStatusBarEvent(true));
                            })));
                        playerData.stage1 = true;
                    } else {
                        signal.dispatch(new FocusControlEvent(playerData.player));
                        signal.dispatch(new ShowStatusBarEvent(true));
                        if (playerData.generate1) {
                            signal.dispatch(new ShowTitleEvent(ToastOverlay.Type.Center, resources.getString(TextRecord.Text_Generate), 1f));
                            signal.dispatch(new GenerateEnemyEvent(true));
                        }
                    }
                }))));
    }

    private void goToMall() {
        Vector2 position = getPosition(MapRecord.Map.Mall, MapRecord.Point.Player);
        createTimeline()
            .after(0f, timeline -> signal.dispatch(new ClearTipBoxEvent(null)))
            .after(0f, timeline -> signal.dispatch(new FadeOverlayEvent(FadeOverlay.FadeState.FadeOut, 0.5f)))
            .after(0.5f, timeline1 -> signal.dispatch(new ShowGoToEvent(0.5f, 0.5f, () -> createTimeline()
                .after(0f, timeline -> signal.dispatch(new ClearEnemyEvent(false)))
                .after(0f, timeline -> signal.dispatch(new ChangeMapEvent(MapRecord.Map.Mall)))
                .after(0f, timeline -> playerData.player.getBody().setTransform(toMeters(position.x), toMeters(position.y), 0))
                .after(0.5f, timeline2 -> signal.dispatch(new FadeOverlayEvent(FadeOverlay.FadeState.FadeIn, 0.5f)))
                .after(0f, timeline ->
                {
                    if (!playerData.stage2) {
                        createTimeline()
                            .after(0f, timeline2 -> signal.dispatch(new FocusControlEvent(null)))
                            .after(0f, timeline2 -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.MALL_1,
                                (box, dialogueId) ->
                                {
                                    signal.dispatch(new FocusControlEvent(playerData.player));
                                    signal.dispatch(new ShowStatusBarEvent(true));
                                })));
                        playerData.stage2 = true;
                    } else {
                        signal.dispatch(new FocusControlEvent(playerData.player));
                        signal.dispatch(new ShowStatusBarEvent(true));
                        if (playerData.generate2) {
                            signal.dispatch(new ShowTitleEvent(ToastOverlay.Type.Center, resources.getString(TextRecord.Text_Generate), 1f));
                            signal.dispatch(new GenerateEnemyEvent(true));
                        }
                    }
                }))));
    }

    private void goToSchool() {
        Vector2 position = getPosition(MapRecord.Map.School, MapRecord.Point.Player);
        createTimeline()
            .after(0f, timeline -> signal.dispatch(new ClearTipBoxEvent(null)))
            .after(0f, timeline -> signal.dispatch(new FadeOverlayEvent(FadeOverlay.FadeState.FadeOut, 0.5f)))
            .after(0.5f, timeline1 -> signal.dispatch(new ShowGoToEvent(0.5f, 0.5f, () -> createTimeline()
                .after(0f, timeline -> signal.dispatch(new ClearEnemyEvent(false)))
                .after(0f, timeline -> signal.dispatch(new ChangeMapEvent(MapRecord.Map.School)))
                .after(0f, timeline -> playerData.player.getBody().setTransform(toMeters(position.x), toMeters(position.y), 0))
                .after(0.5f, timeline2 -> signal.dispatch(new FadeOverlayEvent(FadeOverlay.FadeState.FadeIn, 0.5f)))
                .after(0f, timeline ->
                {
                    if (!playerData.stage3) {
                        createTimeline()
                            .after(0f, timeline2 -> signal.dispatch(new FocusControlEvent(null)))
                            .after(0f, timeline2 -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.SCHOOL_1, (box, dialogueId) ->
                            {
                                signal.dispatch(new FocusControlEvent(playerData.player));
                                signal.dispatch(new ShowStatusBarEvent(true));
                            }
                            )));
                        playerData.stage3 = true;
                    } else {
                        signal.dispatch(new FocusControlEvent(playerData.player));
                        signal.dispatch(new ShowStatusBarEvent(true));
                        if (playerData.generate3) {
                            signal.dispatch(new ShowTitleEvent(ToastOverlay.Type.Center, resources.getString(TextRecord.Text_Generate), 1f));
                            signal.dispatch(new GenerateEnemyEvent(true));
                        }
                    }
                }))));
    }

    public void fightTo(NameType name) {
        signal.dispatch(new ClearEnemyEvent(true));
        signal.dispatch(new GenerateEnemyEvent(false));
        switch (name) {
            case NameType.Clock -> fightToClock();
            case NameType.Hat -> fightToHat();
            case NameType.Cook -> fightToCook();
            case NameType.Computer -> fightToComputer();
        }
    }

    private void fightToClock() {
        Vector2 bossSpawn = getPosition(MapRecord.Map.Station, MapRecord.Point.BossSpawn);
        if (!playerData.boss1) {
            ClockShow clockShow = new ClockShow(resources);
            clockShow.setPosition(toMeters(bossSpawn.x), toMeters(bossSpawn.y));
            createTimeline()
                .after(0f, timeline -> signal.dispatch(new ShowStatusBarEvent(false)))
                .after(0f, timeline -> signal.dispatch(new FocusControlEvent(null)))
                .after(0f, timeline -> stage.addActor(clockShow))
                .after(0f, timeline -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.STATION_2, (box, dialogueId) -> {
                    clockShow.remove();
                    signal.dispatch(new FocusControlEvent(playerData.player));
                    signal.dispatch(new ShowStatusBarEvent(true));
                    signal.dispatch(new SpawnEntityEvent(NameType.Clock, bossSpawn.x, bossSpawn.y));
                    signal.dispatch(new PlayMusicEvent(AudioRecord.Station, false, 0.5f, 0.5f, false));
                })));
        } else {
            signal.dispatch(new SpawnEntityEvent(NameType.Clock, bossSpawn.x, bossSpawn.y));
            signal.dispatch(new PlayMusicEvent(AudioRecord.Station, false, 0.5f, 0.5f, false));
        }
    }

    private void fightToHat() {
        Vector2 bossSpawn = getPosition(MapRecord.Map.Mall, MapRecord.Point.BossSpawn);
        if (!playerData.boss2) {
            HatShow hatShow = new HatShow(resources);
            hatShow.setPosition(toMeters(bossSpawn.x), toMeters(bossSpawn.y));
            createTimeline()
                .after(0f, timeline -> signal.dispatch(new ShowStatusBarEvent(false)))
                .after(0f, timeline -> signal.dispatch(new FocusControlEvent(null)))
                .after(0f, timeline -> stage.addActor(hatShow))
                .after(0f, timeline -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.MALL_2, (box, dialogueId) -> {
                    hatShow.remove();
                    signal.dispatch(new FocusControlEvent(playerData.player));
                    signal.dispatch(new ShowStatusBarEvent(true));
                    signal.dispatch(new SpawnEntityEvent(NameType.Hat, bossSpawn.x, bossSpawn.y));
                    signal.dispatch(new PlayMusicEvent(AudioRecord.Mall, false, 0.5f, 0.5f, false));
                })));
        } else {
            signal.dispatch(new SpawnEntityEvent(NameType.Hat, bossSpawn.x, bossSpawn.y));
            signal.dispatch(new PlayMusicEvent(AudioRecord.Mall, false, 0.5f, 0.5f, false));
        }
    }

    private void fightToCook() {
        Vector2 bossSpawn = getPosition(MapRecord.Map.School, MapRecord.Point.BossSpawn);
        if (!playerData.boss3) {
            CookShow cookShow = new CookShow(resources);
            cookShow.setPosition(toMeters(bossSpawn.x), toMeters(bossSpawn.y));
            createTimeline()
                .after(0f, timeline -> signal.dispatch(new ShowStatusBarEvent(false)))
                .after(0f, timeline -> signal.dispatch(new FocusControlEvent(null)))
                .after(0f, timeline -> stage.addActor(cookShow))
                .after(0f, timeline -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.SCHOOL_2, (box, dialogueId) -> {
                    cookShow.remove();
                    signal.dispatch(new FocusControlEvent(playerData.player));
                    signal.dispatch(new ShowStatusBarEvent(true));
                    signal.dispatch(new SpawnEntityEvent(NameType.Cook, bossSpawn.x, bossSpawn.y));
                    signal.dispatch(new PlayMusicEvent(AudioRecord.School, false, 0.5f, 0.5f, false));
                })));
        } else {
            signal.dispatch(new SpawnEntityEvent(NameType.Cook, bossSpawn.x, bossSpawn.y));
            signal.dispatch(new PlayMusicEvent(AudioRecord.School, false, 0.5f, 0.5f, false));
        }
    }

    private void fightToComputer() {
        Vector2 bossSpawn = getPosition(MapRecord.Map.Home, MapRecord.Point.BossSpawn);
        if (!playerData.boss4) {
            createTimeline()
                .after(0f, timeline -> signal.dispatch(new ShowStatusBarEvent(false)))
                .after(0f, timeline -> signal.dispatch(new FocusControlEvent(null)))
                .after(0f, timeline -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.HOME_1, (box, dialogueId) -> {
                    signal.dispatch(new FocusControlEvent(playerData.player));
                    signal.dispatch(new ShowStatusBarEvent(true));
                    signal.dispatch(new SpawnEntityEvent(NameType.Computer, bossSpawn.x, bossSpawn.y));
                    signal.dispatch(new PlayMusicEvent(AudioRecord.LastBoss, false, 0.5f, 0.5f, false));
                })));
        } else {
            signal.dispatch(new ShowStatusBarEvent(true));
            signal.dispatch(new SpawnEntityEvent(NameType.Computer, bossSpawn.x, bossSpawn.y));
            signal.dispatch(new PlayMusicEvent(AudioRecord.LastBoss, false, 0.5f, 0.5f, false));
        }
    }

    public void defeatTo(NameType name) {
        signal.dispatch(new StopMusicEvent(0.5f));
        Save.save();
        switch (name) {
            case NameType.Clock -> defeatToClock();
            case NameType.Hat -> defeatToHat();
            case NameType.Cook -> defeatToCook();
            case NameType.Computer -> defeatToComputer();
        }
    }

    private void defeatToClock() {
        if (!playerData.boss1) {
            createTimeline()
                .after(0f, timeline -> signal.dispatch(new ShowStatusBarEvent(false)))
                .after(0f, timeline -> signal.dispatch(new FocusControlEvent(null)))
                .after(0f, timeline -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.STATION_3, (box, dialogueId) -> {
                    signal.dispatch(new FocusControlEvent(playerData.player));
                    signal.dispatch(new GoToMapEvent(MapRecord.Map.Home));
                    signal.dispatch(new PlaySoundEvent(AudioRecord.PickUp, 0.5f, 0.5f, 0));
                })));
            playerData.boss1 = true;
            playerData.computer1 = 1;
        }
    }

    private void defeatToHat() {
        if (!playerData.boss2) {
            createTimeline()
                .after(0f, timeline -> signal.dispatch(new ShowStatusBarEvent(false)))
                .after(0f, timeline -> signal.dispatch(new FocusControlEvent(null)))
                .after(0f, timeline -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.MALL_3, (box, dialogueId) -> {
                    signal.dispatch(new FocusControlEvent(playerData.player));
                    signal.dispatch(new GoToMapEvent(MapRecord.Map.Home));
                    signal.dispatch(new PlaySoundEvent(AudioRecord.PickUp, 0.5f, 0.5f, 0));
                })));
            playerData.boss2 = true;
            playerData.computer2 = 1;
        }
    }

    private void defeatToCook() {
        if (!playerData.boss3) {
            createTimeline()
                .after(0f, timeline -> signal.dispatch(new ShowStatusBarEvent(false)))
                .after(0f, timeline -> signal.dispatch(new FocusControlEvent(null)))
                .after(0f, timeline -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.SCHOOL_3, (box, dialogueId) -> {
                    signal.dispatch(new FocusControlEvent(playerData.player));
                    signal.dispatch(new GoToMapEvent(MapRecord.Map.Home));
                    signal.dispatch(new PlaySoundEvent(AudioRecord.PickUp, 0.5f, 0.5f, 0));
                })));
            playerData.boss3 = true;
            playerData.computer3 = 1;
        }
    }

    private void defeatToComputer() {
        if (!playerData.boss4) {
            createTimeline()
                .after(0f, timeline -> signal.dispatch(new ShowStatusBarEvent(false)))
                .after(0f, timeline -> signal.dispatch(new FocusControlEvent(null)))
                .after(0f, timeline -> signal.dispatch(new StartDiologuesEvent(DialoguesRecord.HOME_2, (box, dialogueId) -> {
                    signal.dispatch(new ReturnTitleEvent());
                })));
            playerData.boss4 = true;
        } else {
            signal.dispatch(new ShowStatusBarEvent(false));
            playerData.player.resetBlood();
        }
    }

    private void playerDeath() {
        signal.dispatch(new StopMusicEvent(0.5f));
        createTimeline()
            .after(0f, timeline -> signal.dispatch(new ShowStatusBarEvent(false)))
            .after(1f, timeline -> signal.dispatch(new GoToMapEvent(MapRecord.Map.Home)));
    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent object) {
        if (object instanceof StartGameEvent) {
            gameStart();
        }
        if (object instanceof GoToMapEvent goToMapEvent) {
            goTo(goToMapEvent.map);
        }
        if (object instanceof FightBossEvent fightBossEvent) {
            fightTo(fightBossEvent.nameType);
        }
        if (object instanceof DefeatBossEvent defeatBossEvent) {
            defeatTo(defeatBossEvent.nameType);
        }
        if (object instanceof PlayerDeathEvent) {
            playerDeath();
        }
    }
}
