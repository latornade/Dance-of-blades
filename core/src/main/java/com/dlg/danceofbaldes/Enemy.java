package com.dlg.danceofbaldes;

public class Enemy {
    String name;
    int maxHp;
    int currentHp;
    String texturePath;
    float x = 200f;
    float y = 100f;

    public Enemy(String nameP, int maxHpP, String texture) {
        this.name = nameP;
        this.maxHp = maxHpP;
        this.currentHp = this.maxHp;
        this.texturePath = texture;
    }

    public Enemy(EnemyTemplate template) {
        this.name = template.name();
        this.maxHp = template.maxHp();
        this.currentHp = this.maxHp;
        this.texturePath = template.texture();
    }

    public void takeDamage(int amount) {
        this.currentHp -= amount;
        if (this.currentHp < 0) {
            this.currentHp = 0;
        }
    }

    public boolean isAlive() {
        return this.currentHp != 0;
    }

    public String getName() {
        return this.name;
    }

    public int getCurrentHp() {
        return this.currentHp;
    }

    public int getMaxHp() {
        return this.maxHp;
    }

    public String getTexturePath() { return this.texturePath; }

    public float getX() { return this.x; }

    public float getY() { return this.y; }
}
