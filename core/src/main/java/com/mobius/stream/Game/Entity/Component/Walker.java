package com.mobius.stream.Game.Entity.Component;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mobius.stream.Game.Physic.BodyFactory;
import com.mobius.stream.Game.Physic.PhysicService;
import com.mobius.stream.Game.Physic.RayPoint;

import java.util.HashSet;
import java.util.List;

public class Walker {
    private final HashSet<Fixture> groundFixtures = new HashSet<>();
    private final Vector2 rayNormal = new Vector2();
    private final Vector2 inputVector = new Vector2();
    private final PhysicService physicService;
    private final Body body;
    private final float height;
    private final float PlayerJump = 1f;
    private final float Player_speed = 10f;
    private final float Accelerate_ground = 10f;
    private final float Accelerate_air = 5f;
    private final float rayLength = 1f;
    private final float PlayerDecelerationFactor = 0.9f;
    private final float PlayerRunJump = 30 * MathUtils.degreesToRadians;
    private final Vector2 tempVector1 = new Vector2();
    private final Vector2 tempVector2 = new Vector2();
    private boolean isGrounded = true;

    public Walker(PhysicService physicService, Body body, float height) {
        this.physicService = physicService;
        this.body = body;
        this.height = height;
    }

    public void handleContact(boolean begin, boolean isTouching, Fixture self, Fixture other) {
        if (other.isSensor()) return;
        BodyFactory.Type selfType = (BodyFactory.Type) self.getUserData();
        if (selfType == BodyFactory.Type.FootSensor) {
            redistributeNormal();
            if (begin && isTouching) {
                groundFixtures.add(other);
            } else {
                groundFixtures.remove(other);
            }
            isGrounded = !groundFixtures.isEmpty();
        }
    }

    public void update(Vector2 inputVector) {
        this.inputVector.set(inputVector);
        raycast();
        applySlopeCompensation();
        handleHorizontalMovement();
        handleJump();
        handleGroundDeceleration();
        this.inputVector.setZero();
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    private void raycast() {
        Vector2 start = tempVector1.set(body.getPosition().add(0, -height * 0.25f));
        Vector2 end = tempVector2.set(start).add(0, -rayLength);

        List<RayPoint> hits = physicService.raycast(start, end, 0);
        if (hits.isEmpty()) return;
        RayPoint rayPoint = hits.getFirst();
        rayNormal.set(rayPoint.normal());
    }

    private void redistributeNormal() {
        if (isGrounded) return;
        if (inputVector.y != 0 || inputVector.x == 0) return;
        Vector2 velocity = body.getLinearVelocity();
        Vector2 newDirection = tempVector1.set(rayNormal).rotate90(velocity.x <= 0 ? 1 : -1);
        body.setLinearVelocity(tempVector2.set(velocity.len(), 0).rotateRad(newDirection.angleRad()));
    }

    private void applySlopeCompensation() {
        if (!isGrounded) return;
        Vector2 slopeTangent = tempVector1.set(rayNormal).rotate90(rayNormal.x <= 0 ? -1 : 1);
        float mass = body.getMass();
        float gravity = 10;
        float downSlopeForce = mass * gravity * MathUtils.sin(slopeTangent.angleRad());

        body.applyForceToCenter(slopeTangent.scl(-downSlopeForce), true);
    }

    private void handleHorizontalMovement() {
        if (inputVector.x == 0) return;

        Vector2 moveDirection = tempVector1;
        if (isGrounded) {
            moveDirection.set(rayNormal).rotate90((int) -inputVector.x);
        } else {
            moveDirection.set(inputVector.x, 0);
        }
        Vector2 velocity = body.getLinearVelocity();
        float currentSpeedSq = velocity.len2();
        float maxSpeedSq = Player_speed * inputVector.len();

        if (currentSpeedSq < maxSpeedSq) {
            float acceleration = isGrounded ? Accelerate_ground : Accelerate_air;
            body.applyForceToCenter(moveDirection.scl(acceleration * body.getMass()), true);
        }
    }

    private void handleJump() {
        if (!isGrounded || inputVector.y <= 0) return;
        tempVector1.set(inputVector);
        if (inputVector.x < 0) {
            tempVector1.rotateRad(-PlayerRunJump);
        } else if (inputVector.x > 0) {
            tempVector1.rotateRad(PlayerRunJump);
        }
        body.applyLinearImpulse(tempVector1.scl(PlayerJump * body.getMass()), body.getPosition(), true);
    }

    private void handleGroundDeceleration() {
        if (!isGrounded) return;

        Vector2 velocity = body.getLinearVelocity();
        if (inputVector.dot(velocity) <= 0) {
            velocity.scl(PlayerDecelerationFactor);
            body.setLinearVelocity(velocity);
        }
    }
}
