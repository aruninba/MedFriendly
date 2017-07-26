package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import arun.com.medfriendly.R;

/**
 * Created by arun on 28/8/16.
 */
public class Custommeds extends BaseAdapter {
    Context context;
    ArrayList<String> medname;
    ArrayList<String> medTimes;
    ArrayList<String> medReminder;
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    private TextDrawable mDrawableBuilder;

    public Custommeds(Context context, ArrayList<String> medname, ArrayList<String> medTimes, ArrayList<String> medReminder) {
        this.context = context;
        this.medname = medname;
        this.medTimes = medTimes;
        this.medReminder = medReminder;

    }

    @Override
    public int getCount() {
        return medname.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Listholder holer;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            holer = new Listholder();
            view = inflater.inflate(R.layout.medscustom, null);
            holer.medName = (TextView) view.findViewById(R.id.medname);
            holer.medTimes = (TextView) view.findViewById(R.id.medfrequency);
            holer.thumbNail = (ImageView) view.findViewById(R.id.thumbnail_image);
            holer.reminderNotify = (ImageView) view.findViewById(R.id.reminder_image);
            view.setTag(holer);
        } else {
            holer = (Listholder) view.getTag();
        }
        holer.medName.setText(medname.get(i));

        holer.medTimes.setText("Time: " + getTimes(medTimes.get(i)));
        setReminderTitle(medname.get(i), holer.thumbNail);
        if (medReminder.get(i).equals("yes")) {
            holer.reminderNotify.setImageResource(R.drawable.alarm);
        } else if (medReminder.get(i).equals("no")) {
            holer.reminderNotify.setImageResource(R.drawable.disable_alarm);
        }

        return view;
    }

    private String getTimes(String s) {
        String time = "";
        try {
            JSONObject json = new JSONObject(s);
            JSONArray jsonarray = json.optJSONArray("timeArrays");
            for (int t = 0; t < jsonarray.length(); t++) {
                if (t == 0) {
                    time = jsonarray.getString(t);
                } else {
                    time += "," + jsonarray.getString(t);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return time;
    }

    private class Listholder {
        TextView medName, medTimes;
        ImageView thumbNail, reminderNotify;
    }

    public void setReminderTitle(String title, ImageView thumbNail) {
        String letter = "A";

        if (title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }

        int color = mColorGenerator.getRandomColor();

        // Create a circular icon consisting of  a random background colour and first letter of title
        mDrawableBuilder = TextDrawable.builder()
                .buildRound(letter, color);
        thumbNail.setImageDrawable(mDrawableBuilder);
    }
}
