package com.mobius.stream.Game.Entity.Attribute;

public record Attribute(NameType nameType, EntityType entityType
    , float blood, FireAttribute fireAttribute
    , int pixelWidth, int pixelHeight
    , int textureWidth, int textureHeight) {
}
