package com.rakpak.pak_parak.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textview.MaterialTextView;
import com.rakpak.pak_parak.Model.OnBoardingModal;
import com.rakpak.pak_parak.R;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingHolder> {

    private List<OnBoardingModal> onBoardingModalList;

    public OnboardingAdapter(List<OnBoardingModal> onBoardingModalList) {
        this.onBoardingModalList = onBoardingModalList;
    }

    @NonNull
    @Override
    public OnboardingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ornoarding_screen_layout, parent, false);
        return new OnboardingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingHolder holder, int position) {
        holder.Onboardingdata(onBoardingModalList.get(position));
    }

    @Override
    public int getItemCount() {
        return onBoardingModalList.size();
    }


    public static class OnboardingHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private MaterialTextView title, desc;

         OnboardingHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.OnboardingImage);
            title = itemView.findViewById(R.id.OnbaordingTitle);
            desc = itemView.findViewById(R.id.OnboardingDescptrion);
        }

        void Onboardingdata(OnBoardingModal onBoardingModal){
            title.setText(onBoardingModal.getTitle());
            desc.setText(onBoardingModal.getDescptrion());
            imageView.setImageResource(onBoardingModal.getImage());
       }
    }
}
