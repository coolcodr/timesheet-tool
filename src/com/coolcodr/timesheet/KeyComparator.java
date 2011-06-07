package com.coolcodr.timesheet;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

import org.apache.log4j.Logger;

public class KeyComparator implements Comparator<String> {
    private static Logger logger = Logger.getLogger(KeyComparator.class);

    @Override
    public int compare(String e1, String e2) {
        try {
            String e1DateStr = e1.substring(0, Constants.DATE_FORMAT.length());
            Date e1Date = Constants.getDateFormatter().parse(e1DateStr);
            String e2DateStr = e2.substring(0, Constants.DATE_FORMAT.length());
            Date e2Date = Constants.getDateFormatter().parse(e2DateStr);
            return e1Date.compareTo(e2Date);
        } catch (ParseException e) {
            logger.error(e, e);
            return 0;
        }
    }
}
