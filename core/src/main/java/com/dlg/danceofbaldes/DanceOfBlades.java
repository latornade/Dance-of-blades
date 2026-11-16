package com.dlg.danceofbaldes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DanceOfBlades extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private Texture perso;
    float persoX = 100f;
    float persoY = 100f;
    float vitesse = 5f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        perso = new Texture("perso.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        if (Controllers.getControllers().size > 0) {
            Controller manette = Controllers.getControllers().first();
            // La suite à l'étape 4 se passe ici
        }
        batch.draw(image, 140, 210);
        batch.draw(perso, persoX, persoY);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        perso.dispose();
    }
}
