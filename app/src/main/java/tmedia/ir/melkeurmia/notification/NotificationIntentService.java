package tmedia.ir.melkeurmia.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.leolin.shortcutbadger.ShortcutBadger;
import tmedia.ir.melkeurmia.MainActivity;
import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.tools.AppSharedPref;
import tmedia.ir.melkeurmia.tools.BadgeUtils;
import tmedia.ir.melkeurmia.tools.CONST;

/**
 * Created by klogi
 *
 *
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
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotification() {
        String time = AppSharedPref.read("FINISH_DATE", "");

        Ion.with(getApplicationContext())
                .load(CONST.GET_NEW_ORDER)
                .setBodyParameter("date",time)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e == null){
                            JsonParser parser = new JsonParser();
                            int count = parser.parse(result).getAsJsonObject().get("count").getAsInt();

                            if(count > 2){

                                setNotifyBadge(count);

                                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext() , "M_CH_ID");

                                notificationBuilder.setAutoCancel(true)
                                        .setDefaults(Notification.DEFAULT_ALL)
                                        .setWhen(System.currentTimeMillis())
                                        .setSmallIcon(R.drawable.app_logo)
                                        .setTicker("Hearty365")
                                        .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                                        .setContentTitle("آگهی در ملک ارومیه منتشر شد.")
                                        .setContentText("از اخرین باری که شما وارد نرم افزار شده اید بیش از 10 اگهی در ملک ارومیه منتشر شده است.")
                                        .setContentInfo("اطلاع رسانی");

                                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                        NOTIFICATION_ID,
                                        mainIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                                notificationBuilder.setContentIntent(pendingIntent);
                                notificationBuilder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(getApplicationContext()));

                                final NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                manager.notify(NOTIFICATION_ID, notificationBuilder.build());
                            }

                        }
                    }
                });


    }

    private void setNotifyBadge(int count) {
        BadgeUtils.setBadge(getApplicationContext(), count);

        ShortcutBadger.applyCount(getApplicationContext(), count); //for 1.1.4+

        try {
            Badges.setBadge(getApplicationContext(), count);
        } catch (BadgesNotSupportedException badgesNotSupportedException) {

        }

        NotificationManager mNotificationManager = (NotificationManager) this

                .getSystemService(Context.NOTIFICATION_SERVICE);



        Notification.Builder builder = new Notification.Builder(this)

                .setContentTitle("title").setContentText("text").setSmallIcon(R.drawable.app_logo);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        }

        try {

            Field field = notification.getClass().getDeclaredField("extraNotification");

            Object extraNotification = field.get(notification);

            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);

            method.invoke(extraNotification, count);

        } catch (Exception e) {

            e.printStackTrace();

        }

        mNotificationManager.notify(0,notification);

    }
}
