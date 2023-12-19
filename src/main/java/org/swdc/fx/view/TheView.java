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

/**
 * JavaFX View类，提供很多JavaFX的界面的通用行为。
 */
public class TheView implements EventEmitter {

    /**
     * 事件总线
     */
    private Events events;

    /**
     * JavaFX的View的根节点
     */
    private Node view;

    /**
     * 本View的Controller
     */
    private Object controller;

    /**
     * 依赖注入容器
     */
    private DependencyContext context;

    /**
     * 是否为模态窗口
     */
    private boolean isDialog = false;

    /**
     * 该窗口的主题样式。
     */
    private Theme theme;


    void setDialog(boolean isDialog) {
        this.isDialog = isDialog;
    }

    void setTheme(Theme theme) {
        this.theme = theme;
    }

    /**
     * EventBus注入点
     * @param events EventBus对象
     */
    @Override
    public void setEvents(Events events) {
        this.events = events;
    }

    /**
     * 发送Event
     * @param event 事件对象
     * @param <T>
     */
    @Override
    public <T extends AbstractEvent> void emit(T event) {
        this.events.dispatch(event);
    }

    /**
     * 子类需要Override本方法，
     * 展示一个窗口。
     */
    public void show() {

    }

    /**
     * 子类需要Override本方法
     * 隐藏一个窗口。
     */
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

    /**
     * 子类需要Override本方法
     * 并返回Scene。
     * @return JavaFX Scene
     */
    Scene getScene() {
        return null;
    }


    /**
     * 按照Id搜索组件（不是fx:id！）
     *
     * 如果组件类型不匹配，可能出现ClassCastException
     *
     * @param id 组件的Id
     * @return 找到的组件
     * @param <T> 组件的类型
     */
    public <T> T findById(String id) {
        // 直接lookup，找得到就直接返回
        T look = (T) (this.getView()).lookup("#" + id);
        if (look != null) {
            return look;
        }
        if (view instanceof SplitPane) {
            // splitPane的内容可能找不到，进一步进行搜索
            return findById(id,view);
        }
        if (Parent.class.isAssignableFrom(view.getClass())) {
            // 依然没有找到，遍历Children挨个查询和对比
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

    /**
     * 按照Id查找组件
     *
     * 部分组件无法直接查询到，所以需要进一步搜索。
     *
     * @param id 组件的id
     * @param parent 父组件
     * @return 找到的组件或者null
     * @param <T> 组件类型
     */
    private <T> T findById(String id, Object parent) {
        if (parent instanceof ToolBar) {
            // 查找Toolbar的Item
            ToolBar toolBar = (ToolBar) parent;
            List<Node> tools = toolBar.getItems();
            for (Node item: tools) {
                if (id.equals(item.getId())) {
                    return (T)item;
                }
            }
            return null;
        } else if (parent instanceof SplitPane) {
            // 搜索SplitPane
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
            // 搜索scrollpane
            ScrollPane scrollPane = (ScrollPane)parent;
            if (scrollPane.getContent() != null && id.equals(scrollPane.getContent().getId())) {
                return (T)scrollPane.getContent();
            } else {
                return findById(id,scrollPane.getContent());
            }
        } else if (parent instanceof Pane) {
            // 查找普通的panel
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
            // 搜索TabPane
            List<Tab> tabs = ((TabPane) parent).getTabs();
            for (Tab tab: tabs) {
                T target = this.findById(id,tab.getContent());
                if (target != null) {
                    return target;
                }
                return null;
            }
        } else if (parent instanceof TableView) {
            // 搜索TableColumn
            List<TableColumn> columns = ((TableView)parent).getColumns();
            for (TableColumn column: columns) {
                if (id.equals(column.getId())) {
                    return (T)column;
                }
            }
        } else if (parent instanceof MenuBar) {
            // 搜索Menu。
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

    /**
     * 创建一个JavaFX的Alert窗口（但不会弹出，你需要手动弹出它）
     * @param title 窗口标题
     * @param message 窗口的信息内容
     * @param type 窗口的类型
     * @return Alert窗口对象
     */
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


    /**
     * 得到一个JavaFX View对象，之所以你会看到这个，是因为
     * 你需要通过这个得到非单例的JavaFX View，否则可以完全
     * 使用注入的方式。
     *
     * @param view View的类型
     * @return 获取到的view
     * @param <T> view的类型。
     */
    public <T extends AbstractView> T getView(Class<T> view) {
        return context.getByClass(view);
    }


    /**
     * 如果你不想使用JavaFX的Fxml，你完全可以实现本方法，
     * 通过这里的render返回一个Node。
     * @return
     */
    public Node render() {
        return view;
    }

    protected boolean isDialog() {
        return isDialog;
    }

    protected void closed() {

    }


}
