package com.example.musicclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ComponentName;
//import android.support.v4.app.ActivityCompat;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.musiccommon.MusicCentralApi;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "MusicClient";
    private MusicCentralApi musicService;
    private boolean mIsBound = false;
    private Map<String, String> hm = new HashMap<>();
    private String link = "https://drive.google.com/uc?export=download&id=";

    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        final TextView output = (TextView) findViewById(R.id.output);

        final Button goButton = (Button) findViewById(R.id.goButton);
        final Button stopButton  = findViewById(R.id.stopButton);
        goButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                try {



                    // Call KeyGenerator and get a new ID
                    if (mIsBound) {

                    } else {
                        Log.i(TAG, "Ugo says that the service was not bound!");
                    }

                } catch (Exception e) {

                    Log.e(TAG, e.toString());

                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,""+mIsBound);
        Button goButton = findViewById(R.id.goButton);
        Button stopButton = findViewById(R.id.stopButton);
        Button infoButton = findViewById(R.id.infoButton);
        final TextView output = findViewById(R.id.output);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission("com.example.MusicCentral.PERMISSION") !=
                        PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{"com.example.MusicCentral.PERMISSION"}, 0);
                }
                else{
                    if (!mIsBound) {

                        boolean b = false;
                        Intent i = new Intent(MusicCentralApi.class.getName());

                        // UB:  Stoooopid Android API-20 no longer supports implicit intents
                        // to bind to a service #@%^!@..&**!@
                        // Must make intent explicit or lower target API level to 19.
                        ResolveInfo info = getPackageManager().resolveService(i, 0);
                        i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

                        b = bindService(i, MainActivity.this.mConnection, Context.BIND_AUTO_CREATE);
                        if (b) {
                            Log.i(TAG, ""+this);
                            Log.i(TAG, "Ugo go says bindService() succeeded!");
                            output.setText(R.string.startText);
                        } else {
                            Log.i(TAG, "Ugo says bindService() failed!");
                        }
                    }
                    else {
                        Log.i(TAG, ""+mIsBound);
                    }

                }
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsBound) {
                    try {
                        EditText editText = findViewById(R.id.editText);
                        int index = Integer.parseInt(editText.getText().toString());
                        hm = musicService.getInfo(index);
                        output.setText(musicService.getTitle(index));
                        Log.i(TAG, "" + hm);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"CLICK STOP");
                try {
                    if(mIsBound) {
                        Log.i(TAG, "CLICK UNBIND");
                        mIsBound = false;
                        unbindService(MainActivity.this.mConnection);
                        output.setText(R.string.stopText);
                        Log.i(TAG,"CLICK UNBOUND");
                        Log.i(TAG, ""+mIsBound);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onPause() {

        Log.i(TAG, "entered on pause");
        super.onPause();

        if (mIsBound) {
            unbindService(MainActivity.this.mConnection);
            Log.i(TAG, ""+mIsBound);
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder iservice) {

            musicService = MusicCentralApi.Stub.asInterface(iservice);

            mIsBound = true;

        }

        public void onServiceDisconnected(ComponentName className) {

            musicService = null;

            mIsBound = false;
            Log.i(TAG, "disconnected");

        }
    };

    public void onStart() {
        super.onStart();

        client.connect();
        Action viewAction = Action.newAction(Action.TYPE_VIEW,"MusicClient Page", null,
                Uri.parse("android-app://com.example.musicclient/http/host/path"));
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,"MusicClient Page",
               null, Uri.parse("android-app://com.example.musicclient/http/host/path"));
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
