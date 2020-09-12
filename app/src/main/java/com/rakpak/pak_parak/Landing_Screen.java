package com.rakpak.pak_parak;

import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rakpak.pak_parak.Adapter.OnboardingAdapter;
import com.rakpak.pak_parak.Homepage.goto_homepage;
import com.rakpak.pak_parak.LoginPage.LoginPage;
import com.rakpak.pak_parak.Model.OnBoardingModal;

import java.util.ArrayList;
import java.util.List;

public class Landing_Screen extends Fragment {

    private ViewPager2 viewPager2;
    private LinearLayout LayoutonboardingIndictors;
    private OnboardingAdapter onboardingAdapter;
    private MaterialButton nextbutton;
    private FirebaseAuth Mauth;


    public Landing_Screen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.landing__screen, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Mauth = FirebaseAuth.getInstance();
        nextbutton = view.findViewById(R.id.NextButtonID);
        LayoutonboardingIndictors = view.findViewById(R.id.OnboardingDots);


        viewPager2 = view.findViewById(R.id.OnboardingViewPager);

        setdataon_boarding();



        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewPager2.getCurrentItem()+1 < onboardingAdapter.getItemCount()){
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
                }
                else {
                    // goto home page
                    goto_loginpage(new LoginPage());
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser Muser = Mauth.getCurrentUser();
        if(Muser != null){
            goto_homepage(new goto_homepage());
        }
    }

    private  void setdataon_boarding(){

      List<OnBoardingModal> onBoardingModals = new ArrayList<>();


      OnBoardingModal onBoardingModalone = new OnBoardingModal();
      onBoardingModalone.setImage(R.drawable.islamscreen_one);
       onBoardingModalone.setTitle(getResources().getString(R.string.onbording_title));
       onBoardingModalone.setDescptrion(getResources().getString(R.string.onboarding_desone));

        OnBoardingModal onBoardingModaltwo = new OnBoardingModal();
        onBoardingModaltwo.setImage(R.drawable.islamscreen_two);
       onBoardingModaltwo.setTitle(getResources().getString(R.string.onbording_title));
       onBoardingModaltwo.setDescptrion(getResources().getString(R.string.onboarding_des_two));


        OnBoardingModal onBoardingModalthree = new OnBoardingModal();
        onBoardingModalthree.setImage(R.drawable.islamscreen_three);
        onBoardingModalthree.setTitle(getResources().getString(R.string.onbording_title));
        onBoardingModalthree.setDescptrion(getResources().getString(R.string.onboarding_des_three));

       onBoardingModals.add(onBoardingModalone);
       onBoardingModals.add(onBoardingModaltwo);
       onBoardingModals.add(onBoardingModalthree);

        onboardingAdapter = new OnboardingAdapter(onBoardingModals);
        viewPager2.setAdapter(onboardingAdapter);


        setuponboardingIncodors();
        setcurrentOnboadingIndicators(0);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setcurrentOnboadingIndicators(position);
            }
        });
    }

    private void setuponboardingIncodors(){
        ImageView[] indicatore = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT
        );

        layoutParams.setMargins(8, 0, 8, 0);
        for (int i=0; i<indicatore.length; i++){
            indicatore[i] = new ImageView(getActivity());
            indicatore[i].setImageDrawable(ContextCompat.getDrawable(getActivity(),
                    R.drawable.onboarding_inactive));

            indicatore[i].setLayoutParams(layoutParams);
            LayoutonboardingIndictors.addView(indicatore[i]);
        }

    }

    private void goto_loginpage(Fragment fragment){
        if(fragment != null){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            transaction.replace(R.id.MainID, fragment);
            transaction.commit();
        }
    }

    private void setcurrentOnboadingIndicators(int index){
        int childcount = LayoutonboardingIndictors.getChildCount();

        for(int i=0; i< childcount; i++){
            ImageView imageView = (ImageView) LayoutonboardingIndictors.getChildAt(i);
            if(i == index){
                imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.onboarding_indicator_activity));

            }
            else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.onboarding_inactive));

            }
        }

        if(index ==onboardingAdapter.getItemCount() - 1){
            nextbutton.setText("Start");
        }
        else {
            nextbutton.setText("Next");
        }
    }


    private void goto_homepage(Fragment fragment){
        if(fragment != null){
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slider_from_right    , R.anim.slide_outfrom_left);
            fragmentTransaction.replace(R.id.MainID, fragment);
            fragmentTransaction.commit();
        }
    }
}