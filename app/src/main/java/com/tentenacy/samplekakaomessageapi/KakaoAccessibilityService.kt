package com.tentenacy.samplekakaomessageapi

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class KakaoAccessibilityService : NotificationListenerService() {
    /*
     These are the package names of the apps. for which we want to
     listen the notifications
  */
    private object ApplicationPackageNames {
        const val KAKAOTALK_PACK_NAME = "com.kakao.talk"
    }

    /*
        These are the return codes we use in the method which intercepts
        the notifications, to decide whether we should do something or not
     */
    object InterceptedNotificationCode {
        const val KAKAOTALK_CODE = 1
        const val OTHER_NOTIFICATIONS_CODE = 2 // We ignore all notification with code == 2
    }

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    @SuppressLint("HardwareIds")
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notificationCode = matchNotificationCode(sbn)
        val pack = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text").toString()
        var subtext = ""
        if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                /* Used for SendBroadcast */
                val b =
                    extras[Notification.EXTRA_MESSAGES] as Array<Parcelable>?
                if (b != null) {
                    for (tmp in b) {
                        val msgBundle = tmp as Bundle
                        subtext = msgBundle.getString("text").toString()
                    }
                    Log.d("DetailsEzra1 :", subtext)
                }
                if (subtext.isEmpty()) {
                    subtext = text
                }
                Log.d("DetailsEzra2 :", subtext)
                val intent = Intent("com.tentenacy.samplekakaomessageapi")
                intent.putExtra("Notification Code", notificationCode)
                intent.putExtra("package", pack)
                intent.putExtra("title", title)
                intent.putExtra("text", subtext)
                intent.putExtra("id", sbn.id)
                sendBroadcast(intent)
                /* End */

                /* Used Used for SendBroadcast */
                if (!text.contains("new messages") && !text.contains("WhatsApp Web is currently active") && !text.contains(
                        "WhatsApp Web login"
                    )
                ) {
                    val android_id = Settings.Secure.getString(
                        applicationContext.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                    val devicemodel =
                        Build.MANUFACTURER + Build.MODEL + Build.BRAND + Build.SERIAL
                    val df: DateFormat =
                        SimpleDateFormat("ddMMyyyyHHmmssSSS")
                    val date =
                        df.format(Calendar.getInstance().time)
                    /*
                    Toast.makeText(getApplicationContext(), "Notification : " + notificationCode + "\nPackages : " + pack + "\nTitle : " + title + "\nText : " + text + "\nId : " + date+ "\nandroid_id : " + android_id+ "\ndevicemodel : " + devicemodel,
                            Toast.LENGTH_LONG).show();
                    */
                    val intentPending =
                        Intent(applicationContext, FriendsActivity::class.java)
                    val pendingIntent =
                        PendingIntent.getActivity(this, 0, intentPending, 0)
                    val builder: NotificationCompat.Builder =
                        NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle(resources.getString(R.string.app_name))
                            .setContentText("This is income messagess : $text") as NotificationCompat.Builder
                    builder.setWhen(System.currentTimeMillis())
                    builder.setSmallIcon(R.mipmap.ic_launcher)
                    val largeIconBitmap =
                        BitmapFactory.decodeResource(resources, R.drawable.ic_menu_camera)
                    builder.setLargeIcon(largeIconBitmap)
                    // Make the notification max priority.
                    builder.setPriority(Notification.PRIORITY_MAX)
                    // Make head-up notification.
                    builder.setFullScreenIntent(pendingIntent, true)
                    val notificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(1, builder.build())
                }
                /* End Used for Toast */
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        val notificationCode = matchNotificationCode(sbn)
        if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {
            val activeNotifications =
                this.activeNotifications
            if (activeNotifications != null && activeNotifications.size > 0) {
                for (i in activeNotifications.indices) {
                    if (notificationCode == matchNotificationCode(activeNotifications[i])) {
                        val intent = Intent("com.tentenacy.samplekakaomessageapi")
                        intent.putExtra("Notification Code", notificationCode)
                        sendBroadcast(intent)
                        break
                    }
                }
            }
        }
    }

    private fun matchNotificationCode(sbn: StatusBarNotification): Int {
        val packageName = sbn.packageName
        return if (packageName == ApplicationPackageNames.KAKAOTALK_PACK_NAME) {
            InterceptedNotificationCode.KAKAOTALK_CODE
        } else {
            InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE
        }
    }
}