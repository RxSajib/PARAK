package com.rakpak.pak_parak.LoginPage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.Homepage.goto_homepage;
import com.rakpak.pak_parak.R;
import com.rakpak.pak_parak.SamplePage;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends Fragment {

    private EditText email, password, repassword;
    private RelativeLayout registerbutton;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private DatabaseReference MuserDatabase;

    private MaterialTextView loginpage;
    private MaterialTextView forgetpassword, nothaveaccount;


    /// todo sample button click
    private Button buttonrecprdsample;
    /// todo sample button click

    public RegisterPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.register_page, container, false);



        buttonrecprdsample = view.findViewById(R.id.RecordPage);
        buttonrecprdsample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SamplePage.class);
                startActivity(intent);
            }
        });



        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        nothaveaccount = view.findViewById(R.id.donthaveaccounttext);
        nothaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goto_loginpage(new LoginPage());// good s
            }
        });

        forgetpassword = view.findViewById(R.id.ForgotPassword);
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotforget_password(new ForgotPassword_Page());
            }
        });

        loginpage = view.findViewById(R.id.RegestatonTextID);
        loginpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goto_loginpage(new LoginPage());
            }
        });

        MuserDatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        Mauth = FirebaseAuth.getInstance();
        email = view.findViewById(R.id.RLoginEmailID);
        password = view.findViewById(R.id.RPasswordID);
        repassword = view.findViewById(R.id.RetypePasswordID);
        registerbutton = view.findViewById(R.id.LoginButtoID);

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailtext = email.getText().toString().trim();
                String passwordtext = password.getText().toString().trim();
                String retypepassword = repassword.getText().toString().trim();

                if (emailtext.isEmpty()) {
                    email.setError("Email require");
                } else if (passwordtext.isEmpty()) {
                    password.setError("Password require");
                } else if (retypepassword.isEmpty()) {
                    repassword.setError("Password require");
                } else if (retypepassword.length() < 7) {
                    Toast.makeText(getActivity(), "Password need minimum 7 char", Toast.LENGTH_LONG).show();
                } else if (!retypepassword.equals(passwordtext)) {
      ////////HOW MANY TI ME I TOLD YOU PLEASE COME TO FIXALL THE POINT JUST FIX ONE AND COME CHECK SO WORK I  THIS IS BIG PROBLEM ALSWAYS WHEN I GIVE U 5 POINT FIRST YOU WILL FOILLOW I I SAID AFTER WHEN U FOLLER YOU ONE FIX              Toast.makeText(getActivity(), "Password not match", Toast.LENGTH_LONG).show();
            /// I FIXED SEND ME VOICE MESSAGE SIMPLE NEWS IMAGE IS NOT OPENING WHAT OPENING 
                } else {

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


                    Mauth.createUserWithEmailAndPassword(emailtext, passwordtext)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){

                                        Map<String, Object> usermap = new HashMap<>();
                                        usermap.put(DataManager.UserEmail, emailtext);
                                        usermap.put(DataManager.UserPassword, passwordtext);

                                        CurrentUserID = Mauth.getCurrentUser().getUid();
                                        MuserDatabase.child(CurrentUserID).updateChildren(usermap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        alertDialog.dismiss();
                                                        goto_homepage(new goto_homepage());
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        alertDialog.dismiss();
                                                    }
                                                });

                                    }
                                    else {
                                        alertDialog.dismiss();
                                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    alertDialog.dismiss();
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });


        return view;
    }

    private void goto_homepage(Fragment fragment){
        if(fragment != null){
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.MainID, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser Muser = Mauth.getCurrentUser();
        if(Muser != null){
            goto_homepage(new goto_homepage());
        }
    }

    private void goto_loginpage(Fragment fragment){
        if(fragment != null){
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            fragmentTransaction.replace(R.id.MainID, fragment);
            fragmentTransaction.commit();
        }
    }

    private void gotforget_password(Fragment fragment){
        if(fragment != null){
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            fragmentTransaction.replace(R.id.MainID, fragment).addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}