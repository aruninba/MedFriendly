package arun.com.medfriendly;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import adapter.Custommeds;
import database.DatabaseHelper;

/**
 * Created by Arun on 22-Aug-16.
 */
public class MymedsFragment extends Fragment {
    FloatingActionButton add_mymeds;
    ListView meds;
    View rootview;
    DatabaseHelper dbhelper;
    ArrayList<HashMap<String,String>> mymed_ar = new ArrayList<>();
    ArrayList<String> med_name = new ArrayList<>();
    ArrayList<String> med_Time = new ArrayList<>();
    ArrayList<String> med_Reminder = new ArrayList<>();
    Custommeds custommeds;
    private CoordinatorLayout coordinatorLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootview = inflater.inflate(R.layout.mymeds,container,false);
        add_mymeds = (FloatingActionButton) rootview.findViewById(R.id.mymeds_addbtn);
        meds =(ListView) rootview.findViewById(R.id.mymeds_list);
        coordinatorLayout = (CoordinatorLayout) rootview.findViewById(R.id.coordinatorallMeds);
        updateAdapter();


        add_mymeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent(getActivity(), Addmymeds.class);
                startActivityForResult(inte,1);

            }
        });

        meds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = mymed_ar.get(i).get("Id");
                String medname = mymed_ar.get(i).get("Med_name");
                String reminder = mymed_ar.get(i).get("Reminder");
                String frequency = mymed_ar.get(i).get("Frequency");
                String time = mymed_ar.get(i).get("Time");
                String dosage = mymed_ar.get(i).get("Dosage");
                String scheduleDate = mymed_ar.get(i).get("ScheduleDate");
                String reminderdays = mymed_ar.get(i).get("Enddate");
                String days = mymed_ar.get(i).get("Days");
                System.out.println("days"+days);

                Bundle bun =new Bundle();
                bun.putString("id",id);
                bun.putString("medname",medname);
                bun.putString("reminder",reminder);
                bun.putString("frequency",frequency);
                bun.putString("time",time);
                bun.putString("dosage",dosage);
                bun.putString("scheduleDate",scheduleDate);
                bun.putString("reminderdays",reminderdays);
                bun.putString("days",days);

                Intent inte = new Intent(getActivity(), Editmymeds.class);
                inte.putExtras(bun);
                startActivityForResult(inte,1);

            }
        });

        return  rootview;
    }

    private void updateAdapter() {
        dbhelper = new DatabaseHelper(getActivity());
        mymed_ar.clear();
        med_name.clear();
        med_Time.clear();
        med_Reminder.clear();
        mymed_ar = dbhelper.getmedicines();

        for(int i=0;i<mymed_ar.size();i++){
            med_name.add(mymed_ar.get(i).get("Med_name"));
            med_Time.add(mymed_ar.get(i).get("Time"));
            med_Reminder.add(mymed_ar.get(i).get("Reminder"));
            System.out.println("name"+mymed_ar.get(i).get("Med_name"));
        }

        custommeds = new Custommeds(getActivity(),med_name,med_Time,med_Reminder);
        meds.setAdapter(custommeds);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 1){
            Bundle extras = data.getExtras();
            if(extras != null){
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "New Medicine has been added.!", Snackbar.LENGTH_SHORT);
                snackbar.show();
                updateAdapter();
            }
        }else if(requestCode == 1 && resultCode == 2){
            Bundle extras = data.getExtras();
            if(extras != null){
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Medicine has been deleted.!", Snackbar.LENGTH_SHORT);
                snackbar.show();
                updateAdapter();
            }
        }
    }
}
