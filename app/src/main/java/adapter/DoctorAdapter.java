package adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import arun.com.medfriendly.R;
import model.Dialysis;

/**
 * Created by arun on 04/08/17.
 */

public class DoctorAdapter extends BaseAdapter {
    List<HashMap<String,String>> near;
    Context con;

    public DoctorAdapter(Context activity, List<HashMap<String, String>> nearbyPlacesList_public) {
        this.near = nearbyPlacesList_public;
        this.con = activity;
        System.out.println("adapter::::::"+near.size());
    }

    @Override
    public int getCount() {
        return near.size();
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
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(con.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.dialysisinflate, null);
            holder.day = (TextView) convertView.findViewById(R.id.diaDayInf);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

       holder.day.setText(near.get(position).get("place_name"));
        return convertView;
    }


    public class ViewHolder  {
        public TextView day;
    }
}
