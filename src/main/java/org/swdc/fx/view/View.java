package org.swdc.fx.view;

import jakarta.inject.Scope;
import javafx.stage.StageStyle;
import org.swdc.dependency.annotations.ScopeImplement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Scope
@ScopeImplement(ViewManager.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface View {

    /**
     * fxml的名字和在classpath的位置
     * 为空的话使用render方法渲染
     * @return
     */
    String viewLocation() default "";

    /**
     * 是否为dialog，如果为true，
     * 在显示的时候使用showAndWait
     * @return
     */
    boolean dialog() default false;

    /**
     * 是否为某些view的单元格，如果为true，
     * 那么此view不为单例，不缓存
     * @return
     */
    boolean stage() default true;

    /**
     * stage的title
     * @return
     */
    String title() default "";

    /**
     * 额外的css引用
     * @return
     */
    String[] css() default "";

    /**
     * stage的windowStyle
     * @return
     */
    StageStyle windowStyle() default StageStyle.DECORATED;

    /**
     * 是否允许改变窗口大小.
     * @return
     */
    boolean resizeable() default true;

    /**
     * stage是否允许关闭
     * @return
     */
    boolean closeable() default true;

    String background() default "";

    boolean multiple() default  false;

}
