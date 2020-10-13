package com.rakpak.pak_parak.LoginPage;

import android.icu.text.UnicodeSetSpanner;
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
import com.google.firebase.auth.FirebaseAuth;
import com.rakpak.pak_parak.R;

public class ForgotPassword_Page extends Fragment {

    private RelativeLayout button;
    private EditText email;
    private FirebaseAuth Mauth;
    private RelativeLayout backtologin;

    public ForgotPassword_Page() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.forgot_password__page, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        email = view.findViewById(R.id.ForgetPasswordEmail);
        button = view.findViewById(R.id.ContinueResetPassword);
        Mauth = FirebaseAuth.getInstance();

        backtologin = view.findViewById(R.id.BackID);
        backtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goto_loginpage(new LoginPage());
            }
        });

      button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              String emailtext = email.getText().toString().trim();
              if(emailtext.isEmpty()){
                  email.setError("Email require");
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


                  Mauth.sendPasswordResetEmail(emailtext)
                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  if(task.isSuccessful()){
                                      alertDialog.dismiss();
                                      email.setText("");
                                      Toast.makeText(getActivity(), "We are sending a link on your email address ....", Toast.LENGTH_SHORT).show();

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

    private void goto_loginpage(Fragment fragment){
        if(fragment != null){
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slider_from_right, R.anim.slide_outfrom_left);
            fragmentTransaction.replace(R.id.MainID, fragment);
            fragmentTransaction.commit();
        }
    }
}