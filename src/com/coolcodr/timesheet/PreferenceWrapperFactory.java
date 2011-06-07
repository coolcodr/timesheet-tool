package com.coolcodr.timesheet;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class PreferenceWrapperFactory {
    public static IPreferenceWrapper createWrapper(JComponent component) {
        if (component instanceof JTextField) {
            return new TextFieldPreferenceWrapper((JTextField) component);
        } else if (component instanceof JComboBox) {
            return new ComboBoxPreferenceWrapper((JComboBox) component);
        } else {
            return null;
        }
    }
}
