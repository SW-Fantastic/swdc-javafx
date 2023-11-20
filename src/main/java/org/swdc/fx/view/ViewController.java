package org.swdc.fx.view;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class ViewController<T extends TheView> implements Initializable {

    private T view;

    private URL url;

    private ResourceBundle bundle;


    void setView(T view) {
        this.view = view;
    }

    protected T getView() {
        return this.view;
    }

    /**
     * Do not override this method.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.url = url;
        this.bundle = resourceBundle;
    }

    void viewReady() {
        this.viewReady(url,bundle);
    }


    protected void viewReady(URL url, ResourceBundle resourceBundle) {

    }

}
