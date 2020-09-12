package com.rakpak.pak_parak.Forword;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;
import com.rakpak.pak_parak.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageForward extends Fragment {

    private RecyclerView recyclerViewlist;

    public MessageForward() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.message_forward, container, false);

        recyclerViewlist = view.findViewById(R.id.ForwardViewID);
        recyclerViewlist.setHasFixedSize(true);
        recyclerViewlist.setLayoutManager(new LinearLayoutManager(getActivity()));

        read_data();

        return view;
    }


    private void read_data(){

    }

    public static class ForwardHolder extends RecyclerView.ViewHolder{

        private View Mview;
        private CircleImageView profileimage;
        private MaterialTextView username, online_status;
        private Context context;

        public ForwardHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            context = Mview.getContext();
        }
    }


}