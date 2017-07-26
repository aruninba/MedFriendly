package model;

/**
 * Created by arun_i on 25-Jul-17.
 */

public class Waterintake {
    String id;
    String date;
    String cuplevel;
    String waterlimit;
    String stopposition;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCuplevel() {
        return cuplevel;
    }

    public void setCuplevel(String cuplevel) {
        this.cuplevel = cuplevel;
    }

    public String getWaterlimit() {
        return waterlimit;
    }

    public void setWaterlimit(String waterlimit) {
        this.waterlimit = waterlimit;
    }

    public String getStopposition() {
        return stopposition;
    }

    public void setStopposition(String stopposition) {
        this.stopposition = stopposition;
    }
}
