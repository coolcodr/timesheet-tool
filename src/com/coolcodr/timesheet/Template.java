package com.coolcodr.timesheet;

import java.io.Serializable;

public class Template implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Entry defaultEntry;

    public Template() {
        name = null;
        defaultEntry = null;
    }

    public Template(String name, Entry defaultEntry) {
        this.name = name;
        this.defaultEntry = defaultEntry;
    }

    public String getName() {
        return name;
    }

    public Entry getDefaultEntry() {
        return defaultEntry;
    }

}
