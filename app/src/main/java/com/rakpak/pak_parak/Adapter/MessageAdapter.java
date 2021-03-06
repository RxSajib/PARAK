package com.rakpak.pak_parak.Adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rakpak.pak_parak.BottomSheed.AuidoButtomSheed;
import com.rakpak.pak_parak.BottomSheed.ForwardBottomSheed;
import com.rakpak.pak_parak.BottomSheed.FullSceenImageBottomSheed;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.Forword.MessageForward;
import com.rakpak.pak_parak.ImageFullScreen.ImageFullScreen;
import com.rakpak.pak_parak.Model.UserMessageListModal;
import com.rakpak.pak_parak.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private List<UserMessageListModal> userMessageListModals;
    private DatabaseReference MessageDatabase;
    private String SenderUID;
    private FirebaseAuth Mauth;
    private ProgressDialog Mprogress;

    public MessageAdapter(List<UserMessageListModal> userMessageListModals) {
        this.userMessageListModals = userMessageListModals;
    }


    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mview = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_design_template, null, false);

        return new MessageHolder(mview);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageHolder holder, int position) {

        MessageDatabase = FirebaseDatabase.getInstance().getReference().child("Message");
        MessageDatabase.keepSynced(true);
        Mauth = FirebaseAuth.getInstance();
        SenderUID = Mauth.getCurrentUser().getUid();
        Mprogress = new ProgressDialog(holder.context);

        final UserMessageListModal MessageModalList = userMessageListModals.get(position);
        String from = MessageModalList.getFrom();
        String type = MessageModalList.getType();

        MessageDatabase.child(SenderUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        /// todo reciver image is there
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        holder.recivermessagelayout.setVisibility(View.GONE);
        holder.sendermessagelayout.setVisibility(View.GONE);
        holder.sender_pdf_box.setVisibility(View.GONE);
        holder.reciver_pdf_box.setVisibility(View.GONE);
        holder.imagebox.setVisibility(View.GONE);

        holder.SenderLOngMessageDate.setVisibility(View.GONE);
        holder.sendershotMessageDate.setVisibility(View.GONE);

        /// todo sender and reciver image
        holder.sendderimage.setVisibility(View.GONE);
        holder.reciverimage.setVisibility(View.GONE);
        holder.senderimagetime.setVisibility(View.GONE);
        holder.reciverimagetime.setVisibility(View.GONE);

        holder.recivershort_message.setVisibility(View.GONE);
        holder.sendershort_message.setVisibility(View.GONE);
        holder.sendmessage_time.setVisibility(View.GONE);
        holder.recivemessage_time.setVisibility(View.GONE);

        holder.reciverimage_date.setVisibility(View.GONE);


        holder.recivershortmessage_date.setVisibility(View.GONE);
        holder.reciverlong_message_date.setVisibility(View.GONE);

        /// todo sender and reciver image

        /// todo send and reciver audio
        holder.senderaudiobox.setVisibility(View.GONE);
        holder.reciveraudiobox.setVisibility(View.GONE);
        /// todo send and reciver audio

        if (type.equals("Text")) {
            if (from.equals(SenderUID)) {
                holder.sendermessagelayout.setVisibility(View.VISIBLE);



                holder.sendermessagelayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        CharSequence[] options = new CharSequence[]{
                                "Forward",
                                "Remove for you",
                                "Unsend"
                        };


                        MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(holder.context);
                        Mbuilder.setTitle("Select Action");
                        Mbuilder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i == 0){

                                    ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardTexType);
                                    forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                                }
                                if(i == 1){
                                    delete_message_sender(position, holder);
                                }
                                if(i == 2){
                                    delete_message_everyone(position, holder);
                                }
                            }
                        });

                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();


                        return true;
                    }
                });




                String sendermessage = MessageModalList.getMessage();
                if(sendermessage.length() >= 15){
                    holder.sendmessage_time.setVisibility(View.VISIBLE);
                    holder.SenderLOngMessageDate.setVisibility(View.VISIBLE);
                    holder.sendmessage_time.setText(MessageModalList.getTime());
                    holder.sendmessage.setText(MessageModalList.getMessage());
                    holder.sendershort_message.setText(MessageModalList.getTime());

                    holder.sendershotMessageDate.setText(MessageModalList.getDate());
                    holder.SenderLOngMessageDate.setText(MessageModalList.getDate());



                }
                else {
                    holder.sendershort_message.setVisibility(View.VISIBLE);
                    holder.sendershotMessageDate.setVisibility(View.VISIBLE);
                    holder.sendershort_message.setText(MessageModalList.getTime());
                    holder.sendmessage_time.setText(MessageModalList.getTime());
                    holder.sendmessage.setText(MessageModalList.getMessage());

                    holder.SenderLOngMessageDate.setText(MessageModalList.getDate());
                    holder.sendershotMessageDate.setText(MessageModalList.getDate());


                }

            } else {
                holder.recivermessagelayout.setVisibility(View.VISIBLE);


                holder.recivermessagelayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                        CharSequence[] options = new CharSequence[]{
                                "Forward",
                                "Remove for you",

                        };


                        MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(holder.context);
                        Mbuilder.setTitle("Select Action");
                        Mbuilder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i == 0){

                                    ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardTexType);
                                    forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                                }
                                if(i == 1){
                                    delete_message_reciver(position, holder);
                                }

                            }
                        });

                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();


                        return true;
                    }
                });





                String sendermessage = MessageModalList.getMessage();
                if(sendermessage.length() >= 15){
                   holder.recivemessage_time.setVisibility(View.VISIBLE);
                   holder.reciverlong_message_date.setVisibility(View.VISIBLE);
                    holder.recivemessage_time.setText(MessageModalList.getTime());
                    holder.recivemessage.setText(MessageModalList.getMessage());
                    holder.recivershort_message.setText(MessageModalList.getTime());

                    holder.reciverlong_message_date.setText(MessageModalList.getDate());
                    holder.recivershortmessage_date.setText(MessageModalList.getDate());
                }
                else {
                    holder.recivershortmessage_date.setVisibility(View.VISIBLE);
                    holder.recivershort_message.setVisibility(View.VISIBLE);
                    holder.recivershort_message.setText(MessageModalList.getTime());
                    holder.recivemessage_time.setText(MessageModalList.getTime());
                    holder.recivemessage.setText(MessageModalList.getMessage());

                    holder.reciverlong_message_date.setText(MessageModalList.getDate());
                    holder.recivershortmessage_date.setText(MessageModalList.getDate());


                }

            }
        }

        if(type.equals("Image")){
            holder.imagebox.setVisibility(View.VISIBLE);
            if(from.equals(SenderUID)){
                holder.sendderimage.setVisibility(View.VISIBLE);
                holder.senderimagetime.setVisibility(View.VISIBLE);
                holder.senderimagetime.setText(MessageModalList.getTime());

                holder.sneder_image_date.setText(MessageModalList.getDate());



                holder.sendderimage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                        CharSequence[] options = new CharSequence[]{
                                "Forward",
                                "Remove for you",
                                "Unsend"
                        };


                        MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(holder.context);
                        Mbuilder.setTitle("Select Action");
                        Mbuilder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i == 0){

                                    ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardImageType);
                                    forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                                }
                                if(i == 1){
                                    delete_message_sender(position, holder);
                                }
                                if(i == 2){
                                    delete_message_everyone(position, holder);
                                }
                            }
                        });

                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();


                        return true;



                    }
                });




                Picasso.with(holder.context).load(MessageModalList.getMessage()).resize(800, 800).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(holder.sendderimage,
                        new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(holder.context).load(MessageModalList.getMessage()).resize(800, 800).centerCrop().into(holder.sendderimage);
                            }
                        });


                holder.sendderimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      //  goto_fullscreen(new ImageFullScreen());

                       /* MaterialAlertDialogBuilder Mbuilder  = new MaterialAlertDialogBuilder(holder.context);
                        View Mview = LayoutInflater.from(holder.context).inflate(R.layout.full_scren_dioloag, null, false);

                        MaterialToolbar toolbar = Mview.findViewById(R.id.DioloagToolbarID);
                        PhotoView photoView = Mview.findViewById(R.id.PhotoViewID);

                        Mbuilder.setView(Mview);
                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();*/
/*

                        FullSceenImageBottomSheed fullSceenImageBottomSheed = new FullSceenImageBottomSheed(MessageModalList.getMessage());
                        fullSceenImageBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "open");
*/

                       /* MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(holder.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        View Mview = LayoutInflater.from(holder.context).inflate(R.layout.full_screenimage_bottomsheeed, null, false);

                        PhotoView photoVie = Mview.findViewById(R.id.PhotoView);

                        Mbuilder.setView(Mview);

                        Picasso.with(holder.context).load(MessageModalList.getMessage()).into(photoVie);


                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();*/


                        final Dialog dialog = new Dialog(holder.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                        dialog.setContentView(R.layout.full_screenimage_bottomsheeed);

                        PhotoView photoView = dialog.findViewById(R.id.PhotoView);
                        Picasso.with(holder.context).load(MessageModalList.getMessage()).into(photoView);
                        dialog.show();

                    }



                });

            }
            else {
                holder.reciverimage.setVisibility(View.VISIBLE);
                holder.reciverimagetime.setVisibility(View.VISIBLE);
                holder.reciverimage_date.setVisibility(View.VISIBLE);
                holder.reciverimagetime.setText(MessageModalList.getTime());
                holder.reciverimage_date.setText(MessageModalList.getDate());


                holder.reciverimage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                        CharSequence[] options = new CharSequence[]{
                                "Forward",
                                "Remove for you",

                        };


                        MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(holder.context);
                        Mbuilder.setTitle("Select Action");
                        Mbuilder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i == 0){

                                    ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardImageType);
                                    forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                                }
                                if(i == 1){
                                    delete_message_reciver(position, holder);
                                }

                            }
                        });

                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();



                        return true;
                    }
                });





                Picasso.with(holder.context).load(MessageModalList.getMessage()).networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.reciverimage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(holder.context).load(MessageModalList.getMessage()).into(holder.reciverimage);
                            }
                        });


                holder.reciverimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                      /*  MaterialAlertDialogBuilder Mbuilder  = new MaterialAlertDialogBuilder(holder.context);
                        View Mview = LayoutInflater.from(holder.context).inflate(R.layout.full_scren_dioloag, null, false);

                        MaterialToolbar toolbar = Mview.findViewById(R.id.DioloagToolbarID);
                        MaterialTextView time = Mview.findViewById(R.id.ImageTimeDetails);
                        PhotoView photoView = Mview.findViewById(R.id.PhotoViewID);

                        Picasso.with(holder.context).load(MessageModalList.getMessage()).into(photoView);
                        time.setText(MessageModalList.getTime());



                        Mbuilder.setView(Mview);
                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();*/

                      //  goto_fullscreen(new ImageFullScreen());



                        final Dialog dialog = new Dialog(holder.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                        dialog.setContentView(R.layout.full_screenimage_bottomsheeed);

                        PhotoView photoView = dialog.findViewById(R.id.PhotoView);
                        Picasso.with(holder.context).load(MessageModalList.getMessage()).into(photoView);
                        dialog.show();
                    }

                    private void goto_fullscreen(Fragment fragment) {
                        if(fragment != null){

                            Bundle bundle = new Bundle();
                            bundle.putString("URI", MessageModalList.getMessage());
                            bundle.putString("TIME", MessageModalList.getTime());
                            fragment.setArguments(bundle);

                            FragmentTransaction transaction = ((AppCompatActivity) holder.context).getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
                            transaction.commit();
                        }
                    }

                });


            }
        }

        if(type.equals("Pdf")){
            if(from.equals(SenderUID)){
                holder.sender_pdf_box.setVisibility(View.VISIBLE);
                holder.sender_pdf_time.setText(MessageModalList.getTime());
                holder.senderPdfDate.setText(MessageModalList.getDate());


                holder.sender_pdf_box.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        CharSequence[] options = new CharSequence[]{
                                "Forward",
                                "Remove for you",
                                "Unsend"
                        };


                        MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(holder.context);
                        Mbuilder.setTitle("Select Action");
                        Mbuilder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i == 0){

                                    ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardPdfType);
                                    forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                                }
                                if(i == 1){
                                    delete_message_sender(position, holder);
                                }
                                if(i == 2){
                                    delete_message_everyone(position, holder);
                                }
                            }
                        });

                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();


                        return true;
                    }
                });





                holder.sender_pdf_box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MessageModalList.getMessage()));
                        holder.context.startActivity(myIntent);


                    }
                });


            }
            else {
                holder.reciver_pdf_box.setVisibility(View.VISIBLE);
                holder.reciver_pdf_time.setText(MessageModalList.getTime());
                holder.reciverpdf_date.setText(MessageModalList.getDate());



                holder.reciver_pdf_box.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                        CharSequence[] options = new CharSequence[]{
                                "Forward",
                                "Remove for you",

                        };


                        MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(holder.context);
                        Mbuilder.setTitle("Select Action");
                        Mbuilder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i == 0){

                                    ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardPdfType);
                                    forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                                }
                                if(i == 1){
                                    delete_message_reciver(position, holder);
                                }

                            }
                        });

                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();



                        return true;
                    }
                });





                holder.reciver_pdf_box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MessageModalList.getMessage()));
                        holder.context.startActivity(myIntent);
                    }
                });
            }
        }


        if(type.equals(DataManager.Audio)){
            if(from.equals(SenderUID)){
                holder.senderaudiobox.setVisibility(View.VISIBLE);
                holder.senderaudiotime.setText(MessageModalList.getTime());
                holder.senderaudiodate.setText(MessageModalList.getDate());



                holder.senderaudiobox.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        CharSequence[] options = new CharSequence[]{
                                "Forward",
                                "Remove for you",
                                "Unsend"
                        };


                        MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(holder.context);
                        Mbuilder.setTitle("Select Action");
                        Mbuilder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i == 0){


                                    ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardAudioType);
                                    forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                                }
                                if(i == 1){
                                    delete_message_sender(position, holder);
                                }
                                if(i == 2){
                                    delete_message_everyone(position, holder);
                                }
                            }
                        });

                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();


                        return true;
                    }
                });


                holder.senderaudiobox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       AuidoButtomSheed buttomSheed = new AuidoButtomSheed(MessageModalList.getMessage());
                       buttomSheed.show(((AppCompatActivity)holder.context).getSupportFragmentManager(), "show");
                    }
                });

            }
            else {
                holder.reciveraudiobox.setVisibility(View.VISIBLE);
                holder.reciveraudiotime.setText(MessageModalList.getTime());

                holder.reciveraudiodate.setText(MessageModalList.getDate());


                holder.reciveraudiobox.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                        CharSequence[] options = new CharSequence[]{
                                "Forward",
                                "Remove for you",

                        };


                        MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(holder.context);
                        Mbuilder.setTitle("Select Action");
                        Mbuilder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i == 0){

                                    ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardAudioType);
                                    forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                                }
                                if(i == 1){
                                    delete_message_reciver(position, holder);
                                }

                            }
                        });

                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();



                        return true;
                    }
                });






                holder.reciveraudiobox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AuidoButtomSheed buttomSheed = new AuidoButtomSheed(MessageModalList.getMessage());
                        buttomSheed.show(((AppCompatActivity)holder.context).getSupportFragmentManager(), "show");
                    }
                });
            }
        }





        if(type.equals(DataManager.ForwardImageType)){
            holder.imagebox.setVisibility(View.VISIBLE);
            if (from.equals(SenderUID)) {





                holder.sendderimage.setVisibility(View.VISIBLE);
                holder.senderimagetime.setVisibility(View.VISIBLE);
                holder.senderimagetime.setText(MessageModalList.getTime());
                holder.sneder_image_date.setText(MessageModalList.getDate());





                holder.sendderimage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardImageType);
                        forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");



                        return true;
                    }
                });

                holder.sendderimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(holder.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                        dialog.setContentView(R.layout.full_screenimage_bottomsheeed);

                        PhotoView photoView = dialog.findViewById(R.id.PhotoView);
                        Picasso.with(holder.context).load(MessageModalList.getMessage()).into(photoView);
                        dialog.show();
                    }
                });

                Picasso.with(holder.context).load(MessageModalList.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.sendderimage,
                        new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(holder.context).load(MessageModalList.getMessage()).into(holder.sendderimage);
                            }
                        });








            }

            else {

                /// todo wait i am showing the code media recoder ok

                holder.reciverimage.setVisibility(View.VISIBLE);
                holder.reciverimagetime.setVisibility(View.VISIBLE);
                holder.reciverimagetime.setText(MessageModalList.getTime());
                holder.reciverimage_date.setText(MessageModalList.getDate());


                holder.reciverimage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardImageType);
                        forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");



                        return true;
                    }
                });


                holder.reciverimage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {



                        ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardImageType);
                        forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                        return true;
                    }


                });



                Picasso.with(holder.context).load(MessageModalList.getMessage()).resize(800, 800).centerCrop().networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.reciverimage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(holder.context).load(MessageModalList.getMessage()).resize(800, 800).centerCrop().into(holder.reciverimage);
                            }
                        });


                holder.reciverimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                      /*  MaterialAlertDialogBuilder Mbuilder  = new MaterialAlertDialogBuilder(holder.context);
                        View Mview = LayoutInflater.from(holder.context).inflate(R.layout.full_scren_dioloag, null, false);

                        MaterialToolbar toolbar = Mview.findViewById(R.id.DioloagToolbarID);
                        MaterialTextView time = Mview.findViewById(R.id.ImageTimeDetails);
                        PhotoView photoView = Mview.findViewById(R.id.PhotoViewID);

                        Picasso.with(holder.context).load(MessageModalList.getMessage()).into(photoView);
                        time.setText(MessageModalList.getTime());



                        Mbuilder.setView(Mview);
                        AlertDialog alertDialog = Mbuilder.create();
                        alertDialog.show();*/

                        //  goto_fullscreen(new ImageFullScreen());



                        final Dialog dialog = new Dialog(holder.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                        dialog.setContentView(R.layout.full_screenimage_bottomsheeed);

                        PhotoView photoView = dialog.findViewById(R.id.PhotoView);
                        Picasso.with(holder.context).load(MessageModalList.getMessage()).into(photoView);
                        dialog.show();
                    }

                    private void goto_fullscreen(Fragment fragment) {
                        if(fragment != null){

                            Bundle bundle = new Bundle();
                            bundle.putString("URI", MessageModalList.getMessage());
                            bundle.putString("TIME", MessageModalList.getTime());
                            fragment.setArguments(bundle);

                            FragmentTransaction transaction = ((AppCompatActivity) holder.context).getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
                            transaction.commit();
                        }
                    }

                });




            }
        }

        if(type.equals(DataManager.ForwardTexType)){


            if (from.equals(SenderUID)) {
                holder.sendermessagelayout.setVisibility(View.VISIBLE);


            holder.sendermessagelayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardAudioType);
                    forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                    return true;
                }
            });



                String sendermessage = MessageModalList.getMessage();
                if(sendermessage.length() >= 15){
                  /*  holder.sendmessage_time.setVisibility(View.VISIBLE);
                    holder.sendmessage_time.setText(MessageModalList.getTime());
                    holder.sendmessage.setText(MessageModalList.getMessage());
                    holder.sendershort_message.setText(MessageModalList.getTime());*/

                    holder.sendmessage_time.setVisibility(View.VISIBLE);
                    holder.SenderLOngMessageDate.setVisibility(View.VISIBLE);
                    holder.sendmessage_time.setText(MessageModalList.getTime());
                    holder.sendmessage.setText(MessageModalList.getMessage());
                    holder.sendershort_message.setText(MessageModalList.getTime());

                    holder.sendershotMessageDate.setText(MessageModalList.getDate());
                    holder.SenderLOngMessageDate.setText(MessageModalList.getDate());
                }
                else {
                    /*holder.sendershort_message.setVisibility(View.VISIBLE);
                    holder.sendershort_message.setText(MessageModalList.getTime());
                    holder.sendmessage_time.setText(MessageModalList.getTime());
                    holder.sendmessage.setText(MessageModalList.getMessage());*/

                    holder.sendershort_message.setVisibility(View.VISIBLE);
                    holder.sendershotMessageDate.setVisibility(View.VISIBLE);
                    holder.sendershort_message.setText(MessageModalList.getTime());
                    holder.sendmessage_time.setText(MessageModalList.getTime());
                    holder.sendmessage.setText(MessageModalList.getMessage());

                    holder.SenderLOngMessageDate.setText(MessageModalList.getDate());
                    holder.sendershotMessageDate.setText(MessageModalList.getDate());
                }

            }

            else {

                holder.recivermessagelayout.setVisibility(View.VISIBLE);



                holder.recivermessagelayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {



                        ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardTexType);
                        forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                        return true;
                    }


                });




                String sendermessage = MessageModalList.getMessage();
                if(sendermessage.length() >= 15){
                  /*  holder.recivemessage_time.setVisibility(View.VISIBLE);
                    holder.recivemessage_time.setText(MessageModalList.getTime());
                    holder.recivemessage.setText(MessageModalList.getMessage());
                    holder.recivershort_message.setText(MessageModalList.getTime());*/

                    holder.recivemessage_time.setVisibility(View.VISIBLE);
                    holder.reciverlong_message_date.setVisibility(View.VISIBLE);
                    holder.recivemessage_time.setText(MessageModalList.getTime());
                    holder.recivemessage.setText(MessageModalList.getMessage());
                    holder.recivershort_message.setText(MessageModalList.getTime());

                    holder.reciverlong_message_date.setText(MessageModalList.getDate());
                    holder.recivershortmessage_date.setText(MessageModalList.getDate());

                }
                else {
                    /*holder.recivershort_message.setVisibility(View.VISIBLE);
                    holder.recivershort_message.setText(MessageModalList.getTime());
                    holder.recivemessage_time.setText(MessageModalList.getTime());
                    holder.recivemessage.setText(MessageModalList.getMessage());*/

                    holder.recivershortmessage_date.setVisibility(View.VISIBLE);
                    holder.recivershort_message.setVisibility(View.VISIBLE);
                    holder.recivershort_message.setText(MessageModalList.getTime());
                    holder.recivemessage_time.setText(MessageModalList.getTime());
                    holder.recivemessage.setText(MessageModalList.getMessage());

                    holder.reciverlong_message_date.setText(MessageModalList.getDate());
                    holder.recivershortmessage_date.setText(MessageModalList.getDate());

                }


            }

        }


        if(type.equals(DataManager.ForwardPdfType)){

            if(from.equals(SenderUID)){
                holder.sender_pdf_box.setVisibility(View.VISIBLE);
                holder.sender_pdf_time.setText(MessageModalList.getTime());
                holder.senderPdfDate.setText(MessageModalList.getDate());


                holder.sender_pdf_box.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                        ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardAudioType);
                        forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                        return true;
                    }
                });


                holder.sender_pdf_box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MessageModalList.getMessage()));
                        holder.context.startActivity(myIntent);


                    }
                });


            }

            else {
                holder.reciver_pdf_box.setVisibility(View.VISIBLE);
                holder.reciver_pdf_time.setText(MessageModalList.getTime());
                holder.reciverpdf_date.setText(MessageModalList.getDate());


                holder.reciver_pdf_box.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardPdfType);
                        forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                        return true;
                    }
                });


                holder.reciver_pdf_box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MessageModalList.getMessage()));
                        holder.context.startActivity(myIntent);
                    }
                });
            }

        }


        if(type.equals(DataManager.ForwardAudioType)){
            if(from.equals(SenderUID)){
                holder.senderaudiobox.setVisibility(View.VISIBLE);
                holder.senderaudiotime.setText(MessageModalList.getTime());
                holder.senderaudiodate.setText(MessageModalList.getDate());


                holder.senderaudiobox.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardAudioType);
                        forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                        return true;
                    }
                });


                holder.senderaudiobox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AuidoButtomSheed buttomSheed = new AuidoButtomSheed(MessageModalList.getMessage());
                        buttomSheed.show(((AppCompatActivity)holder.context).getSupportFragmentManager(), "show");
                    }
                });

            }

            else {
                holder.reciveraudiobox.setVisibility(View.VISIBLE);
                holder.reciveraudiotime.setText(MessageModalList.getTime());
                holder.reciveraudiodate.setText(MessageModalList.getDate());


                holder.reciveraudiobox.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        ForwardBottomSheed forwardBottomSheed = new ForwardBottomSheed(MessageModalList.getMessage(), DataManager.ForwardAudioType);
                        forwardBottomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");

                        return true;
                    }
                });


                holder.reciveraudiobox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AuidoButtomSheed buttomSheed = new AuidoButtomSheed(MessageModalList.getMessage());
                        buttomSheed.show(((AppCompatActivity)holder.context).getSupportFragmentManager(), "show");
                    }
                });
            }
        }

    }


    /// todo delete for sender
    private void delete_message_sender(final int position, final MessageHolder messageHolder){

        Mprogress.setTitle("Wait for a moment ...");
        Mprogress.setMessage("Please wait for delete message");
        Mprogress.setCanceledOnTouchOutside(false);
        Mprogress.show();

        DatabaseReference MessageRoot = FirebaseDatabase.getInstance().getReference();
        MessageRoot.child("Message").child(userMessageListModals.get(position).getFrom())
                .child(userMessageListModals.get(position).getTo())
                .child(userMessageListModals.get(position).getMessage_Id())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            userMessageListModals.remove(position);
                            notifyDataSetChanged();
                            Mprogress.dismiss();
                        }
                        else {
                            Toast.makeText(messageHolder.context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Mprogress.dismiss();
                        }
                    }
                });


    }
    /// todo delete for sender

    /// todo delete for reciver
    private void delete_message_reciver(final int position, final MessageHolder messageHolder){

        Mprogress.setTitle("Wait for a moment ...");
        Mprogress.setMessage("Please wait for delete message");
        Mprogress.setCanceledOnTouchOutside(false);
        Mprogress.show();

        DatabaseReference MessageRoot = FirebaseDatabase.getInstance().getReference();
        MessageRoot.child("Message").child(userMessageListModals.get(position).getTo())
                .child(userMessageListModals.get(position).getFrom())
                .child(userMessageListModals.get(position).getMessage_Id())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            userMessageListModals.remove(position);
                            notifyDataSetChanged();
                            Mprogress.dismiss();
                        }
                        else {
                            Toast.makeText(messageHolder.context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Mprogress.dismiss();
                        }
                    }
                });

    }
    /// todo delete for reciver

    /// todo remove for everyone
    private void delete_message_everyone(final int position, final MessageHolder messageHolder){

        Mprogress.setTitle("Wait for a moment ...");
        Mprogress.setMessage("Please wait for delete message");
        Mprogress.setCanceledOnTouchOutside(false);
        Mprogress.show();

        DatabaseReference MessageRoot = FirebaseDatabase.getInstance().getReference();
        MessageRoot.child("Message").child(userMessageListModals.get(position).getTo())
                .child(userMessageListModals.get(position).getFrom())
                .child(userMessageListModals.get(position).getMessage_Id())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                                MessageRoot.child("Message").child(userMessageListModals.get(position).getFrom())
                                        .child(userMessageListModals.get(position).getTo())
                                        .child(userMessageListModals.get(position).getMessage_Id())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){
                                                    /// todo finial task
                                                    userMessageListModals.remove(position);
                                                    notifyDataSetChanged();
                                                    Mprogress.dismiss();
                                                }
                                                else {
                                                    Toast.makeText(messageHolder.context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                    Mprogress.dismiss();
                                                }

                                            }
                                        });
                        }
                        else {
                            Toast.makeText(messageHolder.context,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Mprogress.dismiss();
                        }
                    }
                });
    }
    /// todo remove for everyone


    @Override
    public int getItemCount() {
        return userMessageListModals.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        /// todo send and reciv message
        private Context context;
        private RelativeLayout sendermessagelayout, recivermessagelayout;
        private MaterialTextView sendmessage, recivemessage;
        private MaterialTextView sendmessage_time, recivemessage_time;
        private MaterialTextView recivershort_message, sendershort_message;
        private MaterialTextView sendershotMessageDate, SenderLOngMessageDate;

        private MaterialTextView recivershortmessage_date, reciverlong_message_date;

        /// todo send and reciv message

        ///todo send and recive image
        private RoundedImageView sendderimage, reciverimage;
        private MaterialTextView senderimagetime, reciverimagetime;
        private RelativeLayout imagebox;
        private MaterialTextView sneder_image_date, reciverimage_date;
        ///todo send and recive image

        /// todo sender and reciver pdf
        private RelativeLayout sender_pdf_box, reciver_pdf_box;
        private MaterialTextView sender_pdf_time, reciver_pdf_time, senderPdfDate;
        private MaterialTextView reciverpdf_date;
        /// todo sender and reciver pdf

        /// todo send and recive audio file
        private RelativeLayout senderaudiobox, reciveraudiobox;
        private MaterialTextView senderaudiotime, reciveraudiotime, senderaudiodate, reciveraudiodate;
        /// todo send and recive audio file



        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();

            /// todo send recive audio file
            senderaudiobox = itemView.findViewById(R.id.SendAudio);
            reciveraudiobox = itemView.findViewById(R.id.ReciverAudio);

            senderaudiotime = itemView.findViewById(R.id.SenderAudioTime);
            reciveraudiotime = itemView.findViewById(R.id.ReciverAudioTime);
            senderaudiodate = itemView.findViewById(R.id.SenderAudioDate);
            /// todo send recive audio file

            // todo sender reciver message
            sendermessagelayout = itemView.findViewById(R.id.SenderMessageLayout);
            sendmessage = itemView.findViewById(R.id.SendMessageID);
            sendmessage_time = itemView.findViewById(R.id.SenderMessageTimeID);

            recivermessagelayout = itemView.findViewById(R.id.ReciverMessageLayout);
            recivemessage = itemView.findViewById(R.id.ReciverMessage);
            recivemessage_time = itemView.findViewById(R.id.ReciverMessageTime);

            recivershort_message = itemView.findViewById(R.id.ReciverShoetMessageTime);
            sendershort_message = itemView.findViewById(R.id.SenderShoetMessageTime);

            sendershotMessageDate = itemView.findViewById(R.id.SenderMessageShortDateID);
            SenderLOngMessageDate = itemView.findViewById(R.id.SenderMessageDateID);

            recivershortmessage_date = itemView.findViewById(R.id.ReciverShoetMessageDate);
            reciverlong_message_date = itemView.findViewById(R.id.ReciverLongMessagedate);

            // todo sender reciver message

            // todo sender and reciver image
            sendderimage = itemView.findViewById(R.id.SenderImageID);
            senderimagetime = itemView.findViewById(R.id.SenderImageTime);

            reciverimage = itemView.findViewById(R.id.ImageReciver);
            reciverimagetime = itemView.findViewById(R.id.ReciverTime);
            imagebox = itemView.findViewById(R.id.ImageLayoutID);
            sneder_image_date = itemView.findViewById(R.id.SenderImageDate);
            reciverimage_date = itemView.findViewById(R.id.ReciverImageDate);
            // todo sender and reciver image

            // todo sender and reciver pdf
            sender_pdf_box = itemView.findViewById(R.id.SenderPdfBox);
            reciver_pdf_box = itemView.findViewById(R.id.ReciverPdfBox);

            sender_pdf_time = itemView.findViewById(R.id.SenderPdfTime);
            senderPdfDate = itemView.findViewById(R.id.SenderPdfDate);
            reciver_pdf_time = itemView.findViewById(R.id.ReciverPdfTime);
            reciverpdf_date = itemView.findViewById(R.id.ReciverPdfDate);

            reciveraudiodate = itemView.findViewById(R.id.ReciverAudioDate);
            // todo sender and reciver pdf

        }
    }

}
