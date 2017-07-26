package arun.com.medfriendly;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import database.DatabaseHelper;
import model.Waterintake;
import utilities.CircleProgress;

/**
 * Created by Arun on 22-Aug-16.
 */
public class WaterFragment extends Fragment {
    View rootview;
    private CircleProgress circularProgress;
    private EditText cupLevelEt, waterLimitEt;
    FloatingActionButton fabPlus;
    CoordinatorLayout coordinator;
    public static int startPosition, stopPosition;
    static int finishedColor;
    DatabaseHelper dbHelper;
    SimpleDateFormat sdf;
    Calendar calendar;
    private String date, rowId, cupLevelStr, waterLimitStr;
    private boolean isInsert = false;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.waterintake, container, false);
        initializeView();
        return rootview;
    }

    private void checkUpdateInsert() {
        Waterintake waterIntake = dbHelper.getParticularDate();
        if (date.equalsIgnoreCase(waterIntake.getDate())) {
            rowId = waterIntake.getId();
            isInsert = false;
            updateView(waterIntake);
        } else {
            isInsert = true;
        }
    }

    private void updateView(Waterintake waterIntake) {
        cupLevelStr = waterIntake.getCuplevel();
        waterLimitStr = waterIntake.getWaterlimit();
        cupLevelEt.setText(waterIntake.getCuplevel());
        waterLimitEt.setText(waterIntake.getWaterlimit());
        startPosition = 0;
        stopPosition = Integer.valueOf(waterIntake.getStopposition());
        updateProgressBar();
    }

    private void initializeView() {
        circularProgress = (CircleProgress) rootview.findViewById(R.id.circular_progress);
        cupLevelEt = (EditText) rootview.findViewById(R.id.cupLevelEt);
        waterLimitEt = (EditText) rootview.findViewById(R.id.waterLimitEt);
        fabPlus = (FloatingActionButton) rootview.findViewById(R.id.fabWaterPlus);
        coordinator = (CoordinatorLayout) rootview.findViewById(R.id.coordinatewater);
        dbHelper = new DatabaseHelper(getActivity());
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        calendar = Calendar.getInstance();
        date = sdf.format(calendar.getTime());

        startPosition = 0;
        stopPosition = 0;
        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cupLevelStr = cupLevelEt.getText().toString();
                waterLimitStr = waterLimitEt.getText().toString();
                if (TextUtils.isEmpty(cupLevelStr)) {
                    cupLevelEt.setError("Please enter cup level!");
                } else if (TextUtils.isEmpty(waterLimitStr)) {
                    waterLimitEt.setError("Please enter water intake limit!");
                } else if (Integer.valueOf(cupLevelStr) > Integer.valueOf(waterLimitStr)) {
                    Snackbar snackbar = Snackbar.make(coordinator, "Cup level cannot be larger than water intake limit", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    if (stopPosition < 100) {
                        double noOfCupsPerDay = Double.valueOf(waterLimitStr) / Double.valueOf(cupLevelStr);
                        double progressPerCup = 100 / noOfCupsPerDay;
                        stopPosition += (int) progressPerCup;
                        System.out.println("stoppo"+stopPosition);
                        ObjectAnimator anim = ObjectAnimator.ofInt(circularProgress, "progress", startPosition, stopPosition);
                        anim.setInterpolator(new DecelerateInterpolator());
                        anim.setDuration(800);
                        anim.start();
                        startPosition = stopPosition;
                    }
                }
            }
        });
    }

    private void updateProgressBar() {

        ObjectAnimator anim = ObjectAnimator.ofInt(circularProgress, "progress", startPosition, stopPosition);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(800);
        anim.start();
        startPosition = stopPosition;
    }

    @Override
    public void onPause() {
        super.onPause();
        Waterintake waterintake = new Waterintake();
        waterintake.setDate(date);
        waterintake.setCuplevel(cupLevelEt.getText().toString());
        waterintake.setWaterlimit(waterLimitEt.getText().toString());
        waterintake.setStopposition(String.valueOf(stopPosition));
        if (isInsert) {
            dbHelper.insert_waterIntake(waterintake);
        } else {
            waterintake.setId(rowId);
            dbHelper.updateWaterIntake(waterintake);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUpdateInsert();
    }

    public static int getFinishedColor() {
        if (stopPosition > 75) {
            finishedColor = -65536;
        } else if (stopPosition > 50) {
            finishedColor = Color.rgb(255, 69, 0);
        } else {
            finishedColor = -12414479;
        }
        return finishedColor;
    }
}
