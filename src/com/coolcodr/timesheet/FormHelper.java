package com.coolcodr.timesheet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

public class FormHelper {

    private static Logger logger = Logger.getLogger(FormHelper.class);
    private Preferences prefs = null;
    private Hashtable<String, JComponent> prefComponents = new Hashtable<String, JComponent>();
    private static String lastOpenDir = null;

    public FormHelper() {
        prefs = Preferences.userNodeForPackage(FormHelper.class);
    }

    public void addPrefComponent(String className, String name, JComponent component) {
        String key = className + "." + name;
        prefComponents.put(String.valueOf(key.hashCode()), component);
    }

    public Preferences getPrefs() {
        return prefs;
    }

    public JTextArea textArea() {
        JTextArea txtLog = new JTextArea();
        txtLog.setFont(new Font("Arial", Font.PLAIN, 12));
        txtLog.setEditable(false);
        txtLog.setRows(5);
        return txtLog;
    }

    public JLabel label(String text) {
        JLabel label = new JLabel(text);
        return label;
    }

    public JButton button(String caption, String actionCommand) {
        JButton button = makeButton(null, actionCommand, null, caption);
        return button;
    }

    public JButton button(String image, String caption, String actionCommand) {
        JButton button = makeButton(image, actionCommand, null, caption);
        return button;
    }

    public JButton button(String caption, String actionCommand, ActionListener listener) {
        JButton button = makeButton(null, actionCommand, null, caption);
        if (listener != null) {
            button.addActionListener(listener);
        }
        return button;
    }

    public JCheckBox checkBox(String text) {
        return new JCheckBox(text);
    }

    public JProgressBar progressBar() {
        return new JProgressBar();
    }

    public JLabel bigLabel(String text) {
        JLabel label = label(text);
        Font f = label.getFont();
        f = f.deriveFont(f.getSize() * 1.3f);
        label.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        return label;
    }

    public JLabel sectionLabel(String text) {
        JLabel label = label(text);
        label.setForeground(Color.BLUE);
        Font f = label.getFont();
        label.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        return label;
    }

    public JLabel redLabel(String text) {
        JLabel label = label(text);
        label.setForeground(Color.red);
        return label;
    }

    public JTextField field(String text) {
        int min = 10;
        int pref = 10;
        JTextField field = new JTextField(text);
        field.setColumns(min);
        field.setMinimumSize(field.getPreferredSize());
        if (pref != min) {
            field.setColumns(pref);
        }
        return field;
    }

    public JTextField readonlyField(String text) {
        JTextField field = field(text);
        field.setEditable(false);
        return field;
    }

    public NumericTextField numericField(String text) {
        int min = 20;
        int pref = 20;
        NumericTextField field = new NumericTextField(text);
        field.setColumns(min);
        field.setMinimumSize(field.getPreferredSize());
        if (pref != min) {
            field.setColumns(pref);
        }
        return field;
    }

    public JTextField pathField(String text) {
        JTextField field = field(text);
        field.setEditable(false);
        return field;
    }

    public JFile fileField(String label, String text, String buttonText, String actionCommand, JPanel owner) {
        JTextField field = field(text);
        field.setEditable(false);
        JButton btnBrowse = button(buttonText, actionCommand);
        JLabel labelComponent = label(label);
        return new JFile(labelComponent, field, btnBrowse, owner);
    }

    public JFile folderField(String label, String text, String buttonText, String actionCommand, JPanel owner) {
        JTextField field = field(text);
        field.setEditable(false);
        JButton btnBrowse = button(buttonText, actionCommand);
        JLabel labelComponent = label(label);
        return new JFile(labelComponent, field, btnBrowse, owner, JFileChooser.DIRECTORIES_ONLY);
    }

    public JComboBox bool(boolean value) {
        JComboBox combo = new JComboBox();
        ArrayList<ComboBoxItem> list = new ArrayList<ComboBoxItem>();
        ComboBoxItem trueItem = new ComboBoxItem("TRUE", "TRUE");
        ComboBoxItem falseItem = new ComboBoxItem("FALSE", "FALSE");
        list.add(trueItem);
        list.add(falseItem);
        ComboBoxItem selectedItem = trueItem;
        if (!value) {
            selectedItem = falseItem;
        }
        ArrayListComboBoxModel model = new ArrayListComboBoxModel(list);
        combo.setModel(model);
        combo.setSelectedItem(selectedItem);
        return combo;
    }

    public JRadioButton radio(String caption, String actionCommand, ButtonGroup group) {
        JRadioButton rb = new JRadioButton(caption);
        rb.setActionCommand(actionCommand);
        group.add(rb);
        return rb;
    }

    public void clearPreference() {
        Enumeration<String> iterator = prefComponents.keys();
        while (iterator.hasMoreElements()) {
            String key = iterator.nextElement();
            JComponent component = prefComponents.get(key);
            if (component == null) {
                continue;
            }

            IPreferenceWrapper wrapper = PreferenceWrapperFactory.createWrapper(component);
            wrapper.setValue("");
            getPrefs().remove(key);
        }
    }

    public void loadPreference() {
        Enumeration<String> iterator = prefComponents.keys();
        while (iterator.hasMoreElements()) {
            String key = iterator.nextElement();
            JComponent component = prefComponents.get(key);
            if (component == null) {
                continue;
            }

            IPreferenceWrapper wrapper = PreferenceWrapperFactory.createWrapper(component);
            wrapper.setValue(getPrefs().get(key, ""));
        }
    }

    public void savePreference() {
        Enumeration<String> iterator = prefComponents.keys();
        while (iterator.hasMoreElements()) {
            String key = iterator.nextElement();
            JComponent component = prefComponents.get(key);
            IPreferenceWrapper wrapper = PreferenceWrapperFactory.createWrapper(component);
            getPrefs().put(key, wrapper.getValue());
        }
    }

    public static boolean openFile(JTextField textField, int mode, Component parent) {
        return openFile(textField, mode, JFileChooser.OPEN_DIALOG, parent, null);
    }

    public static boolean openFile(JTextField textField, int selectionMode, int dialogType, Component parent, FileFilter filter) {
        String originalPath = textField.getText().trim();
        String currentdir = Constants.USER_DIR;
        if (originalPath.length() > 0) {
            currentdir = originalPath.substring(0, originalPath.lastIndexOf(Constants.FILE_SEPARATOR));
        } else if (lastOpenDir != null) {
            currentdir = lastOpenDir;
        }
        JFileChooser fc = new JFileChooser();
        if (filter != null) {
            fc.setFileFilter(filter);
        }
        fc.setCurrentDirectory(new File(currentdir));
        fc.setFileSelectionMode(selectionMode);
        fc.setDialogType(dialogType);
        int returnVal = fc.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            textField.setText(file.getPath());
            lastOpenDir = file.getPath();
            lastOpenDir = lastOpenDir.substring(0, lastOpenDir.lastIndexOf(Constants.FILE_SEPARATOR));
            return true;
        } else {
            return false;
        }
    }

    public static JButton makeBigButton(String imageName, String actionCommand, String toolTipText, String caption) {
        String imgLocation = null;
        if (imageName != null) {
            imgLocation = "/big_icons/" + imageName + ".png";
        }
        JButton button = new JButton();
        setButtonProperties(actionCommand, toolTipText, caption, imgLocation, button);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        return button;
    }

    public static JButton makeButton(String imageName, String actionCommand, String toolTipText, String caption) {
        String imgLocation = null;
        if (imageName != null) {
            imgLocation = "/small_icons/" + imageName + ".png";
        }
        JButton button = new JButton();
        setButtonProperties(actionCommand, toolTipText, caption, imgLocation, button);
        return button;
    }

    private static void setButtonProperties(String actionCommand, String toolTipText, String caption, String imgLocation, JButton button) {
        button.setText(caption);
        button.setActionCommand(actionCommand);
        if (toolTipText != null) {
            button.setToolTipText(toolTipText);
        }
        if (imgLocation != null) {
            URL imageURL = FormHelper.class.getResource(imgLocation);
            if (imageURL != null) {
                button.setIcon(new ImageIcon(imageURL, caption));
            } else {
                logger.info("Resource not found: " + imgLocation);
            }
        }
    }

}
