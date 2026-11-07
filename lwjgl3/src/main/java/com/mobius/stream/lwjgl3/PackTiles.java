package com.mobius.stream.lwjgl3;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class PackTiles {
    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();

//        settings.grid = true;
//        settings.minWidth = 256;
//        settings.minHeight = 256;

        settings.paddingX = 0;
        settings.paddingY = 0;
        settings.duplicatePadding = false;
        settings.ignoreBlankImages = false;

        TexturePacker.process(settings, "assets/Texture/Texture/", "assets/Texture/", "Texture");
    }
}
