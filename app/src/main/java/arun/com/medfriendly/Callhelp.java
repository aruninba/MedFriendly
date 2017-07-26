package arun.com.medfriendly;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import utilities.Globalpreferences;

/**
 * Created by Arun on 22-Aug-16.
 */
public class Callhelp extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText contactNameEt, contactNumberEt, messageEt;
    private TextInputLayout contactNameTi, contactNumberTi, messageTi;
    private Spinner spinnerAlert;
    private ArrayList<String> alertBy = new ArrayList<>();
    ArrayAdapter<String> adapter;
    private int positionAlert;
    CoordinatorLayout coordiantorAlert;
    Globalpreferences globalPref;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callhelp);
        initialize();
        getDetails();
    }

    private void getDetails() {
        if(!TextUtils.isEmpty(globalPref.getString("EmergencyName"))){
            contactNameEt.setText(globalPref.getString("EmergencyNumber"));
            contactNumberEt.setText(globalPref.getString("EmergencyName"));
            messageEt.setText(globalPref.getString("EmergencyMessage"));
            spinnerAlert.setSelection(Integer.valueOf(globalPref.getString("EmergencyType")));
        }
    }

    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbarDialysis);
        contactNameTi = (TextInputLayout) findViewById(R.id.input_contactname);
        contactNumberTi = (TextInputLayout) findViewById(R.id.input_contactnumber);
        messageTi = (TextInputLayout) findViewById(R.id.input_message);
        contactNameEt = (EditText) findViewById(R.id.contactNameEt);
        contactNumberEt = (EditText) findViewById(R.id.contactNumberEt);
        messageEt = (EditText) findViewById(R.id.emergencymessage);
        spinnerAlert = (Spinner) findViewById(R.id.alertby_list);
        coordiantorAlert = (CoordinatorLayout) findViewById(R.id.coordinateEmergency);
        globalPref = Globalpreferences.getInstances(Callhelp.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Call help");

        alertBy.clear();
        alertBy.add("Alert through?");
        alertBy.add("Call and Sms");
        alertBy.add("Sms only");
        alertBy.add("Call only");

        adapter = new ArrayAdapter<String>(Callhelp.this, R.layout.spinnerlayout, alertBy);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlert.setAdapter(adapter);

        spinnerAlert.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionAlert = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void saveContact() {
        if (isValid()) {
            globalPref.putString("EmergencyName", contactNameEt.getText().toString());
            globalPref.putString("EmergencyNumber", contactNumberEt.getText().toString());
            globalPref.putString("EmergencyMessage", messageEt.getText().toString());
            globalPref.putString("EmergencyType", String.valueOf(positionAlert));
            Snackbar snackbar = Snackbar.make(coordiantorAlert, "Emergency Contact saved Successfully!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private boolean isValid() {
        boolean valid = false;
        if (TextUtils.isEmpty(contactNameEt.getText().toString())) {
            contactNameTi.setError("Please enter name");
            requestFocus(contactNameEt);
            messageTi.setErrorEnabled(false);
            contactNumberTi.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(contactNumberEt.getText().toString())) {
            contactNameTi.setErrorEnabled(false);
            messageTi.setErrorEnabled(false);
            contactNumberTi.setError("Please enter number");
            requestFocus(contactNumberEt);
        } else if (TextUtils.isEmpty(messageEt.getText().toString())) {
            messageTi.setError("Please enter message");
            requestFocus(messageEt);
            contactNameTi.setErrorEnabled(false);
            contactNumberTi.setErrorEnabled(false);
        } else if (positionAlert == 0) {
            Snackbar snackbar = Snackbar.make(coordiantorAlert, "Please select valid Alert type!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            messageTi.setErrorEnabled(false);
            contactNumberTi.setErrorEnabled(false);
            contactNameTi.setErrorEnabled(false);
        } else {
            messageTi.setErrorEnabled(false);
            contactNumberTi.setErrorEnabled(false);
            contactNameTi.setErrorEnabled(false);
            valid = true;
        }
        return valid;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
           getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                saveContact();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
