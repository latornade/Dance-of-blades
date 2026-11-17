package com.dlg.danceofbaldes;

import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.ScreenAdapter;

public class CombatScreen extends ScreenAdapter {
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

    public CombatScreen(float bpmP, String musicP) {
        this.bpm = bpmP;
        this.music = musicP;
        this.beatTime = 60f / bpm;
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

            if (rStickCentered && rStickX > 0.5f && currentAction.isEmpty()) {
                rStickCentered = false;
                if (lStickX > 0.5f) {
                    currentAction = "THROW";
                } else {
                    currentAction = "CUT";
                }
                startX = playerX;
                actionTimer = beatTime;
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
