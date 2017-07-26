package arun.com.medfriendly;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.zip.Inflater;

import utilities.SpeedometerGauge;

/**
 * Created by Arun on 22-Aug-16.
 */
public class UFcalculator extends Fragment {

    View rootView;
    private EditText postWeightEt, preWeightEt, removeFluidEt, hoursEt;
    private Button calculateBtn;
    private String removeFluidStr, currentWtStr, hoursTakenStr;
    private SpeedometerGauge speedometer;
    private Dialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.ufcalculator, container, false);
        initializeView();
        return rootView;
    }

    private void initializeView() {
        setHasOptionsMenu(true);
        postWeightEt = (EditText) rootView.findViewById(R.id.postweightET);
        preWeightEt = (EditText) rootView.findViewById(R.id.currentweightET);
        removeFluidEt = (EditText) rootView.findViewById(R.id.removeFluidET);
        hoursEt = (EditText) rootView.findViewById(R.id.treatmentTimeET);
        calculateBtn = (Button) rootView.findViewById(R.id.calculateufBtn);

        preWeightEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(preWeightEt.getText().toString())) {
                        Double fluidRemoveDb = Double.valueOf(preWeightEt.getText().toString()) - Double.valueOf(postWeightEt.getText().toString());
                        if (!(fluidRemoveDb < 0.0)) {
                            removeFluidStr = String.format("%.2f", fluidRemoveDb);
                            removeFluidEt.setText(removeFluidStr);
                        }
                    }
                }
            }
        });

        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    currentWtStr = preWeightEt.getText().toString();
                    removeFluidStr = removeFluidEt.getText().toString();
                    hoursTakenStr = hoursEt.getText().toString();
                    Double resultDb = ((Double.valueOf(removeFluidStr) * 1000) / Double.valueOf(hoursTakenStr) / Double.valueOf(currentWtStr));
                    showDialogMeter(resultDb);


                }
            }
        });
    }

    private void showDialogMeter(Double resultDb) {
        alertDialog = new Dialog(getActivity(),R.style.AppTheme);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.alertmeter);
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.show();
        Toolbar toolbar = (Toolbar) alertDialog.findViewById(R.id.toolbar_UFalert);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Payment Options");
        TextView reading_Tv = (TextView) alertDialog.findViewById(R.id.readingMeter);
        TextView result_Tv = (TextView) alertDialog.findViewById(R.id.readingSuggestion);
        reading_Tv.setText("UF Rate: " + String.format("%.1f", resultDb));
        if (resultDb <= 10) {
            reading_Tv.setTextColor(Color.GREEN);
            result_Tv.setText("Great! You are in the green. Your HD treatment will remove water at a safe rate for you.");
        } else if (resultDb > 10 && resultDb <= 13) {
            reading_Tv.setTextColor(Color.BLUE);
            getCorrectedLiquid();
            getCorrectedHours();
            result_Tv.setText("In the U.S., Medicare is looking at a UFR target of less than 13 mL/Kg/Hr, and you are in this range. If you want to be in the green, reduce water weight to "+ getCorrectedLiquid()+" liters, increase HD time to "+getCorrectedHours()+" hours, or both.");
        } else {
            reading_Tv.setTextColor(Color.RED);
            getCorrectedLiquid();
            getCorrectedHours();
            result_Tv.setText("You are in the DANGER zone! If you have cramps, headaches, or feel dizzy, you may have “organ stunning,” which can cause permanent harm. To be in the green, reduce water weight to "+ getCorrectedLiquid()+" liters, increase HD time to "+getCorrectedHours()+ " hours, or both.");
        }



        speedometer = (SpeedometerGauge) alertDialog.findViewById(R.id.speedometer);
        speedometer.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });
        speedometer.setMaxSpeed(30);
        speedometer.setMajorTickStep(5);
        speedometer.setMinorTicks(4);
        speedometer.addColoredRange(0, 10, Color.GREEN);
        speedometer.addColoredRange(10, 13, Color.YELLOW);
        speedometer.addColoredRange(13, 30, Color.RED);
        speedometer.setSpeed(resultDb, 1000, 300);


    }

    private String getCorrectedHours() {
        Double hoursCorrected = ((Double.valueOf(removeFluidStr) * 1000)) / Double.valueOf(currentWtStr) / Double.valueOf("10");
        return String.format("%.1f",hoursCorrected);
    }

    private String getCorrectedLiquid() {
        Double liquidCorrected = (Double.valueOf("10")* Double.valueOf(hoursTakenStr)*Double.valueOf(currentWtStr))/1000;
        return String.format("%.1f",liquidCorrected);
    }

    private Boolean validateFields() {
        Boolean isValid = false;
        if (TextUtils.isEmpty(postWeightEt.getText().toString())) {
            postWeightEt.setError("Please enter post weight!");
        } else if (TextUtils.isEmpty(preWeightEt.getText().toString())) {
            preWeightEt.setError("Please enter current weight!");
        } else if (TextUtils.isEmpty(removeFluidEt.getText().toString())) {
            removeFluidEt.setError("Fluid to be removed cannot be empty!");
        } else if (TextUtils.isEmpty(hoursEt.getText().toString())) {
            hoursEt.setError("Please enter treatment hours!");
        } else {
            isValid = true;
        }

        return isValid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Discard any changes
            case android.R.id.home:
                alertDialog.dismiss();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
