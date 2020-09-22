package com.rakpak.pak_parak.InternetDioloag;

import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.rakpak.pak_parak.R;


public class NoConnectionDioloadPage extends Fragment {


    private RelativeLayout cancelbutton, retry;

    public NoConnectionDioloadPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.no_connection_dioload_page, container, false);

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }
        else {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        }

        cancelbutton = view.findViewById(R.id.CaneclButtonID);
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        retry = view.findViewById(R.id.RetryButton);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(getActivity().WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);

            }
        });

        return view;
    }
}