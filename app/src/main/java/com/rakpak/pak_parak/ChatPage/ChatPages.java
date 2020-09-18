package com.rakpak.pak_parak.ChatPage;
import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rakpak.pak_parak.Adapter.MessageAdapter;
import com.rakpak.pak_parak.Common.Common;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.Helper.Notification;
import com.rakpak.pak_parak.Model.Myresponse;
import com.rakpak.pak_parak.Model.Notifaction;
import com.rakpak.pak_parak.Model.Sender;
import com.rakpak.pak_parak.Model.Token;
import com.rakpak.pak_parak.Model.UserMessageListModal;
import com.rakpak.pak_parak.R;
import com.rakpak.pak_parak.Remote.APIServices;
import com.rakpak.pak_parak.TeastLayout.TeastLayout;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class ChatPages extends Fragment {

    private DatabaseReference NotifactionData;
    private String ReciverUID;
    private FirebaseAuth Mauth;
    private String SenderID;
    private MaterialCardView sendbutton;
    private EditText inputmessage;
    private DatabaseReference Message_database;
    private String CurrentTime, CurrentDate;

    private RecyclerView messageview;
    private MessageAdapter messageAdapter;
    private List<UserMessageListModal> userMessageListModalList = new ArrayList<>();

    private RelativeLayout sendassets;
    private static final int IMAGECODE = 001;
    private StorageReference Msendimage_stores;
    private ProgressDialog Mprogress;
    private static final int PDFCODE = 002;
    private StorageReference Mpdf_Stores;
    private String Current_time, Current_date;
    private DatabaseReference Muser_database;
    private MaterialTextView type_status_text;

    private MaterialTextView username;
    private CircleImageView profileimage;
    private ImageView back_icon;
    private APIServices mservices;
    private String message;

    private RelativeLayout camerabutton;

    private LinearLayout attachbox;
    private static final int PERMISSIONCODE = 100;
    private static final int CAMERAINTENT = 101;
    private Uri camera_imageuri;

    private RelativeLayout backbutton;

    private RelativeLayout activedot;
    private DatabaseReference ONlineData;


    /// todo update function
    MaterialButton button;
    private MediaRecorder mediaRecorder;
    private int Counter, REQUEST_AUDIO_PERMISSION_CODE = 1;
    private String recodfile;
    private StorageReference audiofile;

    private String permission[] = {RECORD_AUDIO, WRITE_EXTERNAL_STORAGE};
    private String mFileName;

    private ProgressDialog Mpogress;


    private Button buttonchange_onclick;
    private RecordView recordView;
    private RecordButton recordButton;
    private MaterialCardView messgecard;
    private Handler handler = new Handler();
    private EditText editText;
    private MaterialCardView floatingActionButton;
    /// todo update function


    private DatabaseReference MtypeData;
    private String reciverimageuri, recivername;


    // todo short notifaction
    private int ShortDesPositive, ShortDesNegative;
    // todo short notifaction

    public ChatPages() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chat_pages, container, false);

        NotifactionData = FirebaseDatabase.getInstance().getReference().child(DataManager.NotifactionUserRoot);

        MtypeData = FirebaseDatabase.getInstance().getReference().child(DataManager.UserTypeRoot);

        ONlineData  = FirebaseDatabase.getInstance().getReference().child(DataManager.UserOnlineRoot);
        ONlineData.keepSynced(true);



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




        /// todo function
         /// todo update function
        Mpogress = new ProgressDialog(getActivity());

        /// todo all function
        floatingActionButton = view.findViewById(R.id.SendButtonID);
        messgecard = view.findViewById(R.id.MessageCard);
        recordView = view.findViewById(R.id.RecordView);
        recordButton = view.findViewById(R.id.RecodButton);

        audiofile  = FirebaseStorage.getInstance().getReference().child("MyAudio");





        recordButton.setRecordView(recordView);




        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        /// this is the button on
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Log.d("RecordView", "onStart");
              //  Toast.makeText(getActivity(), "OnStartRecord", Toast.LENGTH_SHORT).show();

                /// todo start recoding
                if(mypermission()){
                    startrecoding();
                }
                else {

                }


                messgecard.setVisibility(View.GONE);


            }

            @Override
            public void onCancel() {
              //  Toast.makeText(getActivity(), "onCancel", Toast.LENGTH_SHORT).show();


                /// todo work
                ExampleRunnable runnable = new ExampleRunnable(5);
                new Thread(runnable).start();


            }

            @Override
            public void onFinish(long recordTime) {

                /// todo stop recoding
                stoprecoding();

             //   Toast.makeText(getActivity(), "Finish", Toast.LENGTH_SHORT).show();
                messgecard.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLessThanSecond() {
             //   Toast.makeText(getActivity(), "OnLessThanSecond", Toast.LENGTH_SHORT).show();
             //   Log.d("RecordView", "onLessThanSecond");

                messgecard.setVisibility(View.VISIBLE);
            }
        });

        /// todo update function



        activedot = view.findViewById(R.id.ActiveDot);


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        backbutton = view.findViewById(R.id.backButtonID);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });

        Mpdf_Stores = FirebaseStorage.getInstance().getReference().child("Pdf");
        Muser_database = FirebaseDatabase.getInstance().getReference().child("Users");
        Muser_database.keepSynced(true);



        attachbox = view.findViewById(R.id.AttachBox);
        mservices = Common.getFCMClient();
        back_icon = view.findViewById(R.id.Chatpgeback_icon);


        username = view.findViewById(R.id.UserNameText);
        profileimage = view.findViewById(R.id.ReciverProfileimage);

        type_status_text = view.findViewById(R.id.TypingStatusID);



        Mprogress = new ProgressDialog(getActivity());
        Msendimage_stores = FirebaseStorage.getInstance().getReference().child("Image");

        sendassets = view.findViewById(R.id.AddButtonID);

        sendassets.setOnTouchListener((view1, motionEvent) -> {

            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                sendassets.setBackgroundResource(R.drawable.click_animaction_attach);


                // todo send
                CharSequence charSequence[] = new CharSequence[]{
                        "Send Image",
                        "Send PDF",

                };

                MaterialAlertDialogBuilder Mbuider = new MaterialAlertDialogBuilder(getActivity());
                Mbuider.setItems(charSequence, (dialogInterface, i) -> {
                    if (i == 0) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, IMAGECODE);
                    }
                    if (i == 1) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/pdf");
                        startActivityForResult(intent, PDFCODE);
                    }

                });

                AlertDialog alertDialog = Mbuider.create();
                alertDialog.show();
                // todo send


            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                sendassets.setBackgroundResource(R.drawable.unclick_animaction_attach);
            }
            return true;
        });


        messageview = view.findViewById(R.id.MessageList);
        messageview.setHasFixedSize(true);
        messageview.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageAdapter = new MessageAdapter(userMessageListModalList);
        messageview.setAdapter(messageAdapter);


        Message_database = FirebaseDatabase.getInstance().getReference();
        Message_database.keepSynced(true);

        inputmessage = view.findViewById(R.id.MessageInputID);

        inputmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String type = editable.toString();
                if(type.isEmpty()){

                    floatingActionButton.setVisibility(View.GONE);
                    recordButton.setVisibility(View.VISIBLE);

                    attachbox.setVisibility(View.VISIBLE);




                    Calendar calendar_time = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
                    Current_time = simpleDateFormat_time.format(calendar_time.getTime());

                    Calendar calendar_date = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
                    Current_date = simpleDateFormat_date.format(calendar_date.getTime());

                    Map<String, Object> type_map = new HashMap<String, Object>();
                    type_map.put("type", "no_type");
                    type_map.put("time", Current_time);
                    type_map.put("date", Current_date);

                    MtypeData.child(SenderID)
                            .child("type_status").child("type").setValue("notype");
                    MtypeData.child(SenderID)
                            .child("type_status").child("time").setValue(Current_time);
                    MtypeData.child(SenderID)
                            .child("type_status").child("date").setValue(Current_date);

                    /// todo sender and reciver type
                    MtypeData.child(ReciverUID).child(SenderID)
                            .child("type").setValue("notype");

                    MtypeData.child(ReciverUID).child(SenderID)
                            .child("time").setValue(Current_time);

                    MtypeData.child(ReciverUID).child(SenderID)
                            .child("date").setValue(Current_date);


                }
                else {
                    recordButton.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.VISIBLE);
                    attachbox.setVisibility(View.GONE);
                    Calendar calendar_time = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
                    Current_time = simpleDateFormat_time.format(calendar_time.getTime());

                    Calendar calendar_date = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
                    Current_date = simpleDateFormat_date.format(calendar_date.getTime());

                    Map<String, Object> type_map = new HashMap<String, Object>();
                    type_map.put("type", "typing ...");
                    type_map.put("time", Current_time);
                    type_map.put("date", Current_date);

                    MtypeData.child(SenderID)
                            .child("type_status").child("type").setValue("typing ...");
                    MtypeData.child(SenderID)
                            .child("type_status").child("time").setValue(Current_time);
                    MtypeData.child(SenderID)
                            .child("type_status").child("date").setValue(Current_date);

                    /// todo sender and reciver type status
                    MtypeData.child(ReciverUID).child(SenderID)
                            .child("type").setValue("typing ...");

                    MtypeData.child(ReciverUID).child(SenderID)
                            .child("time").setValue(Current_time);

                    MtypeData.child(ReciverUID).child(SenderID)
                            .child("date").setValue(Current_date);
                }
            }
        });


        sendbutton = view.findViewById(R.id.SendButtonID);


        Mauth = FirebaseAuth.getInstance();
        SenderID = Mauth.getCurrentUser().getUid();

        Bundle build = getArguments();
        ReciverUID = build.getString("UID");

        Muser_database.child(ReciverUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild(DataManager.UserFullname)){
                                 recivername = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                username.setText(recivername);
                            }
                            if(dataSnapshot.hasChild("profileimage")){
                                 reciverimageuri = dataSnapshot.child("profileimage").getValue().toString();
                                if(getContext() != null){
                                    Picasso.with(getContext()).load(reciverimageuri).resize(110, 110).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_image_back).into(profileimage, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(getContext()).load(reciverimageuri).resize(110, 110).centerCrop().placeholder(R.drawable.profile_image_back).into(profileimage);

                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        MtypeData.child(ReciverUID).child("type_status")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                                if(dataSnapshot.hasChild("date") && dataSnapshot.hasChild("time") && dataSnapshot.hasChild("type")){
                                    String date = dataSnapshot.child("date").getValue().toString();
                                    String time = dataSnapshot.child("time").getValue().toString();
                                    String type = dataSnapshot.child("type").getValue().toString();

                                    if(type.equals("typing ...")){
                                        type_status_text.setText("Typing ...");
                                    }
                                    else {

                                        Calendar calendar_time = Calendar.getInstance();
                                        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
                                        Current_time = simpleDateFormat_time.format(calendar_time.getTime());

                                        Calendar calendar_date = Calendar.getInstance();
                                        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
                                        Current_date = simpleDateFormat_date.format(calendar_date.getTime());

                                        if(Current_date.equals(date)){
                                            type_status_text.setText("Active today at: "+time);
                                        }
                                        else {
                                            type_status_text.setText(time+" "+date);
                                        }


                                    }
                                }

                            }
                        }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 message = inputmessage.getText().toString().toString();
                if (message.isEmpty()) {
                    Toast.makeText(getActivity(), "input any message", Toast.LENGTH_SHORT).show();
                } else {

                    send_usermessage(message);
                    inputmessage.setText(null);
                }

            }
        });

        readall_message();






        getuser_friend_online();

        onlinecheack("online");


        /// todo end of function







        /// todo DES Order history
        NotifactionData.child(ReciverUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            ShortDesPositive = (int) dataSnapshot.getChildrenCount();
                            ShortDesNegative = (~(ShortDesPositive - 1));
                        }
                        else {
                            ShortDesNegative = 0;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        /// todo DES Order history



        return view;
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




    private void send_usermessage(String Message) {
        String SenderMessageRef = "Message/" + SenderID + "/" + ReciverUID;
        String ReciverMessageRef = "Message/" + ReciverUID + "/" + SenderID;


        DatabaseReference MessagePushID = Message_database.child("Message").child(SenderID).child(ReciverUID).push();

        String usermaessageid = MessagePushID.getKey();

        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
        CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());

        Calendar calendar_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
        CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());


        Map<String, Object> message_map = new HashMap<String, Object>();
        message_map.put("Message", Message);
        message_map.put("Type", DataManager.Text);
        message_map.put("From", SenderID);
        message_map.put("Time", CurrentTime);
        message_map.put("Date", CurrentDate);

        Map<String, Object> message_body = new HashMap<String, Object>();
        message_body.put(SenderMessageRef + "/" + usermaessageid, message_map);
        message_body.put(ReciverMessageRef + "/" + usermaessageid, message_map);

        Message_database.updateChildren(message_body)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            find_userand_sendnotifaction();

                            set_history_textmessage(Message, recivername, DataManager.Text);

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

    /// current user into typing

    private void readall_message() {
        Message_database.child("Message").child(SenderID).child(ReciverUID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        UserMessageListModal message = dataSnapshot.getValue(UserMessageListModal.class);
                        userMessageListModalList.add(message);
                        messageAdapter.notifyDataSetChanged();
                        messageview.smoothScrollToPosition(messageAdapter.getItemCount());
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
    public void onStart() {





        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGECODE && resultCode == RESULT_OK) {

            Mprogress.setMessage("wait for a moment your image is sending");
            Mprogress.setTitle("Please wait ...");
            Mprogress.setCanceledOnTouchOutside(false);
            Mprogress.show();
            Uri uri = data.getData();

            StorageReference filepath = Msendimage_stores.child(uri.getLastPathSegment());
            filepath.putFile(uri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                String Imagedownloaduri = task.getResult().getDownloadUrl().toString();
                                String SenderMessageRef = "Message/" + SenderID + "/" + ReciverUID;
                                String ReciverMessageRef = "Message/" + ReciverUID + "/" + SenderID;

                                DatabaseReference MessagePushID = Message_database.child("Message").child(SenderID).child(ReciverUID).push();
                                String usermaessageid = MessagePushID.getKey();
                                Calendar calendar_time = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
                                CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());

                                Calendar calendar_date = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
                                CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                                Map<String, Object> message_map = new HashMap<String, Object>();
                                message_map.put("Message", Imagedownloaduri);
                                message_map.put("Type", "Image");
                                message_map.put("From", SenderID);
                                message_map.put("Time", CurrentTime);
                                message_map.put("Date", CurrentDate);

                                Map<String, Object> message_body = new HashMap<String, Object>();
                                message_body.put(SenderMessageRef + "/" + usermaessageid, message_map);
                                message_body.put(ReciverMessageRef + "/" + usermaessageid, message_map);

                                Message_database.updateChildren(message_body)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Mprogress.dismiss();

                                                    find_imaguser_andsend_notifacion();

                                                    set_history_textmessage(Imagedownloaduri, recivername, DataManager.Image);

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
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

        if(requestCode == CAMERAINTENT && resultCode == RESULT_OK){
            Log.d("CAMERA", data.getData().toString());
        }

         if(requestCode == PDFCODE && resultCode == RESULT_OK){
            Uri pdf_uri = data.getData();
            Mprogress.setMessage("wait for a moment your pdf is sending");
            Mprogress.setTitle("Please wait ...");
            Mprogress.setCanceledOnTouchOutside(false);
            Mprogress.show();
            StorageReference filepath = Mpdf_Stores.child(pdf_uri.getLastPathSegment());
            filepath.putFile(pdf_uri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                String pdf_downloaduri = task.getResult().getDownloadUrl().toString();

                                String SenderMessageRef = "Message/" + SenderID + "/" + ReciverUID;
                                String ReciverMessageRef = "Message/" + ReciverUID + "/" + SenderID;

                                DatabaseReference MessagePushID = Message_database.child("Message").child(SenderID).child(ReciverUID).push();
                                String usermaessageid = MessagePushID.getKey();
                                Calendar calendar_time = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
                                CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());

                                Calendar calendar_date = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
                                CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                                Map<String, Object> message_map = new HashMap<String, Object>();
                                message_map.put("Message", pdf_downloaduri);
                                message_map.put("Type", "Pdf");
                                message_map.put("From", SenderID);
                                message_map.put("Time", CurrentTime);
                                message_map.put("Date", CurrentDate);

                                Map<String, Object> message_body = new HashMap<String, Object>();
                                message_body.put(SenderMessageRef + "/" + usermaessageid, message_map);
                                message_body.put(ReciverMessageRef + "/" + usermaessageid, message_map);

                                Message_database.updateChildren(message_body)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    finduser_sendPDF();
                                                    set_history_textmessage(pdf_downloaduri, recivername, DataManager.Pdf);
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


                                /// todo end of the code
                            }
                            else {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }


    }

    /// todo find user and send notifaction
    private void find_userand_sendnotifaction(){
        FirebaseDatabase.getInstance().getReference().child(DataManager.NotifactionUserRoot).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild(DataManager.UserFullname)){
                                String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                if(!name.isEmpty()){
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
    private void send_notfaction(final String username){
        FirebaseDatabase.getInstance().getReference().child("Token").child(ReciverUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Token token = dataSnapshot.getValue(Token.class);
                            String sendermessage = username;
                            String title = message;

                            Notifaction notifaction = new Notifaction(sendermessage, title);
                            Sender sender  = new Sender(token.getToken(), notifaction);

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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    /// todo send notifaction to reciver  user -> lastmessage



    /// todo send notication
    private void finduser_sendPDF(){
        FirebaseDatabase.getInstance().getReference().child(DataManager.NotifactionUserRoot).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild(DataManager.UserFullname)){
                                String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                if(!name.isEmpty()){
                                    send_pdfnotfaction(name);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void send_pdfnotfaction(final String username){
        FirebaseDatabase.getInstance().getReference().child("Token").child(ReciverUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Token token = dataSnapshot.getValue(Token.class);
                            String sendermessage = username;
                            String title = message;

                            Notifaction notifaction = new Notifaction(sendermessage, "Send a pdf file");
                            Sender sender  = new Sender(token.getToken(), notifaction);

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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void find_imaguser_andsend_notifacion(){
        FirebaseDatabase.getInstance().getReference().child(DataManager.NotifactionUserRoot).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild(DataManager.UserFullname)){
                                String name = dataSnapshot.child(DataManager.UserFullname).getValue().toString();
                                if(!name.isEmpty()){
                                    send_imagenotfaction(name);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void send_imagenotfaction(final String username){
        FirebaseDatabase.getInstance().getReference().child("Token").child(ReciverUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Token token = dataSnapshot.getValue(Token.class);
                            String sendermessage = username;
                            String title = message;

                            Notifaction notifaction = new Notifaction(sendermessage, "Send a image");
                            Sender sender  = new Sender(token.getToken(), notifaction);

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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /// todo send notication


    @Override
    public void onStop() {
        super.onStop();
        onlinecheack("offline");

        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
        Current_time = simpleDateFormat_time.format(calendar_time.getTime());

        Calendar calendar_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
        Current_date = simpleDateFormat_date.format(calendar_date.getTime());

        Map<String, Object> type_map = new HashMap<String, Object>();
        type_map.put("type", "no_type");
        type_map.put("time", Current_time);
        type_map.put("date", Current_date);

        Muser_database.child(SenderID)
                .child("type_status").child("type").setValue("notype");
        Muser_database.child(SenderID)
                .child("type_status").child("time").setValue(Current_time);
        Muser_database.child(SenderID)
                .child("type_status").child("date").setValue(Current_date);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
        Current_time = simpleDateFormat_time.format(calendar_time.getTime());

        Calendar calendar_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
        Current_date = simpleDateFormat_date.format(calendar_date.getTime());

        Map<String, Object> type_map = new HashMap<String, Object>();
        type_map.put("type", "no_type");
        type_map.put("time", Current_time);
        type_map.put("date", Current_date);

        Muser_database.child(SenderID)
                .child("type_status").child("type").setValue("notype");
        Muser_database.child(SenderID)
                .child("type_status").child("time").setValue(Current_time);
        Muser_database.child(SenderID)
                .child("type_status").child("date").setValue(Current_date);
    }

    private void goto_teastlayout(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.MainID, fragment).addToBackStack(null);
            transaction.commit();
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

        ONlineData.child(SenderID).child(DataManager.UserOnlineRoot).updateChildren(onlinemap)
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
    public void onResume() {
        onlinecheack("online");
        super.onResume();
    }

    private void getuser_friend_online(){
        ONlineData.child(ReciverUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild(DataManager.UserOnlineRoot)){
                                String status_type = dataSnapshot.child(DataManager.UserOnlineRoot).child(DataManager.UserCardActive).getValue().toString();
                                if(status_type.equals("online")){
                                    activedot.setBackgroundResource(R.drawable.active_dot);
                                }
                                else if(status_type.equals("offline")){
                                    activedot.setBackgroundResource(R.drawable.inactive_dot);
                                }
                            }
                        }
                        else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // ok now work its working now ?

    }


    /// todo this is the start recoding function its work android low devices but android 10 is not working
    private void startrecoding(){




        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("hh:mm:ss");
        String CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());

        try {

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/AudioRecording"+CurrentTime+".3gp";

        // app is not working after your changes when it s in debug state its waits just ok u do on your own

        mediaRecorder = new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(mFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mediaRecorder.prepare(); // check it s ok ? yes its work kindly check on all devices and then send tme the amount. thanks ok bro thanks i am teasting the other devices ok where are you from tepalkistan

            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // todo wait

    /// todo stop recoding
    private void stoprecoding(){

        if(mediaRecorder != null){
            try {
                mediaRecorder.stop();
                mediaRecorder.release();


                saveing_data_firebase(mFileName);



                mediaRecorder =  null;
            } catch (Exception e) {
                e.printStackTrace();

                mediaRecorder =  null;
            }
        }




    }

    // todo u understand bro where is the line ?
    /// save to firebase ok


    /// todo its not working my mobile its android 10 running other low devices its work bro its the problem
    private void saveing_data_firebase(String recodfile) {

        if(recodfile != null) {

            Uri uri = Uri.fromFile(new File(recodfile));
            Mprogress.setTitle("Please wait ...");
            Mprogress.setMessage("wait for a moment your recoding is uploading");
            Mprogress.setCanceledOnTouchOutside(false);
            Mprogress.show();

            StorageReference filepath = audiofile.child(uri.getLastPathSegment());
            filepath.putFile(uri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {



                                String AudioURI = task.getResult().getDownloadUrl().toString();


                                String SenderMessageRef = "Message/" + SenderID + "/" + ReciverUID;
                                String ReciverMessageRef = "Message/" + ReciverUID + "/" + SenderID;

                                DatabaseReference MessagePushID = Message_database.child("Message").child(SenderID).child(ReciverUID).push();
                                String usermaessageid = MessagePushID.getKey();
                                Calendar calendar_time = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
                                CurrentTime = simpleDateFormat_time.format(calendar_time.getTime());

                                Calendar calendar_date = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
                                CurrentDate = simpleDateFormat_date.format(calendar_date.getTime());

                                Map<String, Object> message_map = new HashMap<String, Object>();
                                message_map.put("Message", AudioURI);
                                message_map.put("Type", "Audio");
                                message_map.put("From", SenderID);
                                message_map.put("Time", CurrentTime);
                                message_map.put("Date", CurrentDate);

                                Map<String, Object> message_body = new HashMap<String, Object>();
                                message_body.put(SenderMessageRef + "/" + usermaessageid, message_map);
                                message_body.put(ReciverMessageRef + "/" + usermaessageid, message_map);

                                Message_database.updateChildren(message_body)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    /// todo notifaction
                                                    Mprogress.dismiss();

                                                    set_history_textmessage(AudioURI, recivername, DataManager.Audio);

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
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                                Mprogress.dismiss();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                            Mprogress.dismiss();
                        }
                    });

        }
    }


    private boolean mypermission(){
        if (ActivityCompat.checkSelfPermission(getActivity(), RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

            return true;
        }
        else {

            ActivityCompat.requestPermissions(getActivity(), permission, REQUEST_AUDIO_PERMISSION_CODE);
            return false;
        }
    }


    private void set_history_textmessage(String Message, String name, String type){
        Calendar calendar_time = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat(DataManager.TimePattern);
        Current_time = simpleDateFormat_time.format(calendar_time.getTime());

        Calendar calendar_date = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(DataManager.DatePattern);
        Current_date = simpleDateFormat_date.format(calendar_date.getTime());

        Map<String, Object> message_map = new HashMap<String, Object>();
        message_map.put(DataManager.NotifactionProfileUrl, reciverimageuri);
        message_map.put(DataManager.NotifactionDate, Current_date);
        message_map.put(DataManager.NotifactionTime, Current_time);
        message_map.put(DataManager.Notifactionname, recivername);
        message_map.put(DataManager.NotifactionTextBody, Message);
        message_map.put(DataManager.Type, type);
        message_map.put(DataManager.NotifactionSenderID, SenderID);
        message_map.put(DataManager.NotifactionShort, ShortDesNegative);


        NotifactionData.child(ReciverUID)
                .push()
                .updateChildren(message_map)
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
                        Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}