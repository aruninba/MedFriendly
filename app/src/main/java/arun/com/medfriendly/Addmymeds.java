package arun.com.medfriendly;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import api.Apiclient;
import api.Apiinterface;
import database.DatabaseHelper;
import model.MedicineResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utilities.Constants;

/**
 * Created by Arun on 22-Aug-16.
 */
public class Addmymeds extends AppCompatActivity {
    Spinner frequency;
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    ArrayList<String> frequency_ar = new ArrayList<>();
    ArrayList<String> time_ar = new ArrayList<>();
    ArrayList<String> dosage_ar = new ArrayList<>();
    ArrayList<MedicineResponse> medicineAutocomplete = new ArrayList<>();
    ArrayList<String> medicines = new ArrayList<>();
    ArrayAdapter<String> dataAdapter;
    private Calendar mCalendarRemind;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime = 86400000L, rowId;
    ArrayAdapter<String> adapter;
    RelativeLayout time_rl1, time_rl2, time_rl3, time_rl4, time_rl5;
    CardView schedule_ll;
    AutoCompleteTextView medication_ed;
    TextView time_tv1, time_tv2, time_tv3, time_tv4, time_tv5, dosage_tv1, dosage_tv2, dosage_tv3, dosage_tv4, dosage_tv5, schedule_date;
    String currentdate_str, frequency_pos, alarm, days_db, reminderEndDate;
    DecimalFormat decimal;
    RadioButton rb1, rb2;
    Switch switchReminder;

    DatabaseHelper dbhelper;
    Calendar myCalendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmymeds);
        initializeView();
        days_db = "0";
        reminderEndDate = "30-11-2100";
        frequency_ar.add("Once a day");
        frequency_ar.add("Twice a day");
        frequency_ar.add("3 times a day");
        frequency_ar.add("4 times a day");
        frequency_ar.add("5 times a day");

        mCalendarRemind = Calendar.getInstance();
        mHour = mCalendarRemind.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendarRemind.get(Calendar.MINUTE);
        mYear = mCalendarRemind.get(Calendar.YEAR);
        mMonth = mCalendarRemind.get(Calendar.MONTH) + 1;
        mDay = mCalendarRemind.get(Calendar.DATE);

        decimal = new DecimalFormat("0.00");
        dbhelper = new DatabaseHelper(Addmymeds.this);

        long date = System.currentTimeMillis();
        SimpleDateFormat da = new SimpleDateFormat("dd-MM-yyyy");
        currentdate_str = da.format(date);
        schedule_date.setText("Start date: " + currentdate_str);

        adapter = new ArrayAdapter<String>(Addmymeds.this, R.layout.spinnerlayout, frequency_ar);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequency.setAdapter(adapter);
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
                DatePickerDialog dp = new DatePickerDialog(Addmymeds.this, datePicker, myCalendar
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
                final Dialog alert_dosage = new Dialog(Addmymeds.this);
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


            rowId = dbhelper.insert_medicine(medication_str, alarm, frequency_pos, timearray_str, dosagearray_str, schedule_date.getText().toString().replace("Start date: ", ""), reminderEndDate, days_db);
            if (rowId != -1) {
                getTimeForReminder();
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", "Added successfully");
                setResult(1, intent);
                finish();
            } else {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Unable to Add medicine!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        } else {
            medication_ed.setError("Please enter the medicine name!");
            medication_ed.setFocusable(true);
        }
    }

    private String getEndDate(String days_db) {
        String date = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, +Integer.valueOf(days_db));
        Date newDate = calendar.getTime();
        date = sdf.format(newDate);
        System.out.println("new date" + date);
        return date;
    }

    private void addReminder() {
        mCalendarRemind.set(Calendar.MONTH, --mMonth);
        mCalendarRemind.set(Calendar.YEAR, mYear);
        mCalendarRemind.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendarRemind.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendarRemind.set(Calendar.MINUTE, mMinute);
        mCalendarRemind.set(Calendar.SECOND, 0);
        System.out.println("reminder added" + mHour + mMinute);
        new AlarmReceiver().setRepeatAlarm(getApplicationContext(), mCalendarRemind, (int) rowId, 60000L, "Medicine");
    }

    private void getTimeForReminder() {
        for (int z = 0; z < time_ar.size(); z++) {
            String enteredTime = time_ar.get(z);
            System.out.println("times" + enteredTime);
            String[] qq = enteredTime.split(":");
            mHour = Integer.valueOf(qq[0]);
            mMinute = Integer.valueOf(qq[1]);
            addReminder();
        }
    }

    private void initializeView() {
        alarm = "yes";
        myCalendar = Calendar.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbarMeds);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Medicine");
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateAddmed);
        frequency = (Spinner) findViewById(R.id.frequency_list);
        time_rl1 = (RelativeLayout) findViewById(R.id.time_relative1);
        time_rl2 = (RelativeLayout) findViewById(R.id.time_relative2);
        time_rl3 = (RelativeLayout) findViewById(R.id.time_relative3);
        time_rl4 = (RelativeLayout) findViewById(R.id.time_relative4);
        time_rl5 = (RelativeLayout) findViewById(R.id.time_relative5);
        schedule_ll = (CardView) findViewById(R.id.schedule_ll);

        medication_ed = (AutoCompleteTextView) findViewById(R.id.medication_edt);

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


        medication_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= 3) {
                    getAutocomplete(s.toString());
                }
            }
        });
    }

    /**
     * get the medicine name based upon the entered text
     * @param s
     */
    private void getAutocomplete(String s) {
        Apiinterface apiservice = Apiclient.getClient().create(Apiinterface.class);
        Call<ArrayList<MedicineResponse>> call = apiservice.getMedicine(Constants.accessToken, s);
        call.enqueue(new Callback<ArrayList<MedicineResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<MedicineResponse>> call, Response<ArrayList<MedicineResponse>> response) {
                // System.out.println("success code"+response.code()+response.raw().request().url());
                if (response.isSuccess()) {
                    medicineAutocomplete = response.body();
                    medicines.clear();
                    for (int g = 0; g < medicineAutocomplete.size(); g++) {
                        medicines.add(medicineAutocomplete.get(g).getName());
                    }
                    updateEdittextAdapter(medicines);

                }
            }

            @Override
            public void onFailure(Call<ArrayList<MedicineResponse>> call, Throwable t) {

            }
        });
    }

    /**
     * to update the autocomplete adapter with medicine names
     * @param medicines
     */
    private void updateEdittextAdapter(ArrayList<String> medicines) {
        dataAdapter = new ArrayAdapter<String>
                (Addmymeds.this, R.layout.post_spinner, R.id.textView, medicines);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medication_ed.setThreshold(1);
        medication_ed.setAdapter(dataAdapter);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(medication_ed.getWindowToken(), 0);
    }

    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
            mDay = dayOfMonth;
            mMonth = monthOfYear + 1;
            System.out.println("mmonth" + monthOfYear);
        }

    };

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        schedule_date.setText("Start date: " + sdf.format(myCalendar.getTime()));
    }


    private void frequency(int position) {
        if (position == 0) {
            time_ar.clear();
            time_ar.add("08:00");
            dosage_ar.clear();
            dosage_ar.add("1");
            time_rl1.setVisibility(View.VISIBLE);
            time_rl2.setVisibility(View.GONE);
            time_rl3.setVisibility(View.GONE);
            time_rl4.setVisibility(View.GONE);
            time_rl5.setVisibility(View.GONE);

        } else if (position == 1) {
            time_ar.clear();
            time_ar.add("08:00");
            time_ar.add("20:00");
            dosage_ar.clear();
            dosage_ar.add("1");
            dosage_ar.add("1");
            time_tv2.setText("20:00");
            time_rl1.setVisibility(View.VISIBLE);
            time_rl2.setVisibility(View.VISIBLE);
            time_rl3.setVisibility(View.GONE);
            time_rl4.setVisibility(View.GONE);
            time_rl5.setVisibility(View.GONE);

        } else if (position == 2) {
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
            time_rl1.setVisibility(View.VISIBLE);
            time_rl2.setVisibility(View.VISIBLE);
            time_rl3.setVisibility(View.VISIBLE);
            time_rl4.setVisibility(View.GONE);
            time_rl5.setVisibility(View.GONE);

        } else if (position == 3) {
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
            time_rl1.setVisibility(View.VISIBLE);
            time_rl2.setVisibility(View.VISIBLE);
            time_rl3.setVisibility(View.VISIBLE);
            time_rl4.setVisibility(View.VISIBLE);
            time_rl5.setVisibility(View.GONE);

        } else if (position == 4) {
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
            time_rl1.setVisibility(View.VISIBLE);
            time_rl2.setVisibility(View.VISIBLE);
            time_rl3.setVisibility(View.VISIBLE);
            time_rl4.setVisibility(View.VISIBLE);
            time_rl5.setVisibility(View.VISIBLE);

        }
    }

    private void dosage_selection(final TextView tv, final int t) {

        final Dialog alert_dosage = new Dialog(Addmymeds.this);
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
        mTimePicker = new TimePickerDialog(Addmymeds.this, new TimePickerDialog.OnTimeSetListener() {
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
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
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
