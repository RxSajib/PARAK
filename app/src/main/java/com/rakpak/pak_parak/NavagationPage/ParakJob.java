package com.rakpak.pak_parak.NavagationPage;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.Model.JobList;
import com.rakpak.pak_parak.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class ParakJob extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference MjobRoot;
    private RelativeLayout backbutton;
    private int childcount, childnegative;
    private DatabaseReference Muserdatabase;
    private String CurrentUserID;
    private FirebaseAuth Mauth;

    private LinearLayout nodata;

    public ParakJob() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.parak_job, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Muserdatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        nodata = view.findViewById(R.id.NoDataContiner);
        MjobRoot = FirebaseDatabase.getInstance().getReference().child(DataManager.JobApplicationRoot);
        MjobRoot.keepSynced(true);
        backbutton = view.findViewById(R.id.backbuttons);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });



        recyclerView = view.findViewById(R.id.JobsReclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        onlinecheack("online");
        return view;
    }


    @Override
    public void onStart() {

        Query firebaseshort_qury = MjobRoot.orderByChild(DataManager.JobApplicantShort);

        FirebaseRecyclerAdapter<JobList, JobViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<JobList, JobViewHolder>(
                JobList.class,
                R.layout.job_banner,
                JobViewHolder.class,
                firebaseshort_qury
        ) {
            @Override
            protected void populateViewHolder(JobViewHolder jobViewHolder, JobList jobList, int i) {
                String UID = getRef(i).getKey();
                MjobRoot.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    nodata.setVisibility(View.INVISIBLE);
                                    if(dataSnapshot.hasChild(DataManager.JobApplicantFirstName) || dataSnapshot.hasChild(DataManager.JobApplicantLastName)) {
                                        String firstnametext = dataSnapshot.child(DataManager.JobApplicantFirstName).getValue().toString();
                                        String lastnametext = dataSnapshot.child(DataManager.JobApplicantLastName).getValue().toString();

                                        jobViewHolder.setFullnameset(firstnametext+" "+lastnametext);
                                    }
                                    if(dataSnapshot.hasChild(DataManager.JobApplicantGoals)){
                                        String goealtext = dataSnapshot.child(DataManager.JobApplicantGoals).getValue().toString();

                                        jobViewHolder.setApplicantgoealset(goealtext);
                                    }

                                    if(dataSnapshot.hasChild(DataManager.JobApplicantDate)){
                                        String date = dataSnapshot.child(DataManager.JobApplicantDate).getValue().toString();

                                        jobViewHolder.setDateset(date);
                                    }

                                    jobViewHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            goto_jobdetailspage(new JOBApplication_Details(), UID);
                                        }
                                    });
                                }
                                else {
                                    nodata.setVisibility(View.GONE);
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

    public static class JobViewHolder extends RecyclerView.ViewHolder{

        private View Mview;
        private Context context;
        private MaterialTextView fullname, date, applicantgoeal;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            context = Mview.getContext();
            fullname = Mview.findViewById(R.id.ApplicantFullname);
            date = Mview.findViewById(R.id.CurrentDate);
            applicantgoeal = Mview.findViewById(R.id.ApplicantGoel);
        }

        public void setFullnameset(String name){
            fullname.setText(name);
        }
        public void setDateset(String det){
            date.setText(det);
        }
        public void setApplicantgoealset(String applicationgoeal){
            applicantgoeal.setText(applicationgoeal);
        }
    }

    private void goto_jobdetailspage(Fragment fragment, String myuid){
        if(fragment != null){

            Bundle bundle = new Bundle();
            bundle.putString("UID", myuid);
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }
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

        Muserdatabase.child(CurrentUserID).child(DataManager.UserOnlineRoot).updateChildren(onlinemap)
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