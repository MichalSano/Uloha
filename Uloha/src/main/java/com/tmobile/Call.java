package com.tmobile;

import java.util.Date;

class Call {
    private final String phoneNumber;
    private final Date start;
    private final Date end;

    public Call(String phoneNumber, Date start, Date end) {
        this.phoneNumber = phoneNumber;
        this.start = start;
        this.end = end;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }
}