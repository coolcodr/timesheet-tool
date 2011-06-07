package com.coolcodr.timesheet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.java.dev.designgridlayout.DesignGridLayout;

import org.apache.log4j.Logger;

public class MainForm extends JFrame implements ActionListener, Runnable {

    private static final long serialVersionUID = 7717041719716357858L;
    public static long INTERVAL = 15 * Constants.MINUTE;

    private static Logger logger = Logger.getLogger(MainForm.class);

    private static final String ACTION_NEW = RandomStringGenerator.getString();
    private static final String ACTION_CLOSE = RandomStringGenerator.getString();
    private static final String ACTION_GENERATE = RandomStringGenerator.getString();

    private FormHelper fh;

    private JTextField txtNextPrompt;

    private List<Entry> entries;
    private TemplateList templateList;

    private EntryForm entryForm = null;
    private Calendar lastClosedTime = null;
    private long timeDiff = -1;

    public MainForm(List<Entry> entries, TemplateList templateList) {
        setTitle("Timesheet Tool");
        this.entries = entries;
        this.templateList = templateList;
        fh = new FormHelper();

        // setUndecorated(true);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel pnl = new JPanel();
        DesignGridLayout layout = createInnerPanel(this, pnl);

        JButton btnNew = new JButton("New");
        JButton btnClose = new JButton("Close");
        JButton btnGenerate = new JButton("Generate Report");

        txtNextPrompt = fh.readonlyField("");

        btnNew.setActionCommand(ACTION_NEW);
        btnClose.setActionCommand(ACTION_CLOSE);
        btnGenerate.setActionCommand(ACTION_GENERATE);

        btnNew.addActionListener(this);
        btnClose.addActionListener(this);
        btnGenerate.addActionListener(this);

        layout.row().grid(fh.label("Next Prompt")).add(txtNextPrompt).add(btnNew);
        layout.row().right().add(btnGenerate, btnClose);

        fh.loadPreference();

        Thread t = new Thread(this);
        t.start();
    }

    private DesignGridLayout createInnerPanel(JFrame frame, JPanel panel) {
        JPanel innerPanel = new JPanel();
        JScrollPane innerScrollPane = new JScrollPane(innerPanel);
        DesignGridLayout innerLayout = new DesignGridLayout(innerPanel);
        frame.add(innerScrollPane, BorderLayout.CENTER);
        return innerLayout;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getActionCommand().equals(ACTION_NEW)) {
                if ((entryForm == null) || entryForm.isClosed()) {
                    String timeFrom = "";
                    if (lastClosedTime != null) {
                        timeFrom = Constants.getDateTimeFormatter().format(lastClosedTime.getTime());
                    }
                    popup(timeFrom);
                    lastClosedTime = null;
                }
            } else if (e.getActionCommand().equals(ACTION_CLOSE)) {
                writeTemplateList();
                System.exit(0);
            } else if (e.getActionCommand().equals(ACTION_GENERATE)) {
                GenerateReportDialog dialog = new GenerateReportDialog();
                dialog.showDialog(this);
            }
            fh.savePreference();
        } catch (Exception ex) {
            logger.error(ex, ex);
        }
    }

    private void writeTemplateList() {
        if (templateList != null) {
            FileOutputStream fos = null;
            ObjectOutputStream out = null;
            try {
                fos = new FileOutputStream("template_list.obj");
                out = new ObjectOutputStream(fos);
                out.writeObject(templateList);
                out.close();
            } catch (IOException ex) {
                logger.error(ex, ex);
            }
        }
    }

    public void popup(final String timeFrom) throws IOException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    entryForm = new EntryForm(entries, timeFrom, templateList);
                    setSize(entryForm);
                    setCenter(entryForm);
                    entryForm.setVisible(true);
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

    @Override
    public void run() {
        do {
            // Queue<String> q = new LinkedBlockingQueue<String>();
            try {

                Calendar startLunch = Calendar.getInstance();
                startLunch.set(Calendar.HOUR_OF_DAY, 12);
                startLunch.set(Calendar.MINUTE, 30);
                startLunch.set(Calendar.SECOND, 0);
                startLunch.set(Calendar.MILLISECOND, 0);

                Calendar endLunch = Calendar.getInstance();
                endLunch.set(Calendar.HOUR_OF_DAY, 13);
                endLunch.set(Calendar.MINUTE, 30);
                endLunch.set(Calendar.SECOND, 0);
                endLunch.set(Calendar.MILLISECOND, 0);

                Calendar currentTime = Calendar.getInstance();

                if ((currentTime.compareTo(startLunch) > 0) && (currentTime.compareTo(endLunch) < 0)) {
                    continue;
                }

                if ((entryForm != null) && entryForm.isClosed() && (lastClosedTime == null)) {
                    lastClosedTime = Calendar.getInstance();
                    Calendar current = (Calendar) lastClosedTime.clone();
                    current.add(Calendar.MILLISECOND, (int) INTERVAL);
                    txtNextPrompt.setText(Constants.getDateTimeFormatter().format(current.getTime()));
                }

                if (lastClosedTime != null) {
                    timeDiff = currentTime.getTimeInMillis() - lastClosedTime.getTimeInMillis();
                }

                if (((entryForm == null) || entryForm.isClosed()) && ((timeDiff < 0) || (timeDiff > INTERVAL))) {
                    String timeFrom = "";
                    if (lastClosedTime != null) {
                        timeFrom = Constants.getDateTimeFormatter().format(lastClosedTime.getTime());
                    }
                    popup(timeFrom);
                    lastClosedTime = null;
                }

                Thread.sleep(500);
            } catch (Exception e) {
                logger.error(e, e);
            }

        } while (true);
    }
}
