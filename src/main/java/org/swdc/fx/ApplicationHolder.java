package org.swdc.fx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApplicationHolder {

    private static Map<Class,FXApplication> applications = new HashMap<>();

    static void onLaunched(Class clazz, FXApplication inst){
        applications.put(clazz,inst);
    }

    static void onStop(Class clazz) {
        applications.remove(clazz);
    }

    public static <T extends FXApplication> T getApplication(Class<T> clazz) {
        return (T)applications.get(clazz);
    }

}
