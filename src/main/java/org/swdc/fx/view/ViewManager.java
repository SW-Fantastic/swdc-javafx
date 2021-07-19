package org.swdc.fx.view;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.swdc.dependency.DependencyContext;
import org.swdc.dependency.DependencyScope;
import org.swdc.dependency.utils.AnnotationDescription;
import org.swdc.dependency.utils.AnnotationUtil;
import org.swdc.fx.FXResources;
import org.swdc.fx.config.ApplicationConfig;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

public class ViewManager implements DependencyScope {
    
    private Map<Class,List<AbstractView>> views = new ConcurrentHashMap<>();
    private Map<String, AbstractView> namedViews = new ConcurrentHashMap<>();
    private Map<Class, List<AbstractView>> abstractViews = new ConcurrentHashMap<>();

    private DependencyContext context;

    private <T> T initialize(Class clazz,T component) {
        AbstractView view = (AbstractView)component;
        AnnotationDescription description = AnnotationUtil.findAnnotation(clazz,View.class);
        String fxml = description.getProperty(String.class,"viewLocation");
        if (!fxml.isBlank()) {
            try {
                InputStream inputStream = clazz.getModule().getResourceAsStream(fxml);
                if (inputStream == null){
                    throw new RuntimeException("找不到fxml：" + fxml);
                }
                // 加载fxml
                FXMLLoader loader = new FXMLLoader();
                loader.setControllerFactory(context::getByClass);
                Parent parent = loader.load(inputStream);
                view.setView(parent);
                view.setController(loader.getController());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 应用theme
        FXResources resources = context.getByClass(FXResources.class);
        ApplicationConfig config = context.getByClass(resources.getDefaultConfig());


        Boolean isStage = description.getProperty(Boolean.class,"stage");
        if (isStage) {
            Stage stage = new Stage();
            stage.setTitle(description.getProperty(String.class,"title"));
            stage.setResizable(description.getProperty(Boolean.class,"resizeable"));
            stage.initStyle(description.getProperty(StageStyle.class,"windowStyle"));
            Boolean isDialog = description.getProperty(Boolean.class,"dialog");
            Class parent = description.getProperty(Class.class,"dialogParent");
            stage.getIcons().addAll(resources.getIcons());
            if (isDialog) {
                stage.initModality(Modality.APPLICATION_MODAL);
            }
            view.setStage(stage);
        }

        String themeName = config.getTheme();
        Theme theme = Theme.getTheme(themeName,resources.getAssetsFolder());
        theme.applyWithView(view);
        view.setTheme(theme);

        return (T)view;
    }

    @Override
    public Class getScopeType() {
        return View.class;
    }

    @Override
    public <T> T put(String name, Class clazz, Class multiple, T component) {
        AbstractView view = (AbstractView) this.put(name,clazz,component);
        if (multiple != null) {
            List<AbstractView> list = abstractViews.getOrDefault(multiple,new ArrayList<>());
            list.add(view);
            abstractViews.put(multiple,list);
        }
        return (T)view;
    }

    @Override
    public <T> T put(String name, Class clazz, T component) {
        if (!AbstractView.class.isAssignableFrom(clazz)) {
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
        List<AbstractView> list = views.getOrDefault(clazz,new ArrayList<>());
        list.add((AbstractView) target);
        views.put(clazz,list);

        if (!name.equals(clazz.getName())) {
            namedViews.put(name,(AbstractView) component);
        }
        return target;
    }

    @Override
    public void setContext(DependencyContext context) {
        this.context = context;
    }

    @Override
    public <T> T getByClass(Class<T> clazz) {
        List<AbstractView> exists =  views.get(clazz);
        if (exists == null || exists.size() == 0) {
            return null;
        }
        View view = clazz.getAnnotation(View.class);
        if (exists.size() > 1 && !view.multiple()) {
            throw new RuntimeException("多个相同的view，请使用getByAbstract");
        } else if (view.multiple()) {
            return null;
        }
        return (T)exists.get(0);
    }

    @Override
    public <T> T getByName(String name) {
        return (T)namedViews.get(name);
    }

    @Override
    public <T> List<T> getByAbstract(Class<T> parent) {
        return (List<T>) abstractViews.get(parent);
    }

    @Override
    public List<Object> getAllComponent() {
        return views.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * 创建一个不在环境中的view，
     * 如果一些特殊的需求，不允许view处于环境中,例如在应用中动态创建
     * 而不是通过注入获取。
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends AbstractView> T createView(Class<T> clazz) {
        try {
            T view = clazz.getConstructor().newInstance();
            AnnotationDescription description = AnnotationUtil.findAnnotation(clazz,View.class);
            String fxml = description.getProperty(String.class,"viewLocation");
            if (!fxml.isBlank()) {
                try {
                    InputStream inputStream = clazz.getModule().getResourceAsStream(fxml);
                    if (inputStream == null){
                        throw new RuntimeException("找不到fxml：" + fxml);
                    }
                    // 加载fxml
                    FXMLLoader loader = new FXMLLoader();
                    Parent parent = loader.load(inputStream);
                    view.setView(parent);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            Boolean isStage = description.getProperty(Boolean.class,"stage");
            if (isStage) {
                Stage stage = new Stage();
                stage.setTitle(description.getProperty(String.class,"title"));
                stage.setResizable(description.getProperty(Boolean.class,"resizeable"));
                stage.initStyle(description.getProperty(StageStyle.class,"windowStyle"));
                Boolean isDialog = description.getProperty(Boolean.class,"dialog");
                if (isDialog) {
                    stage.initModality(Modality.APPLICATION_MODAL);
                }
                view.setStage(stage);
            }

            return view;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
