package org.swdc.fx;


import javafx.stage.Stage;

public abstract class SplashView {

    private FXResources resources;

    public SplashView(FXResources resources) {
        this.resources = resources;
    }

    public abstract Stage getSplash();

}
