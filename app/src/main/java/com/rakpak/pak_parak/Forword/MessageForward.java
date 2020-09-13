package com.rakpak.pak_parak.Forword;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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

public class MessageForward extends Fragment {

    private RecyclerView recyclerViewlist;
    private DatabaseReference MuserDatabase, Message_database;
    private RelativeLayout haveuser;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private FloatingActionButton sendbutton;
    private DatabaseReference OnlineData;

    public MessageForward() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.message_forward, container, false);

        OnlineData  =FirebaseDatabase.getInstance().getReference().child(DataManager.UserOnlineRoot);
        OnlineData.keepSynced(true);

        Message_database = FirebaseDatabase.getInstance().getReference();
        sendbutton = view.findViewById(R.id.ForwordButtonID);
        sendbutton.setVisibility(View.GONE);
        sendbutton.setColorFilter(Color.WHITE);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();
        haveuser = view.findViewById(R.id.haveUser);
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        MuserDatabase.keepSynced(true);



        /// todo internet connection dioloag
        ConnectivityManager cm =(ConnectivityManager)getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if(isconnected){

            ///open anythings
        }
        else {
            final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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

        }


        /// todo internet connection dialoag





        recyclerViewlist = view.findViewById(R.id.ForwardViewID);
        recyclerViewlist.setHasFixedSize(true);
        recyclerViewlist.setLayoutManager(new LinearLayoutManager(getActivity()));

        read_data();

        readuser_data();

        return view;
    }


    private void read_data(){

    }

    public void readuser_data() {

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
                                if(dataSnapshot.exists()){
                                    haveuser.setVisibility(View.GONE);
                                    if(dataSnapshot.hasChild("profileimage")){
                                        String uri = dataSnapshot.child("profileimage").getValue().toString();
                                        chatHolder.setProfileimageset(uri);
                                    }

                                    if(dataSnapshot.hasChild("Username")){
                                        String name = dataSnapshot.child("Username").getValue().toString();
                                        chatHolder.setUsernameset(name);
                                    }

                                    if(dataSnapshot.hasChild("MYID")){
                                        String myuid = dataSnapshot.child("MYID").getValue().toString();

                                        if(myuid.equals(CurrentUserID)){
                                            chatHolder.mylayout.setVisibility(View.GONE);
                                        }
                                    }





                                    chatHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            boolean selected = true;


                                            String position = getRef(i).getKey();


                                            Bundle bundle = getArguments();
                                            String Imageuri = bundle.getString(DataManager.ForwardImage);

                                            if(!Imageuri.isEmpty()){
                                                sendbutton.setVisibility(View.VISIBLE);

                                                send_message_touser(UID, Imageuri);


                                            }


                                            if(position.equals(chatHolder.Mview)){
                                                chatHolder.chatview.setBackgroundResource(R.drawable.forward_deslelect);
                                                selected = false;



                                            }
                                            else if(!position.equals(chatHolder.Mview)) {
                                                chatHolder.chatview.setBackgroundResource(R.drawable.forward_selection);
                                                selected = true;
                                            }



                                          /*  String _id = dataSnapshot.child("MYID").getValue().toString();
                                            gotochatPage(new ChatPages(), _id);*/
                                        }
                                    });

                                }
                                else {
                                    haveuser.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };

        recyclerViewlist.setAdapter(firebaseRecyclerAdapter);

    }

    public static class ChatHolder extends RecyclerView.ViewHolder{

        private Context context;
        private View Mview;
        private CircleImageView profileimage;
        private MaterialTextView username;
        private MaterialTextView typingstatus;
        private RelativeLayout mylayout;

        private RelativeLayout online_status_dot;
        private MaterialTextView onlinetime_date_status;
        private RelativeLayout chatview;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);


            Mview = itemView;
            chatview = Mview.findViewById(R.id.Rootlayout);
            context = Mview.getContext();
            profileimage = Mview.findViewById(R.id.UserProfileImageID);
            username = Mview.findViewById(R.id.UserNameTextID);
            typingstatus = Mview.findViewById(R.id.UserTypingStatus);
            mylayout = Mview.findViewById(R.id.LayoutID);

            online_status_dot = Mview.findViewById(R.id.OnlineDot);
            onlinetime_date_status = Mview.findViewById(R.id.OnlineTime);
        }

        public void setProfileimageset(String img){

            if(context != null){

                Picasso.with(context).load(img).resize(200, 200).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(profileimage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context).load(img).resize(200, 200).centerCrop().into(profileimage);
                    }
                });


            }

        }

        public void setUsernameset(String nam){
            username.setText(nam);
        }

        public void setlastmessage(String lmess){
            typingstatus.setText(lmess);
        }
    }


    /// todo forwar send user
    private void send_message_touser(String  ReciverUID, String imageuri){

        String SenderMessageRef = "Message/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + ReciverUID;

        DatabaseReference MessagePushID = Message_database.child("Message").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(ReciverUID).push();

        String usermaessageid = MessagePushID.getKey();

        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
       String CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());

        Calendar calendar_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
       String CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());


        Map<String, Object> message_map = new HashMap<String, Object>();
        message_map.put("Message", imageuri);
        message_map.put("Type", "ForwardImage");
        message_map.put("From", FirebaseAuth.getInstance().getCurrentUser().getUid());
        message_map.put("Time", CurrentTime);
        message_map.put("Date", CurrentDate);


        Map<String, Object> message_body = new HashMap<String, Object>();
        message_body.put(SenderMessageRef + "/" + usermaessageid, message_map);



        Message_database.updateChildren(message_body)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                         //   Muser_database.child(ReciverUID).child(ReciverUID).child("last_message_reciver").setValue(Message);
                            //  Muser_database.child(SenderID).child("last_message_sender").setValue(Message);
                         //   find_userand_sendnotifaction();

                            Toast.makeText(getActivity(), "Send success", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
    /// todo forwar send user

}