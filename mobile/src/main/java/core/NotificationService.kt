package core

import activities.MainActivity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vince.childcare.R
import java.util.*

class NotificationService : FirebaseMessagingService() {

  override fun onNewToken(s: String?) {
    super.onNewToken(s)
    Log.e("DeviceToken ==> ", s)
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage?) {

    //Call method to generate notification
    generateNotification(Objects.requireNonNull<RemoteMessage.Notification>(remoteMessage!!.notification).body)
  }

  private fun generateNotification(messageBody: String?) {
    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val mNotifyBuilder = NotificationCompat.Builder(this, "PRIMARY_CH_ID").setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(
        "Firebase Cloud Notification")
        .setContentText(messageBody)
        .setAutoCancel(true)
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent)

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (NOTIFICATION_ID > 1073741824) {
      NOTIFICATION_ID = 0
    }
    notificationManager.notify(NOTIFICATION_ID++, mNotifyBuilder.build())
  }

  companion object {

    var NOTIFICATION_ID = 1
  }
}
