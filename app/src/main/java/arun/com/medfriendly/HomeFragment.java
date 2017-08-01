package arun.com.medfriendly;

import android.*;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.text.Text;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import adapter.DialysisAdapter;
import database.DatabaseHelper;
import model.Dialysis;
import model.Waterintake;
import utilities.CircleProgress;
import utilities.Constants;
import utilities.Globalpreferences;

import static arun.com.medfriendly.R.id.imageView;


public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    View rootView;
    private TextView dialysisRemind, welcomeTv, addDialysisTv;
    private CircleProgress circleProgress;
    private FloatingActionButton fab;
    DatabaseHelper dbHelper;
    SimpleDateFormat sdf;
    Calendar calendar;

    public static int startPositionHome, stopPositionHome;
    private String date;
    static int finishedColor;
    ArrayList<Dialysis> dialysisReminder = new ArrayList<>();
    private AdView mAdView;
    Globalpreferences gpreferences;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.homepage, container, false);
        initialize();
        checkWaterIntake();
        checkDialysis();


        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        final Animation animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);


        final ImageView imageView = (ImageView) rootView.findViewById(R.id.emergency);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!(getActivity().checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) && !(getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) && !(getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(new String[]{android.Manifest.permission.SEND_SMS, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.HOMEPERMISSION);
                    } else {
                        showOption(imageView, animShake);
                    }
                } else {
                    showOption(imageView, animShake);
                }


            }
        });
        return rootView;
    }

    private void showOption(final ImageView imageView, Animation animShake) {
        if (!TextUtils.isEmpty(gpreferences.getString("EmergencyNumber"))) {
            imageView.startAnimation(animShake);
            Handler han = new Handler();
            han.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imageView.clearAnimation();
                }
            }, 3000);

            if(gpreferences.getString("EmergencyType").equals("1")){
                sendSMS();
                makeCall();
            }else if(gpreferences.getString("EmergencyType").equals("2")){
                sendSMS();

            }else{
                makeCall();

            }


        } else {
            Intent inte = new Intent(getActivity(), Callhelp.class);
            startActivity(inte);
        }
    }

    private void makeCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + gpreferences.getString("EmergencyNumber")));
        startActivity(intent);
    }

    private void sendSMS(){
        String message = gpreferences.getString("EmergencyName")+"!!! "+gpreferences.getString("EmergencyMessage")+". Click this link: http://maps.google.com/?q="+currentLatitude+","+currentLongitude;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(gpreferences.getString("EmergencyNumber"), null, message, null, null);
    }

    private void checkDialysis() {
        dialysisReminder.clear();
        dialysisReminder = dbHelper.getDialysis();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        if(dialysisReminder.isEmpty()){
            dialysisRemind.setText("you haven't set your dialysis reminder. set the reminder to get help!");
            addDialysisTv.setText("+ Add Now");
        }else{
            addDialysisTv.setText("+ Edit");
        }
        outerloop:
        for (int t = 0; t < dialysisReminder.size(); t++) {
            if (currentDay == Integer.valueOf(dialysisReminder.get(t).getDay())) {
                dialysisRemind.setText("your dialysis is Today");
                break outerloop;
            } else {
                if (currentDay < Integer.valueOf(dialysisReminder.get(t).getDay())) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK, Integer.valueOf(dialysisReminder.get(t).getDay()));
                    dialysisRemind.setText("your next dialysis is on " + sdf.format(cal.getTime()));
                    break outerloop;
                } else {
                    Calendar cal = Calendar.getInstance();
                    int i = cal.get(Calendar.WEEK_OF_MONTH);
                    cal.set(Calendar.WEEK_OF_MONTH, ++i);
                    cal.set(Calendar.DAY_OF_WEEK, Integer.valueOf(dialysisReminder.get(t).getDay()));
                    dialysisRemind.setText("your next dialysis is on " + sdf.format(cal.getTime()));
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
        gpreferences = Globalpreferences.getInstances(getActivity());
        dialysisRemind = (TextView) rootView.findViewById(R.id.remindDialysis);
        welcomeTv = (TextView) rootView.findViewById(R.id.welcomeTv);
        addDialysisTv = (TextView) rootView.findViewById(R.id.addDialysisTv);
        circleProgress = (CircleProgress) rootView.findViewById(R.id.circular_progress);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fabWaterPlus);
        dbHelper = new DatabaseHelper(getActivity());
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        calendar = Calendar.getInstance();
        date = sdf.format(calendar.getTime());

        Bundle extras = new Bundle();
        extras.putBoolean("is_designed_for_families", true);

        mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
       //  .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        welcomeTv.setText("Welcome Back, "+gpreferences.getString("username"));
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

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.PHOTOPERMISSION: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Some Permission is Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

       //     Toast.makeText(getActivity(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

}
