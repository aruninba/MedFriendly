package model;

import java.io.Serializable;

/**
 * Created by arun_i on 18-Jul-17.
 */

public class Dialysis implements Serializable{
    String id;
    String day;
    String time;
    String hospital;
    String hospitalCoordinates;
    String hospitalLocation;
    String hospitalState;
    String hospitalDistrict;
    String hospitalPincode;
    String hospitalPhone;
    String remindIn;
    String date;

    public String getHospitalCoordinates() {
        return hospitalCoordinates;
    }

    public void setHospitalCoordinates(String hospitalCoordinates) {
        this.hospitalCoordinates = hospitalCoordinates;
    }

    public String getHospitalLocation() {
        return hospitalLocation;
    }

    public void setHospitalLocation(String hospitalLocation) {
        this.hospitalLocation = hospitalLocation;
    }

    public String getHospitalState() {
        return hospitalState;
    }

    public void setHospitalState(String hospitalState) {
        this.hospitalState = hospitalState;
    }

    public String getHospitalDistrict() {
        return hospitalDistrict;
    }

    public void setHospitalDistrict(String hospitalDistrict) {
        this.hospitalDistrict = hospitalDistrict;
    }

    public String getHospitalPincode() {
        return hospitalPincode;
    }

    public void setHospitalPincode(String hospitalPincode) {
        this.hospitalPincode = hospitalPincode;
    }

    public String getHospitalPhone() {
        return hospitalPhone;
    }

    public void setHospitalPhone(String hospitalPhone) {
        this.hospitalPhone = hospitalPhone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getRemindIn() {
        return remindIn;
    }

    public void setRemindIn(String remindIn) {
        this.remindIn = remindIn;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
