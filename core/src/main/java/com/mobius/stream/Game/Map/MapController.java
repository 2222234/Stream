package com.mobius.stream.Game.Map;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Game.Context;
import com.mobius.stream.Game.Event.Game.Entity.CreateParticleBoxEvent;
import com.mobius.stream.Game.Event.Game.Entity.EntityHitEvent;
import com.mobius.stream.Game.Event.Game.Entity.RemoveParticleBoxEvent;
import com.mobius.stream.Game.Event.Game.Map.ChangeMapEvent;
import com.mobius.stream.Game.Event.Game.Map.MapUpdated;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Event.Ui.ShowTitleEvent;
import com.mobius.stream.Game.Map.BPS.BPSController;
import com.mobius.stream.Game.Map.NavigationSystem.Navigation;
import com.mobius.stream.Game.Physic.PhysicService;
import com.mobius.stream.Resource.MapRecord;
import com.mobius.stream.Resource.Player.PlayerData;
import com.mobius.stream.Resource.Resources;
import com.mobius.stream.Stage.Component.ToastOverlay;

import static com.mobius.stream.Game.Physic.PhysicService.toMeters;

public class MapController implements Listener<GameEvent> {
    private final Resources resources;
    private final Stage stage;
    private final OrthographicCamera camera;
    private final Signal<GameEvent> signal;

    private final Render render;
    private final PhysicService physicService;

    private final BPSController bpsController;
    private final Generate generate;
    private final Navigation navigation;

    private final Box2DDebugRenderer debugRenderer;
    private final Context context;

    public MapController(Resources resources, Signal<GameEvent> signal, Stage stage, PlayerData playerData) {
        this.resources = resources;
        this.signal = signal;
        this.stage = stage;

        PoolManager signalPool = new PoolManager();
        signalPool.addPool(EntityHitEvent.class, EntityHitEvent::new);

        camera = (OrthographicCamera) stage.getCamera();

        render = new Render();
        navigation = new Navigation();
        signal.add(navigation);
        physicService = new PhysicService();
        bpsController = new BPSController(signalPool, signal, resources.getAtlas());

        context = new Context(resources, signalPool, signal, physicService, bpsController, navigation, stage, playerData);
        generate = new Generate(context);
        signal.add(generate);

        debugRenderer = new Box2DDebugRenderer();
    }

    public void changeMap(MapRecord.Map map) {
        TiledMap tiledMap = resources.getMap(map);
        Rectangle rectangle = computeMapBounds(tiledMap);

        stage.clear();
        if (context.playerData().player != null) {
            stage.addActor(context.playerData().player);
        }

        bpsController.changeMap(tiledMap);
        bpsController.setBounds(rectangle);
        render.changeMap(tiledMap);
        physicService.changeMap(context, tiledMap);
        navigation.init(tiledMap);
        generate.changeMap(map, tiledMap);

        signal.dispatch(new ShowTitleEvent(ToastOverlay.Type.Main, tiledMap.getProperties().get(MapRecord.MapProperties.Name.name(), String.class), 2));
        signal.dispatch(new MapUpdated(rectangle, true));
    }

    private Rectangle computeMapBounds(TiledMap tiledMap) {
        float unitScale = toMeters(1f);
        MapProperties properties = tiledMap.getProperties();
        int width = properties.get(MapRecord.MapProperties.Width.name(), Integer.class);
        int height = properties.get(MapRecord.MapProperties.Height.name(), Integer.class);
        return new Rectangle(
            0, 0,
            width * MapRecord.tileSize * unitScale,
            height * MapRecord.tileSize * unitScale
        );
    }

    public void update(float delta) {
        physicService.update(delta);
        render.render(camera);
        stage.act(delta);
        stage.draw();

        bpsController.update(delta, camera);
        navigation.update(delta, camera);
        generate.update(delta);

//        debugRenderer.render(physicService.getWorld(), camera.combined);
    }

    public void dispose() {
        debugRenderer.dispose();
        render.dispose();
        bpsController.dispose();
        navigation.dispose();
    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent object) {
        if (object instanceof ChangeMapEvent changeMapEvent) {
            changeMap(changeMapEvent.map);
            return;
        }
        if (object instanceof CreateParticleBoxEvent createParticleBoxEvent) {
            bpsController.addParticleBox(createParticleBoxEvent.particle, createParticleBoxEvent.x, createParticleBoxEvent.y);
            return;
        }
        if (object instanceof RemoveParticleBoxEvent removeParticleBoxEvent) {
            bpsController.removeParticleBox(removeParticleBoxEvent.x, removeParticleBoxEvent.y);
        }
    }
}
