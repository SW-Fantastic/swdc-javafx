package org.swdc.fx;

import org.swdc.config.AbstractConfigure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SWFXApplication {

    /**
     * 指定外部资源文件夹
     * @return
     */
    String assetsFolder();

    /**
     * Splash闪屏类
     * @return
     */
    Class<? extends SplashView> splash();

    /**
     * 用户的外部配置类
     * @return
     */
    Class<? extends AbstractConfigure>[] configs();

    /**
     * 图标资源的名称
     * 请放置于resources/icon里面
     * @return
     */
    String[] icons();

}
