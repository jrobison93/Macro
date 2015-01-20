package com.example.john.macro;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.john.macro.data.MacroContract;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by John on 12/28/2014.
 */
public class DailyUpdateReceiver extends BroadcastReceiver
{
    private static final String LOG_TAG = DailyUpdateReceiver.class.getSimpleName();

    private static final int MACRO_NOTIFICATION_ID = 9999;

    public void onReceive(Context context, Intent intent)
    {
        String todayDate = MacroContract.getDbDateString(new Date());
        Cursor today = context.getContentResolver().query(
                MacroContract.DayEntry.CONTENT_URI,
                new String[]{MacroContract.DayEntry._ID},
                MacroContract.DayEntry.COLUMN_DATETEXT +  " = ?",
                new String[]{todayDate},
                null
        );

        if(!today.moveToFirst())
        {
            ContentValues todayValues = new ContentValues();

            todayValues.put(MacroContract.DayEntry.COLUMN_DATETEXT, todayDate);
            todayValues.put(MacroContract.DayEntry.COLUMN_CALORIES, 0);
            todayValues.put(MacroContract.DayEntry.COLUMN_FAT, 0);
            todayValues.put(MacroContract.DayEntry.COLUMN_PROTEIN, 0);
            todayValues.put(MacroContract.DayEntry.COLUMN_CARBOHYDRATE, 0);
            todayValues.put(MacroContract.DayEntry.COLUMN_FOODS, "");


            context.getContentResolver().insert(MacroContract.DayEntry.CONTENT_URI, todayValues);

            Log.v(LOG_TAG, "Today's entry has been added");

            context.getContentResolver().notifyChange(MacroContract.DayEntry.CONTENT_URI, null);
        }
        else
        {
            Log.v(LOG_TAG, "Today's entry already exists");
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Log.v(LOG_TAG, Boolean.toString(prefs.getBoolean(context.getString(R.string.notification_key), true)));

        if(prefs.getBoolean(context.getString(R.string.notification_key), true))
        {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText("Don't forget to log your macros!")
                    .setAutoCancel(true);

            Intent notificationIntent = new Intent(context, DailySummaryActivity.class)
                    .putExtra(DailySummaryActivity.DATE_KEY, MacroContract.getDbDateString(Calendar.getInstance().getTime()));

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notification.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MACRO_NOTIFICATION_ID, notification.build());
        }
    }
}
