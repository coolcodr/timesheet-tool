package com.coolcodr.timesheet;

import java.util.Comparator;

public class DatetimeComparator implements Comparator<Entry> {
    @Override
    public int compare(Entry e1, Entry e2) {
        return e1.getDatetime().compareTo(e2.getDatetime());
    }
}
