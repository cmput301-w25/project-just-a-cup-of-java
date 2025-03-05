package com.example.just_a_cup_of_java;

public class days_week {
    private String week_day;
    private String month;

    public days_week(String week_day,String month){
        this.week_day = week_day;
        this.month = month;
    }

    public String getWeek_day() {
        return week_day;
    }

    public void setWeek_day(String week_day) {
        this.week_day = week_day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
