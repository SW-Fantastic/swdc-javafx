package org.swdc.fx.view;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public abstract class AbstractView {

    private Object controller;

    private Stage stage;

    private Node view;

    public Node render() {
        return view;
    }

    void setStage(Stage stage) {
        if (view == null) {
            view = this.render();
            if (view == null) {
                throw new RuntimeException("View不存在或加载失败。");
            }
        }
        if (view instanceof Parent) {
            stage.setScene(new Scene((Parent) view));
        } else {
            stage.setScene(new Scene(new BorderPane(view)));
        }
        this.stage = stage;
    }

    void setView(Node view) {
        this.view = view;
    }

    void setController(Object controller) {
        this.controller = controller;
    }

    Stage getStage() {
        return stage;
    }

    public Node getView() {
        return view;
    }

    public <T> T getController() {
        return (T)controller;
    }
}
