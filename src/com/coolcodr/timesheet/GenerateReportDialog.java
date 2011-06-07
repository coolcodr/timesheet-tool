package com.coolcodr.timesheet;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.java.dev.designgridlayout.DesignGridLayout;

import org.apache.log4j.Logger;

public class GenerateReportDialog implements ActionListener {
    private static Logger logger = Logger.getLogger(GenerateReportDialog.class);

    private static final String ACTION_BROWSE_SOURCE = RandomStringGenerator.getString();
    private static final String ACTION_BROWSE_TARGET = RandomStringGenerator.getString();
    private static final String ACTION_GENERATE = RandomStringGenerator.getString();

    private FormHelper fh;
    private JPanel mainPanel;
    private JDialog dialog;

    private JFile jfReportSource;
    private JFile jfReportTarget;

    private void build(JPanel mainPanel) {
        DesignGridLayout layout = new DesignGridLayout(mainPanel);
        jfReportSource = fh.folderField("Report Source", "", "...", ACTION_BROWSE_SOURCE, mainPanel);
        jfReportTarget = fh.fileField("Report Target", "", "...", ACTION_BROWSE_TARGET, mainPanel);
        JButton btnGenerate = new JButton("Generate");
        btnGenerate.setActionCommand(ACTION_GENERATE);
        btnGenerate.addActionListener(this);

        jfReportSource.addToLayout(layout);
        jfReportTarget.addToLayout(layout);
        layout.row().right().add(btnGenerate);

        fh.addPrefComponent(this.getClass().getName(), "jfReportSource", jfReportSource.getField());
        fh.addPrefComponent(this.getClass().getName(), "jfReportTarget", jfReportTarget.getField());
        fh.loadPreference();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getActionCommand().equals(ACTION_GENERATE)) {
                File folder = new File(jfReportSource.getPath());
                File target = new File(jfReportTarget.getPath());
                if (!folder.exists() || !folder.isDirectory()) {
                    return;
                }

                EntryParser parser = new EntryParser();
                Hashtable<String, List<Entry>> table = new Hashtable<String, List<Entry>>();

                for (File f : folder.listFiles()) {
                    if (!f.isFile() || !f.getPath().endsWith(".xml") || !f.getName().startsWith(Constants.TIMESHEET_FILENAME)) {
                        continue;
                    }

                    List<Entry> entries = parser.load(f);
                    Collections.sort(entries, Constants.getDatetimeComparator());
                    Entry lastEntry = null;
                    for (Entry entry : entries) {
                        Calendar startTime = (Calendar) entry.getDatetime().clone();
                        startTime.set(Calendar.HOUR_OF_DAY, 9);
                        startTime.set(Calendar.MINUTE, 0);
                        startTime.set(Calendar.SECOND, 0);
                        startTime.set(Calendar.MILLISECOND, 0);

                        Calendar startLunch = (Calendar) entry.getDatetime().clone();
                        startLunch.set(Calendar.HOUR_OF_DAY, 12);
                        startLunch.set(Calendar.MINUTE, 30);
                        startLunch.set(Calendar.SECOND, 0);
                        startLunch.set(Calendar.MILLISECOND, 0);

                        Calendar endLunch = (Calendar) entry.getDatetime().clone();
                        endLunch.set(Calendar.HOUR_OF_DAY, 13);
                        endLunch.set(Calendar.MINUTE, 30);
                        endLunch.set(Calendar.SECOND, 0);
                        endLunch.set(Calendar.MILLISECOND, 0);

                        if (lastEntry == null) {
                            if (entry.getDatetime().compareTo(startTime) > 0 && entry.getDatetime().compareTo(endLunch) > 0) {
                                entry.setElapsedTime(entry.getDatetime().getTimeInMillis() - startTime.getTimeInMillis() - (1 * Constants.HOUR));
                                lastEntry = entry;
                            } else if (entry.getDatetime().compareTo(startTime) > 0) {
                                entry.setElapsedTime(entry.getDatetime().getTimeInMillis() - startTime.getTimeInMillis());
                                lastEntry = entry;
                            } else {
                                continue;
                            }
                        } else {
                            if (lastEntry.getDatetime().compareTo(startLunch) < 0 && entry.getDatetime().compareTo(endLunch) > 0) {
                                entry.setElapsedTime(entry.getDatetime().getTimeInMillis() - lastEntry.getDatetime().getTimeInMillis() - (1 * Constants.HOUR));
                            } else {
                                entry.setElapsedTime(entry.getDatetime().getTimeInMillis() - lastEntry.getDatetime().getTimeInMillis());
                            }
                            lastEntry = entry;
                        }

                        if (!table.containsKey(entry.getKey())) {
                            table.put(entry.getKey(), new ArrayList<Entry>());
                        }
                        List<Entry> list = table.get(entry.getKey());
                        list.add(entry);
                    }
                }

                StringBuffer sb = new StringBuffer();
                Vector<String> keyList = new Vector(table.keySet());
                Collections.sort(keyList, new KeyComparator());

                for (String key : keyList) {
                    List<Entry> entryList = table.get(key);
                    long totalTimeElapsed = 0;
                    for (Entry entry : entryList) {
                        totalTimeElapsed += entry.getElapsedTime();
                    }
                    String totalTimeElapsedStr = Constants.getOneDecimalFormatter().format((totalTimeElapsed / (double) Constants.HOUR));

                    Entry sample = entryList.get(0);

                    String project = sample.getProject();
                    String task = sample.getTask();
                    String activity = sample.getActivity();
                    String remark = sample.getRemark();
                    String date = Constants.getDateFormatter().format(sample.getDatetime().getTime());
                    String line = date + ", " + project + ", 1.0, " + task + ", GLFN, " + activity + ", " + remark + ", " + totalTimeElapsedStr + Constants.LINE_SEPARATOR;
                    sb.append(line);
                }
                writeTextFile(target.getPath(), sb.toString());
                JOptionPane.showMessageDialog(mainPanel, "Completed.", "Notification", JOptionPane.INFORMATION_MESSAGE);
            }
            fh.savePreference();
        } catch (Exception ex) {
            logger.error(ex, ex);
        }
    }

    public void showDialog(Frame frame) {
        if (dialog == null) {
            fh = new FormHelper();
            mainPanel = new JPanel();
            build(mainPanel);
            dialog = new JDialog(frame, "Report Generation", true);
            dialog.setContentPane(mainPanel);
            dialog.pack();
        }
        setCenter(dialog);
        dialog.setVisible(true);
    }

    private void setCenter(JDialog frame) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
    }

    private static boolean writeTextFile(String fileName, String dataLine) {
        OutputStreamWriter dos;
        try {
            File outFile = new File(fileName);
            dos = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8");
            dos.write(dataLine);
            dos.close();
        } catch (Exception ex) {
            logger.error(ex, ex);
            return (false);
        }
        return (true);
    }

}
