package com.coolcodr.timesheet;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

public class Main {
    private static Logger logger = Logger.getLogger(Main.class);

    private MainForm frame = null;

    private static List<Entry> entries = null;
    private static TemplateList templateList = null;

    public static void main(String[] args) {
        try {
            Locale.setDefault(Locale.ENGLISH);

            readTemplateList();

            EntryParser parser = new EntryParser();
            File data = new File(Constants.TIMESHEET_FILENAME + "_" + Constants.getDateFormatter().format(new Date()) + ".xml");
            if (data.exists()) {
                entries = parser.load(data);
            } else {
                entries = new ArrayList<Entry>();
            }

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Main main = new Main();
            main.popup();

            // do
            // {
            // Thread.sleep(1000);
            // }
            // while (main.isClosed());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    private static void readTemplateList() {
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            File templateListFile = new File("template_list.obj");
            if (templateListFile.exists()) {
                fis = new FileInputStream(templateListFile);
                in = new ObjectInputStream(fis);
                templateList = (TemplateList) in.readObject();
                in.close();
            }
        } catch (Exception ex) {
            logger.error(ex, ex);
        }
        if (templateList != null) {
            for (Template template : templateList.getList()) {
                logger.debug("template: " + template.getName());
                logger.debug("task: " + template.getDefaultEntry().getProject() + ", " + template.getDefaultEntry().getActivity());
            }
        }
    }

    public boolean isClosed() {
        if (frame == null) {
            return true;
        }
        if (frame.isVisible()) {
            return false;
        } else {
            return true;
        }
    }

    public void popup() throws IOException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    frame = new MainForm(entries, templateList);
                    setSize(frame);
                    setCenter(frame);
                    frame.setVisible(true);
                    // Utils.AlertOnWindow(frame);
                } catch (Exception e) {
                    logger.error(e, e);
                }
            }

        });
    }

    private void setSize(JFrame frame) {
        // Toolkit toolkit = Toolkit.getDefaultToolkit();
        // Dimension screenSize = toolkit.getScreenSize();
        // int width = (int) (screenSize.width * (1D / 2D));
        // int height = (int) (screenSize.height * (1D / 2D));
        // frame.setMinimumSize(new Dimension(width, height));
        // frame.setPreferredSize(new Dimension(width, height));
        frame.pack();
    }

    private void setCenter(JFrame frame) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
    }

}
