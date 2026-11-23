package com.dlg.danceofbaldes;

import java.util.HashMap;

public class EnemyLibrary {
    private static final HashMap<String, EnemyTemplate> availableEnemies = new HashMap<String, EnemyTemplate>();

    static {
        availableEnemies.put("default", new EnemyTemplate("default", 2, "perso2.png"));
    }

    public void register(String enemyName, EnemyTemplate enemyTemplate) {
        if (!availableEnemies.containsKey(enemyName)) {
            availableEnemies.put(enemyName, enemyTemplate);
        }
    }

    public static Enemy create(String name) {
        String returnEnemy = "default";
        if (availableEnemies.containsKey(name)) returnEnemy = name;
        return new Enemy(availableEnemies.get(returnEnemy));
    }
}
