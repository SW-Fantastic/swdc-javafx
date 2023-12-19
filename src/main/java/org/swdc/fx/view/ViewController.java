package org.swdc.fx.view;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 这是View的Controller的基类。
 *
 * 请忽略这个接口：Initializable，务必不要覆盖里面的方法，使用本Controller的
 * viewReady进行初始化工作吧。
 *
 * @param <T> View的类型，可不是指的我定义的什么TheView和AbstractView，而是你继承它们之后的
 *           View，View别忘记标@View的注解。
 */
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

    /**
     * 这个不许重写，你需要的不是这个，
     * 这是我在初始化完毕后的回调，它会调用下面的viewReady。
     */
    void viewReady() {
        this.viewReady(url,bundle);
    }


    /**
     * 是的，就是这里，重写这个方法来初始化
     * 你的Controller和view吧。
     */
    protected void viewReady(URL url, ResourceBundle resourceBundle) {

    }

}
