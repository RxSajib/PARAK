package com.rakpak.pak_parak.ProjectPages;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.R;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class Help extends Fragment {

    private RelativeLayout submitbutton;
    private Animation frombottom_animaction;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private EditText helpeinput, fullnameinput, mobileinput, idnumberinput, familymesmbersinput, workinput, locationinput, montlyinput, howwecanhelpyoumoreinput;
    private MaterialButton application;
    private static final int PERMISSIONCODE = 100;
    private static final int PDFCODE = 10;
    private StorageReference MFile;
    private ProgressDialog Mprogress;
    private String PdfDownloadUri;
    private int ConvertNegative, Count;
    private DatabaseReference   Mroot;

    private RelativeLayout Backbutton;

    public Help() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.help, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        submitbutton = view.findViewById(R.id.SubmitApplicationButonID);

        Mroot = FirebaseDatabase.getInstance().getReference().child(DataManager.HelpRoot);
        Mprogress = new ProgressDialog(getActivity());
        MFile = FirebaseStorage.getInstance().getReference().child(DataManager.HelpRootPdf);

        Backbutton = view.findViewById(R.id.BackButtonss);
        Backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        application = view.findViewById(R.id.Application);
        application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(cheackpermission()){
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent, PDFCODE);
                }
                else {

                }
            }
        });

        Mroot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Count = (int) dataSnapshot.getChildrenCount();
                    ConvertNegative = (~(Count - 1));

                }
                else {
                    ConvertNegative = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fullnameinput = view.findViewById(R.id.Name);
        mobileinput = view.findViewById(R.id.MobileNumber);
        idnumberinput = view.findViewById(R.id.IDNoID);
        familymesmbersinput = view.findViewById(R.id.FamilyMemberID);
        workinput = view.findViewById(R.id.IncomeInput);
        locationinput = view.findViewById(R.id.LocationInput);
        montlyinput  =view.findViewById(R.id.MontilyIncome);
        howwecanhelpyoumoreinput = view.findViewById(R.id.HelpText);
        idnumberinput = view.findViewById(R.id.IDNoID);



        radioGroup = view.findViewById(R.id.RadioGroupID);
        helpeinput = view.findViewById(R.id.HelpInputID);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int position = radioGroup.getCheckedRadioButtonId();

                radioButton = view.findViewById(position);
                String answer = radioButton.getText().toString();

                if(answer.equals(DataManager.Yes)){
                    helpeinput.setVisibility(View.VISIBLE);
                }
                if(answer.equals(DataManager.No)){
                    helpeinput.setVisibility(View.GONE);
                }
            }
        });


        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fullname = fullnameinput.getText().toString().trim();
                String mobilenumber = mobileinput.getText().toString().trim();
                String _idnumber = idnumberinput.getText().toString().trim();
                String numberof_family_member = familymesmbersinput.getText().toString().trim();
                String sourceof_income = workinput.getText().toString().trim();
                String location = locationinput.getText().toString().trim();
                String montly_income = montlyinput.getText().toString().trim();
                String how_wecan_help = howwecanhelpyoumoreinput.getText().toString().trim();
                String help = helpeinput.getText().toString().trim();


                if(fullname.isEmpty()){
                    fullnameinput.setError("Name require");
                }
                else if(mobilenumber.isEmpty()){
                    mobileinput.setError("Number require");
                }
                else if(_idnumber.isEmpty()){
                    idnumberinput.setError("Id require");
                }
                else if(sourceof_income.isEmpty()){
                    workinput.setError("your income source require");
                }
                else if(location.isEmpty()){
                    locationinput.setError("Location require");
                }
                else if(montly_income.isEmpty()){
                    montlyinput.setError("Monthly income require");
                }
                else if(how_wecan_help.isEmpty()){
                    howwecanhelpyoumoreinput.setError("Why you need help");
                }
                else {

                    Mprogress.setTitle("Please wait ...");
                    Mprogress.setMessage("wait for a moment your post is sending");
                    Mprogress.setCanceledOnTouchOutside(false);
                    Mprogress.show();

                    Map<String, Object> helpmap = new HashMap<>();
                    helpmap.put(DataManager.Help_Name, fullname);
                    helpmap.put(DataManager.Help_Mobile, mobilenumber);
                    helpmap.put(DataManager.Help_ID, _idnumber);
                    helpmap.put(DataManager.Help_FamilyMembers, numberof_family_member);
                    helpmap.put(DataManager.Help_SourceOfIncome, sourceof_income);
                    helpmap.put(DataManager.Help_Location, location);
                    helpmap.put(DataManager.Help_MontlyIncome, montly_income);
                    helpmap.put(DataManager.HelpDetails, how_wecan_help);
                    helpmap.put(DataManager.WhyNeedHelp, help);
                    helpmap.put(DataManager.HelpPdf, PdfDownloadUri);
                    helpmap.put(DataManager.Short, ConvertNegative);

                    Mroot.push()
                            .updateChildren(helpmap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Mprogress.dismiss();

                                        fullnameinput.setText("");
                                        mobileinput.setText("");
                                        idnumberinput.setText("");
                                        workinput.setText("");
                                        locationinput.setText("");
                                        montlyinput.setText("");
                                        howwecanhelpyoumoreinput.setText("");
                                        familymesmbersinput.setText("");
                                        helpeinput.setText("");

                                        Toast.makeText(getActivity(), "upload success", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }


            }
        });


        return view;
    }


    private boolean cheackpermission(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONCODE);

            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PDFCODE  && resultCode  == RESULT_OK) {

                Mprogress.setTitle("Please wait ...");
                Mprogress.setMessage("wait for a moment your document is sending");
                Mprogress.setCanceledOnTouchOutside(false);
                Mprogress.show();

                Uri filepath = data.getData();
                StorageReference pdffilepath = MFile.child(filepath.getLastPathSegment());
                pdffilepath.putFile(filepath)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    Mprogress.dismiss();
                                    PdfDownloadUri = task.getResult().getDownloadUrl().toString();
                                    application.setText("Success");
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

        }

    }
}