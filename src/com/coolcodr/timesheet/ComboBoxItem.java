package com.coolcodr.timesheet;

public class ComboBoxItem {
    private String valueMember;
    private String displayMember;

    public ComboBoxItem(String valueMember, String displayMember) {
        this.valueMember = valueMember;
        this.displayMember = displayMember;
    }

    public String getValueMember() {
        return valueMember;
    }

    public String getDisplayMember() {
        return displayMember;
    }

    @Override
    public String toString() {
        return displayMember;
    }
}
