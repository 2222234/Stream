package com.mobius.stream.Game.Entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mobius.stream.Game.Animate.AnimMap;
import com.mobius.stream.Game.Animate.AnimationState;
import com.mobius.stream.Game.Animate.EntityStateMachine;
import com.mobius.stream.Game.Animate.StateLogic;
import com.mobius.stream.Game.Context;
import com.mobius.stream.Game.Entity.Attribute.Attribute;
import com.mobius.stream.Game.Entity.Component.Walker;
import com.mobius.stream.Game.Entity.Interface.Controllable;
import com.mobius.stream.Game.Entity.Interface.FireMode;
import com.mobius.stream.Game.Event.Game.Entity.EntityDeathEvent;
import com.mobius.stream.Game.Event.Game.Entity.EntityHitEvent;
import com.mobius.stream.Game.Map.BPS.Particle.FxEvent;
import com.mobius.stream.Game.Map.NavigationSystem.AIController;
import com.mobius.stream.Game.Physic.ContactHandler;
import com.mobius.stream.Game.Physic.PhysicService;
import com.mobius.stream.Resource.BP.EmitterConfig;

import static com.mobius.stream.Game.Physic.PhysicService.toMeters;

public class EntityActor extends Actor implements Controllable, ContactHandler {
    private final Context context;
    private final Attribute attribute;
    private final Body body;

    private final Walker walker;
    private final AIController aiController;
    private final Vector2 controlInput = new Vector2();
    private final Vector2 moveInput = new Vector2();
    private final EntityStateMachine stateMachine;
    private final Color HIT_COLOR = new Color(1f, 0f, 0f, 1f);
    private final float HIT_DURATION = 0.3f;
    private final Color tmpColor = new Color();
    private final Array<EntityHitEvent> hitQueue = new Array<>();
    private final Array<TextureRegion> holdingRegions = new Array<>();
    private final Array<FireMode> fireModes = new Array<>();
    private boolean autoMove = true;
    private boolean facingRight;
    private float hitTime = 0;
    private float blood;
    private boolean dead = false;
    private EntityActor source;
    private float targetRad;
    private int hold;
    private int holding;
    private boolean fire;
    private FireStateMachine fireStateMachine;

    public EntityActor(Context context, Attribute attribute, StateLogic logic, AnimMap animMap, EntityState initial, Body body) {
        this.context = context;
        this.attribute = attribute;

        blood = attribute.blood();

        this.body = body;
        body.setUserData(this);
        setSize(toMeters(attribute.pixelWidth()), toMeters(attribute.pixelHeight()));

        walker = new Walker(context.physicService(), body, toMeters(attribute.pixelHeight()));
        aiController = new AIController(this, context.navigation().getGraph(), context.navigation().getFlowManager().getFlowField());

        AnimationState animState = new AnimationState(context.resources().getAnimationCache());
        stateMachine = new EntityStateMachine(logic, animMap, animState, initial);

        fireStateMachine = new FireStateMachine(attribute.fireAttribute());
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public Body getBody() {
        return body;
    }

    public float getBlood() {
        return blood;
    }

    public void setBlood(float blood) {
        this.blood = blood;
    }

    public void addFireMode(FireMode fireMode, TextureRegion drawable) {
        fireModes.add(fireMode);
        holdingRegions.add(drawable);
    }

    public Walker getWalker() {
        return walker;
    }

    public AIController getAiController() {
        return aiController;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Vector2 position = body.getPosition();
        setPosition(position.x, position.y, Align.center);
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);

        if (!this.dead) {
            processHit(delta);
            if (autoMove) {
                moveInput.set(aiController.inputX, aiController.inputY);
            } else {
                moveInput.set(controlInput);
            }

            if (moveInput.x < 0) facingRight = false;
            if (moveInput.x > 0) facingRight = true;
            walker.update(moveInput);
            stateMachine.update(delta, this);

            if (!fireModes.isEmpty()) {
                holding += hold;
                if (holding < 0) {
                    holding = fireModes.size - 1;
                }
                holding %= fireModes.size;

                fireStateMachine.update(delta);
                FireMode fireMode = fireModes.get(holding);
                if (fireMode != null && fireStateMachine.fire(fire, delta))
                    fireMode.fire(context, position.x, position.y, targetRad);
            }

            reset();
        }
    }

    private void reset() {
        moveInput.setZero();
        controlInput.setZero();
        hold = 0;
        fire = false;
    }

    public void addHitEvent(EntityHitEvent entityHitEvent) {
        hitQueue.add(entityHitEvent);
    }

    private void processHit(float delta) {
        hitTime = Math.max(0, hitTime - delta);
        hitQueue.iterator().forEach(entityHitEvent -> {
            source = entityHitEvent.source;
            blood -= entityHitEvent.amount;
            Vector2 position = body.getPosition();
            body.applyLinearImpulse(entityHitEvent.knockback, body.getPosition(), true);
            FxEvent fxEvent = context.signalPool().obtain(FxEvent.class);
            fxEvent.emitterConfig = EmitterConfig.Hit;
            fxEvent.x = position.x;
            fxEvent.y = position.y;
            fxEvent.direction = entityHitEvent.knockback.angleRad();
            context.bpsController().pushFxEvent(fxEvent);
            hitTime = HIT_DURATION;
            context.signalPool().free(entityHitEvent);
            if (blood <= 0) {
                dead = true;
            }
        });
        hitQueue.clear();
        if (blood <= 0) {
            context.signal().dispatch(new EntityDeathEvent(source, this));
        }
    }

    public boolean isDead() {
        return dead;
    }

    public float getHitTime() {
        return hitTime;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frame = stateMachine.getAnimState().getFrame();
        if (frame == null) return;

        tmpColor.set(Color.WHITE).lerp(HIT_COLOR, hitTime / HIT_DURATION);
        batch.setColor(tmpColor);
        float width = PhysicService.toMeters(attribute.pixelWidth());
        float height = PhysicService.toMeters(attribute.pixelHeight());
        batch.draw(frame, getX(), getY(),
            width / 2f, height / 2f,
            width, height,
            1f, 1f,
            getRotation());
        batch.setColor(Color.WHITE);

        if (!fireModes.isEmpty()) {
            TextureRegion hold = holdingRegions.get(holding);
            if (hold != null) {
                float holdWidth = PhysicService.toMeters(hold.getRegionWidth()) / 2f;
                float holdHeight = PhysicService.toMeters(hold.getRegionHeight()) / 2f;
                float angle = targetRad * MathUtils.radiansToDegrees;
                boolean flip = -90 < angle && angle < 90;
                batch.draw(hold.getTexture(), getX(), getY(),
                    holdWidth / 2f, holdHeight / 2f,
                    holdWidth, holdHeight,
                    1f, 1f,
                    angle,
                    hold.getRegionX(), hold.getRegionY(),
                    hold.getRegionWidth(), hold.getRegionHeight(), true, !flip);
            }
        }
    }

    public void setAutoMove(boolean autoMove) {
        this.autoMove = autoMove;
    }

    @Override
    public void setTargetInput(float targetRad) {
        this.targetRad = targetRad;
    }

    @Override
    public void fire() {
        this.fire = true;
    }

    @Override
    public void setHold(int flag) {
        hold = flag;
    }

    @Override
    public void beginContact(Fixture self, Fixture other, Contact contact) {
        walker.handleContact(true, contact.isTouching(), self, other);
    }

    @Override
    public void endContact(Fixture self, Fixture other, Contact contact) {
        walker.handleContact(false, contact.isTouching(), self, other);
    }

    @Override
    public void preSolve(Fixture self, Fixture other, Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Fixture self, Fixture other, Contact contact, ContactImpulse impulse) {

    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Vector2 getMoveInput() {
        return moveInput;
    }

    @Override
    public void setMoveInput(Vector2 input) {
        controlInput.set(input);
    }

    public int getHolding() {
        return holding;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void resetBlood() {
        dead = false;
        blood = attribute.blood();
        hitTime = 0;
        body.setFixedRotation(true);
        body.setTransform(body.getPosition(), 0);
        context.bpsController().createPlayer(this);
    }
}
