package com.coolcodr.timesheet;

import javax.swing.JTextField;

public class TextFieldPreferenceWrapper implements IPreferenceWrapper {
    private JTextField component;

    public TextFieldPreferenceWrapper(JTextField component) {
        this.component = component;
    }

    @Override
    public String getValue() {
        return component.getText().trim();
    }

    @Override
    public void setValue(String value) {
        component.setText(value);
    }
}
