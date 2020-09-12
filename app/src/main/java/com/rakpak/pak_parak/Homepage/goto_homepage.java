package com.rakpak.pak_parak.Homepage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.rakpak.pak_parak.BottomnavPage.Chatpages;
import com.rakpak.pak_parak.BottomnavPage.JobPage;
import com.rakpak.pak_parak.BottomnavPage.NewsPage;
import com.rakpak.pak_parak.BottomnavPage.UserCard;
import com.rakpak.pak_parak.BuildConfig;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.GlobalChat.GlobalChat;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


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
    private boolean global_isactive = true;
    private RelativeLayout searchbutton;
    private RelativeLayout menu_button;
    private String CurrentTime, CurrentDate;

    public goto_homepage() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.goto_homepage, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

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
        Muserdata = FirebaseDatabase.getInstance().getReference().child("Users");
        Muserdata.keepSynced(true);
        read_user();


        drawerLayout = view.findViewById(R.id.MainDrawerID);
        mainnav = view.findViewById(R.id.MainNavID);

        menu_button.setOnClickListener(view1 -> drawerLayout.openDrawer(Gravity.LEFT));


        View navagation_view = mainnav.inflateHeaderView(R.layout.header_layout);
        final MaterialTextView username = navagation_view.findViewById(R.id.UserNamID);
        final MaterialTextView phonenumber = navagation_view.findViewById(R.id.UserEmailID);
        final CircleImageView profileimage = navagation_view.findViewById(R.id.ProfileIagesNav);

        Muserdata.child(Currentuserid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("profileimage")) {
                                String uri = dataSnapshot.child("profileimage").getValue().toString();
                                if (getContext() != null) {

                                    Picasso.with(getContext()).load(uri).resize(200, 200).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(profileimage, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(getContext()).load(uri).resize(200, 200).centerCrop().placeholder(R.drawable.profile_image_back).into(profileimage);

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
                        "بذریعہ   مستقیم ویلفر گروپ"+"\n"+sharebody;
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

                if (item.getItemId() == R.id.ChatID) {
                    if (chat_isactive) {

                        goto_chatpages(new Chatpages());
                        chat_isactive = false;
                        idcard_isactive = true;
                        jobis_active = true;
                        news_isactive = true;
                        global_isactive = true;
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
                    }
                }




                return true;
            }
        });


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
            transaction.replace(R.id.MainFream, fragment);
            transaction.commit();
        }
    }


    private void goto_newspage(Fragment fragment) {

        if(fragment != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.MainFream, fragment);
            transaction.commit();
        }
    }

    private void goto_jobspage(Fragment fragment) {

        if(fragment != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
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

        goto_usercard(new Chatpages());
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
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("hh:mm a");
        CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());


        Calendar calendar_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("yyyy-MM-dd");
        CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());


        Map<String, Object> onlinemap = new HashMap<String, Object>();
        onlinemap.put(DataManager.UserCardActive, online);
        onlinemap.put(DataManager.UserActiveTime, CurrentTime);
        onlinemap.put(DataManager.UserActiveDate, CurrentDate);

        Muserdata.child(Currentuserid).child(DataManager.UserOnlineRoot).updateChildren(onlinemap)
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