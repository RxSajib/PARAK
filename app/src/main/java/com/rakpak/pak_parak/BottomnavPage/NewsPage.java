package com.rakpak.pak_parak.BottomnavPage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
import com.rakpak.pak_parak.Model.NewsIteams;
import com.rakpak.pak_parak.NewsFullImage.NewsFullimage;
import com.rakpak.pak_parak.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewsPage extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference MnewsDatabase;
    private String CurrentDate;
    private DatabaseReference Muserdatabase;
    private String CurrentUserID;
    private FirebaseAuth Mauth;
    private RelativeLayout news_have;

    public NewsPage() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.news_page, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        news_have = view.findViewById(R.id.NewsHave);
        Muserdatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
        CurrentDate = simpleDateFormat_time.format(calendar_time.getTime());

        MnewsDatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.NewsRoot);
        MnewsDatabase.keepSynced(true);
        recyclerView = view.findViewById(R.id.NewsRecylerViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    //    onlinecheack("online");
        return view;
    }

    @Override
    public void onStart() {

        Query firebasequery = MnewsDatabase.orderByChild("short");

        FirebaseRecyclerAdapter<NewsIteams, NewsHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NewsIteams, NewsHolder>(
                NewsIteams.class,
                R.layout.news_banner,
                NewsHolder.class,
                firebasequery
        ) {
            @Override
            protected void populateViewHolder(final NewsHolder newsHolder, NewsIteams newsIteams, int i) {

                String UID = getRef(i).getKey();

                MnewsDatabase.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()){
                                    news_have.setVisibility(View.GONE);
                                    if(dataSnapshot.hasChild(DataManager.JobVisiable)){



                                        String job_isvisiable = dataSnapshot.child(DataManager.JobVisiable).getValue().toString();

                                        if(job_isvisiable.equals(DataManager.Visiable)){
                                            if(dataSnapshot.hasChild("jobimage")){
                                                String imageuri = dataSnapshot.child("jobimage").getValue().toString();
                                                newsHolder.setNewsimageset(imageuri);
                                            }

                                            if(dataSnapshot.hasChild("message")){
                                                String messagetext = dataSnapshot.child("message").getValue().toString();
                                                newsHolder.setMessageset(messagetext);
                                            }

                                            if(dataSnapshot.hasChild("CurrentDate") || dataSnapshot.hasChild("CurrentTime")){

                                                //     String time = dataSnapshot.child("CurrentTime").getValue().toString();
                                                String date = dataSnapshot.child("CurrentDate").getValue().toString();

                                                if(date.equals(CurrentDate)){
                                                    newsHolder.NewLottiFile.setVisibility(View.VISIBLE);
                                                    newsHolder.time_date.setText("Post Today");
                                                    newsHolder.time_date.setTextColor(getResources().getColor(R.color.red));
                                                }
                                                else {
                                                    newsHolder.NewLottiFile.setVisibility(View.INVISIBLE);
                                                    newsHolder.time_date.setText(date);
                                                }

                                            }

                                            if(dataSnapshot.hasChild(DataManager.NewsWebsite)){
                                                String link = dataSnapshot.child(DataManager.NewsWebsite).getValue().toString();
                                                newsHolder.url.setText(DataManager.HTTPS+link);

                                               newsHolder.url.setPaintFlags(newsHolder.url.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                            }
                                            else {
                                                newsHolder.url.setVisibility(View.GONE);//look link as person click should work ok no long press
                                            }
                                        }
                                        //look please first complete all the point ok for one point you tell and thats also not 1005 why long click just click goesto link

                                        else if(job_isvisiable.equals(DataManager.InVisiable)){
                                            newsHolder.job_layout.setVisibility(View.GONE);
                                        }
                                    }


                                    newsHolder.url.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(dataSnapshot.hasChild(DataManager.NewsWebsite)){
                                                String link = dataSnapshot.child(DataManager.NewsWebsite).getValue().toString();

                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DataManager.HTTPS+link));
                                                startActivity(intent);

                                            }
                                        }
                                    });

                                    newsHolder.newsimage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            gotofullscreen_image(new NewsFullimage(), UID);
                                        }
                                    });

                                }
                                else {
                                    news_have.setVisibility(View.VISIBLE);
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

    public static class NewsHolder extends RecyclerView.ViewHolder{

        private Context context;
        private View Mview;
        private MaterialTextView time_date, message;
        private ImageView newsimage;
        // NewLottiFile
        private LottieAnimationView NewLottiFile;
        private RelativeLayout job_layout;
        private MaterialTextView url;

        public NewsHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            context = Mview.getContext();
            time_date = Mview.findViewById(R.id.NewsTimeAndDate);
            message = Mview.findViewById(R.id.NewsMessage);
            newsimage = Mview.findViewById(R.id.NewsImage);
            NewLottiFile = Mview.findViewById(R.id.NewLottiFile);
            job_layout = Mview.findViewById(R.id.Newslayout);
            url = Mview.findViewById(R.id.NewsUri);
        }

        private void setTime_dateset(String time){
            time_date.setText(time);
        }

        private void setMessageset(String mess){
            message.setText(mess);
        }

        private void setNewsimageset(String image){

            Picasso.with(context).load(image).placeholder(R.drawable.image_background).networkPolicy(NetworkPolicy.OFFLINE).into(newsimage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).placeholder(R.drawable.image_background).into(newsimage);

                }
            });
        }
    }



    private void onlinecheack(String online){

        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
        String CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());


        Calendar calendar_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
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

    private void gotofullscreen_image(Fragment fragment, String UID){
        if(fragment != null){

            Bundle bundle = new Bundle();
            bundle.putString(DataManager.IntentNewsData, UID);
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
        }
    }
}