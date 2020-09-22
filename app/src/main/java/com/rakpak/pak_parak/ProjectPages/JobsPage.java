package com.rakpak.pak_parak.ProjectPages;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

import static android.app.Activity.RESULT_OK;

public class JobsPage extends Fragment {

    private RelativeLayout submitbutton;
    private EditText name, lastname, email, whatsapp, acivement, goeal, job;
    private MaterialButton pdfbutton;
    private String CurrentTime, CurrentDate;
    private static final int PDFREQUEST = 100;
    private StorageReference JobPdf;
    private String Pdfdownloaduri ;
    private DatabaseReference MPdfDatabase;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String EmploymentStatus = "";
    private ProgressDialog Mprogress;
    private int childcount, childnegative;
    private APIServices mservices;
    private RelativeLayout backbutton;

    private DatabaseReference Muserdatabase;
    private String CurrentUserID;
    private FirebaseAuth Mauth;
    private DatabaseReference OnlineData;

    public JobsPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.jobs_page, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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
            alertDialog.show();

        }


        /// todo internet connection dialoag








        Muserdatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        backbutton = view.findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        mservices = Common.getFCMClient();



        Mprogress = new ProgressDialog(getActivity());
        radioGroup = view.findViewById(R.id.RadioGroup);
       radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(RadioGroup radioGroup, int i) {
               int position = radioGroup.getCheckedRadioButtonId();
               radioButton = view.findViewById(position);
               EmploymentStatus = radioButton.getText().toString();


           }
       });

        MPdfDatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.JobApplicationRoot);
        JobPdf = FirebaseStorage.getInstance().getReference().child(DataManager.JobsPDFRoot);

        submitbutton = view.findViewById(R.id.SubmitButtonID);
        name = view.findViewById(R.id.FirstName);
        lastname = view.findViewById(R.id.LastName);
        email = view.findViewById(R.id.Email);
        whatsapp = view.findViewById(R.id.WhatsAppNumber);
        acivement = view.findViewById(R.id.Achivement);
        goeal = view.findViewById(R.id.Goals);
        pdfbutton = view.findViewById(R.id.PdfButton);
        job = view.findViewById(R.id.MyJobs);


        MPdfDatabase.addValueEventListener(new ValueEventListener() {
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

        get_data();



        pdfbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, PDFREQUEST);
            }
        });

        onlinecheack("online");
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PDFREQUEST  && resultCode  == RESULT_OK) {

                Mprogress.setTitle("Please wait for a moment ...");
                Mprogress.setMessage("Your employment status is uploading");
                Mprogress.setCanceledOnTouchOutside(false);
                Mprogress.show();
                Uri imageuri = data.getData();

                /*Toast.makeText(getActivity(),  data.getData().toString(), Toast.LENGTH_SHORT).show();
                pdfbutton.setText("Submitted");
                pdfbutton.setEnabled(false);*/

                StorageReference filepath = JobPdf.child(imageuri.getLastPathSegment());
                filepath.putFile(imageuri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    Pdfdownloaduri = task.getResult().getDownloadUrl().toString();
                                    pdfbutton.setText("Submitted");
                                    Mprogress.dismiss();
                                    find_userand_sendnotifaction();
                                }
                                else {
                                    Mprogress.dismiss();
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Mprogress.dismiss();
                               Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        } catch (Exception ex) {
            Mprogress.dismiss();
            Toast.makeText(getActivity(), ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void get_data(){
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstnametext = name.getText().toString().trim();
                String lastnametext = lastname.getText().toString().trim();
                String emailtext = email.getText().toString().trim();
                String whatsappnumber = whatsapp.getText().toString().trim();
                String acivementtext = acivement.getText().toString().trim();
                String goealtext = goeal.getText().toString().trim();
                String myjobtext = job.getText().toString().trim();

                if(firstnametext.isEmpty()){
                    Toast.makeText(getActivity(), R.string.firatname_toast, Toast.LENGTH_SHORT).show();
                }

                else if(lastnametext.isEmpty()){
                    Toast.makeText(getActivity(), R.string.lastname_toast, Toast.LENGTH_SHORT).show();
                }
                else if(emailtext.isEmpty()){
                    Toast.makeText(getActivity(), R.string.email_toast, Toast.LENGTH_SHORT).show();
                }
                else if(whatsappnumber.isEmpty()){
                    Toast.makeText(getActivity(), R.string.job_number_toast, Toast.LENGTH_SHORT).show();
                }
                else if(myjobtext.isEmpty()){
                    Toast.makeText(getActivity(), R.string.job_toast, Toast.LENGTH_SHORT).show();
                }
                else if(EmploymentStatus.isEmpty()){
                    Toast.makeText(getActivity(), R.string.employment_toast, Toast.LENGTH_SHORT).show();
                }
                else if(acivementtext.isEmpty()){
                    Toast.makeText(getActivity(), R.string.acivement_toast, Toast.LENGTH_SHORT).show();
                }
                else if(goealtext.isEmpty()){
                    Toast.makeText(getActivity(), R.string.status_toast, Toast.LENGTH_SHORT).show();
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

                    Calendar calendar_time = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
                    CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());

                    // yyyy-MM-
                    Calendar calendar_date = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
                    CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                    Map<String, Object> pdfmap = new HashMap<String, Object>();
                    pdfmap.put(DataManager.JobApplicantFirstName, firstnametext);
                    pdfmap.put(DataManager.JobApplicantLastName, lastnametext);
                    pdfmap.put(DataManager.JobApplicantEmail, emailtext);
                    pdfmap.put(DataManager.JobApplicantWhatsAppNumber, whatsappnumber);
                    pdfmap.put(DataManager.JobApplicantAchievement, acivementtext);
                    pdfmap.put(DataManager.JobApplicantGoals, goealtext);
                    pdfmap.put(DataManager.JobApplicantTime, CurrentTime);
                    pdfmap.put(DataManager.JobApplicantDate, CurrentDate);
                    pdfmap.put(DataManager.JobApplicationFile, Pdfdownloaduri);
                    pdfmap.put(DataManager.JobApplicantEmploymentStatus, EmploymentStatus);
                    pdfmap.put(DataManager.JobApplicantShort, childnegative);
                    pdfmap.put(DataManager.JobName, myjobtext);
                    pdfmap.put(DataManager.search, myjobtext.toLowerCase());


                   MPdfDatabase.push().updateChildren(pdfmap)
                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                                alertDialog.dismiss();

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
                           Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                       }
                   })    ;
                }
            }
        });
    }


    /// todo find user and send notifaction
    private void find_userand_sendnotifaction(){
        FirebaseDatabase.getInstance().getReference().child(DataManager.NotifactionUserRoot).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild(DataManager.UserFullname)){
                                String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                if(!name.isEmpty()){
                                    send_notfaction(name);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    /// todo find user and send notifaction


    /// todo send notifaction to reciver
    private void send_notfaction(final String username){
        FirebaseDatabase.getInstance().getReference().child("Token")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            for(DataSnapshot ds : dataSnapshot.getChildren()){

                                Token token = ds.getValue(Token.class);
                                String sendermessage = username;
                                String title = "Recently post new job";

                                Notifaction notifaction = new Notifaction(sendermessage, title);
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
    /// todo send notifaction to reciver  user -> lastmessage


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