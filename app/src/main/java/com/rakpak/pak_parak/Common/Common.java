package com.rakpak.pak_parak.Common;

import com.rakpak.pak_parak.Remote.APIServices;
import com.rakpak.pak_parak.Remote.FCMnotifactionClint;

public class Common {

    public static final String BaseUrl="https://fcm.googleapis.com/";




    public static APIServices getFCMClient(){
        return FCMnotifactionClint.getClint(BaseUrl).create(APIServices.class);
    }
}
