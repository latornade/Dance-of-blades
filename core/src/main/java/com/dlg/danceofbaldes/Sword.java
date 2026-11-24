package com.dlg.danceofbaldes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;

public class Sword {
    TextureRegion region;
    float x;
    float y;
    float originX;
    float originY;
    float rotation;

    public Sword(DanceOfBlades game) {
        Texture texture = game.textures.get("perso4.png");
        this.region = new TextureRegion(texture);
    }

    public void draw(SpriteBatch batch) {
        int width = region.getRegionWidth();
        int height = region.getRegionHeight();
        batch.draw(region, x - originX, y - originY, originX, originY, width, height, 1f, 1f, rotation);
    }

    public void update(float anchorX, float anchorY, ActionType action, float progression, float dir) {
        originX = region.getRegionWidth() / 2f;
        originY = region.getRegionHeight() * 0.15f;
        switch (action) {
            case IDLE, STEP -> {
                x = anchorX + 15f * dir;
                y = anchorY;
                rotation = -25f * dir;
            }
            case CUT -> {
                x = anchorX + 25f * dir;
                y = anchorY;
                rotation = Interpolation.pow4Out.apply(45f * dir, -135f * dir, progression);
            }
            case PARRY -> {
                float offsetX = 45f * dir;
                rotation = -10f * dir;
                if (progression < 0.25) {
                    float localProg = progression / 0.25f;
                    offsetX = Interpolation.pow3Out.apply(15f * dir, 45f * dir, localProg);
                    rotation = Interpolation.pow3Out.apply(-25f * dir, -10f * dir, localProg);
                } else if (progression > 0.75) {
                    float localProg = (progression - 0.75f) / 0.25f;
                    offsetX = Interpolation.pow2Out.apply(45f * dir, 15f * dir, localProg);
                    rotation = Interpolation.pow2Out.apply(-10f * dir, -25f * dir, localProg);
                }
                x = anchorX + offsetX;
                y = anchorY;
            }
            case THROW -> {
                float offsetX;
                if (progression < 0.1) {
                    float localProg = progression / 0.1f;
                    rotation = Interpolation.pow3Out.apply(-25f * dir, -90f * dir, localProg);
                } else {
                    rotation = -90f * dir;
                }
                if (progression < 0.35f) {
                    float localProg = progression / 0.35f;
                    offsetX = Interpolation.pow3Out.apply(15f * dir, 280f * dir, localProg);
                } else {
                    float localProg = (progression - 0.35f) / 0.65f;
                    offsetX = Interpolation.pow2Out.apply(280f * dir, 15f * dir, localProg);
                }
                x = anchorX + offsetX;
                y = anchorY;
            }
        }
    }
}
