/*
 * Copyright 2015 Blanyal D'Souza.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package arun.com.medfriendly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import database.DatabaseHelper;
import model.Dialysis;


public class BootReceiver extends BroadcastReceiver {

    private String reminderId;
    private String timeJson;
    private String scheduleDate;
    private String reminderEndDate;
    private String mRepeatType;
    private String mActive;
    private String mRepeat;
    private String[] mDateSplit;
    private String[] mTimeSplit;
    private int mYear, mMonth, mHour, mMinute, mDay;

    private String reminderIdDia, dayOfWeekDia, timeDia, remindInDia;

    private Calendar mCalendar;
    private AlarmReceiver mAlarmReceiver;

    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long mRepeatTime = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;
    ArrayList<HashMap<String, String>> reminders = new ArrayList<>();
    ArrayList<String> time_ar = new ArrayList<>();
    ArrayList<Dialysis> dialysis_ar = new ArrayList<>();
    Context contextNew;
    DatabaseHelper db;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            db = new DatabaseHelper(context);
            mCalendar = Calendar.getInstance();
            mAlarmReceiver = new AlarmReceiver();
            reminders = db.getmedicines();
            contextNew = context;
            for (int h = 0; h < reminders.size(); h++) {
                reminderId = reminders.get(h).get("Id");
                timeJson = reminders.get(h).get("Time");
                scheduleDate = reminders.get(h).get("ScheduleDate");
                reminderEndDate = reminders.get(h).get("Enddate");
                parseTimeJson(timeJson);
            }

            dialysisReminder();

        }
    }

    private void dialysisReminder() {
        dialysis_ar = db.getDialysis();
        for(int j = 0; j<dialysis_ar.size();j++){
            reminderIdDia = dialysis_ar.get(j).getId();
            dayOfWeekDia = dialysis_ar.get(j).getDay();
            timeDia = dialysis_ar.get(j).getTime();
            remindInDia = dialysis_ar.get(j).getRemindIn();

            String[] qq = timeDia.split(":");
            int mHour = Integer.valueOf(qq[0]);
            int mMinute = Integer.valueOf(qq[1]);


            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK,Integer.valueOf(dayOfWeekDia));
            cal.set(Calendar.HOUR_OF_DAY,mHour);
            if(Integer.valueOf(remindInDia)==0){
                cal.add(Calendar.HOUR_OF_DAY,-24);
            }else if(Integer.valueOf(remindInDia)==1){
                cal.add(Calendar.HOUR_OF_DAY,-6);
            }else{
                cal.add(Calendar.HOUR_OF_DAY,-3);
            }
            cal.set(Calendar.MINUTE,mMinute);
            // Cancel existing notification of the reminder by using its ID
            mAlarmReceiver.cancelAlarm(contextNew, Integer.valueOf(reminderIdDia));
            // Create a new notification
            mAlarmReceiver.setRepeatAlarm(contextNew, cal, Integer.valueOf(reminderIdDia), milWeek, "Dialysis");
        }
    }

    private void parseTimeJson(String time) {
        time_ar.clear();
        try {
            JSONObject json = new JSONObject(time);
            JSONArray jarray = json.getJSONArray("timeArrays");
            for (int j = 0; j < jarray.length(); j++) {
                createAlarm(jarray.getString(j));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createAlarm(String time) {

        mDateSplit = scheduleDate.split("-");
        mTimeSplit = time.split(":");

        mDay = Integer.parseInt(mDateSplit[0]);
        mMonth = Integer.parseInt(mDateSplit[1]);
        mYear = Integer.parseInt(mDateSplit[2]);
        mHour = Integer.parseInt(mTimeSplit[0]);
        mMinute = Integer.parseInt(mTimeSplit[1]);

        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        // Cancel existing notification of the reminder by using its ID
        mAlarmReceiver.cancelAlarm(contextNew, Integer.valueOf(reminderId));
        // Create a new notification
        mAlarmReceiver.setRepeatAlarm(contextNew, mCalendar, Integer.valueOf(reminderId), mRepeatTime, "Medicine");
    }

}
