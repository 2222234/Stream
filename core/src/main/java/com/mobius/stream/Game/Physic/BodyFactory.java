package com.mobius.stream.Game.Physic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mobius.stream.Game.Entity.Attribute.Attribute;

import static com.mobius.stream.Game.Physic.PhysicService.toMeters;

public class BodyFactory {
    public static Body buildBody(Attribute attribute, World world, float x, float y) {
        Body body = createBody(world, x, y);
        buildFixture(body, Math.min(attribute.pixelWidth(), attribute.pixelHeight()), attribute.pixelHeight());
        return body;
    }

    private static Body createBody(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(toMeters(x), toMeters(y));
        bodyDef.fixedRotation = true;
        return world.createBody(bodyDef);
    }

    private static void buildFixture(Body body, float pixelWidth, float pixelHeight) {
        FixtureDef circle = new FixtureDef();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(toMeters(pixelWidth / 2));
        circleShape.setPosition(new Vector2(0, toMeters((pixelHeight - pixelWidth) / 2)));
        circle.shape = circleShape;
        circle.density = 1f;
        circle.friction = 0.1f;
        circle.restitution = 0f;
        body.createFixture(circle).setUserData(Type.Head);

        circleShape.setPosition(new Vector2(0, toMeters(-(pixelHeight - pixelWidth) / 2)));
        body.createFixture(circle).setUserData(Type.Foot);
        circleShape.dispose();

        if (pixelHeight - pixelWidth > 0) {

            FixtureDef polygon = new FixtureDef();
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setAsBox(toMeters((pixelWidth - 1) / 2), toMeters((pixelHeight - pixelWidth) / 2));
            polygon.shape = polygonShape;
            polygon.density = 1f;
            polygon.friction = 0.1f;
            polygon.restitution = 0f;
            body.createFixture(polygon).setUserData(Type.Body);
            polygonShape.dispose();
        }

        FixtureDef circle1 = new FixtureDef();
        CircleShape circle1Shape = new CircleShape();
        circle1Shape.setRadius(toMeters(pixelWidth / 2 - 1));
        circleShape.setPosition(new Vector2(0, toMeters(-(pixelHeight - pixelWidth) / 2 - 2)));
        circle1.shape = circle1Shape;
        circle1.density = 0f;
        circle1.friction = 0f;
        circle1.restitution = 0f;
        circle1.isSensor = true;
        body.createFixture(circle1).setUserData(Type.FootSensor);
        circle1Shape.dispose();

        FixtureDef polygon1 = new FixtureDef();
        PolygonShape polygon1Shape = new PolygonShape();
        polygon1Shape.setAsBox(toMeters(pixelWidth / 2 + 1), toMeters(pixelHeight / 2 + 1));
        polygon1.shape = polygon1Shape;
        polygon1.density = 0f;
        polygon1.friction = 0f;
        polygon1.restitution = 0f;
        polygon1.isSensor = true;
        body.createFixture(polygon1).setUserData(Type.HitBoxSensor);
        polygon1Shape.dispose();
    }

    public enum Type {
        Head, Body, Foot, FootSensor, HitBoxSensor
    }
}
