package rahul.lucky.notificationdemo.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import rahul.lucky.notificationdemo.BuildConfig;
import rahul.lucky.notificationdemo.MainActivity;
import rahul.lucky.notificationdemo.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private static final int NOTIFICATION_ID = 0;

    NotificationCompat.Builder notificationCompat;
    private NotificationManager notificationManager;
    private String channelId;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
                .edit()
                .putString("testToken", token)
                .apply();
    }


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        handleNow(remoteMessage.getData());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("message"));
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow(Map<String, String> body) {
        Log.d(TAG, "Short lived task is done.");
        sendNotification(body);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    private void sendNotification(Map<String, String> messageBody) {

        String name = messageBody.get("title") + " " + messageBody.get("lastname");
        final String user_image = messageBody.get("user_image");
        String message = messageBody.get("body");
        String from_id = messageBody.get("from_id");
        String to_id = messageBody.get("to_id");

        channelId = getString(R.string.default_notification_channel_id);
        notificationCompat = new NotificationCompat.Builder(this, channelId);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        String channelName = getString(R.string.channel_name);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int app_icon = R.mipmap.ic_launcher;

        notificationCompat.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setSmallIcon(app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), app_icon))
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setSound(defaultSoundUri)
                .setContentTitle(name)
                .setContentText(message)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            notificationCompat.setChannelId(channelId);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(NOTIFICATION_ID, notificationCompat.build());

    }
}

