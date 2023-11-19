package org.swdc.fx.view;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.swdc.dependency.DependencyContext;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.event.AbstractEvent;
import org.swdc.dependency.event.Events;

import java.util.List;

public class TheView implements EventEmitter {

    private Events events;

    private Node view;

    private Object controller;

    private DependencyContext context;

    private boolean isDialog = false;

    private Theme theme;


    void setDialog(boolean isDialog) {
        this.isDialog = isDialog;
    }

    void setTheme(Theme theme) {
        this.theme = theme;
    }

    @Override
    public void setEvents(Events events) {
        this.events = events;
    }

    @Override
    public <T extends AbstractEvent> void emit(T event) {
        this.events.dispatch(event);
    }

    public void show() {

    }

    public void hide() {

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

    Node view() {
        return view;
    }

    DependencyContext context() {
        return context;
    }

    Scene getScene() {
        return null;
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

    private <T> T findById(String id, Object parent) {
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
                    T target = findById(id,item);
                    if (target != null) {
                        return target;
                    }
                }
            }
            return null;
        } else if (parent instanceof ScrollPane) {
            ScrollPane scrollPane = (ScrollPane)parent;
            if (scrollPane.getContent() != null && id.equals(scrollPane.getContent().getId())) {
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
                    T next = findById(id, node);
                    if (next != null) {
                        return next;
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
                if (id.equals(column.getId())) {
                    return (T)column;
                }
            }
        } else if (parent instanceof MenuBar) {
            MenuBar menuBar = (MenuBar) parent;
            for (Menu menu : menuBar.getMenus()) {
                T rst = findByIdForMenu(id,menu);
                if (rst != null) {
                    return rst;
                }
            }
        }
        return null;
    }

    private <T> T findByIdForMenu (String id, Menu menu) {
        if (id.equals(menu.getId())) {
            return (T)menu;
        }
        if (menu.getItems().size() > 0) {
            for (MenuItem item: menu.getItems()) {
                if (id.equals(item.getId())) {
                    return (T)item;
                }
                if (item instanceof Menu) {
                    T rst = findByIdForMenu(id,(Menu) item);
                    if (rst != null) {
                        return rst;
                    }
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


    public <T> T getController() {
        return (T)controller;
    }

    public Node getView() {
        return view;
    }


    public <T extends AbstractView> T getView(Class<T> view) {
        return context.getByClass(view);
    }


    public Node render() {
        return view;
    }

    protected boolean isDialog() {
        return isDialog;
    }

    protected void closed() {

    }


}
