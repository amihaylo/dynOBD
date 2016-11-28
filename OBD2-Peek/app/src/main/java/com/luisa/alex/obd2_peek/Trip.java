package com.luisa.alex.obd2_peek;

import android.location.Address;

import java.util.Date;

/**
 * Created by alex on 2016-11-24.
 */

public class Trip {
    //General Stats about the trip
    private Long id;

    private String date;
    private Long duration;
    private String origin;
    private String destination;
    private String timeDeparture;
    private String timeArrival;

    //OBD Specific stats about the trip
    private Integer maxSpeed;
    private Integer maxRPM;

    //TEMP
    public Trip(String date, Long duration, String origin, String timeDeparture,
            String destination, String timeArrival, Integer maxSpeed, Integer maxRPM) {
        this.id = new Long(-1); //this is just a placeholder until the real id from the database can be obtained
        this.date = date;
        this.duration = duration;
        this.origin = origin;
        this.timeDeparture = timeDeparture;
        this.destination = destination;
        this.timeArrival = timeArrival;
        this.maxSpeed = maxSpeed;
        this.maxRPM = maxRPM;
    }


    public Trip(Long id, String date, Long duration, String origin, String timeDeparture,
                String destination, String timeArrival, Integer maxSpeed, Integer maxRPM) {
        this.id = id;
        this.date = date;
        this.duration = duration;
        this.origin = origin;
        this.timeDeparture = timeDeparture;
        this.destination = destination;
        this.timeArrival = timeArrival;
        this.maxSpeed = maxSpeed;
        this.maxRPM = maxRPM;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Integer maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Integer getMaxRPM() {
        return maxRPM;
    }

    public void setMaxRPM(Integer maxRPM) {
        this.maxRPM = maxRPM;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTimeDeparture() {
        return timeDeparture;
    }

    public void setTimeDeparture(String timeDeparture) {
        this.timeDeparture = timeDeparture;
    }

    public String getTimeArrival() {
        return timeArrival;
    }

    public void setTimeArrival(String timeArrival) {
        this.timeArrival = timeArrival;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", duration=" + duration +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", timeDeparture='" + timeDeparture + '\'' +
                ", timeArrival='" + timeArrival + '\'' +
                ", maxSpeed=" + maxSpeed +
                ", maxRPM=" + maxRPM +
                '}';
    }

}
