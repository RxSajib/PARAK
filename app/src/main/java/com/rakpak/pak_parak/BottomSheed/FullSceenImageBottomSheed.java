package com.rakpak.pak_parak.BottomSheed;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rakpak.pak_parak.R;
import com.squareup.picasso.Picasso;

public class FullSceenImageBottomSheed extends BottomSheetDialogFragment {

    String uri;
    Context context;
    PhotoView photoView;

    public FullSceenImageBottomSheed(String uri) {
        this.uri = uri;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.full_screenimage_bottomsheeed, null, false);
        photoView = view.findViewById(R.id.PhotoView);


        if(uri != null){
            Picasso.with(context).load(uri).into(photoView);
        }

        return view;
    }
}
