package com.rakpak.pak_parak.AddJobs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakpak.pak_parak.Common.Common;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.Model.Myresponse;
import com.rakpak.pak_parak.Model.Notifaction;
import com.rakpak.pak_parak.Model.Sender;
import com.rakpak.pak_parak.Model.Token;
import com.rakpak.pak_parak.R;
import com.rakpak.pak_parak.Remote.APIServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddjobsPage extends Fragment {

    private EditText jobtitle, companyname, minsalary, maxsalary, jobdescptrion, contactphone, contactemail;
    private RelativeLayout submitjobbutton;
    private DatabaseReference Mjobs_data, Muserdatabase;
    private String Current_Time, Current_Date;
    private RelativeLayout working_time;
    private MaterialTextView addtime;
    private int childcount, childnegative;
    private RelativeLayout back_icon;
    private ProgressDialog Mprogess;
    private APIServices mservices;
    private FirebaseAuth Mauth;
    private String CurrentuserID;

    public AddjobsPage() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.addjobs_page, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);




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
        CurrentuserID = Mauth.getCurrentUser().getUid();

        mservices = Common.getFCMClient();
        Mprogess = new ProgressDialog(getActivity());
        back_icon = view.findViewById(R.id.back_icon);
        back_icon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    back_icon.setBackgroundResource(R.drawable.click_back_button_animaction);
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    back_icon.setBackgroundResource(R.drawable.click_backbutton_up_animaction);
                }

                return true;
            }
        });

        addtime = view.findViewById(R.id.AddJobsTime);
        working_time = view.findViewById(R.id.WorkingTime);
        Mjobs_data = FirebaseDatabase.getInstance().getReference().child("Jobs");
        Mjobs_data.keepSynced(true);
        jobtitle = view.findViewById(R.id.JobtitleID);
        companyname = view.findViewById(R.id.CompanynameID);
        minsalary = view.findViewById(R.id.SalaryMinimumID);
        maxsalary = view.findViewById(R.id.SalaryOFmaximum);
        jobdescptrion = view.findViewById(R.id.JobDescptrionID);
        contactphone = view.findViewById(R.id.ContactNumberID);
        contactemail = view.findViewById(R.id.ContactEmailID);
        submitjobbutton = view.findViewById(R.id.JobsButtonID);


        Mjobs_data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    childcount = (int)dataSnapshot.getChildrenCount();
                     childnegative = (~(childcount - 1));

                }
                else {
                    childnegative = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        working_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(getActivity());

                CharSequence iteams[] = new CharSequence[]{
                        "1-2",
                        "3-6",
                        "7-12",
                        "13-17",
                        "18-25"
                };

                Mbuilder.setItems(iteams, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       if(i == 0){
                        addtime.setText("1-2");

                       }
                       if(i == 1){
                           addtime.setText("3-6");
                       }
                       if(i == 2){
                           addtime.setText("7-12");
                       }
                       if(i == 3){
                           addtime.setText("13-17");
                       }
                       if(i == 4){
                           addtime.setText("18-25");
                       }
                    }
                });

                AlertDialog alertDialog = Mbuilder.create();
                alertDialog.show();
            }
        });

        submitjobbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jobtitletext = jobtitle.getText().toString().trim().toUpperCase();
                String companynametext = companyname.getText().toString().trim();
                String minsalarytext = minsalary.getText().toString().trim();
                String maxsalarytext = maxsalary.getText().toString().trim();
                String jobdescptriontext = jobdescptrion.getText().toString().trim();
                String contactnumbertext = contactphone.getText().toString().trim();
                String contctemailtext = contactemail.getText().toString().trim();
                String work_time = addtime.getText().toString();

                if(jobtitletext.isEmpty()){
                    Toast.makeText(getActivity(), "job title require", Toast.LENGTH_SHORT).show();
                }
                else if(companynametext.isEmpty()){
                    Toast.makeText(getActivity(), "company name require", Toast.LENGTH_SHORT).show();
                }
                else if(minsalarytext.isEmpty()){
                    Toast.makeText(getActivity(), "Minimum salary require", Toast.LENGTH_SHORT).show();
                }
                else if(maxsalarytext.isEmpty()){
                    Toast.makeText(getActivity(), "Maximum salary require", Toast.LENGTH_SHORT).show();
                }
                else if(jobdescptriontext.isEmpty()){
                    Toast.makeText(getActivity(), "job description require", Toast.LENGTH_SHORT).show();
                }
                else if(contactnumbertext.isEmpty()){
                    Toast.makeText(getActivity(), "contact number require", Toast.LENGTH_SHORT).show();
                }
                else if(contctemailtext.isEmpty()){
                    Toast.makeText(getActivity(), "contact email require", Toast.LENGTH_SHORT).show();
                }
                else if(work_time.equals("Add your working time")){
                    Toast.makeText(getActivity(), "Add your work time", Toast.LENGTH_LONG).show();
                }
                else {

                    final MaterialAlertDialogBuilder Mbulder = new MaterialAlertDialogBuilder(getActivity());
                    View Mview = LayoutInflater.from(getActivity()).inflate(R.layout.loading_layout, null, false);
                    Mbulder.setView(Mview);
                    Mbulder.setCancelable(false);
                    final AlertDialog alertDialog = Mbulder.create();
                    alertDialog.show();
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(alertDialog.getWindow().getAttributes());
                    lp.width = 700;
                    lp.height = 1000;
                    alertDialog.getWindow().setAttributes(lp);

                    /// todo current time
                    Calendar calendar_time = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
                    Current_Time = simpleDateFormat_time.format(calendar_time.getTime());
                    /// todo current time

                    /// todo current date
                    Calendar calendar_date = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
                    Current_Date = simpleDateFormat_date.format(calendar_date.getTime());
                    /// todo current date

                    Map<String, Object> jobmap = new HashMap<String, Object>();
                    jobmap.put("jobtitle", jobtitletext);
                    jobmap.put("company_name", companynametext);
                    jobmap.put("min_salary", minsalarytext);
                    jobmap.put("max_salary", maxsalarytext);
                    jobmap.put("job_descptrion", jobdescptriontext);
                    jobmap.put("contact_number", contactnumbertext);
                    jobmap.put("contact_email", contctemailtext);
                    jobmap.put("Current_Time", Current_Time);
                    jobmap.put("Current_Date", Current_Date);
                    jobmap.put("work_time", work_time);
                    jobmap.put("short", childnegative);

                    Mjobs_data.push().updateChildren(jobmap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        find_user_sendimage_notifaction();

                                        alertDialog.dismiss();
                                        Toast.makeText(getActivity(), "Jobs post success", Toast.LENGTH_SHORT).show();
                                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    }
                                    else {
                                        alertDialog.dismiss();
                                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    alertDialog.dismiss();
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });


        onlinecheack("online");

        return view;
    }



    /// todo find user send job notifaction
    private void find_user_sendimage_notifaction (){
        FirebaseDatabase.getInstance().getReference().child(DataManager.NotifactionUserRoot).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild(DataManager.UserFullname)){
                                String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                if(!name.isEmpty()){
                                    send_image_notfaction(name);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    /// todo find user send job notifaction

    // todo find user and send job
    private void send_image_notfaction(final String username){
        FirebaseDatabase.getInstance().getReference().child("Token")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            for(DataSnapshot ds : dataSnapshot.getChildren()){



                                Token token = ds.getValue(Token.class);
                                String sendermessage = username;
                           //     String title = messagetext;

                                Notifaction notifaction = new Notifaction("New Job is found", sendermessage);
                                Sender sender  = new Sender(token.getToken(), notifaction);

                                mservices.sendNotification(sender).enqueue(new Callback<Myresponse>() {
                                    @Override
                                    public void onResponse(Call<Myresponse> call, Response<Myresponse> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<Myresponse> call, Throwable t) {
                                        Log.i("ERROR", "MESSAGE");

                                    }
                                });
                            }



                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });






    }
    // todo find user and send job''




    private void onlinecheack(String online){

        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
       String CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());


        Calendar calendar_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
       String CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());


        Map<String, Object> onlinemap = new HashMap<String, Object>();
        onlinemap.put(DataManager.UserCardActive, online);
        onlinemap.put(DataManager.UserActiveTime, CurrentTime);
        onlinemap.put(DataManager.UserActiveDate, CurrentDate);

        Muserdatabase.child(CurrentuserID).child(DataManager.UserOnlineRoot).updateChildren(onlinemap)
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