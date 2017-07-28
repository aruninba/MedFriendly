package arun.com.medfriendly;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import database.DatabaseHelper;
import model.Dialysis;
import model.Hospital;

/**
 * Created by arun_i on 17-Jul-17.
 */

public class AddDialysis extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button sunBtn, monBtn, tuesBtn, wedBtn, thuBtn, friBtn, satBtn;
    private EditText diaTimeEt;
    AutoCompleteTextView diaHospitalEt;
    private Spinner diaSpinner;
    private FrameLayout spinFL;
    private String currentDate;
    private int selectedDay = 99, hour, minute, spinnerSelection = 0;
    private Calendar myCalendar;
    private ArrayList<String> reminder_Ar = new ArrayList<>();
    private CoordinatorLayout coordinate;
    private long rowId;
    ArrayAdapter arrayAdapter;
    DatabaseHelper dbhelper;
    private static final long milWeek = 604800000L;
    ArrayList<String> filterHospital = new ArrayList<>();
    ArrayAdapter<String> dataAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adddialysis);
        initializeView();
    }

    private void initializeView() {
        dbhelper = new DatabaseHelper(this);
        toolbar = (Toolbar) findViewById(R.id.toolbarDialysis);
        sunBtn = (Button) findViewById(R.id.day0);
        monBtn = (Button) findViewById(R.id.day1);
        tuesBtn = (Button) findViewById(R.id.day2);
        wedBtn = (Button) findViewById(R.id.day3);
        thuBtn = (Button) findViewById(R.id.day4);
        friBtn = (Button) findViewById(R.id.day5);
        satBtn = (Button) findViewById(R.id.day6);
        sunBtn.setOnClickListener(this);
        monBtn.setOnClickListener(this);
        tuesBtn.setOnClickListener(this);
        wedBtn.setOnClickListener(this);
        thuBtn.setOnClickListener(this);
        friBtn.setOnClickListener(this);
        satBtn.setOnClickListener(this);

        diaTimeEt = (EditText) findViewById(R.id.dialysisTimeEt);
        diaHospitalEt = (AutoCompleteTextView) findViewById(R.id.dialysisHospitalEt);
        diaSpinner = (Spinner) findViewById(R.id.dialysisSpin);
        spinFL = (FrameLayout) findViewById(R.id.dsFrame);
        coordinate = (CoordinatorLayout) findViewById(R.id.coordinatedialysis);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Add Dialysis Reminder");

        reminder_Ar.add("24 hours before Dialysis");
        reminder_Ar.add("6 hours before Dialysis");
        reminder_Ar.add("3 hours before Dialysis");
        arrayAdapter = new ArrayAdapter<String>(AddDialysis.this,R.layout.spinnerlayout,reminder_Ar);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diaSpinner.setAdapter(arrayAdapter);


        myCalendar = Calendar.getInstance();
        hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        minute = myCalendar.get(Calendar.MINUTE);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = df.format(myCalendar.getTime());

        diaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        diaTimeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker();
            }
        });

        diaHospitalEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= 6) {
                    filterHospital.clear();
                    for(int t = 0; t<DialysisReminder.hospitalAr.size(); t++){
                        if(DialysisReminder.hospitalAr.get(t).getHospitalName().toLowerCase().contains(s.toString())){
                            filterHospital.add(DialysisReminder.hospitalAr.get(t).getHospitalName());
                        }
                        if(t==DialysisReminder.hospitalAr.size()-1){
                            System.out.println("t"+t+"size"+DialysisReminder.hospitalAr.size());
                            updateEdittextAdapter(filterHospital);
                        }
                    }
                }
            }
        });
    }

    private Hospital filterHospitalArray() {
        Hospital hospi = new Hospital();
        for(int t = 0; t<DialysisReminder.hospitalAr.size(); t++){
            if(DialysisReminder.hospitalAr.get(t).getHospitalName().toLowerCase().contains(diaHospitalEt.getText().toString().toLowerCase())){
                hospi = DialysisReminder.hospitalAr.get(t);
            }
        }
        return hospi;
    }

    private void updateEdittextAdapter(ArrayList<String> filterHospital) {
        dataAdapter = new ArrayAdapter<String>
                (AddDialysis.this, R.layout.post_spinner, R.id.textView, filterHospital);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diaHospitalEt.setThreshold(1);
        diaHospitalEt.setAdapter(dataAdapter);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(diaHospitalEt.getWindowToken(), 0);

    }

    private void timePicker() {
        TimePickerDialog timePicker;
        timePicker = new TimePickerDialog(AddDialysis.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (minute < 10) {
                    diaTimeEt.setText(hourOfDay + ":0" + minute);
                } else {
                    diaTimeEt.setText(hourOfDay + ":" + minute);
                }
            }
        },hour,minute,false);
        timePicker.setTitle("Select Time");
        timePicker.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    private void saveReminder() {
        if(validateFields()) {
            Dialysis dialysis = new Dialysis();
            dialysis.setDay(String.valueOf(selectedDay));
            dialysis.setTime(diaTimeEt.getText().toString());
            dialysis.setHospital(diaHospitalEt.getText().toString());
            dialysis.setRemindIn(String.valueOf(spinnerSelection));
            dialysis.setDate(currentDate);
            Hospital hospi = filterHospitalArray();
            dialysis.setHospitalCoordinates(hospi.getCoordinates());
            dialysis.setHospitalLocation(hospi.getLocation());
            dialysis.setHospitalState(hospi.getState());
            dialysis.setHospitalDistrict(hospi.getDistrict());
            dialysis.setHospitalPincode(hospi.getPincode());
            dialysis.setHospitalPhone(hospi.getTelephone());
            rowId = dbhelper.insertDialysis(dialysis);
            if(rowId != -1){
                getReminderTime();
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", "Added successfully");
                setResult(1, intent);
                finish();
            }
        }
    }

    private void getReminderTime() {
        String enteredTime = diaTimeEt.getText().toString();
        String[] qq = enteredTime.split(":");
        int mHour = Integer.valueOf(qq[0]);
        int mMinute = Integer.valueOf(qq[1]);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK,selectedDay);
        cal.set(Calendar.HOUR_OF_DAY,mHour);
        if(spinnerSelection==0){
            cal.add(Calendar.HOUR_OF_DAY,-24);
        }else if(spinnerSelection==1){
            cal.add(Calendar.HOUR_OF_DAY,-6);
        }else{
            cal.add(Calendar.HOUR_OF_DAY,-3);
        }
        cal.set(Calendar.MINUTE,mMinute);

        new AlarmReceiver().setRepeatAlarm(getApplicationContext(), cal, (int) rowId, milWeek,"Dialysis");
    }

    private Boolean validateFields() {
        Boolean valid = false;
        if(selectedDay==99){
            showSnackBar("Please select any day!");
        }else if(TextUtils.isEmpty(diaTimeEt.getText().toString())){
            showSnackBar("Please select the time!");
        }else if(TextUtils.isEmpty(diaHospitalEt.getText().toString())){
            showSnackBar("please enter the hospital!");
        }else{
            valid = true;
        }
        return valid;
    }

    private void showSnackBar(String s) {
        Snackbar snack = Snackbar.make(coordinate,s,Snackbar.LENGTH_SHORT);
        snack.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.day0:
                setDayBackground(sunBtn);
                selectedDay = 1;
                return;
            case R.id.day1:
                setDayBackground(monBtn);
                selectedDay = 2;
                return;
            case R.id.day2:
                setDayBackground(tuesBtn);
                selectedDay = 3;
                return;
            case R.id.day3:
                setDayBackground(wedBtn);
                selectedDay = 4;
                return;
            case R.id.day4:
                setDayBackground(thuBtn);
                selectedDay = 5;
                return;
            case R.id.day5:
                setDayBackground(friBtn);
                selectedDay = 6;
                return;
            case R.id.day6:
                setDayBackground(satBtn);
                selectedDay = 7;
                return;
        }

    }

    /**
     * set background color to the selected button
     *
     * @param Btn
     */
    private void setDayBackground(Button Btn) {

        sunBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_rounded_btn));
        sunBtn.setTextColor(Color.parseColor("#000000"));
        monBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_rounded_btn));
        monBtn.setTextColor(Color.parseColor("#000000"));
        tuesBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_rounded_btn));
        tuesBtn.setTextColor(Color.parseColor("#000000"));
        wedBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_rounded_btn));
        wedBtn.setTextColor(Color.parseColor("#000000"));
        thuBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_rounded_btn));
        thuBtn.setTextColor(Color.parseColor("#000000"));
        friBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_rounded_btn));
        friBtn.setTextColor(Color.parseColor("#000000"));
        satBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_rounded_btn));
        satBtn.setTextColor(Color.parseColor("#000000"));

        Btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_rounded_btn));
        Btn.setTextColor(Color.parseColor("#FFFFFF"));


    }


}
