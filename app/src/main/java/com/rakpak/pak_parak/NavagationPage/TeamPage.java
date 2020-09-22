package com.rakpak.pak_parak.NavagationPage;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.Model.TeamModel;
import com.rakpak.pak_parak.R;
import com.rakpak.pak_parak.ViewHolder.TeamViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeamPage extends Fragment {

    private RelativeLayout back_button;
    private RecyclerView listview;
    private DatabaseReference MTeamDatabase;
    private static final int SPAN = 2;
    private LinearLayout nodata;
    private DatabaseReference Muserdatabase;
    private String CurrentUserID;
    private FirebaseAuth Mauth;
    private DatabaseReference OnlieData;

    public TeamPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.team_page, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        OnlieData = FirebaseDatabase.getInstance().getReference().child(DataManager.UserOnlineRoot);

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
            alertDialog.show();

        }


        /// todo internet connection dialoag






        Muserdatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        nodata = view.findViewById(R.id.NoIteamsView);
        MTeamDatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.TeamDatabaseRoot);
        MTeamDatabase.keepSynced(true);
        listview = view.findViewById(R.id.TeamListView);
        listview.setHasFixedSize(true);

        back_button = view.findViewById(R.id.BackButtonID);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        onlinecheack("online");
        return view;
    }

    @Override
    public void onStart() {

        FirebaseRecyclerAdapter<TeamModel, TeamViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TeamModel, TeamViewHolder>(
                TeamModel.class,
                R.layout.team_layout,
                TeamViewHolder.class,
                MTeamDatabase
        ) {
            @Override
            protected void populateViewHolder(TeamViewHolder teamViewHolder, TeamModel teamModel, int i) {

                String uid = getRef(i).getKey();
                MTeamDatabase.child(uid)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    nodata.setVisibility(View.GONE);
                                    if(dataSnapshot.hasChild(DataManager.TeamName)){
                                        String name = dataSnapshot.child(DataManager.TeamName).getValue().toString();
                                        teamViewHolder.setUsernameset(name);
                                    }
                                    if(dataSnapshot.hasChild(DataManager.TeamURI)){
                                        String uri = dataSnapshot.child(DataManager.TeamURI).getValue().toString();
                                        teamViewHolder.setProfileimageset(uri);
                                    }
                                    if(dataSnapshot.hasChild(DataManager.TeamDesignation)){
                                        String des = dataSnapshot.child(DataManager.TeamDesignation).getValue().toString();
                                        teamViewHolder.setDegnictionset(des);
                                    }
                                }
                                else {
                                    nodata.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), SPAN);
        listview.setLayoutManager(gridLayoutManager);
        listview.setAdapter(firebaseRecyclerAdapter);

        super.onStart();
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

        OnlieData.child(CurrentUserID).child(DataManager.UserOnlineRoot).updateChildren(onlinemap)
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