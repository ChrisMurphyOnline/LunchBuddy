package com.example.alantang.lunchbuddy;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Alan on 8/3/2015.
 */
public class FacebookFriend {
    String id;
    String username;
    String name;
    ArrayList<Date> dates;
    int numberOfDates;

    public FacebookFriend(String thisId, String thisName) {
        this.id = thisId;
        this.name = thisName;
        this.username = "";
        this.dates = new ArrayList<Date>();
    }

    public void setUsername(String thisUsername) {
        this.username = thisUsername;
    }

    public void addDate (Date thisDate) {
        this.dates.add(thisDate);
    }

    public void updateNumberOfDates() {
        this.numberOfDates = this.dates.size();
    }
}