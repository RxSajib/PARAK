package com.rakpak.pak_parak.Homepage;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.rakpak.pak_parak.BottomnavPage.Chatpages;
import com.rakpak.pak_parak.BottomnavPage.JobPage;
import com.rakpak.pak_parak.BottomnavPage.NewsPage;
import com.rakpak.pak_parak.BottomnavPage.NotifactionPages;
import com.rakpak.pak_parak.BottomnavPage.UserCard;
import com.rakpak.pak_parak.BuildConfig;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.GlobalChat.GlobalChat;
import com.rakpak.pak_parak.InternetDioloag.NoConnectionDioloadPage;
import com.rakpak.pak_parak.MainActivity;
import com.rakpak.pak_parak.Model.Token;
import com.rakpak.pak_parak.NavagationPage.IDcard_Page;
import com.rakpak.pak_parak.NavagationPage.Mission_And_Vision;
import com.rakpak.pak_parak.NavagationPage.ParakJob;
import com.rakpak.pak_parak.NavagationPage.ProfilePage;
import com.rakpak.pak_parak.NavagationPage.ProjectPage;
import com.rakpak.pak_parak.NavagationPage.TeamPage;
import com.rakpak.pak_parak.R;
import com.rakpak.pak_parak.Search.Search_Page;
import com.rakpak.pak_parak.SetProfile.Setup_Profile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class goto_homepage extends Fragment {

    private DatabaseReference Muserdata;
    private FirebaseAuth Mauth;
    private String Currentuserid;

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView mainnav;
    private ImageView menuicon;
    private Window window;
    private boolean idcard_isactive = true;
    private boolean chat_isactive = true;
    private boolean jobis_active = true;
    private boolean news_isactive = true;
    private boolean notifaction_isactive = true;
    private boolean global_isactive = true;
    private RelativeLayout searchbutton;
    private RelativeLayout menu_button;
    private String CurrentTime, CurrentDate;


    private DatabaseReference OnlineData;
    private static final int IMAGECODE = 1;
    private StorageReference ImageStores;
    private ProgressDialog Mprogress;
    private final int PERMISSION_CODE = 100;
    private AnstronCoreHelper coreHelper;

    public goto_homepage() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.goto_homepage, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Mprogress = new ProgressDialog(getActivity());
        ImageStores = FirebaseStorage.getInstance().getReference().child(DataManager.ProfileImageRoot);
        coreHelper = new AnstronCoreHelper(getActivity());

        OnlineData = FirebaseDatabase.getInstance().getReference().child(DataManager.OnlineUseRoot);

        menu_button = view.findViewById(R.id.MyMenuButtonID);










        searchbutton = view.findViewById(R.id.SearchButtonToolbarID);
       /* searchbutton.setOnTouchListener((view1, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                searchbutton.setBackgroundResource(R.drawable.click_back_button_animaction);


            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                searchbutton.setBackgroundResource(R.drawable.click_backbutton_up_animaction);
            }
            return true;
        });*/

       searchbutton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               goto_search_screen(new Search_Page());
           }
       });


        Mauth = FirebaseAuth.getInstance();
        Currentuserid = Mauth.getCurrentUser().getUid();
        Muserdata = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        Muserdata.keepSynced(true);
        read_user();


        drawerLayout = view.findViewById(R.id.MainDrawerID);
        mainnav = view.findViewById(R.id.MainNavID);

        menu_button.setOnClickListener(view1 -> drawerLayout.openDrawer(Gravity.LEFT));


        View navagation_view = mainnav.inflateHeaderView(R.layout.header_layout);
        final MaterialTextView username = navagation_view.findViewById(R.id.UserNamID);
        final MaterialTextView phonenumber = navagation_view.findViewById(R.id.UserEmailID);
        final CircleImageView profileimage = navagation_view.findViewById(R.id.ProfileIagesNav);
        final ImageView addimage = navagation_view.findViewById(R.id.AddImageID);

        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (permission_extranal_stores()) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, IMAGECODE);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                else {

                }


            }
        });

        Muserdata.child(Currentuserid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("profileimage")) {
                                String uri = dataSnapshot.child("profileimage").getValue().toString();
                                if (getContext() != null) {

                                    Picasso.with(getContext()).load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(profileimage, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(getContext()).load(uri).placeholder(R.drawable.profile_image_back).into(profileimage);

                                        }
                                    });
                                }

                            }
                            if (dataSnapshot.hasChild(DataManager.UserFullname)) {
                                String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                username.setText(name);
                            }
                            if (dataSnapshot.hasChild("Number")) {
                                String number = dataSnapshot.child("Number").getValue().toString();
                                phonenumber.setText(number);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        mainnav.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.LogoutID) {
                onlinecheack("offline");

                Mauth.signOut();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);


            }
            if(item.getItemId() == R.id.IDCARD){
                item.setCheckable(true);
                item.setChecked(true);
                drawerLayout.closeDrawer(Gravity.LEFT);

                goto_idcardpage(new IDcard_Page());
            }
            if(item.getItemId() == R.id.ProfileID){
                item.setCheckable(true);
                item.setChecked(true);
                drawerLayout.closeDrawer(Gravity.LEFT);

                goto_profilepages(new ProfilePage());
            }
            if(item.getItemId() == R.id.MissionVission){
                item.setCheckable(true);
                item.setChecked(true);
                drawerLayout.closeDrawer(Gravity.LEFT);

                goto_missionvision_page(new Mission_And_Vision());
            }
            if(item.getItemId() == R.id.OurTeamID){
                item.setCheckable(true);
                item.setChecked(true);
                drawerLayout.closeDrawer(Gravity.LEFT);

                goto_teampage(new TeamPage());
            }
            if(item.getItemId() == R.id.ProjectID){
                item.setCheckable(true);
                item.setChecked(true);
                drawerLayout.closeDrawer(Gravity.LEFT);

                goto_projectpage(new ProjectPage());
            }
            if(item.getItemId() == R.id.ParakJobID){
                item.setCheckable(true);
                item.setChecked(true);
                drawerLayout.closeDrawer(Gravity.LEFT);

                goto_parakjobpage(new ParakJob());
            }

            if(item.getItemId() == R.id.ShareID){
                item.setCheckable(true);
                item.setChecked(true);
                drawerLayout.closeDrawer(Gravity.LEFT);


                int counter = 0;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                String shareMessage = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";

                String sharebody = shareMessage;
                String sharesubject = "Welcome Brother/sister! \n" +
                        "We're so happy you're here. We founded MUSTAQEEM WELFARE GROUP (Pakistani Community) because we wanted to create a trustworthy and inspiring place for you to find everything you need to help and support as well Click the Link Below to Connect with thousands of Pakistani nationals in UAE" +
                        "\n" +
                        "By MUSTAQEEM WELFARE GROUP\n"+"خوش آمدید بھائی / بہن!" +
                        "\nہم یہاں بہت خوش ہیں۔ ہم نے مستقیم ویلفر گروپ (پاکستانی کمیونٹی) کی بنیاد رکھی کیونکہ ہم آپ کے لئے ایک قابل اعتماد اور متاثر کن جگہ بنانا چاہتے تھے جس کی مدد اور مدد کے لئے آپ کی ضرورت ہے اور ساتھ ہی متحدہ عرب امارات کے ہزاروں پاکستانی شہریوں سے رابطہ قائم کرنے کے لئے نیچے لنک پر کلک کریں\n"+
                        "بذریعہ   مستقیم ویلفر گروپ"+"\n"+"Under The UAE law"+"\n"+sharebody;
                intent.putExtra(Intent.EXTRA_TEXT, sharesubject);
                //  intent.putExtra(Intent.EXTRA_SUBJECT, sharebody);
                startActivity(Intent.createChooser(intent, "share with"));
            }


            return true;
        });


        bottomNavigationView = view.findViewById(R.id.BottomNavViewID);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              /*  if (item.getItemId() == R.id.UserCardID) {
                    if (idcard_isactive) {
                        goto_usercard(new UserCard());
                        idcard_isactive = false;
                        chat_isactive = true;
                        jobis_active = true;
                        news_isactive = true;
                    }

                }*/
              if(item.getItemId() == R.id.NotifactionID){
                  if(notifaction_isactive){
                      goto_notifactionpage(new NotifactionPages());
                      notifaction_isactive = false;
                      chat_isactive = true;
                      idcard_isactive = true;
                      jobis_active = true;
                      news_isactive = true;
                      global_isactive = true;

                  }
              }

                if (item.getItemId() == R.id.ChatID) {
                    if (chat_isactive) {

                        goto_chatpages(new Chatpages());
                        chat_isactive = false;
                        idcard_isactive = true;
                        jobis_active = true;
                        news_isactive = true;
                        global_isactive = true;
                        notifaction_isactive = true;
                    }

                }

                if (item.getItemId() == R.id.NewsID) {

                    if (news_isactive) {
                        goto_newspage(new NewsPage());
                        news_isactive = false;
                        chat_isactive = true;
                        idcard_isactive = true;
                        jobis_active = true;
                        global_isactive = true;
                        notifaction_isactive = true;
                    }

                }

                if(item.getItemId() == R.id.JobsID){
                    if(jobis_active){
                        goto_newspage(new JobPage());
                        news_isactive = true;
                        chat_isactive = true;
                        idcard_isactive = true;
                        jobis_active = false;
                        global_isactive = true;
                        notifaction_isactive = true;
                    }
                }












                return true;
            }
        });





        ConnectivityManager cm =(ConnectivityManager)getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        // todo getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen, R.style.PauseDialog
        else {

           /* MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(getActivity());
            View aleartview = LayoutInflater.from(getActivity()).inflate(R.layout.no_connection_dioloag, null, false);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fedein);
            aleartview.setAnimation(animation);
            Mbuilder.setView(aleartview);

            AlertDialog alertDialog = Mbuilder.create();
            alertDialog.show();*/


          /*  View _v = LayoutInflater.from(getActivity()).inflate(R.layout.no_connection_dioloag, null, false);


            final Dialog dialog = new Dialog(getActivity(), R.style.PauseDialog);

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.send_message);


            dialog.setCanceledOnTouchOutside(false);
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



*/



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

        //goto_page(new NoConnectionDioloadPage());






        onlinecheack("online");
        fcm_token();
        return view;
    }

    private void read_user() {
        Muserdata.child(Currentuserid).child(DataManager.UserFullname)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {

                            goto_profilepage(new Setup_Profile());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void goto_profilepage(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.MainID, fragment);
            transaction.commit();
        }
    }


    private void goto_chatpages(Fragment fragment) {

        if(fragment != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainFream, fragment);
            transaction.commit();
        }
    }

    private void goto_notifactionpage(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainFream, fragment);
            transaction.commit();
        }
    }


    private void goto_newspage(Fragment fragment) {

        if(fragment != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainFream, fragment);
            transaction.commit();
        }
    }

    private void goto_jobspage(Fragment fragment) {

        if(fragment != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainFream, fragment);
            transaction.commit();
        }
    }


    private void goto_usercard(Fragment fragment) {

        if(fragment != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.MainFream, fragment);
            transaction.commit();
        }
    }

    private void goto_globalchat_page(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.MainFream, fragment);
            transaction.commit();
        }
    }

    private void goto_parakjobpage(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }
    }

    // the internet go 3 times bro sorry for that
    // todo   i am trying without download manager ok

    private void fcm_token() {

        String refresstoken = FirebaseInstanceId.getInstance().getToken();

        FirebaseDatabase bd = FirebaseDatabase.getInstance();
        DatabaseReference reference = bd.getReference("Token");
        Token token = new Token(refresstoken, true);


        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    @Override
    public void onStart() {
        super.onStart();

        goto_notifactionpage(new NotifactionPages());
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        jobis_active = true;
        idcard_isactive = true;
        chat_isactive = true;
        news_isactive = true;


        FirebaseUser Muser = Mauth.getCurrentUser();
        if(Muser == null){
            onlinecheack("offline");
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void goto_idcardpage(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }
    }

    private void goto_profilepages(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }

    }

    private void goto_missionvision_page(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }
    }

    private void goto_teampage(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }
    }

    private void goto_search_screen(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }
    }

    private void goto_projectpage(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }
    }


    private void onlinecheack(String online){

        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
        CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());


        Calendar calendar_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
        CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());


        Map<String, Object> onlinemap = new HashMap<String, Object>();
        onlinemap.put(DataManager.UserCardActive, online);
        onlinemap.put(DataManager.UserActiveTime, CurrentTime);
        onlinemap.put(DataManager.UserActiveDate, CurrentDate);

        OnlineData.child(Currentuserid).child(DataManager.UserOnlineRoot).updateChildren(onlinemap)
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGECODE && resultCode == RESULT_OK) {

            Mprogress.setTitle("Please wait ...");
            Mprogress.setMessage("wait for a moment your image is updating");
            Mprogress.setCanceledOnTouchOutside(false);
            Mprogress.show();
            Uri imageuri = data.getData();

            if(imageuri != null){

               final  File file = new File(SiliCompressor.with(getActivity())
               .compress(FileUtils.getPath(getActivity(), imageuri), new File(getActivity().getCacheDir(), "temp")));

               Uri fromfile = Uri.fromFile(file);


              ImageStores.child(coreHelper.getFileNameFromUri(fromfile))
                      .putFile(fromfile)
                      .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                          @Override
                          public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                              if(task.isSuccessful()){
                                  String imagedonloaduri = task.getResult().getDownloadUrl().toString();
                                  Muserdata.child(Currentuserid)
                                          .child(DataManager.profileimage).setValue(imagedonloaduri)
                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                                  if(task.isSuccessful()){
                                                      Mprogress.dismiss();
                                                      Toast.makeText(getActivity(), "profile is updated", Toast.LENGTH_SHORT).show();
                                                  }
                                                  else {
                                                      Mprogress.dismiss();
                                                      Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                  }
                                              }
                                          }).addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          Mprogress.dismiss();
                                          Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                      }
                                  });
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


        }
    }

    private boolean permission_extranal_stores(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
            return false;
        }
    }


    private void goto_page(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment);
            transaction.commit();
        }
    }
}