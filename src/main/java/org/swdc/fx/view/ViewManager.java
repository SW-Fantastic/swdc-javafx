package org.swdc.fx.view;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.dependency.AbstractDependencyScope;
import org.swdc.dependency.DependencyContext;
import org.swdc.fx.FXResources;
import org.swdc.fx.config.ApplicationConfig;
import org.swdc.ours.common.annotations.AnnotationDescription;
import org.swdc.ours.common.annotations.Annotations;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.ResourceBundle;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

/**
 * 这里是View管理器，所有的JavaFX View都会集中在这，同时它是也是一个Scope。
 */
public class ViewManager extends AbstractDependencyScope {

    private static final Logger logger = LoggerFactory.getLogger(ViewManager.class);

    /**
     * 本方法用来初始化一个JavaFX View
     * @param clazz view的类型的class对象
     * @param component view对象（这个是来自注入框架提供的已经进行注入但是尚未初始化的view）
     * @return 初始化后的view
     * @param <T> view类型
     */
    private <T> T initialize(Class clazz,T component) {

        FXResources resources = context.getByClass(FXResources.class);
        ApplicationConfig config = context.getByClass(resources.getDefaultConfig());

        TheView view = (TheView) component;
        AnnotationDescription description = Annotations.findAnnotation(clazz,View.class);
        String fxml = description.getProperty(String.class,"viewLocation");
        if (!fxml.isBlank()) {
            try {
                // 这里的话，肯定是指定了fxml的，但是请注意，你的view绝对不能放在views这个package里面，
                // 不然会和我的views包冲突的。
                InputStream inputStream = clazz.getModule().getResourceAsStream(fxml);
                if (inputStream == null){
                    throw new RuntimeException("找不到fxml：" + fxml);
                }
                // 加载fxml
                FXMLLoader loader = new FXMLLoader();
                loader.setResources(resources.getResourceBundle());
                loader.setControllerFactory(context::getByClass);
                // 你猜我为啥特地指定Controller呢？
                // 如果View是来自其他ModuleLayer的，本模块的classLoader当然找不到它的
                // Controller了，所以必须指定Loader保证Controller的加载。
                loader.setClassLoader(view.getClass().getClassLoader());
                Parent parent = loader.load(inputStream);
                view.setView(parent);
                // 加载Controller
                Object ctrl = loader.getController();
                view.setController(ctrl);
            } catch (Exception e) {
                logger.error("failed to initialized view", e);
                throw new RuntimeException(e);
            }
        }


        Boolean isStage = description.getProperty(Boolean.class,"stage");
        StageStyle style = description.getProperty(StageStyle.class,"windowStyle");

        if (isStage) {
            // 这表示View需要一个Stage（窗口）
            if (view instanceof AbstractView) {
                // 标准JavaFX窗口
                doSetUpStandardStage((AbstractView) view,description,resources);

            } else if (view instanceof AbstractSwingView) {
                // 标准Swing窗口
                doSetUpSwingStage((AbstractSwingView) view,description,resources);

            } else if (view instanceof AbstractSwingDialogView) {
                // Swing的Dialog窗口
                doSetUpSwingDialogView((AbstractSwingDialogView) view,description,resources);

            }

            if (style == StageStyle.TRANSPARENT) {
                // 窗口透明的处理。
                Scene scene = view.getScene();
                scene.setFill(Color.TRANSPARENT);
            }

        }

        // 开始为窗口增加样式和主题效果。
        String themeName = config.getTheme();
        Theme theme = Theme.getTheme(themeName,resources.getAssetsFolder());
        theme.applyWithView(view);
        view.setTheme(theme);
        view.setContext(this.context);
        view.setDialog(description.getProperty(boolean.class,"dialog"));

        if (view.getController() instanceof ViewController) {
            // 此时窗口已经准备完毕，存在Controller的话就可以让它进行初始化了。
            ViewController controller = view.getController();
            controller.setView(view);
            controller.viewReady();
        }

        return (T)view;
    }


    /**
     * 根据注解初始化一个标准的JavaFX窗口
     * @param stdView JavaFX View
     * @param description view的class标注的注解信息
     * @param resources JavaFX资源对象
     */
    private void doSetUpStandardStage(AbstractView stdView,AnnotationDescription description,FXResources resources) {

        Boolean isDialog = description.getProperty(Boolean.class,"dialog");
        StageStyle style = description.getProperty(StageStyle.class,"windowStyle");
        ResourceBundle bundle = resources.getResourceBundle();

        Stage stage = new Stage();
        String title = description.getProperty(String.class,"title");
        stage.setTitle(title.startsWith("%") ? bundle.getString(title.substring(1)): title);
        stage.setResizable(description.getProperty(Boolean.class,"resizeable"));
        stage.initStyle(style);

        stage.getIcons().addAll(resources.getIcons());
        if (isDialog) {
            stage.initModality(Modality.APPLICATION_MODAL);
        }
        stdView.setStage(stage);

    }

    /**
     * 根据注解初始化一个标准的Swing窗口
     * @param swingView Swing View
     * @param description view的class标注的注解信息
     * @param resources JavaFX资源对象
     */
    private void doSetUpSwingStage(AbstractSwingView swingView,AnnotationDescription description,FXResources resources) {

        StageStyle style = description.getProperty(StageStyle.class,"windowStyle");
        ResourceBundle bundle = resources.getResourceBundle();

        JFrame frame = new JFrame();
        String title = description.getProperty(String.class,"title");
        frame.setTitle(title.startsWith("%") ? bundle.getString(title.substring(1)): title);
        frame.setResizable(description.getProperty(Boolean.class,"resizeable"));

        switch (style) {
            case DECORATED:
                frame.setType(Window.Type.NORMAL);
                frame.setUndecorated(false);
                break;
            case UNDECORATED:
                frame.setType(Window.Type.NORMAL);
                frame.setUndecorated(true);
                break;
            case UTILITY:
                frame.setType(Window.Type.UTILITY);
                break;
            case TRANSPARENT:
                frame.setType(Window.Type.UTILITY);
                frame.setUndecorated(true);
                frame.setBackground(new java.awt.Color(0,0,0,0));
        }
        frame.setIconImages(
                resources.getIcons()
                        .stream()
                        .map(img -> SwingFXUtils.fromFXImage(img,null))
                        .collect(Collectors.toList())
        );
        swingView.setStage(frame);
    }

    /**
     * 根据注解初始化一个Swing Dialog窗口
     * @param swingView Swing View
     * @param description view的class标注的注解信息
     * @param resources JavaFX资源对象
     */
    private void doSetUpSwingDialogView(AbstractSwingDialogView swingView,AnnotationDescription description,FXResources resources) {

        Boolean isDialog = description.getProperty(Boolean.class,"dialog");
        StageStyle style = description.getProperty(StageStyle.class,"windowStyle");
        ResourceBundle bundle = resources.getResourceBundle();

        JDialog frame = new JDialog();
        String title = description.getProperty(String.class,"title");
        frame.setTitle(title.startsWith("%") ? bundle.getString(title.substring(1)): title);
        frame.setResizable(description.getProperty(Boolean.class,"resizeable"));
        switch (style) {
            case DECORATED:
                frame.setType(Window.Type.NORMAL);
                frame.setUndecorated(false);
                break;
            case UNDECORATED:
                frame.setType(Window.Type.NORMAL);
                frame.setUndecorated(true);
                break;
            case UTILITY:
                frame.setType(Window.Type.UTILITY);
                break;
            case TRANSPARENT:
                frame.setType(Window.Type.UTILITY);
                frame.setUndecorated(true);
                frame.setBackground(new java.awt.Color(0,0,0,0));
        }
        frame.setIconImages(
                resources.getIcons()
                        .stream()
                        .map(img -> SwingFXUtils.fromFXImage(img,null))
                        .collect(Collectors.toList())
        );
        swingView.setStage(frame);
        if (isDialog) {
            frame.setModal(true);
        }

    }

    /**
     * 包含@View注解的类是属于本Scope的
     * @return
     */
    @Override
    public Class getScopeType() {
        return View.class;
    }

    /**
     * 注入框架将会在添加组件的时候调用本方法
     * @param name 组件名
     * @param clazz 组件类型
     * @param component 组件对象
     * @return 初始化完毕的组件
     * @param <T> 组件类型
     */
    @Override
    public <T> T put(String name, Class clazz, T component) {
        if (!TheView.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("不是一个View：" + clazz.getName());
        }
        T target = null;
        if (Platform.isFxApplicationThread()){
            target = this.initialize(clazz,component);
        } else {
            try {
                FutureTask<T> task = new FutureTask<>(() -> this.initialize(clazz,component));
                Platform.runLater(task);
                target = task.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (target == null) {
            return null;
        }

        AnnotationDescription desc = Annotations.findAnnotation(clazz,View.class);
        boolean multipleViews = desc.getProperty(boolean.class,"multiple");
        if (multipleViews) {
            return target;
        }
        return super.put(name,clazz,target);
    }

    @Override
    public void setContext(DependencyContext context) {
        this.context = context;
    }

    @Override
    public <T> T getByClass(Class<T> clazz) {
        if (!TheView.class.isAssignableFrom(clazz)) {
            return null;
        }
        return super.getByClass(clazz);
    }


}
