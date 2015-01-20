package com.example.john.macro;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Created by John on 12/29/2014.
 */
public class DailySummaryAdapter implements ListAdapter
{
    private static final String LOG_TAG = DailySummaryAdapter.class.getSimpleName();

    private Context context;
    private String[] values;



    public DailySummaryAdapter(Context context, String[] values)
    {
        this.context = context;
        this.values = values;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.v(LOG_TAG, "getView()");
        int layoutId = R.layout.list_item_food;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        String[] parsedValues = Utility.deserialize(values[position], Utility.MACRO_DIVIDER);

        if(parsedValues.length >= 5)
        {
            viewHolder.foodView.setText(parsedValues[0]);
            viewHolder.calorieView.setText(Utility.formatCalories(context, Double.parseDouble(parsedValues[1])));
            viewHolder.fatView.setText(Utility.formatFat(context, Double.parseDouble(parsedValues[2])));
            viewHolder.proteinView.setText(Utility.formatProtein(context, Double.parseDouble(parsedValues[3])));
            viewHolder.carbView.setText(Utility.formatCarb(context, Double.parseDouble(parsedValues[4])));
        }

        return view;

    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }


    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder
    {
        public final TextView fatView;
        public final TextView proteinView;
        public final TextView carbView;
        public final TextView calorieView;
        public final TextView foodView;

        public ViewHolder(View view) {
            fatView = (TextView) view.findViewById(R.id.list_item_food_fat);
            proteinView = (TextView) view.findViewById(R.id.list_item_food_protein);
            carbView = (TextView) view.findViewById(R.id.list_item_food_carb);
            calorieView = (TextView) view.findViewById(R.id.list_item_food_calorie);
            foodView = (TextView) view.findViewById(R.id.list_item_food_textview);
        }
    }
}
