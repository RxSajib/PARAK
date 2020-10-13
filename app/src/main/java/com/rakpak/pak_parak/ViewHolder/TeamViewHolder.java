package com.rakpak.pak_parak.ViewHolder;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textview.MaterialTextView;
import com.rakpak.pak_parak.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public  class TeamViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView profileimage;
    private MaterialTextView username, degniction;
    private Context context;
    private View Mview;

    public TeamViewHolder(@NonNull View itemView) {
        super(itemView);

        Mview = itemView;
        context = Mview.getContext();
        username = Mview.findViewById(R.id.Teamname);
        degniction = Mview.findViewById(R.id.DesgnationID);
        profileimage = Mview.findViewById(R.id.TeamImage);
    }

    public void setProfileimageset(String ima){
        Picasso.with(context).load(ima).resize(200, 200).centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(profileimage, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                Picasso.with(context).load(ima).resize(200, 200).centerCrop().into(profileimage);

            }
        });
    }
    public void setUsernameset(String name){
        username.setText(name);
    }
    public void setDegnictionset(String des){
        degniction.setText(des);
    }
}