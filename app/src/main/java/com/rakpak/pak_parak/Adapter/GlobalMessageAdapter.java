package com.rakpak.pak_parak.Adapter;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.chrisbanes.photoview.PhotoView;
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
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.GlobalChat.GlobalChat;
import com.rakpak.pak_parak.ImageFullScreen.ImageFullScreen;
import com.rakpak.pak_parak.Model.GlobalChatModal;
import com.rakpak.pak_parak.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;


public class GlobalMessageAdapter extends RecyclerView.Adapter<GlobalMessageAdapter.GlobalHolder> {

    private List<GlobalChatModal> globalChatModalslist;
    private Context context;

    public GlobalMessageAdapter(List<GlobalChatModal> globalChatModalslist) {
        this.globalChatModalslist = globalChatModalslist;
    }

    private DatabaseReference MglobalDatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private DatabaseReference MuserDatabase;


    @NonNull
    @Override
    public GlobalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.global_chat_template, null, false);

        return new GlobalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GlobalHolder holder, int position) {

        MglobalDatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.GlobalchatRoot);
        MglobalDatabase.keepSynced(true);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        MuserDatabase.keepSynced(true);
        /// todo gone all component
        holder.timeright.setVisibility(View.GONE);
        holder.timetop.setVisibility(View.GONE);
        holder.textmessagebox.setVisibility(View.GONE);
        holder.imagebox.setVisibility(View.GONE);
        holder.pdflayout.setVisibility(View.GONE);
        /// todo gone all component

        /// todo audio message
        holder.audiobox.setVisibility(View.GONE);
        /// todo audio message

        GlobalChatModal globalChatModal = globalChatModalslist.get(position);
        String type = globalChatModal.getType();


        if (type.equals(DataManager.GlobalchatTextType)) {
            holder.textmessagebox.setVisibility(View.VISIBLE);

            if (globalChatModal.getMessage().length() >= 15) {
                holder.timetop.setVisibility(View.VISIBLE);
                MuserDatabase.child(CurrentUserID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild(DataManager.UserFullname)) {
                                        String myname = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                        if (globalChatModal.getName().equals(myname)) {

                                            holder.message.setText(globalChatModal.getMessage());
                                            holder.username.setText("Me");
                                            holder.timetop.setText(globalChatModal.getTime());
                                            holder.timeright.setText(globalChatModal.getTime());
                                        } else {
                                            holder.message.setText(globalChatModal.getMessage());
                                            holder.username.setText(globalChatModal.getName());
                                            holder.timetop.setText(globalChatModal.getTime());
                                            holder.timeright.setText(globalChatModal.getTime());
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            } else {
                holder.timeright.setVisibility(View.VISIBLE);
                MuserDatabase.child(CurrentUserID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild(DataManager.UserFullname)) {

                                        String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                        if (name.equals(globalChatModal.getName())) {
                                            holder.message.setText(globalChatModal.getMessage());
                                            holder.username.setText("Me");
                                            holder.timetop.setText(globalChatModal.getTime());
                                            holder.timeright.setText(globalChatModal.getTime());
                                        } else {
                                            holder.message.setText(globalChatModal.getMessage());
                                            holder.username.setText(globalChatModal.getName());
                                            holder.timetop.setText(globalChatModal.getTime());
                                            holder.timeright.setText(globalChatModal.getTime());
                                        }


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }


        }

        if (type.equals(DataManager.GlobalImageType)) {
            holder.imagebox.setVisibility(View.VISIBLE);







            MuserDatabase.child(CurrentUserID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.hasChild(DataManager.UserFullname)) {
                                    String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                    if (name.equals(globalChatModal.getName())) {
                                        holder.sender_imageusername.setText("Me");
                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            holder.sender_imageusername.setText(globalChatModal.getName());


            Picasso.with(holder.itemView.getContext()).load(globalChatModal.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(holder.itemView.getContext()).load(globalChatModal.getMessage()).into(holder.imageView);
                }
            });


            holder.image_time.setText(globalChatModal.getTime());



            /*holder.imagebox.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {


                    if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(globalChatModalslist.get(position).getMyID())) {

                        MaterialAlertDialogBuilder Mbulder = new MaterialAlertDialogBuilder(holder.context);
                        Mbulder.setTitle("Delete Message ?");
                        Mbulder.setMessage("Make sure if you delete your message its you permanently lost your data");
                        Mbulder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MglobalDatabase.child(globalChatModalslist.get(position).getMessageKey()).removeValue();
                            }
                        })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });


                        AlertDialog alertDialog = Mbulder.create();
                        alertDialog.show();

                    }


                    return true;
                }
            });*/


            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // new ContextThemeWrapper(holder.context, R.style.PauseDialog), android.R.style.Theme_Black_NoTitleBar_Fullscreen
                    final Dialog dialog = new Dialog(holder.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                    dialog.setContentView(R.layout.full_screenimage_bottomsheeed);

                    PhotoView photoView = dialog.findViewById(R.id.PhotoView);
                    MaterialTextView photoname = dialog.findViewById(R.id.SenderNames);
                    photoname.setText(globalChatModal.getName());
                    RelativeLayout downloadbutton = dialog.findViewById(R.id.ImageDownloadButonID);


                    downloadbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(holder.context);

                            Mbuilder.setTitle(R.string.download_image);
                            Mbuilder.setMessage(R.string.download_message);

                            Mbuilder.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    DownloadManager downloadManager = (DownloadManager) holder.context.getSystemService(Context.DOWNLOAD_SERVICE);
                                    Uri uri = Uri.parse(globalChatModal.getMessage());

                                    DownloadManager.Request request = new DownloadManager.Request(uri);
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    downloadManager.enqueue(request);
                                    Toast.makeText(holder.context, "Downloading ...", Toast.LENGTH_LONG).show();
                                }
                            });

                            Mbuilder.setNegativeButton(R.string.no_need, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                            Mbuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                            AlertDialog alertDialog = Mbuilder.create();
                            alertDialog.show();
                        }
                    });

                    RelativeLayout backbutton = dialog.findViewById(R.id.BackButton);
                    backbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });


                    Picasso.with(holder.context).load(globalChatModal.getMessage()).into(photoView);
                    dialog.show();
                }

                private void goto_fullscreen(Fragment fragment) {

                    if (fragment != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("URI", globalChatModal.getMessage());
                        bundle.putString("TIME", globalChatModal.getTime());
                        fragment.setArguments(bundle);

                        FragmentTransaction transaction = ((AppCompatActivity) holder.context).getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.MainID, fragment).addToBackStack(null);
                        transaction.commit();
                    }
                }
            });




            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    String click_id = globalChatModalslist.get(position).getMyID();

                    if(click_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        MaterialAlertDialogBuilder Mbulder = new MaterialAlertDialogBuilder(holder.context);
                        Mbulder.setTitle("Delete Message ?");
                        Mbulder.setIcon(R.drawable.remove_icon_black);
                        Mbulder.setMessage("Make sure if you delete your message its you permanently lost your data");
                        Mbulder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MglobalDatabase.child(globalChatModalslist.get(position).getMessageKey()).removeValue();

                                globalChatModalslist.remove(position);

                                notifyDataSetChanged();

                            }
                        })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });


                        AlertDialog alertDialog = Mbulder.create();
                        alertDialog.show();
                    }




                    return true;
                }
            });


        }


        if (type.equals(DataManager.GlobalPdfType)) {

            holder.pdflayout.setVisibility(View.VISIBLE);
            MuserDatabase.child(CurrentUserID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.hasChild(DataManager.UserFullname)) {
                                    String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();

                                    if (name.equals(globalChatModal.getName())) {
                                        holder.pdftime.setText(globalChatModal.getTime());
                                        holder.pdfsender.setText("Me");
                                    } else {
                                        holder.pdftime.setText(globalChatModal.getTime());
                                        holder.pdfsender.setText(globalChatModal.getName());
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



            holder.pdflayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(globalChatModalslist.get(position).getMessage()));
                    holder.context.startActivity(myIntent);

                }
            });


            holder.pdflayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    String click_id = globalChatModalslist.get(position).getMyID();

                    if(click_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        MaterialAlertDialogBuilder Mbulder = new MaterialAlertDialogBuilder(holder.context);
                        Mbulder.setTitle("Delete Message ?");
                        Mbulder.setIcon(R.drawable.remove_icon_black);
                        Mbulder.setMessage("Make sure if you delete your message its you permanently lost your data");
                        Mbulder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MglobalDatabase.child(globalChatModalslist.get(position).getMessageKey()).removeValue();

                                globalChatModalslist.remove(position);

                                notifyDataSetChanged();

                            }
                        })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });


                        AlertDialog alertDialog = Mbulder.create();
                        alertDialog.show();
                    }




                    return true;
                }
            });

        }


        /// todo audio message
        if (type.equals(DataManager.Audio)) {
            holder.audiobox.setVisibility(View.VISIBLE);





            holder.audiobox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AuidoButtomSheed auidoButtomSheed = new AuidoButtomSheed(globalChatModal.getMessage());
                    auidoButtomSheed.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "show");
                }
            });
            MuserDatabase.child(CurrentUserID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.hasChild(DataManager.UserFullname)) {
                                    String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();

                                    if (name.equals(globalChatModal.getName())) {
                                        holder.aduiotime.setText(globalChatModal.getTime());
                                        holder.senderaudio_username.setText("Me");
                                    } else {
                                        holder.aduiotime.setText(globalChatModal.getTime());
                                        holder.senderaudio_username.setText(globalChatModal.getName());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



            holder.audiobox.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    String click_id = globalChatModalslist.get(position).getMyID();

                    if(click_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        MaterialAlertDialogBuilder Mbulder = new MaterialAlertDialogBuilder(holder.context);
                        Mbulder.setTitle("Delete Message ?");
                        Mbulder.setIcon(R.drawable.remove_icon_black);
                        Mbulder.setMessage("Make sure if you delete your message its you permanently lost your data");
                        Mbulder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MglobalDatabase.child(globalChatModalslist.get(position).getMessageKey()).removeValue();

                                globalChatModalslist.remove(position);

                                notifyDataSetChanged();

                            }
                        })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });


                        AlertDialog alertDialog = Mbulder.create();
                        alertDialog.show();
                    }




                    return true;
                }
            });

        }
        /// todo audio message


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(globalChatModalslist.get(position).getMyID())) {

                    MaterialAlertDialogBuilder Mbulder = new MaterialAlertDialogBuilder(holder.context);
                    Mbulder.setTitle("Delete Message ?");
                    Mbulder.setMessage("Make sure if you delete your message its you permanently lost your data");
                    Mbulder.setIcon(R.drawable.remove_icon_black);
                    Mbulder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MglobalDatabase.child(globalChatModalslist.get(position).getMessageKey()).removeValue();

                            globalChatModalslist.remove(position);

                            notifyDataSetChanged();

                        }
                    })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });


                    AlertDialog alertDialog = Mbulder.create();
                    alertDialog.show();

                }


                return true;
            }
        });




    }

    @Override
    public int getItemCount() {
        return globalChatModalslist.size();
    }

    public class GlobalHolder extends RecyclerView.ViewHolder {

        private MaterialTextView message, timeright, timetop, username;
        private RelativeLayout textmessagebox, imagebox;


        private MaterialTextView image_time;
        private RoundedImageView imageView;
        private MaterialTextView sender_imageusername;

        /// todo PDF
        private RelativeLayout pdflayout;
        private MaterialTextView pdftime, pdfsender;
        private Context context;
        /// todo PDF

        /// todo audio message
        private RelativeLayout audiobox;
        private MaterialTextView aduiotime, senderaudio_username;
        /// todo audio message


        public GlobalHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();
            // todo usermessage templete
            message = itemView.findViewById(R.id.ReciverMessage);
            timeright = itemView.findViewById(R.id.ReciverShoetMessageTime);
            timetop = itemView.findViewById(R.id.ReciverMessageTime);
            username = itemView.findViewById(R.id.SenderName);
            // todo usermessage templete

            /// todo image
            imagebox = itemView.findViewById(R.id.ImageLayoutID);
            image_time = itemView.findViewById(R.id.ReciverTime);
            imageView = itemView.findViewById(R.id.ImageReciver);
            /// todo image

            /// todo PDF
            pdflayout = itemView.findViewById(R.id.ReciverPdfBox);
            pdftime = itemView.findViewById(R.id.ReciverPdfTime);
            pdfsender = itemView.findViewById(R.id.PdfSenderUserID);
            /// todo PDF

            // todo audio message
            audiobox = itemView.findViewById(R.id.ReciverAudioBox);
            aduiotime = itemView.findViewById(R.id.ReciverAudioTime);
            senderaudio_username = itemView.findViewById(R.id.SenderAudioname);
            // todo audio message

            textmessagebox = itemView.findViewById(R.id.ReciverMessageLayout);
            sender_imageusername = itemView.findViewById(R.id.SenderImageName);
        }
    }


}
