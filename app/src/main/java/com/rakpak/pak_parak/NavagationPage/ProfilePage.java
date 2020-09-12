package com.rakpak.pak_parak.NavagationPage;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

public class ProfilePage extends Fragment {

    private RelativeLayout back_button;
    private Button updatebutton;
    private EditText fullname, phonenumber, work;
    private String CurrentUserID;
    private FirebaseAuth Mauth;
    private DatabaseReference Muser_database;
    private ProgressDialog Mprogress;
    private String CurrentTime, CurrentDate;

    public ProfilePage() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_page, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Mprogress = new ProgressDialog(getActivity());

        Muser_database = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        Muser_database.keepSynced(true);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        fullname = view.findViewById(R.id.EditFullnameID);
        phonenumber = view.findViewById(R.id.EditPhoneNumberID);
        work = view.findViewById(R.id.EditWorkID);

        updatebutton = view.findViewById(R.id.UpdateButtonID);
        back_button = view.findViewById(R.id.back_buttonID);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        /*back_button.setOnTouchListener((view1, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                back_button.setBackgroundResource(R.drawable.click_back_button_animaction);


            }
            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                back_button.setBackgroundResource(R.drawable.click_backbutton_up_animaction);
            }

            return true;
        });*/




        updatebutton.setOnClickListener(view12 -> {

            String name = fullname.getText().toString().trim();
            String phone = phonenumber.getText().toString().trim();
            String mywork = work.getText().toString();

            if(name.isEmpty()){
                Toast.makeText(getActivity(), "Name require", Toast.LENGTH_SHORT).show();
            }
            else if(phone.isEmpty()){
                Toast.makeText(getActivity(), "Phonenumber require", Toast.LENGTH_SHORT).show();
            }
            else if(mywork.isEmpty()){
                Toast.makeText(getActivity(), "Work require", Toast.LENGTH_SHORT).show();
            }
            else {
                Mprogress.setTitle("Wait for a moment");
                Mprogress.setMessage("Please wait your profile is updating");
                Mprogress.setCanceledOnTouchOutside(false);
                Mprogress.show();
                Map<String , Object> usermap = new HashMap<String, Object>();
                usermap.put(DataManager.UserFullname, name);
                usermap.put(DataManager.UserPhonenumber, phone);
                usermap.put(DataManager.UserWork, mywork);

                Muser_database.child(CurrentUserID).updateChildren(usermap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    fullname.setText(null);
                                    phonenumber.setText(null);
                                    work.setText(null);
                                    Mprogress.dismiss();
                                    Toast.makeText(getActivity(), "profile is update" , Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Mprogress.dismiss();
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Mprogress.dismiss();
                                Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });


        Muser_database.child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.exists()){
                                if(dataSnapshot.hasChild(DataManager.UserFullname)){
                                    String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                    fullname.setText(name);
                                }
                                if(dataSnapshot.hasChild(DataManager.UserPhonenumber)){
                                    String number = dataSnapshot.child(DataManager.UserPhonenumber).getValue().toString();
                                    phonenumber.setText(number);
                                }
                                if(dataSnapshot.hasChild(DataManager.UserWork)){
                                    String mywork = dataSnapshot.child(DataManager.UserWork).getValue().toString();
                                    work.setText(mywork);
                                }
                            }
                        }
                        else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        onlinecheack("online");
        return view;
    }


    private void onlinecheack(String online){

        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("hh:mm a");
        CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());


        Calendar calendar_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("yyyy-MM-dd");
        CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());


        Map<String, Object> onlinemap = new HashMap<String, Object>();
        onlinemap.put(DataManager.UserCardActive, online);
        onlinemap.put(DataManager.UserActiveTime, CurrentTime);
        onlinemap.put(DataManager.UserActiveDate, CurrentDate);

        Muser_database.child(CurrentUserID).child(DataManager.UserOnlineRoot).updateChildren(onlinemap)
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