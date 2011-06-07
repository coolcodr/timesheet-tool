package com.coolcodr.timesheet;

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class NumericTextField extends JTextField {

    /**
     * 
     */
    private static final long serialVersionUID = -7171264708924767054L;

    public NumericTextField(String text) {
        super(text);
        addFilter();
    }

    // Add other constructors as required. If you do,
    // be sure to call the "addFilter" method
    public NumericTextField(String text, int columns) {
        super(text, columns);
        addFilter();
    }

    // Add an instance of NumericDocumentFilter as a
    // document filter to the current text field
    private void addFilter() {
        ((AbstractDocument) getDocument()).setDocumentFilter(new NumericDocumentFilter());
    }

    class NumericDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {

            if (string == null) {
                return;
            }
            if (isStringNumeric(string)) {
                super.insertString(fb, offset, string, attr);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) {
                return;
            }
            if (isStringNumeric(text)) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        private boolean isStringNumeric(String string) {
            char[] characters = string.toCharArray();
            for (char c : characters) {
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
            return true;
        }
    }

}
