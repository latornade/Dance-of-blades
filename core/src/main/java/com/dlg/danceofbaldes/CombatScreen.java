package com.dlg.danceofbaldes;

import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.ScreenAdapter;

public class CombatScreen extends ScreenAdapter {
    final DanceOfBlades game;
    Texture charactexture;

    float bpm;
    float beatTime;
    float musicTimer = 0f;
    String music;

    float playerX = 100f;
    float playerY = 100f;
    boolean rStickCentered = true;
    boolean lStickCentered = true;

    String currentAction = "";
    float actionTimer = 0f;
    float startX = 0f;
    float targetX = 0f;

    float rStickX;
    float rStickY;
    float lStickX;
    float lStickY;

    public CombatScreen(DanceOfBlades gameP, float bpmP, String musicP) {
        this.bpm = bpmP;
        this.music = musicP;
        this.beatTime = 60f / bpm;
        this.game = gameP;
    }

    @Override
    public void render(float delta) {
        musicTimer += delta;
        if (musicTimer >= beatTime) {
            musicTimer -= beatTime;
        }
        if (Controllers.getControllers().size > 0) {
            Controller ctrlr = Controllers.getControllers().first();
            rStickX = ctrlr.getAxis(ctrlr.getMapping().axisRightX);
            rStickY = ctrlr.getAxis(ctrlr.getMapping().axisRightY);
            lStickX = ctrlr.getAxis(ctrlr.getMapping().axisLeftX);
            lStickY = ctrlr.getAxis(ctrlr.getMapping().axisLeftY);

            if (rStickCentered && Math.abs(rStickX) > 0.5f && currentAction.isEmpty()) {
                rStickCentered = false;

                float rStickDir = Math.signum(rStickX);
                float lStickDir = Math.signum(lStickX);

                startX = playerX;
                actionTimer = beatTime;

                if (Math.abs(lStickX) > 0.5f) {
                    if (rStickDir == lStickDir) {
                        currentAction = "THROW";
                        targetX = startX + (150f * rStickDir);
                    } else {
                        currentAction = "PARRY";
                    }
                } else {
                    currentAction = "CUT";
                }
            }

            if (!currentAction.isEmpty()) {
                actionTimer -= delta;
                float progression = 1f - (actionTimer / beatTime);
                if (currentAction.equals("THROW")) {
                    playerX = Interpolation.pow3Out.apply(startX, targetX, progression);
                    if (actionTimer <= 0) {
                        playerX = startX;
                        currentAction = "";
                    }
                }
            }

            if (Math.abs(rStickX) < 0.2f && Math.abs(rStickY) < 0.2f) {
                rStickCentered = true;
            } else {
                rStickCentered = false;
            }
            if (Math.abs(lStickX) < 0.2f && Math.abs(lStickY) < 0.2f) {
                lStickCentered = true;
            } else {
                lStickCentered = false;
            }
        }
    }
}
