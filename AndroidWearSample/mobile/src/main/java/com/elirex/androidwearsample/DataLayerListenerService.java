package com.elirex.androidwearsample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.elirex.common.Content;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * @author Sheng-Yuan Wang (2015/11/12).
 */
public class DataLayerListenerService extends WearableListenerService {

    private static final String LOG_TAG = DataLayerListenerService.class.getSimpleName();

    public static final String EXTRA_ARGS = "extra_args";


    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onChannelOpened(Channel channel) {
        channel.getInputStream(mGoogleApiClient).setResultCallback(new ResultCallback<Channel.GetInputStreamResult>() {
            @Override
            public void onResult(Channel.GetInputStreamResult getInputStreamResult) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(
                            getInputStreamResult.getInputStream()));
                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    Bundle args = new Bundle();
                    args.putString(Content.WEARABLE_KEY_MSG, buffer.toString());
                    Intent intent = new Intent(DataLayerListenerService.this, MainActivity.class);
                    intent.putExtra(EXTRA_ARGS, args);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } catch (IOException e) {

                } finally {
                    try {
                        if(reader != null) reader.close();
                    } catch (IOException ex) {}
                }
            }
        });
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if(messageEvent.getPath().equals(Content.MESSAGE_API_PATH)) {
            try {
                String message = new String(messageEvent.getData(), "UTF-8");
                Bundle args = new Bundle();
                args.putString(Content.WEARABLE_KEY_MSG, message);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(EXTRA_ARGS, args);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (UnsupportedEncodingException e) {

            }
        }
    }

}
