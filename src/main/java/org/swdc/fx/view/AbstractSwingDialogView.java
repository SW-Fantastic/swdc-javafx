package org.swdc.fx.view;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import org.swdc.fx.StageCloseEvent;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AbstractSwingDialogView extends TheView {

    private JDialog stage;

    private JFXPanel root;


    public void show(){
        if (this.stage != null) {
            if(stage.isShowing()) {
                this.stage.toFront();
            } else {
                this.stage.setVisible(true);
            }
        }
    }

    public void hide(){
        if (this.stage != null && this.stage.isVisible()) {
            this.stage.setVisible(false);
        }
    }

    public JDialog getStage() {
        return stage;
    }

    void setStage(JDialog stage) {
        Node view = view();
        if (view == null) {
            view = this.render();
            if (view == null) {
                throw new RuntimeException("View不存在或加载失败。");
            }
        }
        root = new JFXPanel();
        if (view instanceof Parent) {
            root.setScene(new Scene((Parent) view));
        } else {
            root.setScene(new Scene(new BorderPane(view)));
        }
        this.stage = stage;
        this.stage.setContentPane(root);
        this.stage.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                emit(new StageCloseEvent(this.getClass()));
            }
        });
    }


    @Override
    Scene getScene() {
        return root.getScene();
    }


}
