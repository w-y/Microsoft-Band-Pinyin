package com.microsoft.band.sdk.sampleapp.listener;

import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.microsoft.band.sdk.sampleapp.tools.Util;

public class NotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        getActiveNotifications();

        String strMsg = sbn.getNotification().tickerText.toString();

        Context context = this.getApplicationContext();

        strMsg = Util.toPinyin(strMsg);
        int sepIndex = strMsg.indexOf(":");

        String strContent = sepIndex >= 0 ? strMsg.substring(sepIndex + 1) : strMsg;
        String strTitle = sepIndex >= 0 ? strMsg.substring(0, sepIndex) : "Notification";

        Util.sendToBand(context, strTitle, strContent);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}
