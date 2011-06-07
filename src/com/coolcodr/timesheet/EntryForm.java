package com.coolcodr.timesheet;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.java.dev.designgridlayout.DesignGridLayout;

import org.apache.log4j.Logger;

public class EntryForm extends JFrame implements ActionListener {
    private static final Logger logger = Logger.getLogger(EntryForm.class);

    private static final long serialVersionUID = -2916052787969295509L;
    private static final String ACTION_DEFAULT = RandomStringGenerator.getString();
    private static final String ACTION_SAVE = RandomStringGenerator.getString();
    private static final String ACTION_RESET = RandomStringGenerator.getString();
    private static final String ACTION_TEMPLATE_CHANGE = RandomStringGenerator.getString();
    private static final String ACTION_ADD_TEMPLATE = RandomStringGenerator.getString();
    private static final String ACTION_REMOVE_TEMPLATE = RandomStringGenerator.getString();

    private FormHelper fh;
    private JComboBox cboProject;
    private JTextField txtTask;
    private JTextField txtRemark;
    private JTextField txtTimeFrom;
    private JComboBox cboPeriod;
    private JComboBox cboActivity;
    private JComboBox cboTemplateList;

    private int oriProject;
    private String oriStrProject;
    private String oriTask;
    private int oriActivity;
    private String oriStrActivity;
    private String oriRemark;
    private List<Entry> entries;
    private boolean isClosed = false;

    private TemplateList templateList;

    public EntryForm(List<Entry> entries, final String timeFrom, TemplateList templateList) {
        setTitle("Timesheet Entry");

        this.templateList = templateList;
        this.entries = entries;
        // if(timeFrom.equals(""))
        // {
        // Calendar nineOClock = Calendar.getInstance();
        // nineOClock.set(Calendar.HOUR_OF_DAY, 9);
        // nineOClock.set(Calendar.MINUTE, 0);
        // nineOClock.set(Calendar.SECOND, 0);
        // timeFrom =
        // Constants.getDateTimeFormatter().format(nineOClock.getTime());
        // }

        fh = new FormHelper();
        // setUndecorated(true);
        setLayout(new BorderLayout());
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        DesignGridLayout layout = createInnerPanel(this, new JPanel());

        cboProject = new JComboBox();
        txtTask = new JTextField();
        txtRemark = new JTextField();
        cboActivity = new JComboBox();
        cboPeriod = new JComboBox();
        cboTemplateList = new JComboBox();
        String timeStr = timeFrom;
        if (timeStr == null || "".equals(timeStr.trim())) {
            Calendar cal = Calendar.getInstance();
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 9, 0, 0);
            timeStr = Constants.getDateTimeFormatter().format(cal.getTime());
        }
        txtTimeFrom = fh.readonlyField(timeStr);
        loadProject(cboProject);
        txtTask.setText("GLTK");
        loadActivity(cboActivity);
        loadPeriod(cboPeriod);
        loadTemplateList(cboTemplateList, templateList);

        JButton btnReset = new JButton("Reset");
        JButton btnSave = new JButton("Save");
        JButton btnDefault = new JButton("Default");
        JButton btnAddTemplate = new JButton("Add");
        JButton btnRemoveTemplate = new JButton("Remove");

        btnReset.setActionCommand(ACTION_RESET);
        btnDefault.setActionCommand(ACTION_DEFAULT);
        btnSave.setActionCommand(ACTION_SAVE);
        btnAddTemplate.setActionCommand(ACTION_ADD_TEMPLATE);
        btnRemoveTemplate.setActionCommand(ACTION_REMOVE_TEMPLATE);
        cboTemplateList.setActionCommand(ACTION_TEMPLATE_CHANGE);

        btnReset.addActionListener(this);
        btnSave.addActionListener(this);
        btnDefault.addActionListener(this);
        btnAddTemplate.addActionListener(this);
        btnRemoveTemplate.addActionListener(this);
        cboTemplateList.addActionListener(this);

        layout.row().center().add(fh.label("What are you doing from"), txtTimeFrom, fh.label("till now?"));
        layout.row().grid(new JLabel("Project")).add(cboProject);
        layout.row().grid(new JLabel("Task")).add(txtTask, btnDefault);
        layout.row().grid(new JLabel("Activity")).add(cboActivity);
        layout.row().grid(new JLabel("Remarks")).add(txtRemark, 3);
        layout.row().grid(new JLabel("Elapsed")).add(cboPeriod);
        layout.row().grid(new JLabel("Template")).add(cboTemplateList, btnAddTemplate, btnRemoveTemplate);

        layout.row().right().add(btnSave, btnReset);

        fh.addPrefComponent(this.getClass().getName(), "cboProject", cboProject);
        fh.addPrefComponent(this.getClass().getName(), "txtTask", txtTask);
        fh.addPrefComponent(this.getClass().getName(), "cboActivity", cboActivity);
        fh.addPrefComponent(this.getClass().getName(), "txtRemark", txtRemark);

        fh.loadPreference();

        oriProject = cboProject.getSelectedIndex();
        oriStrProject = ((ComboBoxItem) cboProject.getSelectedItem()).getValueMember();
        oriTask = txtTask.getText();
        oriActivity = cboActivity.getSelectedIndex();
        oriStrActivity = ((ComboBoxItem) cboActivity.getSelectedItem()).getValueMember();
        oriRemark = txtRemark.getText();
    }

    private DesignGridLayout createInnerPanel(JFrame frame, JPanel panel) {
        JPanel innerPanel = new JPanel();
        JScrollPane innerScrollPane = new JScrollPane(innerPanel);
        DesignGridLayout innerLayout = new DesignGridLayout(innerPanel);
        frame.add(innerScrollPane, BorderLayout.CENTER);
        return innerLayout;
    }

    private void loadProject(JComboBox cbo) {
        ArrayList<ComboBoxItem> documentList = new ArrayList<ComboBoxItem>();
        documentList.add(new ComboBoxItem("PROJ1", "PROJ1 (Project 1)"));
        documentList.add(new ComboBoxItem("PROJ2", "PROJ2 (Project 2)"));
        documentList.add(new ComboBoxItem("PROJ3", "PROJ3 (Project 3)"));

        ArrayListComboBoxModel model = new ArrayListComboBoxModel(documentList);
        cbo.setModel(model);
        cbo.setSelectedIndex(0);
    }

    private void loadActivity(JComboBox cbo) {
        ArrayList<ComboBoxItem> documentList = new ArrayList<ComboBoxItem>();
        documentList.add(new ComboBoxItem("ACT1", "ACT1 (Activity 1)"));
        documentList.add(new ComboBoxItem("ACT2", "ACT2 (Activity 2)"));
        documentList.add(new ComboBoxItem("ACT3", "ACT3 (Activity 3)"));
        ArrayListComboBoxModel model = new ArrayListComboBoxModel(documentList);
        cbo.setModel(model);
        cbo.setSelectedIndex(0);
    }

    private void loadPeriod(JComboBox cbo) {
        ArrayList<ComboBoxItem> documentList = new ArrayList<ComboBoxItem>();
        documentList.add(new ComboBoxItem("-1", "N/A"));
        documentList.add(new ComboBoxItem(String.valueOf(15 * Constants.MINUTE), "15 mins"));
        documentList.add(new ComboBoxItem(String.valueOf(30 * Constants.MINUTE), "30 mins"));
        documentList.add(new ComboBoxItem(String.valueOf(60 * Constants.MINUTE), "1 hr"));
        documentList.add(new ComboBoxItem(String.valueOf(90 * Constants.MINUTE), "1.5 hrs"));
        documentList.add(new ComboBoxItem(String.valueOf(2 * Constants.HOUR), "2 hrs"));
        ArrayListComboBoxModel model = new ArrayListComboBoxModel(documentList);
        cbo.setModel(model);
        cbo.setSelectedIndex(0);
    }

    private void loadTemplateList(JComboBox cbo, TemplateList list) {
        ArrayList<ComboBoxItem> documentList = new ArrayList<ComboBoxItem>();
        documentList.add(new ComboBoxItem("-1", "N/A"));
        if (list != null) {
            for (int i = 0; i < list.getList().size(); i++) {
                documentList.add(new ComboBoxItem(String.valueOf(i), list.getList().get(i).getName()));
            }
        }
        ArrayListComboBoxModel model = new ArrayListComboBoxModel(documentList);
        cbo.setModel(model);
        cbo.setSelectedIndex(0);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ACTION_DEFAULT)) {
            txtTask.setText("GLTK");
        } else if (e.getActionCommand().equals(ACTION_RESET)) {
            cboProject.setSelectedIndex(oriProject);
            cboActivity.setSelectedIndex(oriActivity);

            txtTask.setText(oriTask);
            txtRemark.setText(oriRemark);

            cboProject.repaint();
            cboActivity.repaint();
        } else if (e.getActionCommand().equals(ACTION_SAVE)) {
            String project = ((ComboBoxItem) cboProject.getSelectedItem()).getValueMember();
            String task = txtTask.getText();
            String activity = ((ComboBoxItem) cboActivity.getSelectedItem()).getValueMember();
            String remark = txtRemark.getText();

            long period = Long.parseLong(((ComboBoxItem) cboPeriod.getSelectedItem()).getValueMember());
            if (period <= 0) {
                Entry newEntry = new Entry(project, task, activity, remark, Calendar.getInstance());
                entries.add(newEntry);
            } else {
                Collections.sort(entries, Constants.getDatetimeComparator());
                Calendar morning = Calendar.getInstance();
                morning.set(morning.get(Calendar.YEAR), morning.get(Calendar.MONTH), morning.get(Calendar.DATE), 9, 0, 0);
                Calendar previousDateTime = Calendar.getInstance();
                previousDateTime.add(Calendar.MILLISECOND, -1 * (int) period);
                if (morning.compareTo(previousDateTime) > 0) {
                    previousDateTime = morning;
                }

                Iterator<Entry> itr = entries.iterator();
                while (itr.hasNext()) {
                    Entry previousEntry = itr.next();
                    if ((previousEntry != null) && (previousEntry.getDatetime().compareTo(previousDateTime) > 0)) {
                        previousEntry.setDatetime(previousDateTime);
                    }
                }

                Entry newEntry = new Entry(project, task, activity, remark, Calendar.getInstance());
                entries.add(newEntry);
            }

            EntryParser parser = new EntryParser();
            File data = new File(Constants.TIMESHEET_FILENAME + "_" + Constants.getDateFormatter().format(new Date()) + ".xml");
            parser.write(data, entries);
            setVisible(false);
            isClosed = true;
        } else if (e.getActionCommand().equals(ACTION_TEMPLATE_CHANGE)) {
            String selectedIndexStr = ((ComboBoxItem) cboTemplateList.getSelectedItem()).getValueMember();
            int selectedIndex = Integer.parseInt(selectedIndexStr);
            if (selectedIndex >= 0) {
                Template template = templateList.getList().get(selectedIndex);
                Entry entry = template.getDefaultEntry();
                setValue(cboProject, entry.getProject());
                txtTask.setText(entry.getTask());
                setValue(cboActivity, entry.getActivity());
                txtRemark.setText(entry.getRemark());

                cboProject.repaint();
                cboActivity.repaint();
            }
        } else if (e.getActionCommand().equals(ACTION_ADD_TEMPLATE)) {
            String project = ((ComboBoxItem) cboProject.getSelectedItem()).getValueMember();
            String task = txtTask.getText();
            String activity = ((ComboBoxItem) cboActivity.getSelectedItem()).getValueMember();
            String remark = txtRemark.getText();
            Calendar datetime = Calendar.getInstance();
            Entry newEntry = new Entry(project, task, activity, remark, (Calendar) datetime.clone());

            String name = JOptionPane.showInputDialog(this, "Template Name");
            templateList.getList().add(new Template(name, newEntry));
            loadTemplateList(cboTemplateList, templateList);
        } else if (e.getActionCommand().equals(ACTION_REMOVE_TEMPLATE)) {
            String selectedIndexStr = ((ComboBoxItem) cboTemplateList.getSelectedItem()).getValueMember();
            int selectedIndex = Integer.parseInt(selectedIndexStr);
            if (selectedIndex >= 0) {
                Template template = templateList.getList().get(selectedIndex);
                templateList.getList().remove(template);
                loadTemplateList(cboTemplateList, templateList);
            }
        }
        fh.savePreference();
    }

    private void setValue(JComboBox cbo, String value) {
        ArrayListComboBoxModel model = (ArrayListComboBoxModel) cbo.getModel();
        List<ComboBoxItem> list = model.getList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getValueMember().equals(value)) {
                cbo.setSelectedIndex(i);
                return;
            }
        }
    }

    public boolean isClosed() {
        return isClosed;
    }
}
