package com.example.john.macro.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.john.macro.data.MacroContract.DayEntry;

/**
 * Created by John on 12/27/2014.
 */
public class MacroDbHelper extends SQLiteOpenHelper
{

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "macro.db";

    public MacroDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {

        final String SQL_CREATE_DAY_TABLE =
                "CREATE TABLE " + DayEntry.TABLE_NAME + " (" +
                        DayEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DayEntry.COLUMN_DATETEXT + " TEXT NOT NULL, " +
                        DayEntry.COLUMN_CALORIES + " REAL NOT NULL, " +
                        DayEntry.COLUMN_FAT + " REAL NOT NULL, " +
                        DayEntry.COLUMN_PROTEIN + " REAL NOT NULL, " +
                        DayEntry.COLUMN_CARBOHYDRATE + " REAL NOT NULL, " +
                        DayEntry.COLUMN_FOODS + " TEXT NOT NULL, " +

                        "UNIQUE (" + DayEntry.COLUMN_DATETEXT + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_DAY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + DayEntry.TABLE_NAME);
        onCreate(db);

    }
}
