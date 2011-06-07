package com.coolcodr.timesheet;

import javax.swing.JComboBox;

public class ComboBoxPreferenceWrapper implements IPreferenceWrapper {
    private JComboBox component;

    public ComboBoxPreferenceWrapper(JComboBox component) {
        this.component = component;
    }

    @Override
    public String getValue() {
        return String.valueOf(component.getSelectedIndex());
    }

    @Override
    public void setValue(String value) {
        if (value.equals("")) {
            value = "1";
        }
        component.setSelectedIndex(Integer.valueOf(value));
    }
}
