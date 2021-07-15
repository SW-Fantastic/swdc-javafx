package org.swdc.fx;


import javafx.stage.Stage;

public abstract class SplashView extends Splash {

    public SplashView(FXResources resources) {
        super(resources);
    }

    protected abstract Stage getSplash();

    @Override
    public void show() {
        this.getSplash().show();
    }

    @Override
    public void hide() {
        this.getSplash().hide();
    }
}
