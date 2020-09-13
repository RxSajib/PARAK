package com.rakpak.pak_parak.BottomnavPage;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Contacts;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.rakpak.pak_parak.AddJobs.AddjobsPage;
import com.rakpak.pak_parak.Contact_bottomSheed.Contact_bottomsheed;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.Model.JobsIteams;
import com.rakpak.pak_parak.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JobPage extends Fragment {

    private FloatingActionButton addjobs;
    private RecyclerView joblist;
    private DatabaseReference Jobs_Ref;
    private String Current_Time, Current_Date;
    private DatabaseReference Muserdatabase;
    private String CurrentUserID;
    private FirebaseAuth Mauth;
    private RelativeLayout jobhave;

    public JobPage() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.job_page, container, false);

        jobhave = view.findViewById(R.id.Jobhave);
    //    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Muserdatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        /// todo current time
        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
        Current_Time = simpleDateFormat_time.format(calendar_time.getTime());
        /// todo current time

        /// todo current date
        Calendar calendar_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
        Current_Date = simpleDateFormat_date.format(calendar_date.getTime());
        /// todo current date

        joblist = view.findViewById(R.id.JobsListViewID);
        joblist.setHasFixedSize(true);
        joblist.setLayoutManager(new LinearLayoutManager(getActivity()));

        addjobs = view.findViewById(R.id.AddJobsID);
        addjobs.setColorFilter(Color.WHITE);
        addjobs.setOnClickListener(view1 -> goto_jobs_screen(new AddjobsPage()));
        Jobs_Ref = FirebaseDatabase.getInstance().getReference().child("Jobs");
        Jobs_Ref.keepSynced(true);


        get_jobspost();
   //     onlinecheack("online");

        return view;
    }

    private void goto_jobs_screen(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }
    }

    private void get_jobspost(){

        Query firebase_short = Jobs_Ref.orderByChild("short");

        FirebaseRecyclerAdapter<JobsIteams, JobHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<JobsIteams, JobHolder>(
                JobsIteams.class,
                R.layout.jobs_layout,
                JobHolder.class,
                firebase_short
        ) {
            @Override
            protected void populateViewHolder(JobHolder jobHolder, JobsIteams jobsIteams, int i) {
                String UID = getRef(i).getKey();
                Jobs_Ref.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    jobhave.setVisibility(View.GONE);
                                    if(dataSnapshot.hasChild("company_name")){
                                        String complanyname = dataSnapshot.child("company_name").getValue().toString();
                                        jobHolder.setCompanynameset(complanyname);
                                    }
                                    if(dataSnapshot.hasChild("job_descptrion")){
                                        String jobdetails = dataSnapshot.child("job_descptrion").getValue().toString();
                                        jobHolder.setJobdetailset(jobdetails);
                                    }
                                    if(dataSnapshot.hasChild("jobtitle")){
                                        String title = dataSnapshot.child("jobtitle").getValue().toString();
                                        jobHolder.setJobname_set(title);
                                    }
                                    if(dataSnapshot.hasChild("max_salary") || dataSnapshot.hasChild("min_salary")){
                                        String start_salary = dataSnapshot.child("min_salary").getValue().toString();
                                        String end_salary = dataSnapshot.child("max_salary").getValue().toString(); //min_salary

                                        jobHolder.setSalaryset(start_salary+" - "+end_salary);
                                    }
                                    if(dataSnapshot.hasChild("Current_Date")){
                                        String date = dataSnapshot.child("Current_Date").getValue().toString();
                                        if (date.equals(Current_Date)) {
                                            jobHolder.new_animaction.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            jobHolder.new_animaction.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                    if(dataSnapshot.hasChild("Current_Date")){
                                        String date = dataSnapshot.child("Current_Date").getValue().toString();
                                        if(date.equals(Current_Date)){
                                            jobHolder.PostTimeset("Post today");
                                        }
                                        else {
                                            jobHolder.PostTimeset(date);
                                        }
                                    }
                                    if(dataSnapshot.hasChild("work_time")){
                                        String work_time = dataSnapshot.child("work_time").getValue().toString();
                                        jobHolder.setEmploy_type_set(work_time);
                                    }

                                    jobHolder.jobapply_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Contact_bottomsheed bottomsheed = new Contact_bottomsheed(UID);
                                            bottomsheed.show(getActivity().getSupportFragmentManager(), "open");
                                        }
                                    });
                                }
                                else {
                                    jobhave.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };

        joblist.setAdapter(firebaseRecyclerAdapter);
    }

    public static class JobHolder extends RecyclerView.ViewHolder{

        private MaterialTextView companyname, jobname, employ_type, salary, jobdetails, postime;
        private View Mview;
        private Context context;
        private RelativeLayout jobapply_button;
        private LottieAnimationView new_animaction;

        public JobHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            context = Mview.getContext();
            companyname = Mview.findViewById(R.id.JobTitle);
            jobname = Mview.findViewById(R.id.Jobname);
            employ_type = Mview.findViewById(R.id.EmploymenttypeID);
            salary = Mview.findViewById(R.id.JobSalaryID);
            jobdetails = Mview.findViewById(R.id.JobDetailsID);
            jobapply_button = Mview.findViewById(R.id.JobApplyButtonID);
            new_animaction = Mview.findViewById(R.id.NewAnimaction);
            postime = Mview.findViewById(R.id.PostTime);
        }

        private void setCompanynameset(String c_nam){
            companyname.setText(c_nam);
        }
        private void setJobname_set(String job_nam){
            jobname.setText(job_nam);
        }
        private void setEmploy_type_set(String emp_type){
            employ_type.setText(emp_type);
        }
        private void setSalaryset(String salarydata){
            salary.setText(salarydata);
        }
        private void setJobdetailset(String jobdetailsetdata){
            jobdetails.setText(jobdetailsetdata);
        }
        private void PostTimeset(String tim){
            postime.setText(tim);
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
     //   onlinecheack("offline");
        super.onStop();
    }

    @Override
    public void onResume() {
      //  onlinecheack("online");
        super.onResume();
    }
}