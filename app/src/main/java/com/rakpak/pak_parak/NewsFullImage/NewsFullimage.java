package com.rakpak.pak_parak.NewsFullImage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakpak.pak_parak.DataManager;
import com.rakpak.pak_parak.R;
import com.squareup.picasso.Picasso;


public class NewsFullimage extends Fragment {


    private String Data;
    private MaterialTextView textView;
    private PhotoView photoView;
    private DatabaseReference Mnewsdatabase;


    public NewsFullimage() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.news_fullimage, container, false);

        Mnewsdatabase = FirebaseDatabase.getInstance().getReference().child(DataManager.NewsRoot);

        Bundle bundle = getArguments();
        Data = bundle.getString(DataManager.IntentNewsData);

        photoView = view.findViewById(R.id.Photoviewnews_fullimage);
        textView = view.findViewById(R.id.Textview);

        if(!Data.isEmpty()){
            setall_data();
        }

        return view;
    }

    private void setall_data(){
        Mnewsdatabase.child(Data)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild(DataManager.NewsImage)){
                                String uri = dataSnapshot.child(DataManager.NewsImage).getValue().toString();
                                Picasso.with(getActivity()).load(uri).into(photoView);
                            }
                            if(dataSnapshot.hasChild(DataManager.NewsMessage)){
                                String message = dataSnapshot.child(DataManager.NewsMessage).getValue().toString();
                                textView.setText(message);
                            }
                        }
                        else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}