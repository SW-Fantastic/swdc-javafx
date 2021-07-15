package org.swdc.fx;

import javafx.stage.Stage;

import javax.swing.*;

public abstract class SwingSplashView extends Splash {

    public SwingSplashView(FXResources resources) {
        super(resources);
    }


    public abstract JWindow getSplash();

    @Override
    public void show() {
        getSplash().setVisible(true);
    }

    @Override
    public void hide() {
        getSplash().setVisible(false);
    }
}
