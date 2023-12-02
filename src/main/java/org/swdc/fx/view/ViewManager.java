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

public class ViewManager extends AbstractDependencyScope {

    private static final Logger logger = LoggerFactory.getLogger(ViewManager.class);
    
    private <T> T initialize(Class clazz,T component) {

        FXResources resources = context.getByClass(FXResources.class);
        ApplicationConfig config = context.getByClass(resources.getDefaultConfig());

        TheView view = (TheView) component;
        AnnotationDescription description = Annotations.findAnnotation(clazz,View.class);
        String fxml = description.getProperty(String.class,"viewLocation");
        if (!fxml.isBlank()) {
            try {
                InputStream inputStream = clazz.getModule().getResourceAsStream(fxml);
                if (inputStream == null){
                    throw new RuntimeException("找不到fxml：" + fxml);
                }
                // 加载fxml
                FXMLLoader loader = new FXMLLoader();
                loader.setResources(resources.getResourceBundle());
                loader.setControllerFactory(context::getByClass);
                Parent parent = loader.load(inputStream);
                view.setView(parent);

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

            if (view instanceof AbstractView) {

                doSetUpStandardStage((AbstractView) view,description,resources);

            } else if (view instanceof AbstractSwingView) {

                doSetUpSwingStage((AbstractSwingView) view,description,resources);

            } else if (view instanceof AbstractSwingDialogView) {

                doSetUpSwingDialogView((AbstractSwingDialogView) view,description,resources);

            }

            if (style == StageStyle.TRANSPARENT) {
                Scene scene = view.getScene();
                scene.setFill(Color.TRANSPARENT);
            }

        }

        String themeName = config.getTheme();
        Theme theme = Theme.getTheme(themeName,resources.getAssetsFolder());
        theme.applyWithView(view);
        view.setTheme(theme);
        view.setContext(this.context);
        view.setDialog(description.getProperty(boolean.class,"dialog"));

        if (view.getController() instanceof ViewController) {
            ViewController controller = view.getController();
            controller.setView(view);
            controller.viewReady();
        }

        return (T)view;
    }


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

    @Override
    public Class getScopeType() {
        return View.class;
    }

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
