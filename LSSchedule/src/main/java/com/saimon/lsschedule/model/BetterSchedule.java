package com.saimon.lsschedule.model;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created at 8:32 PM on 1/13/14
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class BetterSchedule {

    private static DateTimeFormatter sTimeFormatter;
    static {
        sTimeFormatter = DateTimeFormat.forPattern("H:m");
        sTimeFormatter = sTimeFormatter.withZone(DateTimeZone.forID("Asia/Kathmandu"));
    }

    private int groupId;
    private LocalTime startTime;
    private LocalTime endTime;

    public static BetterSchedule fromSchedule(Schedule s) {
        BetterSchedule schedule = new BetterSchedule();
        schedule.groupId = s.getGroupId();
        schedule.startTime = LocalTime.parse(s.getStartTime(), sTimeFormatter);
        schedule.endTime = LocalTime.parse(s.getEndTime(), sTimeFormatter);
        return schedule;
    }

    public int getGroupId() {
        return groupId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
