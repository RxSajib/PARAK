package com.rakpak.pak_parak.BottomnavPage;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.rakpak.pak_parak.ChatPage.ChatPages;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.GlobalChat.GlobalChat;
import com.rakpak.pak_parak.Model.OnBoardingModal;
import com.rakpak.pak_parak.Model.UserIteamsList;
import com.rakpak.pak_parak.R;
import com.rakpak.pak_parak.Search.Search_Page;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import carbon.widget.Divider;
import de.hdodenhof.circleimageview.CircleImageView;

public class Chatpages extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference MuserDatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private FloatingActionButton search_button;
    private Animation animation;
    private RelativeLayout haveuser;
    private DatabaseReference OnlineData;

    private EditText user_search;


    public Chatpages() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chatpages, container, false);

        user_search = view.findViewById(R.id.UserSeachID);

        OnlineData = FirebaseDatabase.getInstance().getReference().child(DataManager.UserOnlineRoot);
        OnlineData.keepSynced(true);


        haveuser = view.findViewById(R.id.haveUser);

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.reciver_message_animaction_fatout);

        /// wait i have a call ok

        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        MuserDatabase.keepSynced(true);
        recyclerView = view.findViewById(R.id.ChatRecylrarViewID);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



        user_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input_text = editable.toString();
                if(input_text.isEmpty()){
                    load_alldata();
                }
                else {
                    searching_name(input_text);
                }
            }
        });

        return view;
    }



    private void load_alldata(){

        FirebaseRecyclerAdapter<UserIteamsList, ChatHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserIteamsList, ChatHolder>(
                UserIteamsList.class,
                R.layout.user_design,
                ChatHolder.class,
                MuserDatabase
        ) {
            @Override
            protected void populateViewHolder(final ChatHolder chatHolder, UserIteamsList userIteamsList, int i) {
                final String UID = getRef(i).getKey();

                MuserDatabase.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    /// todo start the code
                                    if(dataSnapshot.hasChild(DataManager.UserFullname)){
                                        if (dataSnapshot.hasChild("MYID")) {
                                            String myuid = dataSnapshot.child("MYID").getValue().toString();

                                            if (myuid.equals(CurrentUserID)) {
                                                chatHolder.mylayout.setVisibility(View.GONE);
                                            }
                                            else {


                                                //// todo code
                                                OnlineData.child(UID)
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.hasChild(DataManager.OnlineUseRoot)) {

                                                                    String online_status = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserCardActive).getValue().toString();
                                                                    if (online_status.equals("online")) {
                                                                        chatHolder.online_status_dot.setBackgroundResource(R.drawable.active_dot);
                                                                        chatHolder.onlinetime_date_status.setText("online now");
                                                                    } else if (online_status.equals("offline")) {
                                                                        chatHolder.online_status_dot.setBackgroundResource(R.drawable.inactive_dot);


                                                                        Calendar calendar_date = Calendar.getInstance();
                                                                        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("yyyy-MM-dd");
                                                                        String CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                                                                        String getonlinetime = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveTime).getValue().toString();
                                                                        String getoninedate = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveDate).getValue().toString();


                                                                        if (getoninedate.equals(CurrentDate)) {
                                                                            chatHolder.onlinetime_date_status.setText("Last online today: " + getonlinetime);
                                                                        } else {
                                                                            chatHolder.onlinetime_date_status.setText("Last online : " + getoninedate);
                                                                        }

                                                                    }


                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });


                                                haveuser.setVisibility(View.GONE);
                                                if (dataSnapshot.hasChild("profileimage")) {
                                                    String uri = dataSnapshot.child("profileimage").getValue().toString();
                                                    chatHolder.setProfileimageset(uri);
                                                }

                                                if (dataSnapshot.hasChild("Username")) {
                                                    String name = dataSnapshot.child("Username").getValue().toString();
                                                    chatHolder.setUsernameset(name);
                                                }


                                                if (dataSnapshot.hasChild(DataManager.UserOnlineRoot)) {
                                                    String online_status = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserCardActive).getValue().toString();
                                                    if (online_status.equals("online")) {
                                                        chatHolder.online_status_dot.setBackgroundResource(R.drawable.active_dot);
                                                        chatHolder.onlinetime_date_status.setText("online now");
                                                    } else if (online_status.equals("offline")) {
                                                        chatHolder.online_status_dot.setBackgroundResource(R.drawable.inactive_dot);


                                                        Calendar calendar_date = Calendar.getInstance();
                                                        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("yyyy-MM-dd");
                                                        String CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                                                        String getonlinetime = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveTime).getValue().toString();
                                                        String getoninedate = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveDate).getValue().toString();


                                                        if (getoninedate.equals(CurrentDate)) {
                                                            chatHolder.onlinetime_date_status.setText("Last online today: " + getonlinetime);
                                                        } else {
                                                            chatHolder.onlinetime_date_status.setText("Last online : " + getoninedate);
                                                        }

                                                    }
                                                }


                                                if (dataSnapshot.child("TypeStatus").hasChild(CurrentUserID)) {
                                                    String type = dataSnapshot.child("TypeStatus").child(CurrentUserID).child("type").getValue().toString();
                                                    if (type.equals("notype")) {
                                                        chatHolder.typingstatus.setText("Notype");

                                                    }
                                                    if (type.equals("typing ...")) {
                                                        chatHolder.typingstatus.setText("typing ...");
                                                    }
                                                }

                                                chatHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        String _id = dataSnapshot.child("MYID").getValue().toString();
                                                        gotochatPage(new ChatPages(), _id);

                                                    }
                                                });


                                            }


                                            //// todo code

                                        }
                                    }
                                    else {
                                        chatHolder.mylayout.setVisibility(View.GONE);
                                    }




                                    //// todo end of the code


                                } else {
                                    haveuser.setVisibility(View.VISIBLE);
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

    private void searching_name(String name){
        String tolowercase = name.toLowerCase();
        Query searchquery = MuserDatabase.orderByChild(DataManager.search).startAt(tolowercase).endAt(tolowercase + "\uf8ff");



        FirebaseRecyclerAdapter<UserIteamsList, ChatHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserIteamsList, ChatHolder>(
                UserIteamsList.class,
                R.layout.user_design,
                ChatHolder.class,
                searchquery
        ) {
            @Override
            protected void populateViewHolder(final ChatHolder chatHolder, UserIteamsList userIteamsList, int i) {
                final String UID = getRef(i).getKey();

                MuserDatabase.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    /// todo start the code
                                    if(dataSnapshot.hasChild(DataManager.UserFullname)){
                                        if (dataSnapshot.hasChild("MYID")) {
                                            String myuid = dataSnapshot.child("MYID").getValue().toString();

                                            if (myuid.equals(CurrentUserID)) {
                                                chatHolder.mylayout.setVisibility(View.GONE);
                                            }
                                            else {


                                                //// todo code
                                                OnlineData.child(UID)
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.hasChild(DataManager.OnlineUseRoot)) {

                                                                    String online_status = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserCardActive).getValue().toString();
                                                                    if (online_status.equals("online")) {
                                                                        chatHolder.online_status_dot.setBackgroundResource(R.drawable.active_dot);
                                                                        chatHolder.onlinetime_date_status.setText("online now");
                                                                    } else if (online_status.equals("offline")) {
                                                                        chatHolder.online_status_dot.setBackgroundResource(R.drawable.inactive_dot);


                                                                        Calendar calendar_date = Calendar.getInstance();
                                                                        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("yyyy-MM-dd");
                                                                        String CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                                                                        String getonlinetime = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveTime).getValue().toString();
                                                                        String getoninedate = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveDate).getValue().toString();


                                                                        if (getoninedate.equals(CurrentDate)) {
                                                                            chatHolder.onlinetime_date_status.setText("Last online today: " + getonlinetime);
                                                                        } else {
                                                                            chatHolder.onlinetime_date_status.setText("Last online : " + getoninedate);
                                                                        }

                                                                    }


                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });


                                                haveuser.setVisibility(View.GONE);
                                                if (dataSnapshot.hasChild("profileimage")) {
                                                    String uri = dataSnapshot.child("profileimage").getValue().toString();
                                                    chatHolder.setProfileimageset(uri);
                                                }

                                                if (dataSnapshot.hasChild("Username")) {
                                                    String name = dataSnapshot.child("Username").getValue().toString();
                                                    chatHolder.setUsernameset(name);
                                                }


                                                if (dataSnapshot.hasChild(DataManager.UserOnlineRoot)) {
                                                    String online_status = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserCardActive).getValue().toString();
                                                    if (online_status.equals("online")) {
                                                        chatHolder.online_status_dot.setBackgroundResource(R.drawable.active_dot);
                                                        chatHolder.onlinetime_date_status.setText("online now");
                                                    } else if (online_status.equals("offline")) {
                                                        chatHolder.online_status_dot.setBackgroundResource(R.drawable.inactive_dot);


                                                        Calendar calendar_date = Calendar.getInstance();
                                                        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("yyyy-MM-dd");
                                                        String CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                                                        String getonlinetime = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveTime).getValue().toString();
                                                        String getoninedate = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveDate).getValue().toString();


                                                        if (getoninedate.equals(CurrentDate)) {
                                                            chatHolder.onlinetime_date_status.setText("Last online today: " + getonlinetime);
                                                        } else {
                                                            chatHolder.onlinetime_date_status.setText("Last online : " + getoninedate);
                                                        }

                                                    }
                                                }


                                                if (dataSnapshot.child("TypeStatus").hasChild(CurrentUserID)) {
                                                    String type = dataSnapshot.child("TypeStatus").child(CurrentUserID).child("type").getValue().toString();
                                                    if (type.equals("notype")) {
                                                        chatHolder.typingstatus.setText("Notype");

                                                    }
                                                    if (type.equals("typing ...")) {
                                                        chatHolder.typingstatus.setText("typing ...");
                                                    }
                                                }

                                                chatHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        String _id = dataSnapshot.child("MYID").getValue().toString();
                                                        gotochatPage(new ChatPages(), _id);
                                                    }
                                                });


                                                //// todo code

                                            }
                                        }





                                        //// todo end of the code

                                    }
                                    else {
                                        chatHolder.mylayout.setVisibility(View.GONE);
                                    }



                                } else {
                                    haveuser.setVisibility(View.VISIBLE);
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


    @Override
    public void onStart() {


    FirebaseRecyclerAdapter<UserIteamsList, ChatHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserIteamsList, ChatHolder>(
                UserIteamsList.class,
                R.layout.user_design,
                ChatHolder.class,
                MuserDatabase
        ) {
            @Override
            protected void populateViewHolder(final ChatHolder chatHolder, UserIteamsList userIteamsList, int i) {
                final String UID = getRef(i).getKey();

                MuserDatabase.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    /// todo start the code
                                    if(dataSnapshot.hasChild(DataManager.UserFullname)){
                                        if (dataSnapshot.hasChild("MYID")) {
                                            String myuid = dataSnapshot.child("MYID").getValue().toString();

                                            if (myuid.equals(CurrentUserID)) {
                                                chatHolder.mylayout.setVisibility(View.GONE);
                                            }
                                            else {


                                                //// todo code
                                                OnlineData.child(UID)
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.hasChild(DataManager.OnlineUseRoot)) {

                                                                    String online_status = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserCardActive).getValue().toString();
                                                                    if (online_status.equals("online")) {
                                                                        chatHolder.online_status_dot.setBackgroundResource(R.drawable.active_dot);
                                                                        chatHolder.onlinetime_date_status.setText("online now");
                                                                    } else if (online_status.equals("offline")) {
                                                                        chatHolder.online_status_dot.setBackgroundResource(R.drawable.inactive_dot);


                                                                        Calendar calendar_date = Calendar.getInstance();
                                                                        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("yyyy-MM-dd");
                                                                        String CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                                                                        String getonlinetime = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveTime).getValue().toString();
                                                                        String getoninedate = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveDate).getValue().toString();


                                                                        if (getoninedate.equals(CurrentDate)) {
                                                                            chatHolder.onlinetime_date_status.setText("Last online today: " + getonlinetime);
                                                                        } else {
                                                                            chatHolder.onlinetime_date_status.setText("Last online : " + getoninedate);
                                                                        }

                                                                    }


                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });


                                                haveuser.setVisibility(View.GONE);
                                                if (dataSnapshot.hasChild("profileimage")) {
                                                    String uri = dataSnapshot.child("profileimage").getValue().toString();
                                                    chatHolder.setProfileimageset(uri);
                                                }

                                                if (dataSnapshot.hasChild("Username")) {
                                                    String name = dataSnapshot.child("Username").getValue().toString();
                                                    chatHolder.setUsernameset(name);
                                                }


                                                if (dataSnapshot.hasChild(DataManager.UserOnlineRoot)) {
                                                    String online_status = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserCardActive).getValue().toString();
                                                    if (online_status.equals("online")) {
                                                        chatHolder.online_status_dot.setBackgroundResource(R.drawable.active_dot);
                                                        chatHolder.onlinetime_date_status.setText("online now");
                                                    } else if (online_status.equals("offline")) {
                                                        chatHolder.online_status_dot.setBackgroundResource(R.drawable.inactive_dot);


                                                        Calendar calendar_date = Calendar.getInstance();
                                                        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("yyyy-MM-dd");
                                                        String CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                                                        String getonlinetime = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveTime).getValue().toString();
                                                        String getoninedate = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserActiveDate).getValue().toString();


                                                        if (getoninedate.equals(CurrentDate)) {
                                                            chatHolder.onlinetime_date_status.setText("Last online today: " + getonlinetime);
                                                        } else {
                                                            chatHolder.onlinetime_date_status.setText("Last online : " + getoninedate);
                                                        }

                                                    }
                                                }


                                                if (dataSnapshot.child("TypeStatus").hasChild(CurrentUserID)) {
                                                    String type = dataSnapshot.child("TypeStatus").child(CurrentUserID).child("type").getValue().toString();
                                                    if (type.equals("notype")) {
                                                        chatHolder.typingstatus.setText("Notype");

                                                    }
                                                    if (type.equals("typing ...")) {
                                                        chatHolder.typingstatus.setText("typing ...");
                                                    }
                                                }

                                                chatHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        String _id = dataSnapshot.child("MYID").getValue().toString();
                                                        gotochatPage(new ChatPages(), _id);
                                                    }
                                                });


                                                //// todo code

                                            }
                                        }





                                    }
                                    else {
                                        chatHolder.mylayout.setVisibility(View.GONE);
                                    }


                                    //// todo end of the code


                                } else {
                                    haveuser.setVisibility(View.VISIBLE);
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

    public static class ChatHolder extends RecyclerView.ViewHolder {

        private Context context;
        private View Mview;
        private CircleImageView profileimage;
        private MaterialTextView username;
        private MaterialTextView typingstatus;
        private RelativeLayout mylayout;

        private RelativeLayout online_status_dot;
        private MaterialTextView onlinetime_date_status;

        public ChatHolder(@NonNull View itemView) {
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

        public void setProfileimageset(String img) {

            if (context != null) {

                Picasso.with(context).load(img).resize(200, 200).centerCrop().into(profileimage);

            }

        }

        public void setUsernameset(String nam) {
            username.setText(nam);
        }

        public void setlastmessage(String lmess) {
            typingstatus.setText(lmess);
        }
    }

    private void gotochatPage(Fragment fragment, String UID) {

        if (fragment != null) {

            Bundle bundle = new Bundle();
            bundle.putString("UID", UID);
            fragment.setArguments(bundle);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right, R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }

    }

    private void goto_search_screen(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right, R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }
    }


    private void onlinecheack(String online) {

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
                        if (task.isSuccessful()) {

                        } else {
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




}