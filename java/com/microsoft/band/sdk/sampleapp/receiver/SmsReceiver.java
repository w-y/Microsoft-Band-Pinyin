package com.microsoft.band.sdk.sampleapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.microsoft.band.sdk.sampleapp.tools.Util;

public class SmsReceiver extends BroadcastReceiver{
    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++){
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                for (SmsMessage message : messages){
                    String strFrom = message.getDisplayOriginatingAddress();
                    String strMsg = message.getDisplayMessageBody();

                    Util.sendToBand(context, strFrom, strMsg);
                }
            }
        }
    }
}