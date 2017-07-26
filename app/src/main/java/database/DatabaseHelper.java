package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

import model.Dialysis;
import model.Waterintake;

/**
 * Created by Arun on 24-Aug-16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASENAME = "Mymedicines";
    private static final int DATABASE_VERSION = 1;


    String medicines = "CREATE TABLE Medicines(Id INTEGER PRIMARY KEY,Med_name TEXT,Reminder TEXT,Frequency TEXT,Time TEXT,Dosage TEXT,ScheduleDate TEXT,Enddate TEXT,Days TEXT)";
    String dialysis = "CREATE TABLE Dialysis(Id INTEGER PRIMARY KEY,Day TEXT, Time TEXT, Hospital TEXT,HospitalCoordinates TEXT,HospitalLocation TEXT, HospitalState TEXT,HospitalDistrict TEXT,HospitalPincode TEXT,HospitalPhone TEXT,Reminder TEXT,Currentdate TEXT)";
    String waterIntake = "CREATE TABLE WaterIntake(Id INTEGER PRIMARY KEY,Date TEXT,CupLevel TEXT, WaterLimit TEXT,StopPosition TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASENAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(medicines);
        db.execSQL(dialysis);
        db.execSQL(waterIntake);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Medicines");
        db.execSQL("DROP TABLE IF EXISTS Dialysis");
        db.execSQL("DROP TABLE IF EXISTS WaterIntake");
        onCreate(db);
    }


    public long insert_medicine(String med_name, String reminder, String frequency, String time, String dosage, String scheduledate, String endDate, String days) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Med_name", med_name);
        values.put("Reminder", reminder);
        values.put("Frequency", frequency);
        values.put("Time", time);
        values.put("Dosage", dosage);
        values.put("ScheduleDate", scheduledate);
        values.put("Enddate", endDate);
        values.put("Days", days);
        long row = db.insert("Medicines", null, values);
        System.out.println("db in1" + med_name + reminder + frequency + time + dosage + days + scheduledate);
        db.close();
        return row;
    }

    public long updateMedicine(String id, String med_name, String reminder, String frequency, String time, String dosage, String scheduledate, String endDate, String days) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Med_name", med_name);
        values.put("Reminder", reminder);
        values.put("Frequency", frequency);
        values.put("Time", time);
        values.put("Dosage", dosage);
        values.put("ScheduleDate", scheduledate);
        values.put("Enddate", endDate);
        values.put("Days", days);
        long row = db.update("Medicines", values, "Id" + "=?",
                new String[]{id});
        System.out.println("db update" + med_name + reminder + frequency + time + dosage + days + scheduledate);
        db.close();
        return row;
    }


    public ArrayList<HashMap<String, String>> getmedicines() {
        ArrayList<HashMap<String, String>> all_medicine = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Medicines", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("Id", cursor.getString(0));
                map.put("Med_name", cursor.getString(1));
                map.put("Reminder", cursor.getString(2));
                map.put("Frequency", cursor.getString(3));
                map.put("Time", cursor.getString(4));
                map.put("Dosage", cursor.getString(5));
                map.put("ScheduleDate", cursor.getString(6));
                map.put("Enddate", cursor.getString(7));
                map.put("Days", cursor.getString(8));
                all_medicine.add(map);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return all_medicine;
    }

    public ArrayList<HashMap<String, String>> getOneMedicines(String id) {
        ArrayList<HashMap<String, String>> all_medicine = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Medicines where Id = ' " + id + "'", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("Id", cursor.getString(0));
                map.put("Med_name", cursor.getString(1));
                map.put("Reminder", cursor.getString(2));
                map.put("Frequency", cursor.getString(3));
                map.put("Time", cursor.getString(4));
                map.put("Dosage", cursor.getString(5));
                map.put("ScheduleDate", cursor.getString(6));
                map.put("Enddate", cursor.getString(7));
                map.put("Days", cursor.getString(7));
                all_medicine.add(map);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return all_medicine;
    }

    public void deleteMedicine(String id) {

        SQLiteDatabase db = this.getReadableDatabase();
        db.delete("Medicines", "Id" + "=" + id, null);
        db.close();
    }


    public long insertDialysis(Dialysis dialysis) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Day", dialysis.getDay());
        cv.put("Time", dialysis.getTime());
        cv.put("Hospital", dialysis.getHospital());
        cv.put("HospitalCoordinates", dialysis.getHospitalCoordinates());
        cv.put("HospitalLocation", dialysis.getHospitalLocation());
        cv.put("HospitalState", dialysis.getHospitalState());
        cv.put("HospitalDistrict", dialysis.getHospitalDistrict());
        cv.put("HospitalPincode", dialysis.getHospitalPincode());
        cv.put("HospitalPhone", dialysis.getHospitalPhone());
        cv.put("Reminder", dialysis.getRemindIn());
        cv.put("Currentdate", dialysis.getDate());
        long id = db.insert("Dialysis", null, cv);
        System.out.println("inserteddialysis" + id);
        db.close();
        return id;
    }

    public long updateDialysis(Dialysis dialysis) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Day", dialysis.getDay());
        cv.put("Time", dialysis.getTime());
        cv.put("Hospital", dialysis.getHospital());
        cv.put("HospitalCoordinates", dialysis.getHospitalCoordinates());
        cv.put("HospitalLocation", dialysis.getHospitalLocation());
        cv.put("HospitalState", dialysis.getHospitalState());
        cv.put("HospitalDistrict", dialysis.getHospitalDistrict());
        cv.put("HospitalPincode", dialysis.getHospitalPincode());
        cv.put("HospitalPhone", dialysis.getHospitalPhone());
        cv.put("Reminder", dialysis.getRemindIn());
        cv.put("Currentdate", dialysis.getDate());
        long id = db.update("Dialysis", cv, "Id" + "=?", new String[]{dialysis.getId()});
        System.out.println("id" + id + dialysis.getHospital() + dialysis.getId());
        db.close();
        return id;

    }


    public ArrayList<Dialysis> getDialysis() {
        ArrayList<Dialysis> dialysises = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Dialysis", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Dialysis dialysis = new Dialysis();
                dialysis.setId(cursor.getString(0));
                dialysis.setDay(cursor.getString(1));
                dialysis.setTime(cursor.getString(2));
                dialysis.setHospital(cursor.getString(3));
                dialysis.setHospitalCoordinates(cursor.getString(4));
                dialysis.setHospitalLocation(cursor.getString(5));
                dialysis.setHospitalState(cursor.getString(6));
                dialysis.setHospitalDistrict(cursor.getString(7));
                dialysis.setHospitalPincode(cursor.getString(8));
                dialysis.setHospitalPhone(cursor.getString(9));
                dialysis.setRemindIn(cursor.getString(10));
                dialysis.setDate(cursor.getString(11));
                dialysises.add(dialysis);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return dialysises;
    }

    public Dialysis getOneDialysis(int id) {
        Dialysis dialysisObj = new Dialysis();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Dialysis Where Id = '" + id + "'", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                dialysisObj.setId(cursor.getString(0));
                dialysisObj.setDay(cursor.getString(1));
                dialysisObj.setTime(cursor.getString(2));
                dialysisObj.setHospital(cursor.getString(3));
                dialysisObj.setHospitalCoordinates(cursor.getString(4));
                dialysisObj.setHospitalLocation(cursor.getString(5));
                dialysisObj.setHospitalState(cursor.getString(6));
                dialysisObj.setHospitalDistrict(cursor.getString(7));
                dialysisObj.setHospitalPincode(cursor.getString(8));
                dialysisObj.setHospitalPhone(cursor.getString(9));
                dialysisObj.setRemindIn(cursor.getString(10));
            } while (cursor.moveToNext());
        }
        return dialysisObj;
    }

    public void deleteDialysis(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete("Dialysis", "Id" + "=" + id, null);
        db.close();
    }


    public long insert_waterIntake(Waterintake waterintake) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Date", waterintake.getDate());
        values.put("CupLevel", waterintake.getCuplevel());
        values.put("WaterLimit", waterintake.getWaterlimit());
        values.put("StopPosition", waterintake.getStopposition());
        long row = db.insert("WaterIntake", null, values);
        db.close();
        return row;
    }


    public Waterintake getParticularDate() {
        Waterintake waterintake = new Waterintake();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM WaterIntake ORDER BY Id DESC LIMIT 1", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                waterintake.setId(cursor.getString(0));
                waterintake.setDate(cursor.getString(1));
                waterintake.setCuplevel(cursor.getString(2));
                waterintake.setWaterlimit(cursor.getString(3));
                waterintake.setStopposition(cursor.getString(4));
                System.out.println("stopposition"+cursor.getString(4));
                System.out.println("setWaterlimit"+cursor.getString(3));
            } while (cursor.moveToNext());
        }
        db.close();
        return waterintake;
    }

    public void updateWaterIntake(Waterintake waterintake) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Date", waterintake.getDate());
        values.put("CupLevel", waterintake.getCuplevel());
        values.put("WaterLimit", waterintake.getWaterlimit());
        values.put("StopPosition", waterintake.getStopposition());
        long rowid = db.update("WaterIntake", values, "Id" + "=?", new String[]{waterintake.getId()});
        System.out.println("update rowid" + rowid + waterintake.getStopposition()+"update id"+waterintake.getId());

        db.close();
    }


}
