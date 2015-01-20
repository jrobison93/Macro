package com.example.john.macro.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by John on 12/27/2014.
 */
public class MacroContract
{
    public static final String CONTENT_AUTHORITY = "com.example.john.macro";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DAY = "day";

    public static final String DATE_FORMAT = "yyyyMMdd";
    private static final String LOG_TAG = MacroContract.class.getSimpleName();

    public static String getDbDateString(Date date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    public static java.util.Date getDateFromDb(String dateText)
    {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public static final class DayEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "day";

        public static final String COLUMN_DATETEXT = "date";
        public static final String COLUMN_CALORIES = "calories";
        public static final String COLUMN_FAT = "fat";
        public static final String COLUMN_PROTEIN = "protein";
        public static final String COLUMN_CARBOHYDRATE = "carbohydrate";

        public static final String COLUMN_FOODS = "foods";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_DAY).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_DAY;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_DAY;

        public static Uri buildDayUri (long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildDayUriWithDate(String date)
        {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_DATETEXT, date).build();
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

}
