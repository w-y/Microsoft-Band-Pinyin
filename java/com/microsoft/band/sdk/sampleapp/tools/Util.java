package com.microsoft.band.sdk.sampleapp.tools;

import android.content.Context;
import com.microsoft.band.sdk.sampleapp.BandNotificationToPinyin;
import com.microsoft.band.sdk.sampleapp.BandNotificationToPinyinActivity;

import net.sourceforge.pinyin4j.PinyinHelper;

public class Util {

    public static String toPinyin(String strMsg) {
        StringBuffer buffer = new StringBuffer();
        char[] text = strMsg.toCharArray();

        for (int i = 0; i < text.length; i++) {
            if (Character.toString(text[i]).matches("[\u4E00-\u9FA5]+")) {
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(text[i]);

                if (pinyinArray.length > 0) {
                    buffer.append(pinyinArray[0]);
                }

                buffer.append(" ");
            } else {
                buffer.append(text[i]);
            }
        }

        return buffer.toString().trim();
    }

    public static void sendToBand(Context context, String title, String message) {
        BandNotificationToPinyin mApp = (BandNotificationToPinyin) context.getApplicationContext();
        BandNotificationToPinyinActivity mActivity = (BandNotificationToPinyinActivity)mApp.getCurrentActivity();

        if (mActivity != null) {
            mActivity.sendToBand(title, message);
        }
    }
}