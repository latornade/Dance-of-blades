package com.dlg.danceofbaldes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Game;
import games.rednblack.miniaudio.MiniAudio;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DanceOfBlades extends Game {
    public SpriteBatch batch;
    public MiniAudio miniAudio;
    public TextureManager textures;
    private Object androidAssets = null;

    public DanceOfBlades() {}

    public DanceOfBlades(Object androidAssets) {
        this.androidAssets = androidAssets;
    }

    @Override
    public void create() {
        this.miniAudio = new MiniAudio();
        if (this.androidAssets != null) miniAudio.setupAndroid(androidAssets);
        batch = new SpriteBatch();
        this.textures = new TextureManager();
        this.setScreen(new TitleScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (getScreen() != null) getScreen().dispose();
        super.dispose();
        miniAudio.dispose();
        textures.dispose();
    }

    @Override
    public void pause() {
        super.pause();
        miniAudio.stopEngine();
    }

    @Override
    public void resume() {
        super.resume();
        miniAudio.startEngine();
    }
}
