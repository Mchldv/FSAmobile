package com.dvlchm.fsa;

/**
 * Created by User on 5/16/2017.
 */
public class Task {
    private String address;
    private String placeName;
    private String date;
    private Boolean done;

    Task(String address, String placeName, String date, Boolean done)
    {
        this.address=address;
        this.placeName=placeName;
        this.date=date;
        this.done=done;
    }
}
