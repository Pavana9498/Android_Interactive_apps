package com.example.musicservice;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.musiccommon.MusicCentralApi;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicCentral extends Service {

    private MusicCentralApi musicApi;

    private String[] titles;
    private String[] artists;
    private String[] urls;

   private final MusicCentralApi.Stub musicBinder = new MusicCentralApi.Stub() {

       @Override
       public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                              double aDouble, String aString) throws RemoteException {

       }

       public Map<String, String> getInfo(int index) {

           titles = getResources().getStringArray(R.array.titles);
           artists = getResources().getStringArray(R.array.artists);
           urls = getResources().getStringArray(R.array.links);


           checkCallingPermission("com.example.MusicCentral.PERMISSION");
           Map<String, String> hm= new HashMap<>();
           try {
               hm.put("title", titles[index-1]);
               hm.put("artist", artists[index-1]);
               hm.put("url", urls[index-1]);
               Log.i("Service click", "" + hm);

               return hm;
           }catch (Exception e){
               Log.i("tag", ""+e);
           }

           return hm;
       }

       public String getTitle(int index) {

           checkCallingPermission("com.example.MusicCentral.PERMISSION");

           return titles[index];

       }

   };

   @Override
   public IBinder onBind(Intent intent) {

       return musicBinder;
   }

}
