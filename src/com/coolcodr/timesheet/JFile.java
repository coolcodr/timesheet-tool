package com.coolcodr.timesheet;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.IRow;

public class JFile implements ActionListener {

    private JLabel label;
    private JButton btnBrowse;
    private JTextField txtPath;
    private String actionCommand = null;
    private JPanel owner = null;
    private int fileChooserMode = JFileChooser.FILES_ONLY;

    public JFile(JLabel label, JTextField txtPath, JButton btnBrowse, JPanel owner) {
        this(label, txtPath, btnBrowse, owner, JFileChooser.FILES_ONLY);
    }

    public JFile(JLabel label, JTextField txtPath, JButton btnBrowse, JPanel owner, int fileChooserMode) {
        super();

        this.label = label;
        this.btnBrowse = btnBrowse;
        this.txtPath = txtPath;

        actionCommand = btnBrowse.getActionCommand();
        this.owner = owner;
        if (this.owner != null) {
            this.btnBrowse.addActionListener(this);
        }

        this.fileChooserMode = fileChooserMode;
    }

    public JLabel getLabel() {
        return label;
    }

    public JButton getButton() {
        return btnBrowse;
    }

    public JTextField getField() {
        return txtPath;
    }

    public IRow addToLayout(DesignGridLayout layout) {
        return addToLayout(layout, 3);
    }

    public IRow addToLayout(DesignGridLayout layout, int colSpan) {
//		Dimension d = btnBrowse.getPreferredSize();
//		Dimension m = new Dimension(d.height, 10);
//		btnBrowse.setMaximumSize(m);
//		return layout.row().grid(label).add(txtPath, btnBrowse);
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
//		p.add(label, BorderLayout.LINE_START);
        p.add(txtPath, BorderLayout.CENTER);
        p.add(btnBrowse, BorderLayout.LINE_END);
        return layout.row().grid(label).add(p);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (actionCommand != null && actionCommand != "" && owner != null && e.getActionCommand() == actionCommand) {
            FormHelper.openFile(getField(), fileChooserMode, owner);
        }
    }

    public void setEnabled(boolean enabled) {
        btnBrowse.setEnabled(enabled);
    }

    public String getPath() {
        return txtPath.getText().toString().trim();
    }
}
