package com.mobius.stream.Resource;

public enum DialoguesRecord {
    START_1("start_1"),
    START_2("start_2"),
    START_3("start_3"),
    STATION_1("station_1"),
    STATION_2("station_2"),
    STATION_3("station_3"),
    MALL_1("mall_1"),
    MALL_2("mall_2"),
    MALL_3("mall_3"),
    SCHOOL_1("school_1"),
    SCHOOL_2("school_2"),
    SCHOOL_3("school_3"),
    HOME_1("home_1"),
    HOME_2("home_2");
    public final String id;

    DialoguesRecord(String id) {
        this.id = id;
    }
}
