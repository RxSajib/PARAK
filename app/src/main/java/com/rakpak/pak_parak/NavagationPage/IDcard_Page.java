package com.rakpak.pak_parak.NavagationPage;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.zxing.WriterException;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.net.NetPermission;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import de.hdodenhof.circleimageview.CircleImageView;

public class IDcard_Page extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference MuserDatabase;
    private String CurrentuserID;
    private FirebaseAuth Mauth;

    private CircleImageView profileimage;
    private MaterialTextView username, id_number, phonenumber;
    private MaterialTextView cardstatus;
    private Typeface typefacesignesser, typefacenormal;

    private ImageView qrcodeimage;
    private String TAG = "Qrcodetag";
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;

    private String _idnumber, name, number;
    private MaterialTextView jobs;

    // todo issue date and exp date android:id="@+id/ESigID"
    private MaterialTextView issue_date, exp_date;
    private MaterialTextView status;
    // todo issue date and exp date
    private RelativeLayout back_icon;

    private Typeface sign_typeface;
    private MaterialTextView locationtext;
    private DatabaseReference OnlineData;

    public IDcard_Page() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout._idcard__page, container, false);
  //      getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        OnlineData = FirebaseDatabase.getInstance().getReference().child(DataManager.UserOnlineRoot);

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




        sign_typeface = ResourcesCompat.getFont(getContext(), R.font.sincures);

        status = view.findViewById(R.id.ESigID);
        back_icon = view.findViewById(R.id.back_icons);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });

       /* back_icon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    back_icon.setBackgroundResource(R.drawable.click_back_button_animaction);
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    back_icon.setBackgroundResource(R.drawable.click_backbutton_up_animaction);
                }

                return true;
            }
        });*/
        // todo issue date and exp date
        issue_date = view.findViewById(R.id.Issue_date);
        exp_date = view.findViewById(R.id.Expdate_id);
        // todo issue date and exp date

        jobs = view.findViewById(R.id.JobsTexts);
        qrcodeimage = view.findViewById(R.id.QrCodeImage);

        typefacesignesser = ResourcesCompat.getFont(getActivity(), R.font.sincures);
        typefacenormal = ResourcesCompat.getFont(getActivity(), R.font.ubuntureguiar);

        cardstatus = view.findViewById(R.id.StatusTexts);

        profileimage = view.findViewById(R.id.ProfileImage);
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        MuserDatabase.keepSynced(true);
        username = view.findViewById(R.id.CardFullname);
        id_number = view.findViewById(R.id.CardIDNO);
        phonenumber = view.findViewById(R.id.CardPhoeNumber);

        Mauth = FirebaseAuth.getInstance();
        CurrentuserID = Mauth.getCurrentUser().getUid();



        onlinecheack("online");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MuserDatabase.child(CurrentuserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("profileimage")) {

                                String uri = dataSnapshot.child("profileimage").getValue().toString();
                                if (getContext() != null) {

                                    Picasso.with(getContext()).load(uri).resize(200,200).centerCrop().networkPolicy(NetworkPolicy.OFFLINE)
                                            .into(profileimage, new Callback() {
                                                @Override
                                                public void onSuccess() {

                                                }

                                                @Override
                                                public void onError() {
                                                    Picasso.with(getContext()).load(uri).resize(200,200).centerCrop().into(profileimage);
                                                }
                                            });


                                }
                            }
                            if (dataSnapshot.hasChild("active_status")) {
                                String status = dataSnapshot.child("active_status").getValue().toString();
                                if (status.equals("InActive")) {
                                  /*  cardstatus.setText("NotVerify");
                                    cardstatus.setTypeface(typefacenormal);*/
                                }
                                if (status.equals("Active")) {
                                    /*cardstatus.setText("Verify");
                                    cardstatus.setTypeface(typefacesignesser);*/
                                }
                            }

                            if (dataSnapshot.hasChild(DataManager.UserPhonenumber)) {
                                number = dataSnapshot.child(DataManager.UserPhonenumber).getValue().toString();
                                phonenumber.setText(number);
                            }

                            if (dataSnapshot.hasChild(DataManager.UserFullname)) {
                                name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                username.setText(name);
                            }

                            if (dataSnapshot.hasChild(DataManager.UserID)) {
                                _idnumber = dataSnapshot.child(DataManager.UserID).getValue().toString();
                                id_number.setText("MS-QM-786-2020-" + _idnumber);

                            }
                            // jobs
                            if(dataSnapshot.hasChild(DataManager.UserWork)){
                                String jobstext = dataSnapshot.child(DataManager.UserWork).getValue().toString();
                                jobs.setText(jobstext);
                            }
                            if(dataSnapshot.hasChild(DataManager.UserLocation)){
                                String location = dataSnapshot.child(DataManager.UserLocation).getValue().toString();
                                cardstatus.setText(location);
                            }

                            /// todo there is the issue date and exp date
                            if(dataSnapshot.hasChild(DataManager.UserActiveDate)){
                                String active_date = dataSnapshot.child(DataManager.UserActiveDate).getValue().toString();
                                issue_date.setText(active_date);
                            }
                            if(dataSnapshot.hasChild(DataManager.UserExpDate)){
                                String exp_datestring = dataSnapshot.child(DataManager.UserExpDate).getValue().toString();
                                exp_date.setText(exp_datestring);
                            }
                            if(dataSnapshot.hasChild(DataManager.UserCardActive)){
                                String active_string = dataSnapshot.child(DataManager.UserCardActive).getValue().toString();

                                if(active_string.equals("active_card")){

                                    if(dataSnapshot.hasChild(DataManager.UsercardStatus)){
                                        String statustext = dataSnapshot.child(DataManager.UsercardStatus).getValue().toString();
                                        status.setText(statustext);
                                        status.setTypeface(sign_typeface);

                                    }
                                }
                            }
                            /// todo there is the issue date and exp date

                           /* if (name.length() > 0 || _idnumber.length() > 0 || number.length() > 0) {

                                String inputbalue = "Name: " + name + "\n" + "Number: " + number + "\n" + "ID: " + id_number;

                                if(!inputbalue.isEmpty()){
                                    WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                                    Display display = manager.getDefaultDisplay();
                                    Point point = new Point();
                                    display.getSize(point);
                                    int width = point.x;
                                    int heigth = point.y;

                                    int smalldimantion = width < heigth ? width : heigth;
                                    smalldimantion = smalldimantion * 3 / 4;
                                    qrgEncoder = new QRGEncoder(inputbalue, null, QRGContents.Type.TEXT, smalldimantion);
                                    try {
                                        bitmap = qrgEncoder.encodeAsBitmap();
                                        qrcodeimage.setImageBitmap(bitmap);
                                    } catch (WriterException e) {
                                        Log.d("ERROR", e.toString());
                                    }
                                }


                            }*/
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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

        OnlineData.child(CurrentuserID).child(DataManager.UserOnlineRoot).updateChildren(onlinemap)
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