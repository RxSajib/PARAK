package com.rakpak.pak_parak.LoginPage;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rakpak.pak_parak.Homepage.goto_homepage;
import com.rakpak.pak_parak.R;

public class LoginPage extends Fragment {

    private MaterialTextView signnupbutton;
    private FirebaseAuth Mauth;
    private RelativeLayout loginbutton;
    private EditText email, password;
    private ProgressDialog Mprogress;
    private FirebaseAuth mauth;

    public LoginPage() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_page, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Mauth = FirebaseAuth.getInstance();
        Mprogress = new ProgressDialog(getActivity());
        email = view.findViewById(R.id.LoginEmailID);
        password = view.findViewById(R.id.LoginPasswordID);

        loginbutton = view.findViewById(R.id.LoginButtoID);

        Mauth = FirebaseAuth.getInstance();

        signnupbutton =view.findViewById(R.id.SignUpButtonID);
        signnupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goto_reg_page(new RegisterPage());
            }
        });


        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailtext = email.getText().toString();
                String passwordtext = password.getText().toString();

                if(emailtext.isEmpty()){
                    email.setError("Email require");
                }
                else if(passwordtext.isEmpty()){
                    password.setError("Password require");
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

                    Mauth.signInWithEmailAndPassword(emailtext, passwordtext)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        alertDialog.dismiss();
                                        goto_homepage(new goto_homepage());
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



        return view;
    }

    public void goto_reg_page(Fragment fragment){

        if(fragment != null){
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            fragmentTransaction.replace(R.id.MainID, fragment);
            fragmentTransaction.commit();
        }

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
}