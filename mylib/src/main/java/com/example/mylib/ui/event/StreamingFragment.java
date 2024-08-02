package com.example.mylib.ui.event;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mylib.R;
import com.example.mylib.ToastMessage;
import com.example.mylib.api.event.StartStream;
import com.example.mylib.api.event.StreamStartRequest;
import com.example.mylib.api.event.StreamStartResponse;
import com.example.mylib.prefs.Datastore;

import okhttp3.ResponseBody;

public class StreamingFragment extends Fragment {

    private View endButton;
    View blackoutView;
    View hideButton;
    View chatButton;
    TextView messageCountView;

    TextView chatCountText;

    ImageView locationStatus;
    ImageView videoStatus;
    ImageView observerStatus;
    TextView incidentLog;
    TextView endEventButton;

    AlertDialog chatDialog;

    String sessionId;
    String incidentId;
    String eventTitle;

    ChatPressedListener listener;
    StreamListener streamListener;

    int messageCountAtResume = -1; //-1 til initialized, the delta between this and the current

    final public static String spiceKeyUpdate = "update";
    final public static int incidentUpdateInterval = 5000; // ten seconds
    final public static int heartBeatInterval = 10 * 1000; // 55 minutes


    final public static String TAG = "STREAMING";

    Handler incidentUpdateHandler = new Handler();
    Handler heartBeatHandler = new Handler();

    boolean uploadedLastLocation = false;
    boolean hasDisplayedFirstMessageDialog = false;

    private final String spiceKeyStreamStart = "streamStart";
    private final String spiceKeyHeartbeat = "heartbeat";
    private Datastore mDatastre;
    private boolean chatStarted = false;

    public StreamingFragment() {

    }

    public StreamingFragment(String eventTitle, String sessionId, String incidentId, StreamListener streamListener, ChatPressedListener listener) {
        this.eventTitle = eventTitle;
        this.sessionId = sessionId;
        this.incidentId = incidentId;
        this.streamListener = streamListener;
        this.listener = listener;
    }

    public void startIncident(String eventTitle, String incidentId) {
        endEventButton.setText("End" +eventTitle);
//        locationStatus.setImageResource(R.drawable.ic_incident_status_location_active);
//        if (eventTitle.equalsIgnoreCase(" Walk Safe ")) {
//            incidentLog.setText(getString(R.string.status_ongoing_walk_safe));
//        } else {
//            incidentLog.setText(eventTitle + getString(R.string.status_ongoing_default));
//        }
        this.incidentId = incidentId;
        this.eventTitle = eventTitle;
        messageCountAtResume = -1;
        startStreamEvent();
    }

    public interface ChatPressedListener {
        public void onOpenChatPressed();
        public void onEndPressed();
    }

    public interface StreamListener {
        public void onStreamStart(StreamStartResponse response);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        mDatastre = new Datastore(getActivity().getApplicationContext());
        if (incidentId != null) {
            startStreamEvent();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_streaming, container, false);
        Log.d(TAG, "onCreateView: ");
        endButton = rootView.findViewById(R.id.streaming_ongoing_end);
        endEventButton = rootView.findViewById(R.id.end_button);
        return rootView;
    }


    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");

        if (incidentId != null) {
//            chatButton.setBackgroundResource(R.drawable.black_button_selector);
        }
        setOnClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        endEventButton.setText("End" +eventTitle);
//        if (mDatastre.getTimerUUID() != null) {
//            endEventButton.setText("End Timer");
//        } else {
//            endEventButton.setText("End" +eventTitle);
//        }
        if (incidentId != null) {
            messageCountAtResume = -1;
//            locationStatus.setImageResource(R.drawable.ic_incident_status_location_active);
//            if (mDatastre.isLocationOnly()) {
////                startUpdatingIncident();
//            } else {
//                setVideoStatus(true);
//            }
//            if (chatStarted) {
//                if (eventTitle.equalsIgnoreCase(" Walk Safe ")) {
//                    incidentLog.setText(getString(R.string.walk_safe_observer_present_msg));
//                } else {
//                    incidentLog.setText(getString(R.string.sos_observer_present_msg));
//                }
//                observerStatus.setImageResource(R.drawable.ic_incident_status_observer_active);
//            } else {
//                if (eventTitle.equalsIgnoreCase(" Walk Safe ")) {
//                    incidentLog.setText(getString(R.string.status_ongoing_walk_safe));
//                } else {
//                    incidentLog.setText(eventTitle + getString(R.string.status_ongoing_default));
//                }
//            }
        }
//        messageCountView.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
//        stopUpdatingIncident();
//        if (chatDialog != null && chatDialog.isShowing()) {
//            chatDialog.dismiss();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stopUpdatingIncident();
    }

    private void startStreamEvent() {
        String mediaType = "Video";

        StreamStartRequest requestContent = new StreamStartRequest(incidentId, mediaType);
        StartStream request = new StartStream();
        request.startEventStream(requestContent, new StartStream.OnResponseListener() {
            @Override
            public void onResponse(StreamStartResponse result) {
                streamListener.onStreamStart(result);
//                startUpdatingIncident();
                ToastMessage.toastMessage(getActivity(), "Start Stream : Success");
            }

            @Override
            public void onErrorResponse(ResponseBody errBody) {

            }

            @Override
            public void onFailure() {
            }
        });
    }

//    private void handleIncidentUpdateResponse(IncidentResponse response) {
//        //handle chat
//        if (response.incident_chat.size() > 0 && !hasDisplayedFirstMessageDialog) {
//            hasDisplayedFirstMessageDialog = true;
//            chatButtonPressed();
//        }
//        if (messageCountAtResume == -1) {
//            messageCountAtResume = response.incident_chat.size();
//            return;
//        } else if (messageCountAtResume == response.incident_chat.size()) {
//            //no new messages, do nothing
//        } else {
//            showChatCount(response.incident_chat.size());
//        }
//
//        //handle status bar
//        if (response.observers_watching.size() > 0) {
//            chatStarted = true;
//            if (eventTitle.equalsIgnoreCase(" Walk Safe ")) {
//                incidentLog.setText(getString(R.string.walk_safe_observer_present_msg));
//            } else {
//                incidentLog.setText(getString(R.string.sos_observer_present_msg));
//            }
//            observerStatus.setImageResource(R.drawable.ic_incident_status_observer_active);
//        } else {
//            observerStatus.setImageResource(R.drawable.ic_incident_status_observer_inactive);
//        }
//
//        if (uploadedLastLocation) {
//            locationStatus.setImageResource(R.drawable.ic_incident_status_location_active);
//        } else {
//            locationStatus.setImageResource(R.drawable.ic_incident_status_location_inactive);
//        }
//        try {
//        } catch (Exception ignored) { }
//    }

//    private void showChatCount(Integer count) {
//        chatCountText.setVisibility(View.VISIBLE);
//        chatCountText
//                .setText(String.valueOf(count - messageCountAtResume));
//    }

//    public void setEventStatus() {
//        if (eventTitle.equalsIgnoreCase(" Walk Safe ")) {
//            incidentLog.setText(getString(R.string.walk_safe_status_ongoing));
//        } else if (eventTitle.equalsIgnoreCase(" Timer ")) {
//            incidentLog.setText(getString(R.string.timer_status_ongoing));
//        } else {
//            incidentLog.setText(getString(R.string.sos_status_ongoing));
//        }
//    }

//    public void setVideoStatus(boolean wasSuccessful) {
//        if (wasSuccessful) {
//            videoStatus.setImageResource(R.drawable.ic_incident_status_video_active);
//        } else {
//            videoStatus.setImageResource(R.drawable.ic_incident_status_video_inactive);
//        }
//    }

//    public void setLocationStatus(boolean wasSuccessful) {
//        uploadedLastLocation = wasSuccessful;
//    }

    private void startUpdatingIncident() {
        incidentUpdateRunnable.run();
    }

    private void stopUpdatingIncident() {
        incidentUpdateHandler.removeCallbacks(incidentUpdateRunnable);
    }

//    private void updateIncident() {
//
//        IncidentDetail request = new IncidentDetail();
//        request.getIncidentDetail(incidentId, new IncidentDetail.OnResponseListener() {
//            @Override
//            public void onResponse(IncidentResponse result) {
//                if (result == null) {
//                    return;
//                }
//                Log.e(TAG, result.status_message != null ? result.status_message : "No status message");
//                handleIncidentUpdateResponse(result);
//            }
//
//            @Override
//            public void onErrorResponse(ResponseBody errBody) {
//
//            }
//
//            @Override
//            public void onFailure() {
////                Log.e(TAG, getString(R.string.simple_error));
//            }
//        });
//    }

//    private void blackoutViewPressed() {
//        blackoutView.setVisibility(View.GONE);
//        blackoutView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//    }

//    private void hideButtonPressed() {
//        blackoutView.setVisibility(View.VISIBLE);
//        blackoutView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
//    }

//    private void chatButtonPressed() {
//        chatCountText.setVisibility(View.GONE);
//        if (hasDisplayedFirstMessageDialog) {
//            listener.onOpenChatPressed();
//        }
//    }

    private void endButtonPressed() {
        listener.onEndPressed();
    }

    Runnable incidentUpdateRunnable = new Runnable() {
        @Override
        public void run() {
//            updateIncident();
            incidentUpdateHandler.postDelayed(incidentUpdateRunnable, incidentUpdateInterval);
        }
    };

    private void setOnClickListeners() {
//        blackoutView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                blackoutViewPressed();
//            }
//        });
//        hideButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                hideButtonPressed();
//            }
//        });
//        chatButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chatButtonPressed();
//            }
//        });
        endButton.setOnClickListener(view -> {
            endButtonPressed();
            getActivity().onBackPressed();
        });
    }
}
