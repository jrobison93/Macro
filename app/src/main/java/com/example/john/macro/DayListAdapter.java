package com.example.john.macro;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by John on 12/27/2014.
 */
public class DayListAdapter extends CursorAdapter
{
    private static final String LOG_TAG = DayListAdapter.class.getSimpleName();

    public DayListAdapter(Context context, Cursor cursor, int flags)
    {
        super(context, cursor, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.list_item_day;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String date = Utility.formatDate(cursor.getString(DayListFragment.COL_DATE));
        viewHolder.dateView.setText(date);

        float fat = cursor.getFloat(DayListFragment.COL_FAT);
        viewHolder.fatView.setText(Utility.formatFat(context, fat));

        float protein = cursor.getFloat(DayListFragment.COL_PROTEIN);
        viewHolder.proteinView.setText(Utility.formatProtein(context, protein));

        float carb = cursor.getFloat(DayListFragment.COL_CARBOHYDRATE);
        viewHolder.carbView.setText(Utility.formatCarb(context, carb));

        float calories = cursor.getFloat(DayListFragment.COL_CALORIES);
        viewHolder.calorieView.setText(Utility.formatCalories(context, calories));
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder
    {
        public final TextView dateView;
        public final TextView fatView;
        public final TextView proteinView;
        public final TextView carbView;
        public final TextView calorieView;

        public ViewHolder(View view) {
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            fatView = (TextView) view.findViewById(R.id.list_item_fat_textview);
            proteinView = (TextView) view.findViewById(R.id.list_item_protein_textview);
            carbView = (TextView) view.findViewById(R.id.list_item_carb_textview);
            calorieView = (TextView) view.findViewById(R.id.list_item_calorie_textview);
        }
    }
}
