package adapter;

/**
 * Created by Ravi on 29/07/15.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import arun.com.medfriendly.R;
import model.NavDrawerItem;
import utilities.Globalpreferences;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    ArrayList<Integer> images=new ArrayList<>();
    Context context;
    Globalpreferences globalpreferences;
    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data, ArrayList<Integer> images) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.images=images;
        this.context = context;
        globalpreferences = Globalpreferences.getInstances(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawerlist_customview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.image.setImageResource(images.get(position));
        if(current.getTitle().equalsIgnoreCase(globalpreferences.getString("selectedClass"))){
            holder.title.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
        }else{
            holder.title.setTextColor(ContextCompat.getColor(context,R.color.black));

        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            image= (ImageView) itemView.findViewById(R.id.image_nav);
        }
    }
}
