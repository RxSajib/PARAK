package com.rakpak.pak_parak.Services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.rakpak.pak_parak.Model.Token;

public class MyFirebaseServices extends FirebaseInstanceIdService {

    private String TAG ="MyFirebaseIdService";
    private FirebaseAuth Mauth;
    private String Currentuser;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken= FirebaseInstanceId.getInstance().getToken();
        Mauth = FirebaseAuth.getInstance();



        //   Log.d(TAG, "Refreshed token: " + refreshedToken);


        if(refreshedToken!=null){
            sendNotification(refreshedToken);



        }


    }

    private void sendNotification(String refreshedToken) {

        FirebaseDatabase db= FirebaseDatabase.getInstance();
        DatabaseReference referance=db.getReference("Token");
        Token token=new Token(refreshedToken,true);

        FirebaseUser Muser = Mauth.getCurrentUser();
        if(Muser != null){
            referance.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
        }



    }
}
