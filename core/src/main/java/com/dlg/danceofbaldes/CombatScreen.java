package com.dlg.danceofbaldes;

import com.badlogic.gdx.ScreenAdapter;

public class CombatScreen extends ScreenAdapter {
    float bpm;
    float beatTime = 60f / bpm;
    float musicTimer = 0f;
    String music;

    public CombatScreen(float bpmP, String musicP) {
        this.bpm = bpmP;
        this.music = musicP;
    }

    @Override
    public void render(float delta) {
        musicTimer += delta;
        if (musicTimer >= beatTime) {
            musicTimer -= beatTime;
        }
    }
}
