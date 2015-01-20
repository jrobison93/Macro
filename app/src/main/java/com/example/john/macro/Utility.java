package com.example.john.macro;

import android.content.Context;
import android.text.TextUtils;

import com.example.john.macro.data.MacroContract;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by John on 12/27/2014.
 */
public class Utility
{
    private static final String DATE_FORMAT = "yyyyMMdd";
    public static String FOOD_DIVIDER = "_#_food_#_divider_#_";
    public static String MACRO_DIVIDER = "_x_macro_x_divider_x_";

    public static String formatFat(Context context, double fat)
    {
        return context.getString(R.string.format_fat, fat);
    }

    public static String formatProtein(Context context, double protein)
    {
        return context.getString(R.string.format_protein, protein);
    }

    public static String formatCarb(Context context, double carb)
    {
        return context.getString(R.string.format_carb, carb);
    }

    public static String formatCalories(Context context, double calories)
    {
        return context.getString(R.string.format_calories, calories);
    }

    public static String serialize(String content[], String divider)
    {
        return TextUtils.join(divider, content);
    }

    public static String[] deserialize(String content, String divider)
    {
        return content.split(divider);
    }

    static String formatDate(String dateString) {
        Date date = MacroContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }



}
