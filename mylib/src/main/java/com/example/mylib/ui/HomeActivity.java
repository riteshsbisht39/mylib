package com.example.mylib.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.mylib.KuvrrSDK;
import com.example.mylib.R;
import com.example.mylib.ToastMessage;
import com.example.mylib.api.event.EndEvent;
import com.example.mylib.api.event.IncidentEndStatus;
import com.example.mylib.api.event.IncidentRequest;
import com.example.mylib.api.event.IncidentResponse;
import com.example.mylib.api.event.SendEvent;
import com.example.mylib.api.event.StopStream;
import com.example.mylib.api.event.StreamStartResponse;
import com.example.mylib.api.event.StreamStopResponse;
import com.example.mylib.prefs.Datastore;
import com.example.mylib.service.IncidentService;
import com.example.mylib.ui.event.StreamingFragment;

import java.util.ArrayList;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import okhttp3.ResponseBody;

public class HomeActivity extends AppCompatActivity implements StreamingFragment.StreamListener,
        StreamingFragment.ChatPressedListener {

    private static final String TAG = "HomeActivity";
    private Datastore ds;
    private static Context context;
    private FrameLayout streamingView;

    private LinearLayout streamFunctionsLayout;

    private ImageView icVideoOn;

    private ImageView icVideoOff;

    private ImageView icSwitchCam;

    private ImageView icSpeakerOn;

    private ImageView icSpeakerOff;

    private ImageView icLocalAudioUnMute;

    private ImageView icLocalAudioMute;

    private String mCurrentIncidentUUID;
    private Boolean isTwoWayStream = false;
    private String eventTitle = " SOS ";

    private boolean isRemoteUserJoined = false;
    private boolean isJoined = false;
    private RtcEngine agoraEngine;
    private ChannelMediaOptions options;
    private SurfaceView localSurfaceView;
    private int uid = 110;
    private ServiceConnection mIncidentServiceConn;
    private IncidentService.IncidentServiceBinder mIncidentServiceBinder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ds = new Datastore(getApplicationContext());
        context = getApplicationContext();

        streamingView = findViewById(R.id.local_stream_video_view);
        streamFunctionsLayout = findViewById(R.id.stream_functions_layout);
        sendEvent();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveChannel();
        stopIncidentService();
    }

    private void sendEvent() {
        switchToStreaming();
        IncidentRequest incidentRequest = new IncidentRequest(ds.getDeviceUuid());

        Location currentLocation = KuvrrSDK.getCurrentLocation();
        if (currentLocation != null) {
            incidentRequest.lat = (float) currentLocation.getLatitude();
            incidentRequest.lng = (float) currentLocation.getLongitude();
            incidentRequest.horizontal_accuracy = currentLocation.getAccuracy();
        } else { //currentLocation may be null if the device has never acquired a location. give fake values.
            incidentRequest.lat = 1;
            incidentRequest.lng = 1;
            incidentRequest.horizontal_accuracy = 100;
        }

        incidentRequest.responder_type = "sos";

        incidentRequest.pb_trigger = false;

        incidentRequest.app_location_only = false;
        incidentRequest.ems = true;
        incidentRequest.media_type = "Video";

        SendEvent request = new SendEvent();
        request.sendEvent(incidentRequest, new SendEvent.OnResponseListener() {
            @Override
            public void onResponse(IncidentResponse result) {
                ToastMessage.toastMessage(context, "Sent SOS Event : Success");
                mCurrentIncidentUUID = result.uuid;
                isTwoWayStream = result.two_way_live_stream;
                handleIncident(result.uuid, result.channel_name, result.portrait_only);
            }

            @Override
            public void onErrorResponse(ResponseBody errBody) {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void handleIncident(String incidentUUID, String chName, Boolean portraitOnly) {
        if (portraitOnly != null) {
            if (portraitOnly) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        }
        streamingView.setVisibility(View.VISIBLE);
        setupVideoSDKEngine();
        setupLocalVideo();
        joinChannel(chName);

        Intent foregroundIncidentServiceIntent = new Intent(this, IncidentService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(foregroundIncidentServiceIntent);
        } else {
            startService(foregroundIncidentServiceIntent);
        }

        mIncidentServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mIncidentServiceBinder = (IncidentService.IncidentServiceBinder) iBinder;
                mIncidentServiceBinder.startIncident(ds.getSessionId(), mCurrentIncidentUUID);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mIncidentServiceBinder = null;
            }
        };
        bindService(foregroundIncidentServiceIntent, mIncidentServiceConn, Context.BIND_AUTO_CREATE);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(
                R.id.incident_fragment_container);
        if (currentFragment instanceof StreamingFragment) {
            ((StreamingFragment) currentFragment).startIncident(eventTitle, mCurrentIncidentUUID);
        }
    }

    private void switchToStreaming() {
        try {
            getSupportFragmentManager().popBackStackImmediate();
        } catch (Exception ignored) {
        }
        StreamingFragment fragment = new StreamingFragment(eventTitle, ds.getSessionId(),
                mCurrentIncidentUUID, this,this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.incident_fragment_container, fragment)
                .commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void onStreamStart(StreamStartResponse response) {

    }

    private void stopIncidentService() {
        try {
            if (mIncidentServiceConn != null)
                unbindService(mIncidentServiceConn);
            mIncidentServiceConn = null;
            mIncidentServiceBinder = null;
            IncidentService.getInstance().onDestroy();
            stopService(new Intent(this, IncidentService.class));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void setupVideoSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
//            config.mAppId = mDatastore.getUserLSAppId();
            config.mAppId = "e9c9b52fcad241bcb1655f58fc2c16d6";
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
            agoraEngine.enableVideo();

            agoraEngine.muteLocalVideoStream(false);
            agoraEngine.muteLocalAudioStream(false);

            options = new ChannelMediaOptions();
            options.channelProfile = io.agora.rtc2.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
            options.clientRoleType = io.agora.rtc2.Constants.CLIENT_ROLE_BROADCASTER;
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    private void setupLocalVideo() {
        findViewById(R.id.local_user_img).setVisibility(View.VISIBLE);
        localSurfaceView = new SurfaceView(getBaseContext());
        streamingView.addView(localSurfaceView);
        agoraEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
        localSurfaceView.setVisibility(View.VISIBLE);
        agoraEngine.startPreview();
        streamFunctionsLayout.setVisibility(View.VISIBLE);
//        icLocalAudioMute.setEnabled(true);
//        icLocalAudioMute.setColorFilter(null);
//        icVideoOff.setEnabled(true);
//        icVideoOff.setColorFilter(null);
//        icSwitchCam.setEnabled(true);
//        icSwitchCam.setColorFilter(null);
    }

    private void setupRemoteVideo(int uid) {
//        streamingViewRemote.setVisibility(View.VISIBLE);
//        remoteSurfaceView = new SurfaceView(getBaseContext());
//        streamingViewRemote.addView(remoteSurfaceView);
//        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
//        remoteSurfaceView.setVisibility(View.VISIBLE);
    }

    public void joinChannel(String channelName) {
        try {
            agoraEngine.joinChannel(null, channelName, uid, options);
        } catch (Exception e) {
            Log.d(TAG, "joinChannel Error: " + e.getLocalizedMessage());
        }
    }

    public void leaveChannel() {
        if (agoraEngine != null) {
            if (isJoined) {
                agoraEngine.leaveChannel();
                agoraEngine.stopPreview();

                streamingView.removeView(localSurfaceView);

                isJoined = false;
                isRemoteUserJoined = false;
//                if (streamEndTimer != null) {
//                    streamEndTimer.cancel();
//                }
                new Thread(() -> {
                    RtcEngine.destroy();
                    agoraEngine = null;
                }).start();
            }
        }
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserJoined(int ruid, int elapsed) {
            runOnUiThread(() -> {
                if (isTwoWayStream && !isRemoteUserJoined) {
                    isRemoteUserJoined = true;
                    setupRemoteVideo(ruid);
                }
            });
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
//            runOnUiThread(() -> {
//            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(() -> {
//                streamingViewRemote.setVisibility(View.GONE);
                isRemoteUserJoined = false;
            });
        }

        @Override
        public void onUserMuteVideo(int uid, boolean muted) {
            super.onUserMuteVideo(uid, muted);
            runOnUiThread(() -> {
//                if (remoteSurfaceView != null) {
//                    remVideoMuted = muted;
//                    if (muted)
//                        remoteSurfaceView.setVisibility(View.GONE);
//                    else
//                        remoteSurfaceView.setVisibility(View.VISIBLE);
//                }
            });
        }
    };

    private void stopStreamEvent(String incidentUuid) {
        StopStream request = new StopStream();
        request.stopEventStream(incidentUuid, new StopStream.OnResponseListener() {
            @Override
            public void onResponse() {
            }

            @Override
            public void onErrorResponse(ResponseBody errBody) {

            }

            @Override
            public void onFailure() {

            }
        });
    }


    private void putStatus(String status, String statusMessage) {
        IncidentEndStatus requestContent = new IncidentEndStatus(status, statusMessage);
        EndEvent request = new EndEvent();
        request.endEventWithStatus(mCurrentIncidentUUID, requestContent, new EndEvent.OnResponseListener() {
            @Override
            public void onResponse(IncidentResponse result) {
                ToastMessage.toastMessage(context, "EndEvent : onSuccess");
            }

            @Override
            public void onErrorResponse(ResponseBody errBody) {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void onOpenChatPressed() {

    }

    @Override
    public void onEndPressed() {
        streamingView.setVisibility(View.GONE);
        streamFunctionsLayout.setVisibility(View.GONE);
        stopStreamEvent(mCurrentIncidentUUID);
        putStatus(getString(R.string.event_end_other),
                "Testing");
//        finish();
    }
}