package com.example.john.macro.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.example.john.macro.DailyUpdateReceiver;
import com.example.john.macro.data.MacroContract.DayEntry;

import java.util.Calendar;
import java.util.Vector;

/**
 * Created by John on 12/28/2014.
 */
public class MacroProvider extends ContentProvider
{
    private static final String LOG_TAG = MacroProvider.class.getSimpleName();
    private static MacroDbHelper mHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final SQLiteQueryBuilder sDayByDateQueryBuilder;
    private static final String sDateSettingSelection =
            DayEntry.TABLE_NAME + "." + DayEntry.COLUMN_DATETEXT + " = ? ";

    static
    {
        sDayByDateQueryBuilder = new SQLiteQueryBuilder();
        sDayByDateQueryBuilder.setTables(DayEntry.TABLE_NAME);
    }


    private static final int DAY = 100;
    private static final int DAY_WITH_DATE = 101;

    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MacroContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MacroContract.PATH_DAY, DAY);
        matcher.addURI(authority, MacroContract.PATH_DAY + "/*", DAY_WITH_DATE);

        return matcher;
    }

    private Cursor getDayByDate(Uri uri, String[] projection, String sortOrder)
    {
        String date = DayEntry.getDateFromUri(uri);
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String[] selectionArgs;
        String selection;

        if(date != null)
        {
            selection = sDateSettingSelection;
            selectionArgs = new String[] {date};
        }
        else
        {
            return null;
        }

        return sDayByDateQueryBuilder.query(db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }


    @Override
    public boolean onCreate()
    {
        Log.v(LOG_TAG, "onCreate()");

        mHelper = new MacroDbHelper(getContext());

        Calendar cal = Calendar.getInstance();

        Log.v(LOG_TAG, MacroContract.getDbDateString(cal.getTime()));
        cal.add(Calendar.DATE, -7);
        Log.v(LOG_TAG, MacroContract.getDbDateString(cal.getTime()));

        Cursor cursor = query(
                MacroContract.DayEntry.CONTENT_URI,
                null,
                MacroContract.DayEntry.COLUMN_DATETEXT + " >= ?",
                new String[]{MacroContract.getDbDateString(cal.getTime())},
                null
        );

        if(cursor.getCount() == 0)
        {
            cal.add(Calendar.DATE, 8);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(7);

            for (int i = 0; i < 7; i++) {
                ContentValues dayValues = new ContentValues();

                cal.add(Calendar.DATE, -1);
                dayValues.put(DayEntry.COLUMN_DATETEXT, MacroContract.getDbDateString(cal.getTime()));
                dayValues.put(DayEntry.COLUMN_CALORIES, 0);
                dayValues.put(DayEntry.COLUMN_FAT, 0);
                dayValues.put(DayEntry.COLUMN_PROTEIN, 0);
                dayValues.put(DayEntry.COLUMN_CARBOHYDRATE, 0);
                dayValues.put(DayEntry.COLUMN_FOODS, "");

                cVVector.add(dayValues);
            }

            ContentValues[] cVArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cVArray);


            bulkInsert(DayEntry.CONTENT_URI, cVArray);

            Intent alarmIntent = new Intent(getContext(), DailyUpdateReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, 0);

            int interval = 86400000;


            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            long midnightMilli = c.getTimeInMillis();



            //long midnightMilli = System.currentTimeMillis() + 30000;

            AlarmManager am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC, midnightMilli, interval, pi);

        }

        cursor.close();

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor retCursor;

        switch (sUriMatcher.match(uri))
        {
            case DAY:
                retCursor = mHelper.getReadableDatabase().query(
                        DayEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case DAY_WITH_DATE:
                retCursor = getDayByDate(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }



        return retCursor;
    }

    @Override
    public String getType(Uri uri)
    {
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case DAY:
                return DayEntry.CONTENT_TYPE;
            case DAY_WITH_DATE:
                return DayEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match)
        {
            case DAY:
            {
                long _id = db.insert(DayEntry.TABLE_NAME, null, values);
                if(_id > 0)
                {
                    returnUri = DayEntry.buildDayUri(_id);
                }
                else
                {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match)
        {
            case DAY:
                rowsDeleted = db.delete(DayEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(selection == null || rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match)
        {
            case DAY:
                rowsUpdated = db.update(DayEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(selection == null || rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case DAY:
                db.beginTransaction();
                int returnCount = 0;
                try
                {
                    for(ContentValues value : values)
                    {
                        long _id = db.insert(DayEntry.TABLE_NAME, null, value);
                        if(_id != -1)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally
                {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
