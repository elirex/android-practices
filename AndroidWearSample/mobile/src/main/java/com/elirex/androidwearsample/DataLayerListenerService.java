package com.elirex.androidwearsample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.elirex.common.Content;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.UnsupportedEncodingException;

/**
 * @author Sheng-Yuan Wang (2015/11/12).
 */
public class DataLayerListenerService extends WearableListenerService {

    private static final String LOG_TAG = DataLayerListenerService.class.getSimpleName();

    public static final String EXTRA_ARGS = "extra_args";

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
