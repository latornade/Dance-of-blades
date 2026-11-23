package com.dlg.danceofbaldes;

import java.util.ArrayList;
import java.util.List;

public class Encounter {
    float mapX;
    float mapY;
    float mapSize;
    String mapTexturePath;

    String musicName;
    ArrayList<Enemy> enemies = new ArrayList<>();

    public Encounter(float x, float y, float size, String music, String texture, Enemy... enemies) {
        this.mapX = x;
        this.mapY = y;
        this.mapSize = size;
        this.mapTexturePath = texture;

        this.musicName = music;
        for (Enemy enemy : enemies) this.enemies.add(enemy);
    }

    public boolean isDefeated() {
        for (Enemy enemy : this.enemies) {
            if (enemy.isAlive()) return false;
        }
        return true;
    }

    public Enemy getFirstAliveEnemy() {
        for (Enemy enemy : this.enemies) {
            if (enemy.isAlive()) return enemy;
        }
        return null;
    }

    public String getMapTexturePath() {
        return this.mapTexturePath;
    }
}
