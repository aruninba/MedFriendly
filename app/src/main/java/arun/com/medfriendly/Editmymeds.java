package arun.com.medfriendly;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import database.DatabaseHelper;

/**
 * Created by arun_i on 12-Jul-17.
 */

public class Editmymeds extends AppCompatActivity {
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    String currentdate_str,reminderEndDate, frequency_pos, days_db, alarm, frequency_ed = "0", reminder_ed, time_ed, dosage_ed, reminderDays,scheduleDate, medname_ed, medId;
    Spinner frequency;
    ArrayList<String> frequency_ar = new ArrayList<>();
    ArrayList<String> time_ar = new ArrayList<>();

    ArrayList<String> dosage_ar = new ArrayList<>();
    private Calendar mCalendarRemind;
    private int mYear, mMonth, mHour, mMinute, mDay;
    ArrayAdapter<String> adapter;
    RelativeLayout time_rl1, time_rl2, time_rl3, time_rl4, time_rl5;
    CardView schedule_ll;
    EditText medication_ed;
    TextView time_tv1, time_tv2, time_tv3, time_tv4, time_tv5, dosage_tv1, dosage_tv2, dosage_tv3, dosage_tv4, dosage_tv5, schedule_date;
    DecimalFormat decimal;
    RadioButton rb1, rb2;
    Switch switchReminder;
    private long mRepeatTime = 86400000L,rowId;
    DatabaseHelper dbhelper;
    Calendar myCalendar;
    AlarmReceiver mAlarmReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmymeds);
        initializeView();
        reminderEndDate = "30-11-2100";
        mAlarmReceiver = new AlarmReceiver();
        mCalendarRemind = Calendar.getInstance();
        mHour = mCalendarRemind.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendarRemind.get(Calendar.MINUTE);
        mYear = mCalendarRemind.get(Calendar.YEAR);
        mMonth = mCalendarRemind.get(Calendar.MONTH) + 1;
        mDay = mCalendarRemind.get(Calendar.DATE);

        if (getIntent().getExtras() != null) {
            medId = getIntent().getExtras().getString("id");
            medname_ed = getIntent().getExtras().getString("medname");
            reminder_ed = getIntent().getExtras().getString("reminder");
            frequency_ed = getIntent().getExtras().getString("frequency");
            time_ed = getIntent().getExtras().getString("time");
            dosage_ed = getIntent().getExtras().getString("dosage");
            scheduleDate = getIntent().getExtras().getString("scheduleDate");
            reminderDays = getIntent().getExtras().getString("reminderdays");
            days_db = getIntent().getExtras().getString("days");
            parseJsonArray();
        }
        schedule_date.setText("Start date: " + scheduleDate);
        medication_ed.setText(medname_ed);
        if (days_db.equalsIgnoreCase("0")) {
            rb1.setChecked(true);
            rb2.setChecked(false);
        } else {
            rb1.setChecked(false);
            rb2.setChecked(true);
            rb2.setText("number of days: " + days_db);
        }

        if (reminder_ed != null && !reminder_ed.isEmpty() && reminder_ed.equalsIgnoreCase("yes")) {
            switchReminder.setChecked(true);
        } else {
            switchReminder.setChecked(false);
        }
        frequency(Integer.valueOf(frequency_ed));

        frequency_ar.add("Once a day");
        frequency_ar.add("Twice a day");
        frequency_ar.add("3 times a day");
        frequency_ar.add("4 times a day");
        frequency_ar.add("5 times a day");

        decimal = new DecimalFormat("0.00");
        dbhelper = new DatabaseHelper(this);

        long date = System.currentTimeMillis();
        SimpleDateFormat da = new SimpleDateFormat("dd-MM-yyyy");
        currentdate_str = da.format(date);

        adapter = new ArrayAdapter<String>(this, R.layout.spinnerlayout, frequency_ar);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequency.setAdapter(adapter);
        frequency.setSelection(Integer.valueOf(frequency_ed));
        switchReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    alarm = "yes";
                } else {
                    alarm = "no";
                }
            }
        });

        schedule_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dp = new DatePickerDialog(Editmymeds.this, datePicker, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dp.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                dp.show();
            }
        });


        time_tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timepicker(time_tv1, 0);

            }
        });

        time_tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timepicker(time_tv2, 1);
            }
        });

        time_tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timepicker(time_tv3, 2);
            }
        });


        time_tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timepicker(time_tv4, 3);
            }

        });


        time_tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timepicker(time_tv5, 4);
            }
        });


        dosage_tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dosage_selection(dosage_tv1, 0);


            }
        });

        dosage_tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dosage_selection(dosage_tv2, 1);
            }
        });

        dosage_tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dosage_selection(dosage_tv3, 2);
            }
        });


        dosage_tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dosage_selection(dosage_tv4, 3);
            }
        });

        dosage_tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dosage_selection(dosage_tv5, 4);
            }
        });

        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                days_db = "0";
                rb2.setChecked(false);
                rb2.setText("number of days");
            }
        });

        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb1.setChecked(false);
                final Dialog alert_dosage = new Dialog(Editmymeds.this);
                alert_dosage.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alert_dosage.setContentView(R.layout.durationdialog);
                alert_dosage.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                alert_dosage.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                alert_dosage.setCancelable(true);
                alert_dosage.show();

                final EditText dosage_tv = (EditText) alert_dosage.findViewById(R.id.edit_dosage);
                Button save = (Button) alert_dosage.findViewById(R.id.save_dosage);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rb2.setText("number of days: " + dosage_tv.getText().toString());
                        days_db = dosage_tv.getText().toString();
                        reminderEndDate = getEndDate(days_db);
                        alert_dosage.dismiss();
                    }
                });
            }
        });


        frequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                frequency_pos = position + "";
                frequency(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void saveReminder() {
        String medication_str = medication_ed.getText().toString();
        if (medication_str != null && !medication_str.isEmpty()) {
            JSONObject json = new JSONObject();
            try {
                json.put("timeArrays", new JSONArray(time_ar));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String timearray_str = json.toString();


            JSONObject json1 = new JSONObject();
            try {
                json1.put("dosageArrays", new JSONArray(dosage_ar));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String dosagearray_str = json1.toString();


            rowId = dbhelper.updateMedicine(medId, medication_str, alarm, frequency_pos, timearray_str, dosagearray_str, schedule_date.getText().toString().replace("Start date: ", ""),reminderEndDate, days_db);
            if (rowId != -1) {
                getTimeForReminder();
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", "Edited successfully");
                setResult(1, intent);
                finish();
            } else {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Unable to Edit medicine!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        } else {
            medication_ed.setError("Please enter the medicine name!");
            medication_ed.setFocusable(true);
        }
    }

    private void deleteReminder() {
        AlertDialog.Builder alert = new AlertDialog.Builder(Editmymeds.this);
        alert.setMessage("Do you want to delete this Medicine?");
        alert.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new AlarmReceiver().cancelAlarm(Editmymeds.this,Integer.valueOf(medId));
                dbhelper.deleteMedicine(medId);
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", "Deleted successfully");
                setResult(2, intent);
                finish();
            }
        });
        alert.show();

    }

    private String getEndDate(String days_db) {
        String date = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, +Integer.valueOf(days_db));
        Date newDate = calendar.getTime();
        date = sdf.format(newDate);
        System.out.println("new date"+date);
        return date;
    }


    private void addReminder() {
        mCalendarRemind.set(Calendar.MONTH, --mMonth);
        mCalendarRemind.set(Calendar.YEAR, mYear);
        mCalendarRemind.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendarRemind.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendarRemind.set(Calendar.MINUTE, mMinute);
        mCalendarRemind.set(Calendar.SECOND, 0);
        new AlarmReceiver().setRepeatAlarm(getApplicationContext(), mCalendarRemind, (int)rowId, mRepeatTime, "Medicine");
    }

    private void getTimeForReminder() {
        mAlarmReceiver.cancelAlarm(getApplicationContext(), (int)rowId);
        for(int z = 0; z< time_ar.size(); z++){
            String enteredTime = time_ar.get(z);
            System.out.println("times"+enteredTime);
            String[] qq = enteredTime.split(":");
            mHour = Integer.valueOf(qq[0]);
            mMinute = Integer.valueOf(qq[1]);
            addReminder();
        }
    }

    private void parseJsonArray() {
        try {
            JSONObject json = new JSONObject(time_ed);
            JSONArray jsonarray = json.optJSONArray("timeArrays");
            for (int t = 0; t < jsonarray.length(); t++) {
                time_ar.add(jsonarray.optString(t));
            }

            JSONObject jsonDosage = new JSONObject(dosage_ed);
            JSONArray arrayDosage = jsonDosage.optJSONArray("dosageArrays");
            for (int t = 0; t < arrayDosage.length(); t++) {
                dosage_ar.add(arrayDosage.optString(t));
            }
            System.out.println("dosage" + arrayDosage.length() + "dosagearray" + dosage_ar.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mDay = dayOfMonth;
            mMonth = monthOfYear+1;
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        schedule_date.setText("Start date: " + sdf.format(myCalendar.getTime()));
    }

    private void frequency(Integer position) {
        if (position == 0) {
            if (time_ar.size() == 1) {
                time_tv1.setText(time_ar.get(0));
                dosage_tv1.setText("Take "+dosage_ar.get(0));
            } else {
                time_ar.clear();
                time_ar.add("08:00");
                dosage_ar.clear();
                dosage_ar.add("1");
            }
            time_rl1.setVisibility(View.VISIBLE);
            time_rl2.setVisibility(View.GONE);
            time_rl3.setVisibility(View.GONE);
            time_rl4.setVisibility(View.GONE);
            time_rl5.setVisibility(View.GONE);

        } else if (position == 1) {
            if (time_ar.size() == 2) {
                time_tv1.setText(time_ar.get(0));
                time_tv2.setText(time_ar.get(1));
                dosage_tv1.setText("Take "+dosage_ar.get(0));
                dosage_tv2.setText("Take "+dosage_ar.get(1));
            } else {
                time_ar.clear();
                time_ar.add("08:00");
                time_ar.add("20:00");
                dosage_ar.clear();
                dosage_ar.add("1");
                dosage_ar.add("1");
                time_tv2.setText("20:00");
            }
            time_rl1.setVisibility(View.VISIBLE);
            time_rl2.setVisibility(View.VISIBLE);
            time_rl3.setVisibility(View.GONE);
            time_rl4.setVisibility(View.GONE);
            time_rl5.setVisibility(View.GONE);

        } else if (position == 2) {
            if (time_ar.size() == 3) {
                time_tv1.setText(time_ar.get(0));
                time_tv2.setText(time_ar.get(1));
                time_tv3.setText(time_ar.get(2));
                dosage_tv1.setText("Take "+dosage_ar.get(0));
                dosage_tv2.setText("Take "+dosage_ar.get(1));
                dosage_tv3.setText("Take "+dosage_ar.get(2));
            } else {
                time_ar.clear();
                time_ar.add("08:00");
                time_ar.add("13:00");
                time_ar.add("20:00");
                dosage_ar.clear();
                dosage_ar.add("1");
                dosage_ar.add("1");
                dosage_ar.add("1");
                time_tv2.setText("13:00");
                time_tv3.setText("20:00");
            }
            time_rl1.setVisibility(View.VISIBLE);
            time_rl2.setVisibility(View.VISIBLE);
            time_rl3.setVisibility(View.VISIBLE);
            time_rl4.setVisibility(View.GONE);
            time_rl5.setVisibility(View.GONE);

        } else if (position == 3) {
            if (time_ar.size() == 4) {
                time_tv1.setText(time_ar.get(0));
                time_tv2.setText(time_ar.get(1));
                time_tv3.setText(time_ar.get(2));
                time_tv4.setText(time_ar.get(3));
                dosage_tv1.setText("Take "+dosage_ar.get(0));
                dosage_tv2.setText("Take "+dosage_ar.get(1));
                dosage_tv3.setText("Take "+dosage_ar.get(2));
                dosage_tv4.setText("Take "+dosage_ar.get(3));
            } else {
                time_ar.clear();
                time_ar.add("08:00");
                time_ar.add("12:00");
                time_ar.add("16:00");
                time_ar.add("20:00");
                dosage_ar.clear();
                dosage_ar.add("1");
                dosage_ar.add("1");
                dosage_ar.add("1");
                dosage_ar.add("1");
                time_tv2.setText("12:00");
                time_tv3.setText("16:00");
                time_tv4.setText("20:00");
            }
            time_rl1.setVisibility(View.VISIBLE);
            time_rl2.setVisibility(View.VISIBLE);
            time_rl3.setVisibility(View.VISIBLE);
            time_rl4.setVisibility(View.VISIBLE);
            time_rl5.setVisibility(View.GONE);

        } else if (position == 4) {
            System.out.println("time position" + time_ar.size());
            if (time_ar.size() == 5) {
                System.out.println("time position");
                time_tv1.setText(time_ar.get(0));
                time_tv2.setText(time_ar.get(1));
                time_tv3.setText(time_ar.get(2));
                time_tv4.setText(time_ar.get(3));
                time_tv5.setText(time_ar.get(4));
                dosage_tv1.setText("Take "+dosage_ar.get(0));
                dosage_tv2.setText("Take "+dosage_ar.get(1));
                dosage_tv3.setText("Take "+dosage_ar.get(2));
                dosage_tv4.setText("Take "+dosage_ar.get(3));
                dosage_tv5.setText("Take "+dosage_ar.get(4));
            } else {
                System.out.println("time position454254");
                time_ar.clear();
                time_ar.add("08:00");
                time_ar.add("12:00");
                time_ar.add("16:00");
                time_ar.add("20:00");
                time_ar.add("20:00");
                dosage_ar.clear();
                dosage_ar.add("1");
                dosage_ar.add("1");
                dosage_ar.add("1");
                dosage_ar.add("1");
                dosage_ar.add("1");
            }
            time_rl1.setVisibility(View.VISIBLE);
            time_rl2.setVisibility(View.VISIBLE);
            time_rl3.setVisibility(View.VISIBLE);
            time_rl4.setVisibility(View.VISIBLE);
            time_rl5.setVisibility(View.VISIBLE);

        }

    }

    private void initializeView() {
        alarm = "yes";
        myCalendar = Calendar.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbarMeds);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Medicine");
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateAddmed);
        frequency = (Spinner) findViewById(R.id.frequency_list);
        time_rl1 = (RelativeLayout) findViewById(R.id.time_relative1);
        time_rl2 = (RelativeLayout) findViewById(R.id.time_relative2);
        time_rl3 = (RelativeLayout) findViewById(R.id.time_relative3);
        time_rl4 = (RelativeLayout) findViewById(R.id.time_relative4);
        time_rl5 = (RelativeLayout) findViewById(R.id.time_relative5);
        schedule_ll = (CardView) findViewById(R.id.schedule_ll);

        medication_ed = (EditText) findViewById(R.id.medication_edt);
        medication_ed.setFocusable(false);

        time_tv1 = (TextView) findViewById(R.id.time_1);
        time_tv2 = (TextView) findViewById(R.id.time_2);
        time_tv3 = (TextView) findViewById(R.id.time_3);
        time_tv4 = (TextView) findViewById(R.id.time_4);
        time_tv5 = (TextView) findViewById(R.id.time_5);

        dosage_tv1 = (TextView) findViewById(R.id.dosage1);
        dosage_tv2 = (TextView) findViewById(R.id.dosage2);
        dosage_tv3 = (TextView) findViewById(R.id.dosage3);
        dosage_tv4 = (TextView) findViewById(R.id.dosage4);
        dosage_tv5 = (TextView) findViewById(R.id.dosage5);


        schedule_date = (TextView) findViewById(R.id.schedule_startdate_tv);

        rb1 = (RadioButton) findViewById(R.id.radio_continuous);
        rb2 = (RadioButton) findViewById(R.id.radio_numberofdays);

        switchReminder = (Switch) findViewById(R.id.switch_reminder);

    }

    private void dosage_selection(final TextView tv, final int t) {

        final Dialog alert_dosage = new Dialog(Editmymeds.this);
        alert_dosage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert_dosage.setContentView(R.layout.alertdosage);
        alert_dosage.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        alert_dosage.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alert_dosage.setCancelable(true);
        alert_dosage.show();
        final EditText dosage_tv = (EditText) alert_dosage.findViewById(R.id.edit_dosage);
        Button save = (Button) alert_dosage.findViewById(R.id.save_dosage);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("Take " + dosage_tv.getText().toString());
                dosage_ar.set(t, tv.getText().toString().replace("Take ", ""));
                alert_dosage.dismiss();
            }
        });
    }

    private void timepicker(final TextView tv, final int t) {
        String q = tv.getText().toString();
        String[] qq = q.split(":");
        String hour_st = qq[0];
        int hour = Integer.valueOf(hour_st);
        int minute = 00;

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(Editmymeds.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (selectedMinute < 10) {
                    tv.setText(selectedHour + ":0" + selectedMinute);
                } else {
                    tv.setText(selectedHour + ":" + selectedMinute);
                }
                time_ar.set(t, tv.getText().toString());

            }
        }, hour, minute, false);//Yes 24 hour time

        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_edit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.delete_reminder:
                deleteReminder();
                return true;

            case R.id.save_reminder:
                saveReminder();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
