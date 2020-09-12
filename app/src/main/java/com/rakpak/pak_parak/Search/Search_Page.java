package com.rakpak.pak_parak.Search;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rakpak.pak_parak.BottomnavPage.Chatpages;
import com.rakpak.pak_parak.ChatPage.ChatPages;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.Model.UserIteamsList;
import com.rakpak.pak_parak.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Search_Page extends Fragment {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference MuserDatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private FloatingActionButton search_button;
    private Animation animation;
    private RelativeLayout backbutton;


    public Search_Page() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search__page, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        backbutton = view.findViewById(R.id.backButton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        toolbar = view.findViewById(R.id.SearchToolvarID);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        // Available Chat
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        /*((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);
*/
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView = view.findViewById(R.id.ChatRecylrarViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setHasOptionsMenu(true);


        onlinecheack("online");
        return view;
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.SearchID).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newtext) {
                search_iteams(newtext);
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newtext) {
                search_iteams(newtext);
                return true;

            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public void onStart() {

        FirebaseRecyclerAdapter<UserIteamsList, ChatSearchHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserIteamsList, ChatSearchHolder>(
                UserIteamsList.class,
                R.layout.user_design,
                ChatSearchHolder.class,
                MuserDatabase
        ) {
            @Override
            protected void populateViewHolder(final ChatSearchHolder chatSearchHolder, UserIteamsList userIteamsList, int i) {
                    String UID = getRef(i).getKey();
                    MuserDatabase.child(UID)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        if(dataSnapshot.hasChild("Username")){
                                            String nam = dataSnapshot.child("Username").getValue().toString();
                                            chatSearchHolder.setUsernameset(nam);
                                        }
                                        if(dataSnapshot.hasChild("profileimage")){
                                            String uri = dataSnapshot.child("profileimage").getValue().toString();
                                            chatSearchHolder.setProfileimageset(uri);
                                        }
                                        if(dataSnapshot.hasChild("MYID")){
                                            String myid = dataSnapshot.child("MYID").getValue().toString();
                                            if(myid.equals(CurrentUserID)){
                                                chatSearchHolder.mylayout.setVisibility(View.GONE);
                                            }
                                        }

                                        if(dataSnapshot.hasChild(DataManager.UserOnlineRoot)){
                                            String online_status = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserCardActive).getValue().toString();
                                            if(online_status.equals("online")){
                                                chatSearchHolder.online_status_dot.setBackgroundResource(R.drawable.active_dot);
                                                chatSearchHolder.onlinetime_date_status.setText("online now");
                                            }
                                            else if(online_status.equals("offline")){
                                                chatSearchHolder.online_status_dot.setBackgroundResource(R.drawable.inactive_dot);


                                                Calendar calendar_date = Calendar.getInstance();
                                                SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("yyyy-MM-dd");
                                                String CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                                                String getonlinetime = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveTime).getValue().toString();
                                                String getoninedate = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveDate).getValue().toString();


                                                if(getoninedate.equals(CurrentDate)){
                                                    chatSearchHolder.onlinetime_date_status.setText("Last online today: "+getonlinetime);
                                                }
                                                else {
                                                    chatSearchHolder.onlinetime_date_status.setText("Last online : "+getoninedate);
                                                }

                                            }
                                        }


                                        chatSearchHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String _id = dataSnapshot.child("MYID").getValue().toString();
                                                gotochatPage(new ChatPages(), _id);
                                            }
                                        });

                                    }
                                    else {

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        super.onStart();
    }

    public static class ChatSearchHolder extends RecyclerView.ViewHolder{

        private Context context;
        private View Mview;
        private CircleImageView profileimage;
        private MaterialTextView username;
        private MaterialTextView typingstatus;
        private RelativeLayout mylayout;

        private RelativeLayout online_status_dot;
        private MaterialTextView onlinetime_date_status;


        public ChatSearchHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            context = Mview.getContext();
            profileimage = Mview.findViewById(R.id.UserProfileImageID);
            username = Mview.findViewById(R.id.UserNameTextID);
            typingstatus = Mview.findViewById(R.id.UserTypingStatus);
            mylayout = Mview.findViewById(R.id.LayoutID);
            online_status_dot = Mview.findViewById(R.id.OnlineDot);
            onlinetime_date_status = Mview.findViewById(R.id.OnlineTime);

        }

        private void setProfileimageset(String img){
            Picasso.with(context).load(img).resize(200, 200).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_image_back).into(profileimage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(img).resize(200,200).centerCrop().placeholder(R.drawable.profile_image_back).into(profileimage);
                }
            });
        }

        private void setUsernameset(String nam){
            username.setText(nam);
        }
    }


    private void gotochatPage(Fragment fragment, String UID){

        if(fragment != null){

            Bundle bundle = new Bundle();
            bundle.putString("UID", UID);
            fragment.setArguments(bundle);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }

    }

    private void search_iteams(String mysearch){
        String query = mysearch.toLowerCase();
        final Query firebaseQry = MuserDatabase.orderByChild("search").startAt(query).endAt(query + "\uf8ff");



        FirebaseRecyclerAdapter<UserIteamsList, ChatSearchHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserIteamsList, ChatSearchHolder>(
                UserIteamsList.class,
                R.layout.user_design,
                ChatSearchHolder.class,
                firebaseQry
        ) {
            @Override
            protected void populateViewHolder(final ChatSearchHolder chatSearchHolder, UserIteamsList userIteamsList, int i) {
                String UID = getRef(i).getKey();
                MuserDatabase.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    if(dataSnapshot.hasChild("Username")){
                                        String nam = dataSnapshot.child("Username").getValue().toString();
                                        chatSearchHolder.setUsernameset(nam);
                                    }
                                    if(dataSnapshot.hasChild("profileimage")){
                                        String uri = dataSnapshot.child("profileimage").getValue().toString();
                                        chatSearchHolder.setProfileimageset(uri);
                                    }
                                    if(dataSnapshot.hasChild("MYID")){
                                        String myid = dataSnapshot.child("MYID").getValue().toString();
                                        if(myid.equals(CurrentUserID)){
                                            chatSearchHolder.mylayout.setVisibility(View.GONE);
                                        }
                                    }

                                    if(dataSnapshot.hasChild(DataManager.UserOnlineRoot)){
                                        String online_status = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserCardActive).getValue().toString();
                                        if(online_status.equals("online")){
                                            chatSearchHolder.online_status_dot.setBackgroundResource(R.drawable.active_dot);
                                            chatSearchHolder.onlinetime_date_status.setText("online now");
                                        }
                                        else if(online_status.equals("offline")){
                                            chatSearchHolder.online_status_dot.setBackgroundResource(R.drawable.inactive_dot);


                                            Calendar calendar_date = Calendar.getInstance();
                                            SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("yyyy-MM-dd");
                                            String CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                                            String getonlinetime = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveTime).getValue().toString();
                                            String getoninedate = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveDate).getValue().toString();


                                            if(getoninedate.equals(CurrentDate)){
                                                chatSearchHolder.onlinetime_date_status.setText("Last online today: "+getonlinetime);
                                            }
                                            else {
                                                chatSearchHolder.onlinetime_date_status.setText("Last online : "+getoninedate);
                                            }

                                        }
                                    }



                                    chatSearchHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String _id = dataSnapshot.child("MYID").getValue().toString();
                                            gotochatPage(new ChatPages(), _id);
                                        }
                                    });

                                }
                                else {

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

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

        MuserDatabase.child(CurrentUserID).child(DataManager.UserOnlineRoot).updateChildren(onlinemap)
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