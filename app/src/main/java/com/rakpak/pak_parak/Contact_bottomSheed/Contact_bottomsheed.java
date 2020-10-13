package com.rakpak.pak_parak.Contact_bottomSheed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakpak.pak_parak.R;

public class Contact_bottomsheed extends BottomSheetDialogFragment {

    String _id;
    private ImageView cross_button; MaterialTextView phonenumber, emailaddress;
    private DatabaseReference Mjobs_data;

    private MaterialTextView callbutton, emailbutton;

    public Contact_bottomsheed(String _id) {
        this._id = _id;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.job_contact_bottomsheed, null, false);

        Mjobs_data = FirebaseDatabase.getInstance().getReference().child("Jobs");
        cross_button = view.findViewById(R.id.CrossButtonID);
        phonenumber = view.findViewById(R.id.PhoneNumberText);
        emailaddress = view.findViewById(R.id.EmailAddressText);

        callbutton = view.findViewById(R.id.CallText);
        emailbutton = view.findViewById(R.id.EmailButtonID);

        cross_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Mjobs_data.child(_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild("contact_number")){
                                String number = dataSnapshot.child("contact_number").getValue().toString();
                                phonenumber.setText(number);

                                callbutton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:"+number));
                                        startActivity(intent);
                                    }
                                });
                            }
                            if(dataSnapshot.hasChild("contact_email")){
                                String email = dataSnapshot.child("contact_email").getValue().toString();
                                emailaddress.setText(email);

                                emailbutton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.putExtra(Intent.EXTRA_EMAIL, email);
                                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                                        intent.putExtra(Intent.EXTRA_TEXT, "");

                                        intent.setType("message/rfc822");
                                        startActivity(Intent.createChooser(intent, "open with email"));
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return view;
    }
}
