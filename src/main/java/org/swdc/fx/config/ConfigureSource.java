package org.swdc.fx.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigureSource {

    /**
     * 这里是和Assets目录相对的路径。
     * @return
     */
    String value();

    /**
     * Config文件类型
     * @return
     */
    ConfigFormat format();

    /**
     * 外部还是内部，
     * 内部从module里面读取，只读不能修改。
     * @return
     */
    boolean external() default true;

}
