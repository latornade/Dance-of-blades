package com.dlg.danceofbaldes;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.ScreenAdapter;

public class TitleScreen extends ScreenAdapter {
    final DanceOfBlades game;

    BitmapFont font;

    int chosenSlot;

    int chosenParam;

    boolean lStickCenterX;
    boolean lStickCenterY;

    boolean rStickCenterX;
    boolean rStickCenterY;

    public TitleScreen(DanceOfBlades gameP) {
        this.game = gameP;
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.NAVY);
        game.batch.begin();
        font.setColor(chosenSlot == 1 ? Color.YELLOW : Color.WHITE);
        font.draw(game.batch, "Sauvegarde 1", 50, 300);
        font.setColor(chosenSlot == 2 ? Color.YELLOW : Color.WHITE);
        font.draw(game.batch, "Sauvegarde 2", 50, 250);
        font.setColor(chosenSlot == 3 ? Color.YELLOW : Color.WHITE);
        font.draw(game.batch, "Sauvegarde 3", 50, 200);

        font.setColor(chosenParam == 1 ? Color.CYAN : Color.WHITE);
        font.draw(game.batch, "Volume", 400, 300);
        font.setColor(chosenParam == 2 ? Color.CYAN : Color.WHITE);
        font.draw(game.batch, "Plein Ecran", 400, 250);
        game.batch.end();
        if (Controllers.getControllers().size > 0) {
            Controller ctrlr = Controllers.getControllers().first();

            float yAxixLeft = ctrlr.getAxis(ctrlr.getMapping().axisLeftY);
            float xAxixLeft = ctrlr.getAxis(ctrlr.getMapping().axisLeftX);

            if (Math.abs(yAxixLeft) < 0.2f) lStickCenterY = true;
            else if (lStickCenterY && yAxixLeft > 0.5f) {
                chosenSlot++;
                lStickCenterY = false;
            } else if (lStickCenterY && yAxixLeft < -0.5f) {
                chosenSlot--;
                lStickCenterY = false;
            }

            if (chosenSlot < 1) chosenSlot = 1;
            if (chosenSlot > 3) chosenSlot = 3;

            if (Math.abs(xAxixLeft) < 0.2f) lStickCenterX = true;
            else if (lStickCenterX && xAxixLeft > 0.5f) {
                game.setScreen(new CombatScreen(game, 160, "music1"));
            } else if (lStickCenterX && xAxixLeft < -0.5f) {
                chosenSlot = 1;
                lStickCenterX = false;
            }

            float axeYDroit = ctrlr.getAxis(ctrlr.getMapping().axisRightY);
            float axeXDroit = ctrlr.getAxis(ctrlr.getMapping().axisRightX);

            if (Math.abs(axeYDroit) < 0.2f) rStickCenterY = true;
            else if (rStickCenterY && axeYDroit > 0.5f) { chosenParam++; rStickCenterY = false; }
            else if (rStickCenterY && axeYDroit < -0.5f) { chosenParam--; rStickCenterY = false; }

            if (chosenParam < 1) chosenParam = 1; if (chosenParam > 2) chosenParam = 2;

            if (Math.abs(axeXDroit) < 0.2f) rStickCenterX = true;
            else if (rStickCenterX && axeXDroit < -0.5f) {
                System.out.println("Paramètre " + chosenParam + " validé !");
                rStickCenterX = false;
            }
            else if (rStickCenterX && axeXDroit > 0.5f) {
                System.out.println("Action annulée dans les paramètres");
                rStickCenterX = false;
            }
        }
    }
}
