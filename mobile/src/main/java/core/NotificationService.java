package core;

import activities.MainActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vince.childcare.R;
import java.util.Objects;

public class NotificationService extends FirebaseMessagingService {

  public static int NOTIFICATION_ID = 1;

  @Override public void onNewToken(String s) {
    super.onNewToken(s);
    Log.e("DeviceToken ==> ", s);
  }

  @Override public void onMessageReceived(RemoteMessage remoteMessage) {

    //Call method to generate notification
    generateNotification(Objects.requireNonNull(remoteMessage.getNotification()).getBody());
  }

  private void generateNotification(String messageBody) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder mNotifyBuilder =
        new NotificationCompat.Builder(this, "PRIMARY_CH_ID").setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle("Firebase Cloud Notification")
        .setContentText(messageBody)
        .setAutoCancel(true)
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent);

    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    if (NOTIFICATION_ID > 1073741824) {
      NOTIFICATION_ID = 0;
    }
    if (notificationManager != null) {
      notificationManager.notify(NOTIFICATION_ID++, mNotifyBuilder.build());
    }
  }
}
