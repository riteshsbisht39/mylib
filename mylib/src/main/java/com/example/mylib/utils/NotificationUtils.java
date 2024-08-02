package com.example.mylib.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.example.mylib.R;

public class NotificationUtils extends ContextWrapper {
    public enum CHANNEL {
        ACTIVE_INCIDENT

    }
    public static final String ACTIVE_INCIDENT_CHANNEL_ID = CHANNEL.ACTIVE_INCIDENT.name();

    private NotificationManager mManager;

    public NotificationUtils(Context base) {
        super(base);
    }

    public void sendAlert(CHANNEL channel, int notificationId, NotificationCompat.Builder builder) {
        String channelId = createActiveIncidentChannel();

        builder.setChannelId(channelId);
        getManager().notify(notificationId, builder.build());
    }

    public String createActiveIncidentChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel androidChannel = new NotificationChannel(ACTIVE_INCIDENT_CHANNEL_ID,getString(R.string.channel_active_incident_name), NotificationManager.IMPORTANCE_HIGH);
            androidChannel.setDescription(getString(R.string.channel_active_incident_description));
            androidChannel.setBypassDnd(true);
            androidChannel.enableLights(true);
            androidChannel.enableVibration(true);
            androidChannel.setLightColor(Color.RED);
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            getManager().createNotificationChannel(androidChannel);
        }
        return ACTIVE_INCIDENT_CHANNEL_ID;
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
}
