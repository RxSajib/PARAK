package com.rakpak.pak_parak.ImageFullScreen;

import android.app.Dialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.textview.MaterialTextView;
import com.rakpak.pak_parak.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ImageFullScreen extends Fragment {

    private String uri, time;
    private PhotoView photoView;
    private MaterialTextView timetext;


    public ImageFullScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.image_full_screen, container, false);
  //      getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        /// todo internet connection dioloag
        ConnectivityManager cm =(ConnectivityManager)getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);

            dialog.setContentView(R.layout.no_connection_dioloag);
            dialog.show();


            RelativeLayout button = dialog.findViewById(R.id.RetryButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(getActivity().WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                    dialog.dismiss();
                }
            });

            RelativeLayout cancelbutton = dialog.findViewById(R.id.CaneclButtonID);

            cancelbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });

        }


        /// todo internet connection dialoag






        photoView = view.findViewById(R.id.PhotoViewID);
        Bundle bundle = getArguments();
        uri = bundle.getString("URI");
        time = bundle.getString("TIME");
        timetext = view.findViewById(R.id.ImageTimeDetails);
        timetext.setText(time);

        /*if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
            getActivity().setco;
        }
        else {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }*/

        if(uri != null){

            Picasso.with(getContext()).load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(photoView, new Callback() {
                @Override
                public void onSuccess() {
                    Picasso.with(getContext()).load(uri).into(photoView);
                }

                @Override
                public void onError() {

                }
            });


        }

        return view;
    }
}