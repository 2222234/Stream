package com.mobius.stream.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mobius.stream.Resource.Player.PlayerData;

public class Save {
    private static final String SAVE_FILE = "player_save.json";
    private static final Json json = new Json();
    private static PlayerData playerData;

    public static void save() {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        String jsonStr = json.toJson(playerData);
        file.writeString(jsonStr, false);
    }

    public static boolean exists() {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        return file.exists();
    }

    public static PlayerData createNew() {
        playerData = new PlayerData();
        playerData.cost = 500;
        save();
        return playerData;
    }

    public static PlayerData load() {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        if (!file.exists()) {
            return createNew();
        }
        String jsonStr = file.readString();
        playerData = json.fromJson(PlayerData.class, jsonStr);
        return playerData;
    }
}

