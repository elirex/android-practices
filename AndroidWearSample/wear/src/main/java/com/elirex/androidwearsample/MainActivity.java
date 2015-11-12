package com.elirex.androidwearsample;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import com.elirex.common.Content;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    private GoogleApiClient mGoogleApiClient;
    private Node mNode;
    private boolean mResolvingError = false;

    private Button mSyncingDataButton, mSendMessageButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR);

        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mSyncingDataButton = (Button) findViewById(R.id.button_syncing_data);
        mSyncingDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncingData("Sync Data");
            }
        });

        mSendMessageButton = (Button) findViewById(R.id.button_send_msg);
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendMessage("Open on the phone");
            }
        });

        // final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        // stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
        //     @Override
        //     public void onLayoutInflated(WatchViewStub stub) {
        //         mTextView = (TextView) stub.findViewById(R.id.text);
        //     }
        // });
    }

    private void syncingData(String msg) {
       if(mGoogleApiClient.isConnected()) {
           PutDataMapRequest putDataMapRequest =
                   PutDataMapRequest.create(Content.DATA_API_PATH);
           putDataMapRequest.getDataMap().putString(Content.WEARABLE_KEY_MSG, msg);
           putDataMapRequest.getDataMap().putDouble("timestamp", System.currentTimeMillis());
           PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
           PendingResult<DataApi.DataItemResult> pendingResult =
                   Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
           pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
               @Override
               public void onResult(DataApi.DataItemResult result) {
                   Log.d(LOG_TAG, "Syncing Data Result:" + result.getStatus().getStatusMessage());
               }
           });

       } else {
           Log.i(LOG_TAG, "Wearable not connected");
       }
    }

    private void sendMessage(String msg) {
        if(mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode.getId(),
                    Content.MESSAGE_API_PATH, msg.getBytes())
                    .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Log.d(LOG_TAG, "Send Message Result:" + sendMessageResult.getStatus().getStatusMessage());
                        }
                    });
        }
    }

    private void resolvedNode() {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                       for(Node node : nodes.getNodes()) {
                          mNode = node;
                       }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mResolvingError) mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        resolvedNode();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection has been interrupted.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if(mResolvingError) {
            return;
        } else if(result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            if(result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
                Log.i(LOG_TAG, "The Wearable API is unavailable");
            }
            // Can show dialog using GoogleApiAvailability.getErrorDialog();
            mResolvingError = true;
        }
    }

}
