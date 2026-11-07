package com.mobius.stream.Game.Dialogue;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mobius.stream.Resource.ResourcesRecord;

public class DialogueLoader
    extends AsynchronousAssetLoader<DialogueDatabase, DialogueLoader.Param> {

    private DialogueDatabase data;

    public DialogueLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName,
                                                  FileHandle file,
                                                  Param parameter) {
        Array<AssetDescriptor> deps = new Array<>();
        deps.add(new AssetDescriptor<>(ResourcesRecord.Texture, TextureAtlas.class));
        return deps;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName,
                          FileHandle file, Param parameter) {
        data = new Json().fromJson(DialogueDatabase.class, file);
    }

    @Override
    public DialogueDatabase loadSync(AssetManager manager, String fileName,
                                     FileHandle file, Param parameter) {
        DialogueDatabase result = data;
        data = null;
        return result;
    }

    public static class Param extends AssetLoaderParameters<DialogueDatabase> {
    }
}
