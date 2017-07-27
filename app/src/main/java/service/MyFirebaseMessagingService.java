package service;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import arun.com.medfriendly.MainNavigationDrawer;
import utilities.Config;
import utilities.NotificationUtils;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        System.out.println("received push notification");
        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            Log.e(TAG, "Notification Title: " + remoteMessage.getNotification().getTitle());

            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(final String message) {

        System.out.println("handleNotification" + message);
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

            // app is in foreground, broadcast the push message

            ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
            final List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            componentInfo.getPackageName();

           /* Handler handler = new Handler(getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Tabactivity.tabactivity.show_notification(message);

                  if(taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.t2s.foodhub.Addressdetails")){
                      Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
                      Addressdetails.addressdetails.show_notification(message);
                  }else if(taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.t2s.foodhub.Basket")){
                      Basket.basket.show_notification(message);
                  }else if(taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.t2s.foodhub.Menuclassnative")) {
                      Menuclassnative.menuclassnative.show_notification(message);
                  }else if(taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.t2s.foodhub.ReviewOrder")){
                      ReviewOrder.revieworder.show_notification(message);
                  }else if(taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.t2s.foodhub.Subcategory")){
                      Subcategory.subcategory.show_notification(message);
                  }*//*else if(taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.t2s.mytakeaway.AddDeliveryCharge")){
                      AddDeliveryCharge.addDeliveryCharge.dialog_method(message);
                  }else if(taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.t2s.mytakeaway.Staffs")){
                      Staffs.staffs.dialog_method(message);
                  }else if(taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.t2s.mytakeaway.AddStaffs")){
                      AddStaffs.addStaffs.dialog_method(message);
                  }else if(taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.t2s.mytakeaway.SMSScheduler")){
                      SMSScheduler.smsScheduler.dialog_method(message);
                  }else if(taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.t2s.mytakeaway.SMSSchedulerTemplates")){
                      SMSSchedulerTemplates.smsSchedulerTemplates.dialog_method(message);
                  }*//*
                }
            });
*/
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

        } else {
            // If the app is in background, firebase itself handles the notification

        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainNavigationDrawer.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
