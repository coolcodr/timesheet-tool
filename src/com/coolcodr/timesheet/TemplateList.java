package com.coolcodr.timesheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TemplateList implements Serializable {
    private static final long serialVersionUID = 6113404891478120130L;

    private List<Template> list = new ArrayList<Template>();

    public TemplateList() {

    }

    public List<Template> getList() {
        return list;
    }
}
