package com.luisa.alex.obd2_peek;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luisarojas on 2016-11-28.
 */

public class Tuple implements Parcelable {

    private String key;
    private String value;

    public Tuple(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Tuple(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.value);
    }

    private void readFromParcel(Parcel in){
        this.key = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Tuple createFromParcel(Parcel in) {
                    return new Tuple(in);
                }

                public Tuple[] newArray(int size) {
                    return new Tuple[size];
                }
            };
}