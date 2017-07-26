package arun.com.medfriendly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by arun_i on 25-Jul-17.
 */

public class Settings extends Fragment implements View.OnClickListener {
    View rootView;
    CardView callhelp_ll, profile_ll;
    TextView version_txt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.settings, container, false);
        initialize();
        return rootView;
    }

    private void initialize() {
        callhelp_ll = (CardView) rootView.findViewById(R.id.callhelp_ll);
        callhelp_ll.setOnClickListener(this);
        profile_ll = (CardView) rootView.findViewById(R.id.profile_ll);
        profile_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.callhelp_ll:
                Intent intent = new Intent(getActivity(),Callhelp.class);
                startActivity(intent);
                break;
            case R.id.profile_ll:
                Intent inten = new Intent(getActivity(),Profile.class);
                startActivity(inten);
                break;
        }
    }
}
