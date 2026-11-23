package com.dlg.danceofbaldes;

import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.ScreenAdapter;

import java.util.ArrayList;

public class MapScreen extends ScreenAdapter {
    final DanceOfBlades game;

    float charactX = 100f;
    float charactY = 100f;
    float speed = 250f;

    ArrayList<Encounter> encounters = new ArrayList<>();

    public MapScreen(DanceOfBlades gameP) {
        this.game = gameP;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.FOREST);

        if(Controllers.getControllers().size > 0) {
            Controller ctrlr = Controllers.getControllers().first();
            float Xaxis = ctrlr.getAxis(ctrlr.getMapping().axisLeftX);
            float Yaxis = ctrlr.getAxis(ctrlr.getMapping().axisLeftY);

            if(Math.abs(Xaxis) > 0.15f) charactX += Xaxis * speed * delta;
            if(Math.abs(Yaxis) > 0.15f) charactY -= Yaxis * speed * delta;
        }
        game.batch.begin();
        game.batch.draw(game.textures.get("perso.png"), charactX, charactY);
        for (Encounter encounter : encounters) {
            if (!encounter.isDefeated()) {
                Texture encounterTexture = game.textures.get(encounter.getMapTexturePath());
                game.batch.draw(encounterTexture, encounter.mapX, encounter.mapY);
            }
        }
        game.batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
