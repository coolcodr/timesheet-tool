package com.coolcodr.timesheet;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Entry implements Serializable {

    private static final long serialVersionUID = 5325815740363925146L;

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static SimpleDateFormat getDateTimeFormatter() {
        return new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
    }

    private String project = "";
    private String task = "";
    private String activity = "";
    private String remark = "";
    private Calendar datetime = null;
    private long elapsedTime = -1;

    public Entry() {

    }

    public Entry(String project, String task, String activity, String remark, Calendar datetime) {
        super();
        this.project = project;
        this.task = task;
        this.activity = activity;
        this.remark = remark;
        this.datetime = datetime;
    }

    public String getProject() {
        return project;
    }

    public String getTask() {
        return task;
    }

    public String getActivity() {
        return activity;
    }

    public String getRemark() {
        return remark;
    }

    public Calendar getDatetime() {
        return datetime;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setDatetime(Calendar datetime) {
        this.datetime = datetime;
    }

    public void readFrom(Element ele) throws ParseException {
        for (Iterator j = ele.elementIterator(); j.hasNext();) {
            Element child = (Element) j.next(); // do something
            if (child.getName().equalsIgnoreCase("project")) {
                setProject(child.getText());
            } else if (child.getName().equalsIgnoreCase("task")) {
                setTask(child.getText());
            } else if (child.getName().equalsIgnoreCase("activity")) {
                setActivity(child.getText());
            } else if (child.getName().equalsIgnoreCase("remark")) {
                setRemark(child.getText());
            } else if (child.getName().equalsIgnoreCase("datetime")) {
                Date d = getDateTimeFormatter().parse(child.getText());
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                setDatetime(c);
            }
        }
    }

    public Element toElement() {
        Element entryEle = DocumentHelper.createElement("entry");

        Element projectEle = DocumentHelper.createElement("project");
        Element taskEle = DocumentHelper.createElement("task");
        Element activityEle = DocumentHelper.createElement("activity");
        Element remarkEle = DocumentHelper.createElement("remark");
        Element datetimeEle = DocumentHelper.createElement("datetime");

        String d = getDateTimeFormatter().format(datetime.getTime());
        projectEle.setText(project);
        taskEle.setText(task);
        activityEle.setText(activity);
        remarkEle.setText(remark);
        datetimeEle.setText(d);

        entryEle.add(projectEle);
        entryEle.add(taskEle);
        entryEle.add(activityEle);
        entryEle.add(remarkEle);
        entryEle.add(datetimeEle);

        return entryEle;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getKey() {
        String datetimeStr = "";
        if (datetime != null) {
            datetimeStr = Constants.getDateFormatter().format(datetime.getTime());
        }
        return datetimeStr + "_" + project + "_" + task + "_" + activity + "_" + remark;

    }
}
