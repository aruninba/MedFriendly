package adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import arun.com.medfriendly.R;
import model.Dialysis;
import utilities.Constants;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by arun_i on 18-Jul-17.
 */

public class DialysisAdapter extends BaseAdapter {
    Context context;
    ArrayList<Dialysis> dialysises;

    public DialysisAdapter(Context context, ArrayList<Dialysis> dialysises) {
        this.context = context;
        this.dialysises = dialysises;
    }


    @Override
    public int getCount() {
        return dialysises.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.dialysisinflate, null);
            holder.day = (TextView) convertView.findViewById(R.id.diaDayInf);
            holder.hospital = (TextView) convertView.findViewById(R.id.diaPlaceInf);
            holder.map = (ImageView) convertView.findViewById(R.id.mapIcon);
            holder.phone = (ImageView) convertView.findViewById(R.id.phoneIcon);
            holder.coordinatorLayoutinf = (CoordinatorLayout) convertView.findViewById(R.id.coordinatedialysisinflate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Dialysis dialysis = dialysises.get(position);
        //get day name of the week
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.getDefault());
        String weekdays[] = dfs.getWeekdays();
        String nameOfDay = weekdays[Integer.valueOf(dialysis.getDay())];

        holder.day.setText(nameOfDay+ " @ " + dialysis.getTime());
        holder.hospital.setText(dialysis.getHospital());

        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coordinates = dialysis.getHospitalCoordinates();
                System.out.println("coor" + coordinates);
                String[] coordina = coordinates.split(",");
                if (coordinates.equalsIgnoreCase("NA")) {
                    String location = dialysis.getHospitalLocation() + "," + dialysis.getHospitalState() + "," + dialysis.getHospitalDistrict() + "," + dialysis.getHospitalPincode();
                    System.out.println("loca" + location);
                    Geocoder geocoder = new Geocoder(context);
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses.size() > 0) {
                        double latitude = addresses.get(0).getLatitude();
                        double longitude = addresses.get(0).getLongitude();
                        System.out.println("lati" + latitude + "longi" + longitude);
                        launchMap(latitude, longitude);
                    }else{
                        showSnack(holder.coordinatorLayoutinf,"Sorry, location not available!");
                    }
                } else {
                    launchMap(Double.valueOf(coordina[0]), Double.valueOf(coordina[1]));
                }
            }
        });

        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dialysis.getHospitalPhone().equalsIgnoreCase("NA")){
                    showSnack(holder.coordinatorLayoutinf,"Sorry, phone number not available!");
                }else {

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    if(dialysis.getHospitalPhone().contains(",")){
                        String[] phone = dialysis.getHospitalPhone().split(",");
                        intent.setData(Uri.parse("tel:" + phone[0]));
                    }else{
                        intent.setData(Uri.parse("tel:" +dialysis.getHospitalPhone()));
                    }
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        showSnack(holder.coordinatorLayoutinf,"Please enable permission in settings to call!");
                        return;
                    }
                    context.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    private void showSnack(CoordinatorLayout coordinatorLayoutinf, String msg){
        Snackbar sn = Snackbar.make(coordinatorLayoutinf,msg,Snackbar.LENGTH_SHORT);
        sn.show();
    }

    private void launchMap(double latitude, double longitude) {
     /*   Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=13.0244,80.1774&daddr="+latitude+","+longitude));
        context.startActivity(intent);*/
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" +latitude+","+longitude));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public class ViewHolder  {
        public TextView day,  hospital;
        public ImageView map,phone;
       public CoordinatorLayout coordinatorLayoutinf;
    }

}
