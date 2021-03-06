package com.rakpak.pak_parak.NavagationPage;

import android.app.Dialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.ProjectPages.CharityPage;
import com.rakpak.pak_parak.ProjectPages.JobsPage;
import com.rakpak.pak_parak.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProjectPage extends Fragment {


    private MaterialCardView charity, jobs, Quarn;
    private RelativeLayout backbutton;
    private DatabaseReference Muserdatabase;
    private String CurrentUserID;
    private FirebaseAuth Mauth;
    private DatabaseReference OnLineData;

    private AdView adView;

    public ProjectPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.project_page, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        adView = view.findViewById(R.id.MyprojectAds);
        MobileAds.initialize(getContext(), DataManager.BannerAdsID);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        OnLineData = FirebaseDatabase.getInstance().getReference().child(DataManager.UserOnlineRoot);
        OnLineData.keepSynced(true);


        /// todo internet connection dioloag
        ConnectivityManager cm =(ConnectivityManager)getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {

            MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(getActivity());
            View viewinternet = LayoutInflater.from(getActivity()).inflate(R.layout.no_connection_message, null, false);




            MaterialButton exitbutton = viewinternet.findViewById(R.id.ExitButton);
            exitbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });

            Mbuilder.setView(viewinternet);
            AlertDialog alertDialog = Mbuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertDialog.show();

        }


        /// todo internet connection dialoag




        Muserdatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        charity = view.findViewById(R.id.CharityButtonID);
        jobs = view.findViewById(R.id.JobsButtonIDs);
        Quarn = view.findViewById(R.id.QuranButtoID);

        backbutton = view.findViewById(R.id.BackButton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        charity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goto_charitypage(new CharityPage());
            }
        });

        jobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goto_jobspage(new JobsPage());
            }
        });

        Quarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        onlinecheack("online");
        return view;
    }

    private void goto_charitypage(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }
    }

    private void goto_jobspage(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }
    }


    private void onlinecheack(String online){

        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("hh:mm a");
        String CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());


        Calendar calendar_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("yyyy-MM-dd");
        String CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());


        Map<String, Object> onlinemap = new HashMap<String, Object>();
        onlinemap.put(DataManager.UserCardActive, online);
        onlinemap.put(DataManager.UserActiveTime, CurrentTime);
        onlinemap.put(DataManager.UserActiveDate, CurrentDate);

        OnLineData.child(CurrentUserID).child(DataManager.UserOnlineRoot).updateChildren(onlinemap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                        }
                        else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onStop() {
        onlinecheack("offline");
        super.onStop();
    }

    @Override
    public void onResume() {
        onlinecheack("online");
        super.onResume();
    }
}