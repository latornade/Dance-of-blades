package com.dlg.danceofbaldes;

import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.ScreenAdapter;

public class MapScreen extends ScreenAdapter {
    final DanceOfBlades game;

    Texture perso;
    float charactX = 100f;
    float charactY = 100f;
    float speed = 5f;

    public MapScreen(DanceOfBlades gameP) {
        this.game = gameP;
        perso = new Texture("perso.png");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.FOREST);

        if(Controllers.getControllers().size > 0) {
            Controller ctrlr = Controllers.getControllers().first();
            float Xaxis = ctrlr.getAxis(ctrlr.getMapping().axisLeftX);
            float Yaxis = ctrlr.getAxis(ctrlr.getMapping().axisLeftY);

            if(Math.abs(Xaxis) > 0.15f) charactX += Xaxis * speed;
            if(Math.abs(Yaxis) > 0.15f) charactY -= Yaxis * speed;
        }
        game.batch.begin();
        game.batch.draw(perso, charactX, charactY);
        game.batch.end();
    }

    @Override
    public void dispose() {
        perso.dispose();
    }
}
