package org.swdc.fx.view;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.swdc.dependency.DependencyContext;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.event.AbstractEvent;
import org.swdc.dependency.event.Events;
import org.swdc.fx.StageCloseEvent;

import java.util.List;

public abstract class AbstractView implements EventEmitter {

    private Object controller;

    private Stage stage;

    private Node view;

    private Events events;

    private Theme theme;

    private DependencyContext context;

    private boolean isDialog = false;

    public Node render() {
        return view;
    }

    public void show(){
        if (this.stage != null) {
            if(!isDialog) {
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
        this.stage.setOnCloseRequest((e) -> {
            this.emit(new StageCloseEvent(this.getClass()));
            this.hide();
        });
    }

    protected void closed() {

    }

    void setView(Node view) {
        this.view = view;
    }

    void setController(Object controller) {
        this.controller = controller;
    }

    void setContext(DependencyContext context) {
        this.context = context;
    }

    public Stage getStage() {
        return stage;
    }

    public Node getView() {
        return view;
    }


    public <T extends AbstractView> T getView(Class<T> view) {
        return context.getByClass(view);
    }

    @Override
    public void setEvents(Events events) {
        this.events = events;
    }

    @Override
    public <T extends AbstractEvent> void emit(T event) {
        this.events.dispatch(event);
    }

    public <T> T getController() {
        return (T)controller;
    }

    public <T> T findById(String id) {
        T look = (T) (this.getView()).lookup("#" + id);
        if (look != null) {
            return look;
        }
        if (view instanceof SplitPane) {
            return findById(id,view);
        }
        if (Parent.class.isAssignableFrom(view.getClass())) {
            List<Node> childs = ((Parent)view).getChildrenUnmodifiable();
            for (Node node : childs){
                if (id.equals(node.getId())) {
                    return (T)node;
                } else {
                    T target = findById(id,node);
                    if (target != null) {
                        return target;
                    }
                }
            }
        }
        return null;
    }

    private <T> T findById(String id, Node parent) {
        if (parent instanceof ToolBar) {
            ToolBar toolBar = (ToolBar) parent;
            List<Node> tools = toolBar.getItems();
            for (Node item: tools) {
                if (id.equals(item.getId())) {
                    return (T)item;
                }
            }
            return null;
        } else if (parent instanceof SplitPane) {
            SplitPane splitPane = (SplitPane)parent;
            for (Node item: splitPane.getItems()) {
                if (id.equals(item.getId())) {
                    return (T)item;
                } else {
                    Node target = findById(id,item);
                    if (target != null) {
                        return (T) target;
                    }
                }
            }
            return null;
        } else if (parent instanceof ScrollPane) {
            ScrollPane scrollPane = (ScrollPane)parent;
            if (scrollPane.getContent().getId().equals(id)) {
                return (T)scrollPane.getContent();
            } else {
                return findById(id,scrollPane.getContent());
            }
        } else if (parent instanceof Pane) {
            Pane pane = (Pane)parent;
            for (Node node: pane.getChildren()) {
                if (id.equals(node.getId())) {
                    return (T)node;
                } else {
                    Node next = findById(id, node);
                    if (next != null) {
                        return (T)next;
                    }
                }
            }
            return null;
        } else if (parent instanceof TabPane) {
            List<Tab> tabs = ((TabPane) parent).getTabs();
            for (Tab tab: tabs) {
                T target = this.findById(id,tab.getContent());
                if (target != null) {
                    return target;
                }
                return null;
            }
        } else if (parent instanceof TableView) {
            List<TableColumn> columns = ((TableView)parent).getColumns();
            for (TableColumn column: columns) {
                if (column.getId().equals(id)) {
                    return (T)column;
                }
            }
        }
        return null;
    }

    public Alert alert(String title,String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.setTitle(title);

        theme.applyWithAlert(alert);

        return alert;
    }

    void setDialog(boolean isDialog) {
        this.isDialog = isDialog;
    }

    void setTheme(Theme theme) {
        this.theme = theme;
    }

}
