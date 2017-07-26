package arun.com.medfriendly;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import adapter.DialysisAdapter;
import database.DatabaseHelper;
import model.Dialysis;
import model.Hospital;
import utilities.Constants;
import utilities.RecyclerItemClickListener;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by arun_i on 17-Jul-17.
 */

public class DialysisReminder extends Fragment {
    private View rootView;
    private ListView listview;
    private FloatingActionButton floatingAB;
    private CoordinatorLayout coordinatorLayout;
    DialysisAdapter dialysisAdapter;
    DatabaseHelper dbhelper;
    ArrayList<Dialysis> dialysisReminder = new ArrayList<>();
    public static ArrayList<Hospital> hospitalAr = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialysisreminder, container, false);
        initializeView();
        updateAdapter();
        readHospitalCSV();
        return rootView;
    }


    private void initializeView() {
        listview = (ListView) rootView.findViewById(R.id.recyclerDialysis);
        floatingAB = (FloatingActionButton) rootView.findViewById(R.id.floatdialysis);
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinateDialysis);
        dbhelper = new DatabaseHelper(getActivity());

        floatingAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent(getActivity(), AddDialysis.class);
                startActivityForResult(inte, 1);
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dialysis dialysis = dialysisReminder.get(position);
                Intent inte = new Intent(getActivity(), EditDialysis.class);
                inte.putExtra("Dialysis", dialysis);
                startActivityForResult(inte, 2);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(checkSelfPermission(getActivity(),Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) ) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constants.PERMISSION_REQUEST_CODE);
            }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "New Reminder has been added.!", Snackbar.LENGTH_SHORT);
                snackbar.show();
                updateAdapter();
            }
        } else if (requestCode == 2 && resultCode == 2) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, extras.getString("MESSAGE", ""), Snackbar.LENGTH_SHORT);
                snackbar.show();
                updateAdapter();
            }
        }
    }

    private void updateAdapter() {
        dialysisReminder.clear();
        dialysisReminder = dbhelper.getDialysis();
        dialysisAdapter = new DialysisAdapter(getActivity(), dialysisReminder);
        listview.setAdapter(dialysisAdapter);
    }

    private void readHospitalCSV() {
        try {
            JSONArray hospArray = new JSONArray(loadJSONFromAsset());
            for (int t = 0; t < hospArray.length(); t++) {
                JSONObject hospObject = hospArray.getJSONObject(t);
                Hospital hospital = new Hospital();
                hospital.setId(hospObject.getString("Sr_No"));
                hospital.setCoordinates(hospObject.getString("Location_Coordinates"));
                hospital.setLocation(hospObject.getString("Location"));
                hospital.setHospitalName(hospObject.getString("Hospital_Name"));
                hospital.setState(hospObject.getString("State"));
                hospital.setDistrict(hospObject.getString("District"));
                hospital.setPincode(hospObject.getString("Pincode"));
                hospital.setTelephone(hospObject.getString("Telephone"));
                hospitalAr.add(hospital);
            }
            System.out.println("hospitalar" + hospitalAr.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("hospital.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case Constants.PERMISSION_REQUEST_CODE:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                       ) {
                    // All Permissions Granted
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

}
