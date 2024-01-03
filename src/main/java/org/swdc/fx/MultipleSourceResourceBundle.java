package org.swdc.fx;

import java.util.*;
import java.util.stream.Collectors;

public class MultipleSourceResourceBundle extends ResourceBundle {

    private List<ResourceBundle> bundles;
    private ResourceBundle main;

    public MultipleSourceResourceBundle(ResourceBundle mainlyBundle, ResourceBundle ...bundle) {
        this.main = mainlyBundle;
        this.bundles = new ArrayList<>();
        this.bundles.addAll(Arrays.asList(bundle));
    }

    @Override
    protected Object handleGetObject(String key) {
        if (main.containsKey(key)) {
            return main.getObject(key);
        }
        for (ResourceBundle bundle : bundles) {
            if (bundle.containsKey(key)) {
                return bundle.getObject(key);
            }
        }
        return null;
    }

    @Override
    public Enumeration<String> getKeys() {
        Set<String> rst = bundles.stream().map(ResourceBundle::getKeys).flatMap(it -> {
            ArrayList<String> keySet = new ArrayList<>();
            while (it.hasMoreElements()) {
                keySet.add(it.nextElement());
            }
            return keySet.stream();
        }).collect(Collectors.toSet());

        rst.addAll(main.keySet());
        return Collections.enumeration(rst);
    }

    @Override
    public boolean containsKey(String key) {
        if (main.containsKey(key)) {
            return true;
        }
        for (ResourceBundle bundle : bundles) {
            if (bundle.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    public void addResource(ResourceBundle bundle) {
        bundles.add(bundle);
    }

}
