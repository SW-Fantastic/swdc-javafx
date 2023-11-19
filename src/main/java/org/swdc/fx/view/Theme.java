package org.swdc.fx.view;

import com.asual.lesscss.LessEngine;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.ours.common.annotations.AnnotationDescription;
import org.swdc.ours.common.annotations.Annotations;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Theme {

    private static final Logger logger = LoggerFactory.getLogger(Theme.class);

    private static Map<String,Theme> themes = new ConcurrentHashMap<>();

    private String name;

    private File assetsRoot;

    private boolean ready;

    public Theme(String name, File assets) {
        this.name = name;
        this.assetsRoot = assets;
    }

    /**
     * 编译主题的less
     */
    private void prepare() {
        File themeFolder = assetsRoot.toPath()
                .resolve("skin")
                .resolve(this.name)
                .toFile();
        if (!themeFolder.exists()) {
            throw new RuntimeException("样式的文件夹不存在：" + themeFolder.getAbsolutePath());
        }
        File[] files = themeFolder.listFiles();
        if (files == null) {
            throw new RuntimeException("样式文件夹是空的：" + themeFolder.getAbsolutePath());
        }
        try {
            for (File file: files) {
                if (file.isFile() && file.getName().endsWith("less")) {
                    String cssName = file.getName().replace("less", "css");
                    File css = new File(file.getParent() + File.separator + cssName);
                    if (css.exists()) {
                        css.delete();
                    }
                    LessEngine lessEngine = new LessEngine();
                    lessEngine.compile(file,css);
                } else if (file.isFile() && (
                        file.getName().toLowerCase().endsWith("ttf") ||
                                file.getName().toLowerCase().endsWith("otf")||
                                file.getName().toLowerCase().endsWith("ttc"))) {
                    try {
                        Font font = Font.loadFont(new FileInputStream(file),12.0);
                        logger.info(" font :" + font.getFamily() + " loaded");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            this.ready = true;
        } catch (Exception e) {
            throw new RuntimeException("less编译失败：",e);
        }
    }

    /**
     * 给view添加样式
     * @param view
     */
    public void applyWithView(TheView view) {
        if (!this.ready) {
            this.prepare();
        }

        File skinAssets = assetsRoot.toPath()
                .resolve("skin")
                .resolve(this.name)
                .toFile();

        AnnotationDescription desc = Annotations.findAnnotation(view.getClass(),View.class);
        List<String> stylesList = null;
        Scene root = view.getScene();

        //Stage stage = view.getStage();
        try {
            String defaultStyle = skinAssets
                    .toPath()
                    .resolve("stage.css")
                    .toUri()
                    .toURL()
                    .toExternalForm();



            stylesList = root.getStylesheets();
            if (view instanceof AbstractView) {
                AbstractView stdView = (AbstractView) view;
                stylesList = stdView.getStage()
                        .getScene()
                        .getStylesheets();
            }

            String background = desc.getProperty(String.class,"background");
            String backgroundUri = null;
            if (!background.isBlank()) {
                backgroundUri = skinAssets.toPath().resolve("images")
                        .resolve(background)
                        .toUri()
                        .toURL()
                        .toExternalForm();
            }

            stylesList.clear();
            stylesList.add(defaultStyle);

            String[] additionalStyleSheets = desc.getProperty(String[].class,"css");
            for (String styleName: additionalStyleSheets) {
                if (styleName.isBlank()) {
                    continue;
                }
                String styleUri = skinAssets
                        .toPath()
                        .resolve(styleName)
                        .toUri()
                        .toURL()
                        .toExternalForm();
                stylesList.add(styleUri);
            }
            root.getRoot().setStyle("-fx-background-image: url(" + backgroundUri + ");" );
        } catch (Exception e){
            throw new RuntimeException("渲染出现异常：",e);
        }
    }

    public void applyWithAlert(Alert alert) {
        if (!this.ready) {
            this.prepare();
        }

        File skinAssets = assetsRoot.toPath()
                .resolve("skin")
                .resolve(this.name)
                .toFile();

        try {
            String defaultStyle = skinAssets
                    .toPath()
                    .resolve("stage.css")
                    .toUri()
                    .toURL()
                    .toExternalForm();

            alert.getDialogPane().getStylesheets().add(defaultStyle);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Theme getTheme(String name,File assets) {
        if (themes.containsKey(name)) {
            return themes.get(name);
        }
        Theme theme = new Theme(name,assets);
        themes.put(name,theme);
        return theme;
    }

}
