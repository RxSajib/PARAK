package com.rakpak.pak_parak.NavagationPage;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JOBApplication_Details extends Fragment {

    private MaterialTextView name, email, number, status, acivement, goals, job;
    private String TapUID;
    private DatabaseReference ApplicationRoot;
    private MaterialButton downloadpdfbutton;
    private CheckBox checkBox;
    private String uri;
    private RelativeLayout backbutton;

    private ProgressDialog Mprogress;
    private DatabaseReference Muserdatabase;
    private String CurrentUserID;
    private FirebaseAuth Mauth;
    private DatabaseReference OnlineData;

    public JOBApplication_Details() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout._j_o_b_application__details, container, false);

        OnlineData = FirebaseDatabase.getInstance().getReference().child(DataManager.UserOnlineRoot);
        OnlineData.keepSynced(true);


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



        Muserdatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        Mprogress = new ProgressDialog(getActivity());

        backbutton = view.findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        checkBox = view.findViewById(R.id.CheckBox);
        downloadpdfbutton = view.findViewById(R.id.DownloadPDFButtonID);
        ApplicationRoot = FirebaseDatabase.getInstance().getReference().child(DataManager.JobApplicationRoot);
        ApplicationRoot.keepSynced(true);
        Bundle bundle = getArguments();
        TapUID = bundle.getString("UID");

        name = view.findViewById(R.id.ApplicantName);
        email = view.findViewById(R.id.ApplicantEmail);
        number = view.findViewById(R.id.ApplicantNumber);
        status = view.findViewById(R.id.ApplicantStatus);
        acivement = view.findViewById(R.id.Acivement);
        goals = view.findViewById(R.id.Goals);
        job = view.findViewById(R.id.ApplicantJobs);


        ApplicationRoot.child(TapUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild(DataManager.JobApplicantFirstName) || dataSnapshot.hasChild(DataManager.JobApplicantLastName)){
                                String firstname = dataSnapshot.child(DataManager.JobApplicantFirstName).getValue().toString();
                                String lastname = dataSnapshot.child(DataManager.JobApplicantLastName).getValue().toString();

                                name.setText(firstname + " "+lastname);
                            }
                            if(dataSnapshot.hasChild(DataManager.JobApplicantEmail)){
                                String emailtext = dataSnapshot.child(DataManager.JobApplicantEmail).getValue().toString();
                                email.setText(emailtext);
                            }
                            if(dataSnapshot.hasChild(DataManager.JobApplicantWhatsAppNumber)){
                                String whatsappnumber = dataSnapshot.child(DataManager.JobApplicantWhatsAppNumber).getValue().toString();
                                number.setText(whatsappnumber);
                            }
                            if(dataSnapshot.hasChild(DataManager.JobApplicantAchievement)){
                                String acivementtext = dataSnapshot.child(DataManager.JobApplicantAchievement).getValue().toString();
                                acivement.setText(acivementtext);
                            }
                            if(dataSnapshot.hasChild(DataManager.JobName)){
                                String myjob = dataSnapshot.child(DataManager.JobName).getValue().toString();
                                job.setText(myjob);
                            }
                            if(dataSnapshot.hasChild(DataManager.JobApplicantGoals)){
                                String goealstext = dataSnapshot.child(DataManager.JobApplicantGoals).getValue().toString();
                                goals.setText(goealstext);
                            }
                            if(dataSnapshot.hasChild(DataManager.JobApplicantEmploymentStatus)){
                                String statustext = dataSnapshot.child(DataManager.JobApplicantEmploymentStatus).getValue().toString();
                                status.setText(statustext);
                            }
                            if(dataSnapshot.hasChild(DataManager.JobApplicationFile)){
                                uri = dataSnapshot.child(DataManager.JobApplicationFile).getValue().toString();

                                checkBox.setVisibility(View.VISIBLE);
                                downloadpdfbutton.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked()){
                    downloadpdfbutton.setBackgroundColor(getResources().getColor(R.color.pdf_button_active));
                    downloadpdfbutton.setEnabled(true);

                    if(!uri.isEmpty()){
                        downloadpdfbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(myIntent);
                            }
                        });
                    }
                }
                else {
                    downloadpdfbutton.setBackgroundColor(getResources().getColor(R.color.pdf_button_inactive));
                    downloadpdfbutton.setEnabled(false);
                }
            }
        });

        onlinecheack("online");

        return view;
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

        OnlineData.child(CurrentUserID).child(DataManager.UserOnlineRoot).updateChildren(onlinemap)
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