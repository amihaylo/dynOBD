package com.luisa.alex.obd2_peek;

/**
 * Created by luisarojas on 2016-11-13.
 */

public class OBDData {

    private String title;
    private String data;

    public OBDData(String title, String data) {
        this.data = data;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
