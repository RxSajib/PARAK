package com.rakpak.pak_parak.SetProfile;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.Homepage.goto_homepage;
import com.rakpak.pak_parak.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class Setup_Profile extends Fragment {


    private CircleImageView profileimage;
    private EditText username, phonenumber;
    private RelativeLayout continuebutton;
    private DatabaseReference MuserDatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private static final int ACTION_CODE = 01;
    private StorageReference MprofileStores;
    private String DownloadUri ;
    private RelativeLayout professionbutton;

    private String value = "sajabsaosdoasudoasugdoasgdoauds";
    private String CurrentTime, CurrentDate;
    private int Short;
    private int NegativeShort;
    private int PersonID;

    private EditText jobinput;
    private EditText location;
    private DatabaseReference MNotifactionDatabase;

    private DatabaseReference TypeData;
    private final int IMAGE_PERMISSION_CODE = 100;
    private AnstronCoreHelper anstronCoreHelper;


    public Setup_Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.setup__profile, container, false);


        TypeData = FirebaseDatabase.getInstance().getReference().child(DataManager.UserTypeRoot);

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





        MNotifactionDatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.NotifactionUserRoot);
        location = view.findViewById(R.id.LocationID);
       getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        jobinput = view.findViewById(R.id.JobsInputID);


        professionbutton = view.findViewById(R.id.ProfessionButtonID);
        professionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        MprofileStores = FirebaseStorage.getInstance().getReference();
        anstronCoreHelper = new AnstronCoreHelper(getActivity());
        String newstring = value.substring(0, Math.min(value.length(), 5));



        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        profileimage = view.findViewById(R.id.ProfileImageID);
        username = view.findViewById(R.id.FillNameID);
        phonenumber = view.findViewById(R.id.PhoneNumberID);
        continuebutton = view.findViewById(R.id.ContinueButtonID);

        MuserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Short = (int) dataSnapshot.getChildrenCount();
                    NegativeShort =  (~(Short - 1));

                    PersonID = (int) dataSnapshot.getChildrenCount();

                }
                else {
                    NegativeShort = 0;
                    PersonID = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(cheacking_read_image_permission()){
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, ACTION_CODE);
                }
                else{

                }



            }
        });

        continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = username.getText().toString().trim();
                String number = phonenumber.getText().toString().trim();
                String jobs = jobinput.getText().toString().trim();
                String locationtext = location.getText().toString().trim();

                if(name.isEmpty()){
                    username.setError("Username require");
                }
                else if(number.isEmpty()){
                    phonenumber.setError("Number require");
                }
                else if(jobs.isEmpty()){
                    jobinput.setError("Jobs require");
                }
                else if(locationtext.isEmpty()){
                    location.setError("Location require");
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

                    ///

                    Calendar calendar_time = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
                    CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());


                    Calendar calendar_date = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
                    CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                    //

                    Map<String, Object> usermap = new HashMap<String, Object>();
                    usermap.put("Username", name);
                    usermap.put("Number", number);
                    usermap.put("MYID", CurrentUserID);
                    usermap.put("profileimage", DownloadUri);
                    usermap.put("Time", CurrentTime);
                    usermap.put("Date", CurrentDate);
                    usermap.put("ShortDes", NegativeShort);
                    usermap.put("ID", +PersonID);
                    usermap.put("active_status", "InActive");
                    usermap.put("Jobs", jobs);
                    usermap.put("search", name.toLowerCase());
                    usermap.put("location", locationtext);


                    MuserDatabase.child(CurrentUserID).updateChildren(usermap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        TypeData.child(CurrentUserID).child("type_status")
                                                .child("type").setValue("notype");

                                        TypeData.child(CurrentUserID).child("type_status")
                                                .child("time").setValue(CurrentTime);

                                        TypeData.child(CurrentUserID).child("type_status")
                                                .child("date").setValue(CurrentDate);


                                        alertDialog.dismiss();
                                        homepage(new goto_homepage());



                                    }
                                    else {
                                        alertDialog.dismiss();
                                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                alertDialog.dismiss();
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_CODE && resultCode == RESULT_OK) {
            Uri imageuri = data.getData();
            profileimage.setImageURI(imageuri);



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



            if(imageuri != null){

                final File file = new File(SiliCompressor.with(getActivity())
                .compress(FileUtils.getPath(getActivity(), imageuri), new File(getActivity().getCacheDir(), "temp")));

                Uri fromfile = Uri.fromFile(file);

                MprofileStores.child("ProfileImage/").child(anstronCoreHelper.getFileNameFromUri(fromfile))
                        .putFile(fromfile)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    DownloadUri = task.getResult().getDownloadUrl().toString();
                                    alertDialog.dismiss();
                                }
                                else {
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                alertDialog.dismiss();
                            }
                        });

            }


        }
    }

    private void homepage(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment);
            transaction.commit();
        }
    }

    private boolean cheacking_read_image_permission(){
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_PERMISSION_CODE);

            return false;
        }
    }
}