package com.rakpak.pak_parak.BottomnavPage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rakpak.pak_parak.BottomSheed.AuidoButtomSheed;
import com.rakpak.pak_parak.ChatPage.ChatPages;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.Model.HistoryModel;
import com.rakpak.pak_parak.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


public class NotifactionPages extends Fragment {

    private FloatingActionButton delatebutton;
    private RecyclerView notifactionview;
    private DatabaseReference MnotifactionData;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private String CurrentTime;
    private FloatingActionButton removebutton;
    private ProgressDialog Mprogress;
    private RelativeLayout history;

    public NotifactionPages() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.notifaction_pages, container, false);
        Mprogress = new ProgressDialog(getActivity());


        history = view.findViewById(R.id.History);

        delatebutton = view.findViewById(R.id.RemoveButton);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();
        MnotifactionData = FirebaseDatabase.getInstance().getReference().child(DataManager.My_HistoryRoot).child(CurrentUserID);
        MnotifactionData.keepSynced(true);



        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
        CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());




        delatebutton.setColorFilter(Color.WHITE);
        delatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        notifactionview = view.findViewById(R.id.NotifactionViewID);
        notifactionview.setHasFixedSize(true);
        notifactionview.setLayoutManager(new LinearLayoutManager(getActivity()));



        

        delatebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Mprogress.setTitle("Please wait ...");
                Mprogress.setMessage("Removing all history");
                Mprogress.setCanceledOnTouchOutside(false);
                Mprogress.show();
                MnotifactionData.removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Mprogress.dismiss();
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
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view;
    }


    @Override
    public void onStart() {

    Query DESQUERY = MnotifactionData.orderByChild(DataManager.NotifactionShort);

        FirebaseRecyclerAdapter<HistoryModel, NotifactionHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<HistoryModel, NotifactionHolder>(
                HistoryModel.class,
                R.layout.mess_notifaction,
                NotifactionHolder.class,
                DESQUERY
        ) {
            @Override
            protected void populateViewHolder(NotifactionHolder notifactionHolder, HistoryModel notifactionModal, int i) {
                String UID = getRef(i).getKey();

                MnotifactionData.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()) {

                                    if (dataSnapshot.hasChild(DataManager.Type)) {
                                        String type = dataSnapshot.child(DataManager.Type).getValue().toString();


                                        if (type.equals(DataManager.Text)) {
                                            notifactionHolder.textbox.setVisibility(View.VISIBLE);
                                            notifactionHolder.imagebox.setVisibility(View.GONE);
                                            notifactionHolder.pdfbox.setVisibility(View.GONE);
                                            notifactionHolder.audiobox.setVisibility(View.GONE);

                                            if (dataSnapshot.hasChild(DataManager.Notifactionname)) {
                                                String name = dataSnapshot.child(DataManager.Notifactionname).getValue().toString();
                                                notifactionHolder.setNotifactionusernameset(name);
                                            }
                                            if (dataSnapshot.hasChild(DataManager.NotifactionProfileUrl)) {
                                                String uri = dataSnapshot.child(DataManager.NotifactionProfileUrl).getValue().toString();
                                                notifactionHolder.notifactionimageset(uri);
                                            }
                                            if (dataSnapshot.hasChild(DataManager.NotifactionTextBody)) {
                                                String message = dataSnapshot.child(DataManager.NotifactionTextBody).getValue().toString();
                                                notifactionHolder.setnotifactionmessage(message);
                                            }
                                            if (dataSnapshot.hasChild(DataManager.NotifactionTime) || dataSnapshot.hasChild(DataManager.NotifactionDate)) {
                                                String time = dataSnapshot.child(DataManager.NotifactionTime).getValue().toString();
                                                String date = dataSnapshot.child(DataManager.NotifactionDate).getValue().toString();
                                                if (time.equals(CurrentTime)) {
                                                    notifactionHolder.time.setText("Today");
                                                    notifactionHolder.time.setTextColor(Color.RED);
                                                } else {
                                                    notifactionHolder.setnotifaction_time(date);
                                                    notifactionHolder.time.setTextColor(Color.GRAY);
                                                }
                                            }

                                            if (dataSnapshot.hasChild(DataManager.NotifactionSenderID)) {
                                                String id = dataSnapshot.child(DataManager.NotifactionSenderID).getValue().toString();

                                                if (!id.isEmpty()) {
                                                    notifactionHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            goto_chat_page(new ChatPages(), id);

                                                        }
                                                    });
                                                }

                                            }
                                        }
                                        if (type.equals(DataManager.Image)) {
                                            notifactionHolder.imagebox.setVisibility(View.VISIBLE);
                                            notifactionHolder.textbox.setVisibility(View.GONE);
                                            notifactionHolder.pdfbox.setVisibility(View.GONE);
                                            notifactionHolder.audiobox.setVisibility(View.GONE);


                                            if (dataSnapshot.hasChild(DataManager.NotifactionTextBody)) {
                                                String uri = dataSnapshot.child(DataManager.NotifactionTextBody).getValue().toString();
                                                notifactionHolder.setsernderimage(uri);
                                            }

                                            if (dataSnapshot.hasChild(DataManager.NotifactionProfileUrl)) {
                                                String uri = dataSnapshot.child(DataManager.NotifactionProfileUrl).getValue().toString();
                                                notifactionHolder.set_imageprofile(uri);
                                            }

                                            if (dataSnapshot.hasChild(DataManager.Notifactionname)) {
                                                String name = dataSnapshot.child(DataManager.Notifactionname).getValue().toString();
                                                notifactionHolder.setimageusername(name);
                                            }

                                            if (dataSnapshot.hasChild(DataManager.NotifactionTime) || dataSnapshot.hasChild(DataManager.NotifactionDate)) {
                                                String time = dataSnapshot.child(DataManager.NotifactionTime).getValue().toString();
                                                String date = dataSnapshot.child(DataManager.NotifactionDate).getValue().toString();
                                                if (time.equals(CurrentTime)) {
                                                    notifactionHolder.imagetime.setText("Today");
                                                    notifactionHolder.imagetime.setTextColor(Color.RED);
                                                } else {
                                                    notifactionHolder.setimagesenderTime(date);
                                                    notifactionHolder.imagetime.setTextColor(Color.GRAY);
                                                }
                                            }

                                            if (dataSnapshot.hasChild(DataManager.NotifactionSenderID)) {
                                                String id = dataSnapshot.child(DataManager.NotifactionSenderID).getValue().toString();

                                                if (!id.isEmpty()) {
                                                    notifactionHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            goto_chat_page(new ChatPages(), id);

                                                        }
                                                    });
                                                }

                                            }

                                        }

                                        if (type.equals(DataManager.Pdf)) {
                                            notifactionHolder.imagebox.setVisibility(View.GONE);
                                            notifactionHolder.textbox.setVisibility(View.GONE);
                                            notifactionHolder.pdfbox.setVisibility(View.VISIBLE);
                                            notifactionHolder.audiobox.setVisibility(View.GONE);


                                            if (dataSnapshot.hasChild(DataManager.NotifactionProfileUrl)) {
                                                String uri = dataSnapshot.child(DataManager.NotifactionProfileUrl).getValue().toString();
                                                notifactionHolder.set_pdf_profileimage(uri);
                                            }
                                            if (dataSnapshot.hasChild(DataManager.Notifactionname)) {
                                                String name = dataSnapshot.child(DataManager.Notifactionname).getValue().toString();
                                                notifactionHolder.set_pdf_username(name);
                                            }

                                            if (dataSnapshot.hasChild(DataManager.NotifactionTime) || dataSnapshot.hasChild(DataManager.NotifactionDate)) {
                                                String time = dataSnapshot.child(DataManager.NotifactionTime).getValue().toString();
                                                String date = dataSnapshot.child(DataManager.NotifactionDate).getValue().toString();
                                                if (time.equals(CurrentTime)) {
                                                    notifactionHolder.pdf_time.setText("Today");
                                                    notifactionHolder.pdf_time.setTextColor(Color.RED);
                                                } else {
                                                    notifactionHolder.set_pdf_time(date);
                                                    notifactionHolder.pdf_time.setTextColor(Color.GRAY);
                                                }
                                            }

                                            if (dataSnapshot.hasChild(DataManager.NotifactionSenderID)) {
                                                String id = dataSnapshot.child(DataManager.NotifactionSenderID).getValue().toString();

                                                if (!id.isEmpty()) {
                                                    notifactionHolder.pdfbox.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            goto_chat_page(new ChatPages(), id);

                                                        }
                                                    });
                                                }

                                            }


                                            notifactionHolder.pdfbox.setOnLongClickListener(new View.OnLongClickListener() {
                                                @Override
                                                public boolean onLongClick(View view) {

                                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataSnapshot.child(DataManager.NotifactionTextBody).getValue().toString()));
                                                    notifactionHolder.context.startActivity(myIntent);

                                                    return true;
                                                }
                                            });


                                        }
                                        if (type.equals(DataManager.Audio)) {
                                            notifactionHolder.imagebox.setVisibility(View.GONE);
                                            notifactionHolder.textbox.setVisibility(View.GONE);
                                            notifactionHolder.pdfbox.setVisibility(View.GONE);
                                            notifactionHolder.audiobox.setVisibility(View.VISIBLE);


                                            if (dataSnapshot.hasChild(DataManager.Notifactionname)) {
                                                String name = dataSnapshot.child(DataManager.Notifactionname).getValue().toString();
                                                notifactionHolder.set_audio_username(name);
                                            }
                                            if (dataSnapshot.hasChild(DataManager.NotifactionProfileUrl)) {
                                                String uri = dataSnapshot.child(DataManager.NotifactionProfileUrl).getValue().toString();
                                                notifactionHolder.set_audio_profileimage(uri);
                                            }

                                            if (dataSnapshot.hasChild(DataManager.NotifactionTime) || dataSnapshot.hasChild(DataManager.NotifactionDate)) {
                                                String time = dataSnapshot.child(DataManager.NotifactionTime).getValue().toString();
                                                String date = dataSnapshot.child(DataManager.NotifactionDate).getValue().toString();
                                                if (time.equals(CurrentTime)) {
                                                    notifactionHolder.audio_time.setText("Today");
                                                    notifactionHolder.audio_time.setTextColor(Color.RED);
                                                } else {
                                                    notifactionHolder.set_pdf_time(date);
                                                    notifactionHolder.audio_time.setTextColor(Color.GRAY);
                                                }
                                            }

                                            if (dataSnapshot.hasChild(DataManager.NotifactionSenderID)) {
                                                String id = dataSnapshot.child(DataManager.NotifactionSenderID).getValue().toString();

                                                if (!id.isEmpty()) {
                                                    notifactionHolder.audiobox.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            goto_chat_page(new ChatPages(), id);

                                                        }
                                                    });
                                                }

                                            }


                                            notifactionHolder.audiobox.setOnLongClickListener(new View.OnLongClickListener() {
                                                @Override
                                                public boolean onLongClick(View view) {

                                                    AuidoButtomSheed buttomSheed = new AuidoButtomSheed(dataSnapshot.child(DataManager.NotifactionTextBody).getValue().toString());
                                                    buttomSheed.show(getActivity().getSupportFragmentManager(), "show");

                                                    return true;
                                                }
                                            });

                                        }


                                    } else {

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };

        notifactionview.setAdapter(firebaseRecyclerAdapter);
        super.onStart();
    }


    public static class NotifactionHolder extends RecyclerView.ViewHolder{



        /// todo message box
        private View Mview;
        private Context context;
        private MaterialTextView notifactionusername, time, message;
        private CircleImageView notifactionimage;
        private MaterialCardView textbox;
        /// todo message box

        /// todo imagebox
        private ImageView imageprofileimage;
        private MaterialTextView imagesendername, imagetime;
        private MaterialCardView imagebox;
        private RoundedImageView image;
        /// todo imagebox

        // todo pdf
        private CircleImageView pdfprofileimage;
        private MaterialTextView pdf_time, pdf_sender_name;
        private RelativeLayout pdf_download_button;
        private MaterialCardView pdfbox;
        // todo pdf

        /// todo audio
        private CircleImageView audio_profilemage;
        private MaterialTextView audio_username, audio_time;
        private RelativeLayout  audioplaybutton;
        private MaterialCardView audiobox;
        /// todo audio




        public NotifactionHolder(@NonNull View itemView) {
            super(itemView);



            Mview = itemView;
            context = Mview.getContext();
            notifactionusername = Mview.findViewById(R.id.UserName);
            time = Mview.findViewById(R.id.NotifactionTime);
            notifactionimage = Mview.findViewById(R.id.NotifactionProfileimage);
            message = Mview.findViewById(R.id.MessageText);
            textbox = Mview.findViewById(R.id.TextMessageBox);

            /// todo image box
            image = Mview.findViewById(R.id.NotifactionImage);
            imageprofileimage = Mview.findViewById(R.id.NotifactionImageProfileImage);
            imagesendername = Mview.findViewById(R.id.UserImageName);
            imagetime = Mview.findViewById(R.id.NotifactionImageTime);
            imagebox = Mview.findViewById(R.id.ImageBox);
            /// todo image box

            /// todo pdf box
            pdfprofileimage = Mview.findViewById(R.id.PdfSenderProfileImage);
            pdf_time = Mview.findViewById(R.id.PdfTime);
            pdf_sender_name = Mview.findViewById(R.id.UserPdfSenderName);
            pdf_download_button = Mview.findViewById(R.id.PdfDownloadButton);
            pdfbox = Mview.findViewById(R.id.PdfBox);
            /// todo pdf box

            /// todo audio
            audio_profilemage = Mview.findViewById(R.id.AudioSenderProfileImage);
            audio_username = Mview.findViewById(R.id.UserAudioSenderName);
            audio_time  = Mview.findViewById(R.id.AudioTime);
            audiobox = Mview.findViewById(R.id.AudioBox);
            audioplaybutton = Mview.findViewById(R.id.PlayAudioButton);
            /// todo audio



        }




        public void notifactionimageset(String uri){
            Picasso.with(context).load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(notifactionimage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(uri).into(notifactionimage);
                }
            });
        }

        public void setNotifactionusernameset(String nam){
            notifactionusername.setText(nam);
        }
        public void setnotifaction_time(String tim){
            time.setText(tim);
        }
        public void setnotifactionmessage(String body){
            message.setText(body);
        }



        /// todo image set
        private void setsernderimage(String img){

            Picasso.with(context).load(img).networkPolicy(NetworkPolicy.OFFLINE).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(img).into(image);
                }
            });


        }
        private void setimageusername(String nam){
            imagesendername.setText(nam);
        }
        private void setimagesenderTime(String tim){
            imagetime.setText(tim);
        }
        private void set_imageprofile(String profile){

            Picasso.with(context).load(profile).networkPolicy(NetworkPolicy.OFFLINE).into(imageprofileimage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(profile).into(imageprofileimage);
                }
            });

        }
        /// todo image set

        /// todo pdf set
        public void set_pdf_profileimage(String img){

            Picasso.with(context).load(img).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(pdfprofileimage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(context).load(img).into(pdfprofileimage);
                        }
                    });

        }
        public void set_pdf_username(String nam){
            pdf_sender_name.setText(nam);
        }
        public void set_pdf_time(String tim){
            pdf_time.setText(tim);
        }
        /// todo pdf set

        /// todo audio set
        private void set_audio_profileimage(String img){
            Picasso.with(context).load(img).networkPolicy(NetworkPolicy.OFFLINE).into(audio_profilemage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(img).into(audio_profilemage);
                }
            });
        }

        private void set_audio_username(String nam){
            audio_username.setText(nam);
        }
        private void set_audio_time(String tim){
            audio_time.setText(tim);
        }
        /// todo audio set




    }


    private void goto_chat_page(Fragment fragment, String ID){
        if(fragment != null){
            Bundle bundle = new Bundle();
            bundle.putString("UID", ID);

            fragment.setArguments(bundle);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right, R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();

        }
    }


   /* @Override
    public void onStart() {

        FirebaseRecyclerAdapter<HistoryModel, NotifactionHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<HistoryModel, NotifactionHolder>(
                HistoryModel.class,
                R.layout.mess_notifaction,
                NotifactionHolder.class,
                MnotifactionData
        ) {
            @Override
            protected void populateViewHolder(NotifactionHolder notifactionHolder, HistoryModel historyModel, int i) {
                String UID = getRef(i).getKey();
                MnotifactionData.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };

        super.onStart();
    }

    public static class NotifactionHolder extends RecyclerView.ViewHolder{

        public NotifactionHolder(@NonNull View itemView) {
            super(itemView);
        }
    }*/

}
