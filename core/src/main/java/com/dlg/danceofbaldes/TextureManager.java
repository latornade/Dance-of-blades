package com.dlg.danceofbaldes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class TextureManager {
    HashMap<String, Texture> textures = new HashMap<>();

    public Texture get (String path) {
        String target = "default_enemy.png";
        if (path != null && Gdx.files.internal(path).exists()) target = path;
        if (!textures.containsKey(target)) this.textures.put(target, new Texture(Gdx.files.internal(target)));
        return textures.get(target);
    }

    public void dispose() {
        for (String key : textures.keySet()) {
            textures.get(key).dispose();
        }
        textures.clear();
    }
}
