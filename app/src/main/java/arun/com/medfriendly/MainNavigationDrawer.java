package arun.com.medfriendly;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import utilities.Globalpreferences;


/**
 * Created by Arun on 19-Jul-16.
 */
public class MainNavigationDrawer extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    private Toolbar a_toolbar;
    private FragmentDrawer a_fragmentDrawer;
    String savedtitle;
    boolean doubleBackToExitPressedOnce = false;
    Globalpreferences globalpreferences;
    public static MainNavigationDrawer mainNavigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_naviagtiondrawer);
        a_toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(a_toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mainNavigationDrawer = this;
        a_fragmentDrawer = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        a_fragmentDrawer.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), a_toolbar);
        a_fragmentDrawer.setDrawerListener(this);
        globalpreferences = Globalpreferences.getInstances(MainNavigationDrawer.this);
        displayview(0);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        System.out.println("posi" + position);
        displayview(position);
    }

    private void displayview(int position) {
        Fragment fragment = null;
        String title = "";
        Bundle bundle = new Bundle();

        switch (position) {

            case 0:

                fragment = new HomeFragment();
                title = "Home";
                globalpreferences.putString("selectedClass", "Home");

                break;

            case 1:

                fragment = new MymedsFragment();
                title = "My Meds";
                globalpreferences.putString("selectedClass", "Reminder");
                break;
            case 2:

                fragment = new WaterFragment();
                title = "Water Intake Monitor";
                globalpreferences.putString("selectedClass", "Water intake");
                break;
            case 3:
                fragment = new UFcalculator();
                title = "UF Calculator";
                globalpreferences.putString("selectedClass", "UF calculator");
                break;

            case 4:
                fragment = new DialysisReminder();
                globalpreferences.putString("selectedClass", "Dialysis");
                title = "Dialysis Reminder";

                break;

            /*case 5:
                fragment = new DoctorsFragment();
                globalpreferences.putString("selectedClass", "Doctors");
                title = "Doctors";

                break;*/

            case 6:
                fragment = new HospitalsFragment();
                globalpreferences.putString("selectedClass", "Hospitals");
                title = "Hospitals";

                break;
            /*case 6:
                fragment = new UFcalculator();
                globalpreferences.putString("selectedClass", "Treatment Monitor");
                title = "Treatment Monitor";

                break;*/
            case 7:
                fragment = new Settings();
                globalpreferences.putString("selectedClass", "Settings");
                title = "Settings";
                break;


            default:
                break;

        }

        if (fragment != null) {

            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(title);
        }
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public String currentFragment() {
        String frag = "";
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container_body);
        if (f instanceof HomeFragment) {
            frag = "HomeFragment";
        } else if (f instanceof WaterFragment) {
            frag = "WaterFragment";
        }
        return frag;
    }


/*
    public void switchFragments(){
        Fragment fragment = new WaterFragment();
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(title);
    }
*/


}
