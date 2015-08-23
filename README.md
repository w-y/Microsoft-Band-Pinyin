# Microsoft-Band-Pinyin 安卓版本

## 简介

微软手环（Microsoft Band）环不能支持中文字符显示，Microsoft-Band-Pinyin把安卓手机上的通知（或者短消息）转成拼音后发送到手环（tile）上。

支持的Android API版本是19及以上。
使用的sdk版本是microsoft-band-1.3.10622.3.jar。

## 使用

需要先安装Microsoft Health
，如果没有Google Play，可以通过下载apk的方式安装，开发使用的版本为1.3.10625.4-9。

Google Play: <https://play.google.com/store/apps/details?id=com.microsoft.kapp>

首先需要增加一个tile，所有的通知都会发给这个tile：

![image](http://77wdbg.com1.z0.glb.clouddn.com/githubScreenshot_2015-08-23-15-52-04.png?imageView/2/w/250/q/85)


如果tile满了，需要通过管理界面去掉一个现有的tile：

![image](http://77wdbg.com1.z0.glb.clouddn.com/githubScreenshot_2015-08-23-15-51-31.png?imageView/2/w/250/q/85)

通过NotificationListenerService来实现获取手机通知内容并转为拼音，通过BroadcastReceiver方式获取短信内容及转拼音。

在AndroidManifest.xml中，可以根据需要进行配置。

### 通知内容转拼音

    <service
      android:name="com.microsoft.band.sdk.sampleapp.listener.NotificationListener"
      android:label="@string/service_name"
      android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" >
      <intent-filter>
        <action android:name="android.service.notification.NotificationListenerService" />
       </intent-filter>
    </service>


需要开启Notification Access Permission，才能获得通知的内容，设置>安全>通知访问：


![image](http://77wdbg.com1.z0.glb.clouddn.com/githubBE5C34EA93188F7C0D08FF7739861D35.jpg?imageView/2/w/250/q/85)


![image](http://77wdbg.com1.z0.glb.clouddn.com/github2F4F5B4216189A053A52EBB0EFF5D94C.jpg?imageView/2/w/250/q/85)

选择Band Notification To Pinyin Service:

![image](http://77wdbg.com1.z0.glb.clouddn.com/githubE218C200D2A0CE3EACF429D09AF19C46.jpg?imageView/2/w/250/q/85)


### 短信内容转拼音

由于手机的通知中也会包括短信内容，因此这部分可以省略，也可以通过这段代码只把短信内容发送到手环上：

    <receiver android:name="com.microsoft.band.sdk.sampleapp.receiver.SmsReceiver">
        <intent-filter android:priority="999">
            <action android:name="android.provider.Telephony.SMS_RECEIVED" />
        </intent-filter>
    </receiver>
    

## 参考资料

[微软手环SDK][1]

[1]: http://developer.microsoftband.com/bandSDK SDK
