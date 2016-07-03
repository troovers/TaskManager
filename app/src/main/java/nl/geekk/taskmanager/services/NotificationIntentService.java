package nl.geekk.taskmanager.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import nl.geekk.taskmanager.R;
import nl.geekk.taskmanager.controller.TaskManager;
import nl.geekk.taskmanager.receivers.NotificationEventReceiver;
import nl.geekk.taskmanager.view.MainActivity;

/**
 * Created by Thomas on 19-6-2016.
 */
public class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotification() {
        new FetchTaskStats().execute();
    }

    private class FetchTaskStats extends AsyncTask<Void, Void, Boolean> {
        private NotificationCompat.Builder builder;
        private NotificationManager manager;
        private TaskManager taskManager;

        @Override
        protected void onPreExecute() {
            builder = new NotificationCompat.Builder(NotificationIntentService.this);
            taskManager = new TaskManager(NotificationIntentService.this);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            int numberOfTasks = (taskManager.getTasks()).size();
            String notification;

            switch (numberOfTasks) {
                case 0:
                    return false;
                case 1:
                    notification = "U heeft 1 openstaande taak";
                    break;
                default:
                    notification = "U heeft "+numberOfTasks+" openstaande taken";
                    break;
            }


            builder.setContentTitle("Taakbeheer")
                    .setAutoCancel(true)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setContentText(notification)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setVibrate(new long[]{500, 500, 500})
                    .setLights(Color.GREEN, 3000, 3000);

            PendingIntent pendingIntent = PendingIntent.getActivity(NotificationIntentService.this,
                    NOTIFICATION_ID,
                    new Intent(NotificationIntentService.this, MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(NotificationIntentService.this));

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                manager = (NotificationManager) NotificationIntentService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }
}
