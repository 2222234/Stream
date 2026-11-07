package com.mobius.stream.Resource;

import static com.mobius.stream.Resource.ResourcesRecord.*;

public class MapRecord {
    public static final int tileSize = 16;

    public static float toMeters(float pixels) {
        return pixels / MapRecord.tileSize;
    }

    public static float toPixels(float meters) {
        return meters * MapRecord.tileSize;
    }

    public enum Map {
        Home(Map_Home), Station(Map_Station), Mall(Map_Mall), School(Map_School), Demo(Map_Demo);
        public final String name;

        Map(String name) {
            this.name = name;
        }
    }

    public enum MapProperties {
        Name, CameraX, CameraY, Width, Height, Type
    }

    public enum Point {
        Player, Computer, BossShow, BossSpawn
    }

    public enum Layer {
        Physic, Background, RoadSign, Interaction, Particle, Platform, Point, Generate
    }
}
