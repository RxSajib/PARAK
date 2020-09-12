package com.rakpak.pak_parak.ImageFullScreen;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

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