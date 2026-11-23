package com.dlg.danceofbaldes;

import java.util.ArrayList;

public class EncounterBuilder {
    float mapX = 0f;
    float mapY = 0f;
    float mapSize = 64f;
    String musicName = "default";
    String mapTexturePath = null;
    ArrayList<Enemy> enemies = new ArrayList<>();

    public EncounterBuilder setPos(float x, float y) {
        this.mapX = x;
        this.mapY = y;
        return this;
    }

    public EncounterBuilder setSize(float size) {
        this.mapSize = size;
        return this;
    }

    public EncounterBuilder setMusic(String music) {
        this.musicName = music;
        return this;
    }

    public EncounterBuilder setTexture(String texture) {
        this.mapTexturePath = texture;
        return this;
    }

    public EncounterBuilder addEnemy(String enemyName) {
        this.enemies.add(EnemyLibrary.create(enemyName));
        return this;
    }

    public EncounterBuilder addEnemies(String enemyName, int amount) {
        for (int i = 0; i <= amount; i++) {
            addEnemy(enemyName);
        }
        return this;
    }

    public Encounter build() {
        if (this.enemies.isEmpty()) throw new IllegalStateException("an encounter must contain at least 1 enemy");
    }
}
