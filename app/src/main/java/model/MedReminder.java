package model;

/**
 * Created by arun_i on 13-Jul-17.
 */

public class MedReminder {
    String medId;
    String medName;
    String medAlarm;
    String medFreqency;
    String medTimes;
    String medDosage;
    String medScheduleDate;
    String medDays;

    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getMedAlarm() {
        return medAlarm;
    }

    public void setMedAlarm(String medAlarm) {
        this.medAlarm = medAlarm;
    }

    public String getMedFreqency() {
        return medFreqency;
    }

    public void setMedFreqency(String medFreqency) {
        this.medFreqency = medFreqency;
    }

    public String getMedTimes() {
        return medTimes;
    }

    public void setMedTimes(String medTimes) {
        this.medTimes = medTimes;
    }

    public String getMedDosage() {
        return medDosage;
    }

    public void setMedDosage(String medDosage) {
        this.medDosage = medDosage;
    }

    public String getMedScheduleDate() {
        return medScheduleDate;
    }

    public void setMedScheduleDate(String medScheduleDate) {
        this.medScheduleDate = medScheduleDate;
    }

    public String getMedDays() {
        return medDays;
    }

    public void setMedDays(String medDays) {
        this.medDays = medDays;
    }
}
