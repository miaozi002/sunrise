package com.sunrise.model;

public class StationDetail {

    private int id;
    private String name;
    private String alarm;
    private int comm;
    private int alm;
    private int load;
    private int voltage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public int getComm() {
        return comm;
    }

    public void setComm(int comm) {
        this.comm = comm;
    }

    public int getAlm() {
        return alm;
    }

    public void setAlm(int alm) {
        this.alm = alm;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

}
