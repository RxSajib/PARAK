package com.rakpak.pak_parak.GlobalChat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rakpak.pak_parak.Adapter.GlobalMessageAdapter;
import com.rakpak.pak_parak.Adapter.MessageAdapter;
import com.rakpak.pak_parak.Common.Common;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.Model.GlobalChatModal;
import com.rakpak.pak_parak.Model.Myresponse;
import com.rakpak.pak_parak.Model.Notifaction;
import com.rakpak.pak_parak.Model.Sender;
import com.rakpak.pak_parak.Model.Token;
import com.rakpak.pak_parak.Model.TypeModel;
import com.rakpak.pak_parak.Model.UserMessageListModal;
import com.rakpak.pak_parak.R;
import com.rakpak.pak_parak.Remote.APIServices;

import java.io.File;
import java.io.IOException;
import java.net.InterfaceAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class GlobalChat extends Fragment {

    private RecyclerView recyclerView;
    private MaterialCardView sendbutton;
    private EditText inputmessage;
    private DatabaseReference MglobalChat;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private DatabaseReference Muser_database;
    private String Currentuser_name;
    private String CurrentTime, CurrentDate;
    private List<GlobalChatModal> globalChatModalList = new ArrayList<>();
    private GlobalMessageAdapter messageAdapter;
    private RelativeLayout attach_button;
    private static final int IMAGEREQUEST_CODE = 100;

    private StorageReference MimageStores;
    private ProgressDialog Mprogress;
    private static final int PDFCODE = 101;
    private StorageReference PdfStores;

    private DatabaseReference MuserDatabase;

    private RecyclerView typingview;
    private String Username;
    private String get_name;
    private RelativeLayout backbutton;
    private APIServices mservices;
    private String messagetext;
    private LinearLayout attachbox;

    /// todo all function is there

    private MediaRecorder mediaRecorder = null;
    private int Counter, REQUEST_AUDIO_PERMISSION_CODE = 1;
    private String recodfile;
    private StorageReference audiofile;

    private String permission[] = {RECORD_AUDIO, WRITE_EXTERNAL_STORAGE};
    private String mFileName;
    private RecordView recordView;
    private RecordButton recordButton;
    private Handler handler = new Handler();
    private MaterialCardView messgecard;

    private DatabaseReference MNotifactionUserDatabase;
    private DatabaseReference GlobalTypeData;
    private DatabaseReference OnlineRoot;
    /// todo all function is there



    public GlobalChat() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.global_chat, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        OnlineRoot = FirebaseDatabase.getInstance().getReference().child(DataManager.OnlineUseRoot);

        GlobalTypeData = FirebaseDatabase.getInstance().getReference().child(DataManager.GlobalTypeRoot);

        /// todo internet connection dioloag
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo activnetwkinfo = cm.getActiveNetworkInfo();

        boolean isconnected = activnetwkinfo != null && activnetwkinfo.isConnected();
        if (isconnected) {

            ///open anythings
        } else {
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


        MNotifactionUserDatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.NotifactionUserRoot);

        messgecard = view.findViewById(R.id.MessageCard);

        /// todo all function is there

        recordButton = view.findViewById(R.id.GlobalRecordButtonID);
        recordView = view.findViewById(R.id.GlobalRecordViewID);

        audiofile = FirebaseStorage.getInstance().getReference().child("GroupAudio");


        /// todo all function is there


        attachbox = view.findViewById(R.id.GlobalAttachBox);
        backbutton = view.findViewById(R.id.GlobalBackButtonID);

        recordButton.setRecordView(recordView);

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

                if (checkpermission()) {
                    startrecoding();
                } else {

                }


                messgecard.setVisibility(View.GONE);


            }

            @Override
            public void onCancel() {


                ExampleRunnable runnable = new ExampleRunnable(5);
                new Thread(runnable).start();


            }

            @Override
            public void onFinish(long recordTime) {

                stoprecoding();

                messgecard.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLessThanSecond() {


                messgecard.setVisibility(View.VISIBLE);
            }
        });


        mservices = Common.getFCMClient();
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });


        typingview = view.findViewById(R.id.CouuentUserTypingID);
        typingview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        typingview.setLayoutManager(linearLayoutManager);

        PdfStores = FirebaseStorage.getInstance().getReference().child("GlobalPDF");
        Mprogress = new ProgressDialog(getActivity());
        MimageStores = FirebaseStorage.getInstance().getReference().child("GlobalImage");

        attach_button = view.findViewById(R.id.GlobalAttach);
        Muser_database = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);
        Muser_database.keepSynced(true);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        Muser_database.child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild(DataManager.UserFullname)) {
                                Currentuser_name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        recyclerView = view.findViewById(R.id.ChatListID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageAdapter = new GlobalMessageAdapter(globalChatModalList);
        recyclerView.setAdapter(messageAdapter);
        inputmessage = view.findViewById(R.id.GlobalMessageInput);

        /// todo controlling the image
        inputmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                if (input.isEmpty()) {
                    sendbutton.setVisibility(View.GONE);
                    recordButton.setVisibility(View.VISIBLE);

                    attachbox.setVisibility(View.VISIBLE);
                } else {
                    recordButton.setVisibility(View.GONE);
                    sendbutton.setVisibility(View.VISIBLE);

                    attachbox.setVisibility(View.GONE);
                }
            }
        });
        /// todo controlling the image


        MglobalChat = FirebaseDatabase.getInstance().getReference().child("GlobalChat");
        MglobalChat.keepSynced(true);
        sendbutton = view.findViewById(R.id.GlobalMessageSendButton);
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messagetext = inputmessage.getText().toString();
                if (messagetext.isEmpty()) {
                    Toast.makeText(getActivity(), "Type any message", Toast.LENGTH_LONG).show();
                } else {

                    Calendar calendar_time = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("hh:mm a");
                    CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());

                    Calendar calendar_date = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("dd MMM yyyy");
                    CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                    Map<String, Object> globalmap = new HashMap<String, Object>();
                    globalmap.put("message", messagetext);
                    globalmap.put("name", Currentuser_name);
                    globalmap.put("time", CurrentTime);
                    globalmap.put("date", CurrentDate);
                    globalmap.put("type", "text");

                    MglobalChat.push().updateChildren(globalmap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        find_userand_sendnotifaction();

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

                    inputmessage.setText(null);
                }
            }
        });


        attach_button.setOnTouchListener((view1, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                attach_button.setBackgroundResource(R.drawable.click_animaction_attach);
                MaterialAlertDialogBuilder Mbuilder = new MaterialAlertDialogBuilder(getActivity());
                CharSequence options[] = new CharSequence[]{
                        "Send Image",
                        "Send Pdf"
                };

                Mbuilder.setItems(options, (dialogInterface, i) -> {
                    if (i == 0) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, IMAGEREQUEST_CODE);
                    }
                    if (i == 1) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/pdf");
                        startActivityForResult(intent, PDFCODE);
                    }
                });
                AlertDialog alertDialog = Mbuilder.create();
                alertDialog.show();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                attach_button.setBackgroundResource(R.drawable.unclick_animaction_attach);
            }

            return true;
        });

        readall_message();


        Muser_database = FirebaseDatabase.getInstance().getReference().child(DataManager.UserRoot);

        inputmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String typing = editable.toString();
                if (!typing.isEmpty()) {

                    Map<String, Object> tyeingmap = new HashMap<String, Object>();
                    tyeingmap.put(DataManager.GlobalChatTypeStatus, "typing ...");

                    GlobalTypeData.child(CurrentUserID).updateChildren(tyeingmap);
                } else {
                    Map<String, Object> tyeingmap = new HashMap<String, Object>();
                    tyeingmap.put(DataManager.GlobalChatTypeStatus, "notyping ...");

                    GlobalTypeData.child(CurrentUserID).updateChildren(tyeingmap);
                }
            }
        });

        Muser_database.child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild(DataManager.UserFullname)) {
                                get_name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        onlinecheack("online");


        return view;
    }


    private void readall_message() {
        MglobalChat
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        GlobalChatModal message = dataSnapshot.getValue(GlobalChatModal.class);
                        globalChatModalList.add(message);
                        messageAdapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGEREQUEST_CODE && resultCode == RESULT_OK) {

            Mprogress.setMessage("wait for a moment your image is sending");
            Mprogress.setTitle("Please wait ...");
            Mprogress.setCanceledOnTouchOutside(false);
            Mprogress.show();

            Uri imageuri = data.getData();
            StorageReference filepath = MimageStores.child(imageuri.getLastPathSegment());
            filepath.putFile(imageuri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                String Imagedownloaduri = task.getResult().getDownloadUrl().toString();

                                Calendar calendar_time = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("hh:mm a");
                                CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());

                                Calendar calendar_date = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("dd MMM yyyy");
                                CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                                Map<String, Object> globalmap = new HashMap<String, Object>();
                                globalmap.put("message", Imagedownloaduri);
                                globalmap.put("name", Currentuser_name);
                                globalmap.put("time", CurrentTime);
                                globalmap.put("date", CurrentDate);
                                globalmap.put("type", "image");

                                MglobalChat.push().updateChildren(globalmap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                find_user_sendimage_notifaction();

                                                Mprogress.dismiss();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Mprogress.dismiss();
                                                Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                Mprogress.dismiss();
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Mprogress.dismiss();
                            Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        if (requestCode == PDFCODE && resultCode == RESULT_OK) {
            Mprogress.setMessage("wait for a moment your pdf is sending");
            Mprogress.setTitle("Please wait ...");
            Mprogress.setCanceledOnTouchOutside(false);
            Mprogress.show();
            Uri pdfuri = data.getData();

            StorageReference filapath = PdfStores.child(pdfuri.getLastPathSegment());
            filapath.putFile(pdfuri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                String PDfdownloduri = task.getResult().getDownloadUrl().toString();

                                Calendar calendar_time = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("hh:mm a");
                                CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());

                                Calendar calendar_date = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("dd MMM yyyy");
                                CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                                Map<String, Object> globalmap = new HashMap<String, Object>();
                                globalmap.put("message", PDfdownloduri);
                                globalmap.put("name", Currentuser_name);
                                globalmap.put("time", CurrentTime);
                                globalmap.put("date", CurrentDate);
                                globalmap.put("type", "PDf");

                                MglobalChat.push().updateChildren(globalmap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    fine_user_send_pdf_notifaction();
                                                    Mprogress.dismiss();
                                                } else {
                                                    Mprogress.dismiss();
                                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Mprogress.dismiss();
                                                Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
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
    }

    /// todo typing status global chat
    @Override
    public void onStart() {

        FirebaseRecyclerAdapter<TypeModel, TypeHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TypeModel, TypeHolder>(
                TypeModel.class,
                R.layout.typing_layot,
                TypeHolder.class,
                GlobalTypeData
        ) {
            @Override
            protected void populateViewHolder(TypeHolder typeHolder, TypeModel typeModel, int i) {
                String UID = getRef(i).getKey();
                GlobalTypeData.child(UID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    Muser_database.child(UID)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(DataManager.GlobalChatTypeStatus)) {
                                                        String typing_status = dataSnapshot.child(DataManager.GlobalChatTypeStatus).getValue().toString();
                                                        if (typing_status.equals(DataManager.GlobalChatTyping)) {
                                                            typeHolder.typelayout.setVisibility(View.VISIBLE);

                                                            if (dataSnapshot.hasChild(DataManager.UserFullname)) {
                                                                Username = dataSnapshot.child(DataManager.UserFullname).getValue().toString();

                                                                if (!get_name.isEmpty()) {
                                                                    if (get_name.equals(Username)) {
                                                                        typeHolder.typelayout.setVisibility(View.GONE);
                                                                    } else {
                                                                        typeHolder.setUsernameset(Username);
                                                                        typeHolder.setTypestatusset("Typing ...");
                                                                    }
                                                                }
                                                            }

                                                        } else if (typing_status.equals(DataManager.GlobalChatNoTyping)) {
                                                            typeHolder.typelayout.setVisibility(View.GONE);

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };

        typingview.setAdapter(firebaseRecyclerAdapter);
        super.onStart();
    }
    /// todo typing status global chat

    public static class TypeHolder extends RecyclerView.ViewHolder {

        private MaterialTextView username, typestatus;
        private View Mview;
        private Context context;
        private LinearLayout typelayout;


        public TypeHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            context = Mview.getContext();
            username = Mview.findViewById(R.id.TypeUserNameID);
            typestatus = Mview.findViewById(R.id.UsernameTypeText);
            typelayout = Mview.findViewById(R.id.TypeLayout);
        }

        public void setUsernameset(String nam) {
            username.setText(nam);
        }

        public void setTypestatusset(String status) {
            typestatus.setText(status);
        }

    }

    @Override
    public void onStop() {
        onlinecheack("offline");
        Map<String, Object> tyeingmap = new HashMap<String, Object>();
        tyeingmap.put(DataManager.GlobalChatTypeStatus, "notyping ...");

        Muser_database.child(CurrentUserID).updateChildren(tyeingmap);
        super.onStop();
    }


    /// todo find user and send notifaction
    private void find_userand_sendnotifaction() {
        FirebaseDatabase.getInstance().getReference().child(DataManager.NotifactionUserRoot).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild(DataManager.UserFullname)) {
                                String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                if (!name.isEmpty()) {
                                    send_notfaction(name);


                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    /// todo find user and send notifaction


    /// todo send notifaction to reciver
    private void send_notfaction(final String username) {
        FirebaseDatabase.getInstance().getReference().child("Token")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                Token token = ds.getValue(Token.class);
                                String sendermessage = username;
                                String title = messagetext;

                                Notifaction notifaction = new Notifaction(title, sendermessage);
                                Sender sender = new Sender(token.getToken(), notifaction);

                                mservices.sendNotification(sender).enqueue(new Callback<Myresponse>() {
                                    @Override
                                    public void onResponse(Call<Myresponse> call, Response<Myresponse> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<Myresponse> call, Throwable t) {
                                        Log.i("ERROR", "MESSAGE");

                                    }
                                });
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }
    /// todo send notifaction to reciver  user -> lastmessage


    /// todo find user send image notifaction
    private void find_user_sendimage_notifaction() {
        FirebaseDatabase.getInstance().getReference().child(DataManager.NotifactionUserRoot).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild(DataManager.UserFullname)) {
                                String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                if (!name.isEmpty()) {
                                    send_image_notfaction(name);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    /// todo find user send image notifaction

    // todo find user and send image
    private void send_image_notfaction(final String username) {
        FirebaseDatabase.getInstance().getReference().child("Token")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                Token token = ds.getValue(Token.class);
                                String sendermessage = username;
                                String title = messagetext;

                                Notifaction notifaction = new Notifaction("Send a image", sendermessage);
                                Sender sender = new Sender(token.getToken(), notifaction);

                                mservices.sendNotification(sender).enqueue(new Callback<Myresponse>() {
                                    @Override
                                    public void onResponse(Call<Myresponse> call, Response<Myresponse> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<Myresponse> call, Throwable t) {
                                        Log.i("ERROR", "MESSAGE");

                                    }
                                });
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }
    // todo find user and send image''

    // todo fine user and send PDF notifaction
    private void fine_user_send_pdf_notifaction() {
        FirebaseDatabase.getInstance().getReference().child(DataManager.NotifactionUserRoot).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild(DataManager.UserFullname)) {
                                String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                if (!name.isEmpty()) {
                                    send_PDF_notfaction(name);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    // todo fine user and send PDF notifaction

    /// todo send PDF
    private void send_PDF_notfaction(final String username) {
        FirebaseDatabase.getInstance().getReference().child("Token")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                Token token = ds.getValue(Token.class);
                                String sendermessage = username;
                                String title = messagetext;

                                Notifaction notifaction = new Notifaction("Send a PDF", sendermessage);
                                Sender sender = new Sender(token.getToken(), notifaction);

                                mservices.sendNotification(sender).enqueue(new Callback<Myresponse>() {
                                    @Override
                                    public void onResponse(Call<Myresponse> call, Response<Myresponse> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<Myresponse> call, Throwable t) {
                                        Log.i("ERROR", "MESSAGE");

                                    }
                                });
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }
    /// todo send PDF


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


        OnlineRoot.child(CurrentUserID).child(DataManager.UserOnlineRoot).updateChildren(onlinemap)
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


    @Override
    public void onResume() {
        onlinecheack("online");
        super.onResume();
    }


    /// todo start recoding and stop recoding
    private void startrecoding() {

        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("hh:mm:ss");
        String CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/AudioRecording" + CurrentTime + ".3gp";

        mediaRecorder = new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(mFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {

            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void stoprecoding() {

        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                saveing_data_firebase(mFileName);

                mediaRecorder = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();

                mediaRecorder = null;
            }
        }


    }
    /// todo start recoding and stop recoding


    /// todo run time permission and save audio in firebase database
    private void saveing_data_firebase(String recodfile) {

        Uri uri = Uri.fromFile(new File(recodfile));
        Mprogress.setTitle("Please wait ...");
        Mprogress.setMessage("wait for a moment your voice is uploading");
        Mprogress.setCanceledOnTouchOutside(false);
        Mprogress.show();

        StorageReference filepath = audiofile.child(uri.getLastPathSegment());
        filepath.putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            String audiouri = task.getResult().getDownloadUrl().toString();

                            Calendar calendar_time = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("hh:mm a");
                            CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());

                            Calendar calendar_date = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("dd MMM yyyy");
                            CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                            Map<String, Object> globalmap = new HashMap<String, Object>();
                            globalmap.put("message", audiouri);
                            globalmap.put("name", Currentuser_name);
                            globalmap.put("time", CurrentTime);
                            globalmap.put("date", CurrentDate);
                            globalmap.put("type", "Audio");

                            MglobalChat.push().updateChildren(globalmap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                fine_user_send_audio_notifaction();
                                                Mprogress.dismiss();
                                            } else {
                                                Mprogress.dismiss();
                                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Mprogress.dismiss();
                                            Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {

                            Mprogress.dismiss();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Mprogress.dismiss();
                    }
                });
    }


    class ExampleRunnable implements Runnable {

        int second;

        public ExampleRunnable(int second) {
            this.second = second;
        }

        @Override
        public void run() {
            for (int i = 0; i <= 5; i++) {

                if (i == 5) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            messgecard.setVisibility(View.VISIBLE);
                        }
                    });

                }

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private boolean checkpermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {

            ActivityCompat.requestPermissions(getActivity(), permission, REQUEST_AUDIO_PERMISSION_CODE);
            return false;
        }
    }
    /// todo run time permission and save audio in firebase database


    /// todo send audio notifaction is there
    private void fine_user_send_audio_notifaction() {
        FirebaseDatabase.getInstance().getReference().child(DataManager.NotifactionUserRoot).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild(DataManager.UserFullname)) {
                                String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                if (!name.isEmpty()) {
                                    send_Audio_notfaction(name);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void send_Audio_notfaction(String name) {
        FirebaseDatabase.getInstance().getReference().child("Token")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                Token token = ds.getValue(Token.class);
                                String sendermessage = name;
                                String title = messagetext;

                                Notifaction notifaction = new Notifaction("Send a Audio", sendermessage);
                                Sender sender = new Sender(token.getToken(), notifaction);

                                mservices.sendNotification(sender).enqueue(new Callback<Myresponse>() {
                                    @Override
                                    public void onResponse(Call<Myresponse> call, Response<Myresponse> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<Myresponse> call, Throwable t) {
                                        Log.i("ERROR", "MESSAGE");

                                    }
                                });
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }


    /// todo send audio notifaction is there


}