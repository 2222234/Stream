package com.mobius.stream.Game.Map;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mobius.stream.Game.Context;
import com.mobius.stream.Game.Entity.Attribute.Attribute;
import com.mobius.stream.Game.Entity.Attribute.EntityType;
import com.mobius.stream.Game.Entity.Attribute.NameType;
import com.mobius.stream.Game.Entity.EntityActor;
import com.mobius.stream.Game.Entity.EntityFactory;
import com.mobius.stream.Game.Event.Game.Camera.FocusCameraEvent;
import com.mobius.stream.Game.Event.Game.Entity.ClearEnemyEvent;
import com.mobius.stream.Game.Event.Game.Entity.EntityDeathEvent;
import com.mobius.stream.Game.Event.Game.Entity.GenerateEnemyEvent;
import com.mobius.stream.Game.Event.Game.Entity.SpawnEntityEvent;
import com.mobius.stream.Game.Event.Game.Map.NavigationTargetEvent;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Event.Global.DefeatBossEvent;
import com.mobius.stream.Game.Event.Global.PlayerDeathEvent;
import com.mobius.stream.Game.Event.Ui.ShowTitleEvent;
import com.mobius.stream.Game.Map.BPS.Particle.FxEvent;
import com.mobius.stream.Resource.BP.EmitterConfig;
import com.mobius.stream.Resource.MapRecord;
import com.mobius.stream.Resource.Player.PlayerData;
import com.mobius.stream.Resource.TextRecord;
import com.mobius.stream.Stage.Component.ToastOverlay;

public class Generate implements Listener<GameEvent> {
    private final Context context;
    private final Array<EntityActor> enemys;
    private final Array<Rectangle> generateRegions;
    private final int refreshTime = 5;
    private final int generateTime = 5;
    private final int duration = 1;
    private final int locate = 5;
    private final int max = 50;
    private final float recoverTime = 0.5f;
    private final float ratio = 0.1f;
    private final Vector2 temp = new Vector2();
    private boolean generate;
    private float time;
    private float generateTimer;
    private float recoverTimer;
    private MapRecord.Map mapRecord;

    public Generate(Context context) {
        this.context = context;
        enemys = new Array<>();
        generateRegions = new Array<>();
    }

    private void spawnPlayer(float x, float y) {
        EntityActor entityActor = EntityFactory.createPlayer(context, x, y);
        context.playerData().player = entityActor;
        entityActor.setAutoMove(false);
        context.stage().addActor(entityActor);

        context.bpsController().createPlayer(entityActor);

        context.signal().dispatch(new FocusCameraEvent(entityActor, 1f));
    }

    private void setEnemy(EntityActor entityActor) {
        enemys.add(entityActor);
        context.stage().addActor(entityActor);
        context.bpsController().createEnemy(entityActor);
        context.navigation().addAIController(entityActor);
    }

    public void spawnEntity(NameType nameType, float x, float y) {
        switch (nameType) {
            case Dude -> spawnPlayer(x, y);
            case Clock -> setEnemy(EntityFactory.createClock(context, x, y));
            case Hat -> setEnemy(EntityFactory.createHat(context, x, y));
            case Cook -> setEnemy(EntityFactory.createCook(context, x, y));
            case Computer -> setEnemy(EntityFactory.createComputer(context, x, y));
        }
    }

    public void entityDeath(EntityActor source, EntityActor target) {
        FxEvent fxEvent = context.signalPool().obtain(FxEvent.class);
        fxEvent.emitterConfig = EmitterConfig.Death;
        fxEvent.x = target.getX();
        fxEvent.y = target.getY();
        context.bpsController().pushFxEvent(fxEvent);
        Attribute attribute = target.getAttribute();
        target.getBody().setFixedRotation(false);
        if (attribute.entityType() == EntityType.Enemy) {
            int cost = attribute.nameType().cost;
            PlayerData playerData = context.playerData();
            playerData.cost += cost;
            context.signal().dispatch(new ShowTitleEvent(ToastOverlay.Type.Sub, "+" + context.resources().getString(TextRecord.Text_Money) + cost, 1f));
            destroyEntity(target, true);
        }
        check(target);
    }

    public void check(EntityActor target) {
        switch (target.getAttribute().nameType()) {
            case Dude -> context.signal().dispatch(new PlayerDeathEvent());
            case Clock -> context.signal().dispatch(new DefeatBossEvent(NameType.Clock));
            case Hat -> context.signal().dispatch(new DefeatBossEvent(NameType.Hat));
            case Cook -> context.signal().dispatch(new DefeatBossEvent(NameType.Cook));
            case Computer -> context.signal().dispatch(new DefeatBossEvent(NameType.Computer));
        }
    }

    public void destroyEntity(EntityActor entityActor, boolean removeEnemys) {
        context.physicService().destroy(entityActor.getBody());
        context.bpsController().destroyEntity(entityActor);
        context.navigation().removeAIController(entityActor);
        entityActor.remove();
        if (removeEnemys) {
            if (enemys.contains(entityActor, true)) {
                enemys.removeValue(entityActor, true);
            }
        }
    }

    public void update(float delta) {
        EntityActor player = context.playerData().player;
        if (player != null) {
            context.signal().dispatch(new NavigationTargetEvent(player.getX(), player.getY()));
        }
        time += delta;
        time %= (refreshTime + generateTime);
        generate(delta);
        recover(delta);
        fire();
    }

    private void generate(float delta) {
        if (generate) {
            if (time > refreshTime) {
                generateTimer += delta;
                if (generateTimer >= duration) {
                    if (!generateRegions.isEmpty()) {
                        for (int i = 0; i < locate; i++) {
                            if (enemys.size >= max) break;
                            Rectangle rectangle = generateRegions.get(MathUtils.random(generateRegions.size - 1));
                            chooseEnemy(rectangle.x + MathUtils.random(rectangle.width), rectangle.y + MathUtils.random(rectangle.height));
                        }
                    }
                    time = 0;
                }
            }
        }
    }

    private void recover(float delta) {
        recoverTimer += delta;
        if (recoverTimer >= recoverTime) {
            EntityActor player = context.playerData().player;
            if (!player.isDead()) {
                if (player.getHitTime() == 0) {
                    float blood = Math.min(player.getBlood() + player.getAttribute().blood() * ratio, player.getAttribute().blood());
                    player.setBlood(blood);
                }
            }
            recoverTimer = 0;
        }
    }

    private void fire() {
        EntityActor player = context.playerData().player;
        if (!player.isDead()) {
            enemys.forEach(entityActor -> {
                temp.set(player.getPosition());
                temp.sub(entityActor.getPosition());
                float distance = entityActor.getAttribute().fireAttribute().distance();
                if (distance < 0 || temp.len2() <= distance * distance) {
                    entityActor.setHold(MathUtils.randomSign());
                    entityActor.setTargetInput(temp.angleRad());
                    entityActor.fire();
                }
            });
        }
    }

    private void chooseEnemy(float x, float y) {
        switch (mapRecord) {
            case Station -> {
                if (MathUtils.randomBoolean()) {
                    setEnemy(EntityFactory.createTicket(context, x, y));
                } else {
                    setEnemy(EntityFactory.createTrain(context, x, y));
                }
            }
            case Mall -> {
                if (MathUtils.randomBoolean()) {
                    setEnemy(EntityFactory.createStair(context, x, y));
                } else {
                    setEnemy(EntityFactory.createPos(context, x, y));
                }
            }
            case School -> {
                if (MathUtils.randomBoolean()) {
                    setEnemy(EntityFactory.createCan(context, x, y));
                } else {
                    setEnemy(EntityFactory.createTray(context, x, y));
                }
            }
        }
    }

    private void clearEnemy() {
        enemys.forEach(entityActor -> {
            FxEvent fxEvent = context.signalPool().obtain(FxEvent.class);
            fxEvent.emitterConfig = EmitterConfig.Death;
            fxEvent.x = entityActor.getX();
            fxEvent.y = entityActor.getY();
            context.bpsController().pushFxEvent(fxEvent);
            destroyEntity(entityActor, false);
        });
        enemys.clear();
    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent object) {
        if (object instanceof SpawnEntityEvent spawnEntityEvent) {
            spawnEntity(spawnEntityEvent.nameType, spawnEntityEvent.x, spawnEntityEvent.y);
        }
        if (object instanceof EntityDeathEvent entityDeathEvent) {
            entityDeath(entityDeathEvent.source, entityDeathEvent.target);
        }
        if (object instanceof ClearEnemyEvent) {
            clearEnemy();
        }
        if (object instanceof GenerateEnemyEvent generateEnemyEvent) {
            generate = generateEnemyEvent.generate;
        }
    }

    public void changeMap(MapRecord.Map mapRecord, TiledMap map) {
        this.mapRecord = mapRecord;
        generateRegions.clear();
        MapLayer mapLayer = map.getLayers().get(MapRecord.Layer.Generate.name());
        if (mapLayer == null) return;
        for (MapObject mapObject : mapLayer.getObjects()) {
            generateRegions.add(((RectangleMapObject) mapObject).getRectangle());
        }
    }
}
