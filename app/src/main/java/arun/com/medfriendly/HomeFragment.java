package arun.com.medfriendly;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class HomeFragment extends Fragment {

    View rootView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.homepage, container, false);
       // AdView adview = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("0FA20932AFE1095798444FD9AAB7D425")
                .build();
        MobileAds.initialize(getActivity(), "ca-app-pub-5196489113179685~8798505821");

        return  rootView;
    }


}
