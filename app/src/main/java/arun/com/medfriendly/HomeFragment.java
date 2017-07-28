package arun.com.medfriendly;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import adapter.DialysisAdapter;
import database.DatabaseHelper;
import model.Dialysis;
import model.Waterintake;
import utilities.CircleProgress;


public class HomeFragment extends Fragment {

    View rootView;
    private TextView dialysisRemind;
    private CircleProgress circleProgress;
    private FloatingActionButton fab;
    DatabaseHelper dbHelper;
    SimpleDateFormat sdf;
    Calendar calendar;

    public static int startPositionHome, stopPositionHome;
    private String date;
    static int finishedColor;
    ArrayList<Dialysis> dialysisReminder = new ArrayList<>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.homepage, container, false);
        initialize();
        checkWaterIntake();
        checkDialysis();
        return  rootView;
    }

    private void checkDialysis() {
        dialysisReminder.clear();
        dialysisReminder = dbHelper.getDialysis();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        outerloop:
        for (int t = 0; t<dialysisReminder.size(); t++){
            if(currentDay == Integer.valueOf(dialysisReminder.get(0).getDay())){
                dialysisRemind.setText("Hi Arun, \n your next dialysis is Today");
                break outerloop;
            }else{
                if(currentDay > Integer.valueOf(dialysisReminder.get(0).getDay())){
                    Calendar cal = Calendar.getInstance();
                    int i = cal.get(Calendar.WEEK_OF_MONTH);
                    cal.set(Calendar.WEEK_OF_MONTH, ++i);
                    cal.set(Calendar.DAY_OF_WEEK,Integer.valueOf(dialysisReminder.get(0).getDay()));
                    dialysisRemind.setText("Hi Arun, \n your next dialysis is on "+sdf.format(cal.getTime()));
                    break outerloop;
                }else{
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK,Integer.valueOf(dialysisReminder.get(0).getDay()));
                    dialysisRemind.setText("Hi Arun, \n your next dialysis is on "+sdf.format(cal.getTime()));
                    break outerloop;
                }
            }
        }
    }

    private void checkWaterIntake() {
        Waterintake waterIntake = dbHelper.getParticularDate();
        if (date.equalsIgnoreCase(waterIntake.getDate())) {
            startPositionHome = 0;
            stopPositionHome = Integer.valueOf(waterIntake.getStopposition());
            ObjectAnimator anim = ObjectAnimator.ofInt(circleProgress, "progress", startPositionHome, stopPositionHome);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(800);
            anim.start();
            startPositionHome = stopPositionHome;
        }
    }



    private void initialize() {
        dialysisRemind = (TextView) rootView.findViewById(R.id.remindDialysis);
        circleProgress = (CircleProgress) rootView.findViewById(R.id.circular_progress);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fabWaterPlus);
        dbHelper = new DatabaseHelper(getActivity());
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        calendar = Calendar.getInstance();
        date = sdf.format(calendar.getTime());
    }

    public static int getFinishedColor() {
        if (stopPositionHome > 75) {
            finishedColor = -65536;
        } else if (stopPositionHome > 50) {
            finishedColor = Color.rgb(255, 69, 0);
        } else {
            finishedColor = -12414479;
        }
        return finishedColor;
    }
}
