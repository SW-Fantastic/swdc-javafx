package org.swdc.fx.view;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.swdc.fx.StageCloseEvent;

/**
 * 标准的JavaFX窗口
 */
public abstract class AbstractView extends TheView {

    private Stage stage;


    public void show(){
        if (this.stage != null) {
            if(!isDialog()) {
                this.stage.show();
            } else {
                this.stage.showAndWait();
            }
        }
    }

    public void hide(){
        if (this.stage != null && this.stage.isShowing()) {
            this.stage.close();
        }
    }

    void setStage(Stage stage) {
        Node view = getView();
        if (getView() == null) {
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
        this.stage.setOnCloseRequest((e) -> {
            this.emit(new StageCloseEvent(this.getClass()));
            this.hide();
        });
    }

    public Stage getStage() {
        return stage;
    }


    @Override
    Scene getScene() {
        return stage == null ? null : stage.getScene();
    }
}
