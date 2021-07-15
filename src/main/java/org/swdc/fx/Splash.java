package org.swdc.fx;

public abstract class Splash {

    protected FXResources resources;

    public Splash(FXResources resources) {
        this.resources = resources;
    }

    public abstract void show();

    public abstract void hide();

}
