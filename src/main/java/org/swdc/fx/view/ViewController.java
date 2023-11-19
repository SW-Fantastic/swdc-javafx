package org.swdc.fx.view;

import javafx.fxml.Initializable;

public interface ViewController<T extends TheView> extends Initializable {

    void setView(T view);

    T getView();

}
