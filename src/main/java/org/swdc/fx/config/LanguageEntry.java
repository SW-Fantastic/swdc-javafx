package org.swdc.fx.config;

public class LanguageEntry {

    private String name;
    private String local;

    public LanguageEntry(String name, String local) {
        this.name = name;
        this.local = local;
    }

    public LanguageEntry() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getName() {
        return name;
    }

    public String getLocal() {
        return local;
    }

    @Override
    public String toString() {
        return name;
    }
}
