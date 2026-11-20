package com.dlg.danceofbaldes;

import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.ScreenAdapter;

public class CombatScreen extends ScreenAdapter {
    MusicManager bgm;

    ActionType queuedAction = ActionType.IDLE;
    float queuedTargetX = 0f;

    final DanceOfBlades game;
    Texture charactexture;

    float actionDuration;

    float playerX = 100f;
    float playerY = 100f;
    boolean rStickCentered = true;
    boolean lStickCentered = true;

    ActionType currentAction = ActionType.IDLE;
    float actionTimer = 0f;
    float startX = 0f;
    float targetX = 0f;

    float rStickX;
    float rStickY;
    float lStickX;
    float lStickY;

    public CombatScreen(DanceOfBlades gameP, String musicP) {
        this.bgm = new MusicManager(musicP);
        this.bgm.setVolume(0.5f, false);
        this.bgm.play();
        this.game = gameP;
        this.charactexture = new Texture("perso.png");
        this.actionDuration = 0.8f * this.bgm.getCurrentBeatDuration();
    }

    @Override
    public void render(float delta) {
        bgm.update();
        // Calcule où on en est dans le battement (de 0.0 au début, à 1.0 à la fin)
        float pulse = bgm.getPulse();
        if (bgm.isNewBeat()) {
            if (Controllers.getControllers().size > 0) {
                Controller ctrlr = Controllers.getControllers().first();
                if (ctrlr.canVibrate()) {
                    int duration = (bgm.isMesureStart()) ? 80 : 50;
                    float strength = (bgm.isMesureStart()) ? 0.4f : 0.15f;
                    ctrlr.startVibration(duration, strength);
                }
            }
        }

        // On inverse pour avoir 1.0 au moment du "boum" et 0.0 à la fin, avec une courbe douce
        float intensity = Interpolation.fade.apply(1f - pulse);

        // On crée une couleur qui va du Blanc (sur le beat) au Saumon (entre les beats)
        Color bgColor = new Color(Color.SALMON).lerp(Color.WHITE, intensity);
        ScreenUtils.clear(bgColor);

        manageInputs();
        animateAction(delta);
        draw();
    }

    private void manageInputs() {
        if (Controllers.getControllers().size == 0) return;

        Controller ctrlr = Controllers.getControllers().first();
        rStickX = ctrlr.getAxis(ctrlr.getMapping().axisRightX);
        rStickY = ctrlr.getAxis(ctrlr.getMapping().axisRightY);
        lStickX = ctrlr.getAxis(ctrlr.getMapping().axisLeftX);
        lStickY = ctrlr.getAxis(ctrlr.getMapping().axisLeftY);

        ActionType desiredAction = ActionType.IDLE;
        if (Math.abs(rStickX) > 0.2f) {
            if (Math.abs(lStickX) > 0.2f) {
                float rStickDir = Math.signum(rStickX);
                float lStickDir = Math.signum(lStickX);
                desiredAction = (rStickDir == lStickDir) ? ActionType.THROW : ActionType.PARRY;
            } else {
                desiredAction = ActionType.CUT;
            }
        } else if (Math.abs(lStickX) > 0.2f) {
            desiredAction = ActionType.STEP;
        }

        boolean idle = currentAction == ActionType.IDLE;
        boolean outDeadzone = (actionTimer < actionDuration * 0.4f);
        boolean emptyQueue = (queuedAction == ActionType.IDLE) && outDeadzone;

        boolean inCooldown = ((currentAction == ActionType.CUT || currentAction == ActionType.STEP) && actionTimer > actionDuration - 0.12f);

        boolean triggerValid = false;

        if (desiredAction == ActionType.STEP && lStickCentered && (idle || emptyQueue)) {
            triggerValid = true;
        }

        if (desiredAction != ActionType.STEP && desiredAction != ActionType.IDLE) {
            if (idle && rStickCentered) {
                triggerValid = true;
            }
            if (inCooldown) {
                triggerValid = true;
            }
            if (emptyQueue) {
                triggerValid = true;
            }
        }

        if (triggerValid) {
            if (desiredAction == ActionType.STEP) lStickCentered = false;
            else rStickCentered = false;

            if (inCooldown && !idle) {
                currentAction = desiredAction;
                if (currentAction != ActionType.CUT) {
                    targetX = startX + (150f * Math.signum(lStickX));
                }
            } else if (idle) {
                startX = playerX;
                currentAction = desiredAction;
                actionDuration = 0.8f * bgm.getCurrentBeatDuration();
                actionTimer = actionDuration;
                if (currentAction != ActionType.CUT) {
                    targetX = startX + (150f * Math.signum(lStickX));
                }
            } else {
                queuedAction = desiredAction;
                float futureStartX = (currentAction == ActionType.STEP) ? targetX : startX;
                switch (queuedAction) {
                    case PARRY :
                    case THROW :
                    case STEP :
                        queuedTargetX = futureStartX + (150f * Math.signum(lStickX));
                        break;
                    case CUT:
                        break;
                    default:
                        break;
                }

            }
        }

        rStickCentered = Math.abs(rStickX) < 0.2f;
        lStickCentered = Math.abs(lStickX) < 0.2f;

    }

    private void animateAction(float delta) {
        if (currentAction == ActionType.IDLE) return;

        actionTimer -= delta;
        float progression = 1f - (actionTimer / actionDuration);

        switch (currentAction) {
            case THROW:
                //System.out.println("THROW");
                if (progression < 0.3f) {
                    float localProg = progression / 0.3f;
                    playerX = Interpolation.pow3Out.apply(startX, targetX, localProg);
                } else {
                    float localProg = (progression - 0.3f) / 0.7f;
                    playerX = Interpolation.pow2Out.apply(targetX, startX, localProg);
                }
                break;
            case CUT:
                //System.out.println("CUT");
                break;
            case PARRY:
                //System.out.println("PARRY");
                if (progression < 0.5f) {
                    float localProg = progression / 0.5f;
                    playerX = Interpolation.pow3Out.apply(startX, targetX, localProg);
                } else {
                    float localProg = (progression - 0.5f) / 0.5f;
                    playerX = Interpolation.pow2Out.apply(targetX, startX, localProg);
                }
                break;
            case STEP:
                //System.out.println("STEP");
                playerX = Interpolation.pow2Out.apply(startX, targetX, progression);
                break;
        }

        if (actionTimer <= 0) {
            if (currentAction == ActionType.THROW || currentAction == ActionType.PARRY) {
                playerX = startX;
            } else if (currentAction == ActionType.STEP) {
                playerX = targetX;
            }
            if (queuedAction != ActionType.IDLE) {
                startX = playerX;
                targetX = queuedTargetX;
                currentAction = queuedAction;
                actionDuration = 0.8f * bgm.getCurrentBeatDuration();
                actionTimer = actionDuration;

                queuedAction = ActionType.IDLE;
            } else {
                currentAction = ActionType.IDLE;
            }
        }
    }

    private void draw() {
        game.batch.begin();
        game.batch.draw(charactexture, playerX, playerY);
        game.batch.end();
    }

    @Override
    public void dispose() {
        charactexture.dispose();
        bgm.dispose();
        super.dispose();
    }
}
