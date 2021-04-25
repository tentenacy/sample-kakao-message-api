package com.tentenacy.samplekakaomessageapi

import android.app.AlertDialog
import android.content.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.talk.model.Friend
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.template.model.TextTemplate
import kotlinx.android.synthetic.main.activity_message.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MessageActivity : AppCompatActivity() {

    private val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
    private val ACTION_NOTIFICATION_LISTENER_SETTINGS =
        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
    private var enableNotificationListenerAlertDialog: AlertDialog? = null

    private var imageChangeBroadcastReceiver: ReceiveBroadcastReceiver? = null

    private lateinit var friend: Friend
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        friend = intent.getParcelableExtra("friend")!!

        btn_send_message.setOnClickListener {
            val defaultText = TextTemplate(
                text = et_message.text.toString(),
                link = Link(
                    webUrl = "https://developers.kakao.com",
                    mobileWebUrl = "https://developers.kakao.com"
                )
            )

            TalkApiClient.instance.sendDefaultMessage(
                listOf(friend.uuid),
                defaultText
            ) { result, error ->
                if (error != null) {
                    Log.e("MessageActivity", "메시지 보내기 실패", error)
                } else if (result != null) {
                    Log.i("MessageActivity", "메시지 보내기 성공 ${result.successfulReceiverUuids}")

                    if (result.failureInfos != null) {
                        Log.d(
                            "MessageActivity",
                            "메시지 보내기에 일부 성공했으나, 일부 대상에게는 실패 \n${result.failureInfos}"
                        )
                    }
                }
            }
        }

        if (!isNotificationServiceEnabled()) {
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog()
            enableNotificationListenerAlertDialog?.show()
        }

        // Finally we register a receiver to tell the MainActivity when a notification has been received
        imageChangeBroadcastReceiver = ReceiveBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.tentenacy.samplekakaomessageapi")
        registerReceiver(imageChangeBroadcastReceiver, intentFilter)
    }

    /**
     * Is Notification Service Enabled.
     */
    private fun isNotificationServiceEnabled(): Boolean {
        val pkgName = packageName
        val flat = Settings.Secure.getString(
            contentResolver,
            ENABLED_NOTIFICATION_LISTENERS
        )
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":".toRegex()).toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * Receive Broadcast Receiver.
     */
    inner class ReceiveBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val receivedNotificationCode = intent.getIntExtra("Notification Code", -1)
            val packages = intent.getStringExtra("package")
            val title = intent.getStringExtra("title")
            val text = intent.getStringExtra("text")
            if (text != null) {
                if (!text.contains("new messages") && !text.contains("WhatsApp Web is currently active") && !text.contains(
                        "WhatsApp Web login"
                    )
                ) {
                    val android_id = Settings.Secure.getString(
                        applicationContext.getContentResolver(),
                        Settings.Secure.ANDROID_ID
                    )
                    val devicemodel =
                        Build.MANUFACTURER + Build.MODEL + Build.BRAND + Build.SERIAL
                    val df: DateFormat = SimpleDateFormat("ddMMyyyyHHmmssSSS")
                    val date = df.format(Calendar.getInstance().time)
                    tv_messages.text =
                        "Notification : $receivedNotificationCode\nPackages : $packages\nTitle : $title\nText : $text\nId : $date\nandroid_id : $android_id\ndevicemodel : $devicemodel"
                    /**
                     * Log.d("DetailsEzraatext2 :", "Notification : " + receivedNotificationCode + "\nPackages : " + packages + "\nTitle : " + title + "\nText : " + text + "\nId : " + date+ "\nandroid_id : " + android_id+ "\ndevicemodel : " + devicemodel);
                     */
                }
            }
        }
    }

    /**
     * Build Notification Listener Alert Dialog.
     */
    private fun buildNotificationServiceAlertDialog(): AlertDialog? {
        val alertDialogBuilder =
            AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.notification_listener_service)
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation)
        alertDialogBuilder.setPositiveButton(R.string.yes,
            DialogInterface.OnClickListener { dialog, id -> startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS)) })
        alertDialogBuilder.setNegativeButton(R.string.no,
            DialogInterface.OnClickListener { dialog, id ->
                // If you choose to not enable the notification listener
                // the app. will not work as expected
            })
        return alertDialogBuilder.create()
    }
}