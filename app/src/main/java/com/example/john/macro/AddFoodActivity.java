package com.example.john.macro;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.john.macro.data.MacroContract;

import java.util.ArrayList;


public class AddFoodActivity extends ActionBarActivity implements AddResultReceiver.Receiver
{

    private static final String LOG_TAG = AddFoodActivity.class.getSimpleName();

    public static AddResultReceiver mReceiver;

    public static final String RECEIVER_TAG = "receiver";
    public static final int SEARCH_CODE = 0;
    public static final int FOOD_CODE = 1;
    private static String[] names;
    private static String[] numbers;

    private String date;

    private static ArrayAdapter<String> mAdapter;
    private static ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        mReceiver = new AddResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        date = getIntent().getStringExtra(DailySummaryActivity.DATE_KEY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_food, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSearchClick(View view)
    {

        EditText query = (EditText)findViewById(R.id.food_search_edittext);
        String queryString = query.getText().toString();


        Intent intent = new Intent(this, AddFoodService.class);
        intent.putExtra(AddFoodService.QUERY_EXTRA, queryString);
        intent.putExtra(RECEIVER_TAG, mReceiver);
        intent.putExtra(AddFoodService.INTENT_CODE, 0);
        this.startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData)
    {
        if(resultCode == SEARCH_CODE)
        {
            Log.v(LOG_TAG, Boolean.toString(resultData.getBoolean(AddFoodService.ERROR_KEY)));
            if(!resultData.getBoolean(AddFoodService.ERROR_KEY))
            {
                ArrayList<String> namesList = resultData.getStringArrayList(AddFoodService.NAME_KEY);
                ArrayList<String> numbersList = resultData.getStringArrayList(AddFoodService.NUMBER_KEY);

                names = namesList.toArray(new String[namesList.size()]);
                numbers = numbersList.toArray(new String[numbersList.size()]);

                mAdapter = new ArrayAdapter<String>(this, R.layout.search_item, R.id.search_textview, names);
                mListView.setAdapter(mAdapter);

            }
            else
            {
                Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();

                names = new String[]{};
                numbers = new String[]{};

                mAdapter = new ArrayAdapter<String>(this, R.layout.search_item, R.id.search_textview, names);
                mListView.setAdapter(mAdapter);

            }

        }
        else if(resultCode == FOOD_CODE)
        {

            double calories = resultData.getDouble(AddFoodService.CAL_KEY);
            double fat = resultData.getDouble(AddFoodService.FAT_KEY);
            double protein = resultData.getDouble(AddFoodService.PROT_KEY);
            double carb = resultData.getDouble(AddFoodService.CARB_KEY);
            String name = resultData.getString(AddFoodService.NAME_KEY);

            String[] foodVals = new String[]{name, calories + "", fat + "", protein + "", carb + ""};
            String newFood = Utility.serialize(foodVals, Utility.MACRO_DIVIDER);

            Cursor cursor = getContentResolver().query(
                    MacroContract.DayEntry.CONTENT_URI,
                    null,
                    MacroContract.DayEntry.COLUMN_DATETEXT + " = ?",
                    new String[]{date},
                    null
            );

            cursor.moveToFirst();

            double oldCals = cursor.getDouble(DayListFragment.COL_CALORIES);
            double oldFat = cursor.getDouble(DayListFragment.COL_FAT);
            double oldProtein = cursor.getDouble(DayListFragment.COL_PROTEIN);
            double oldCarb = cursor.getDouble(DayListFragment.COL_CARBOHYDRATE);

            String oldFood = cursor.getString(DayListFragment.COL_FOODS);

            String updatedFood;

            if(oldFood == "" || oldFood == null || oldFood.length() == 0)
            {
                updatedFood = newFood;
            }
            else
            {
                updatedFood = Utility.serialize(new String[]{oldFood, newFood}, Utility.FOOD_DIVIDER);
            }



            ContentValues updatedValues = new ContentValues();
            updatedValues.put(MacroContract.DayEntry.COLUMN_DATETEXT, date);
            updatedValues.put(MacroContract.DayEntry.COLUMN_CALORIES, oldCals + calories);
            updatedValues.put(MacroContract.DayEntry.COLUMN_FAT, oldFat + fat);
            updatedValues.put(MacroContract.DayEntry.COLUMN_PROTEIN, oldProtein + protein);
            updatedValues.put(MacroContract.DayEntry.COLUMN_CARBOHYDRATE, oldCarb + carb);
            updatedValues.put(MacroContract.DayEntry.COLUMN_FOODS, updatedFood);

            getContentResolver().insert(MacroContract.DayEntry.CONTENT_URI, updatedValues);

            cursor.close();

            names = new String[]{};
            numbers = new String[]{};

            Intent intent = new Intent(this, DailySummaryActivity.class)
                    .putExtra(DailySummaryActivity.DATE_KEY, date);
            startActivity(intent);



        }


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_food, container, false);
            mListView = (ListView)(rootView.findViewById(R.id.search_listview));

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    String number = numbers[position];

                    Intent intent = new Intent(getActivity(), AddFoodService.class);
                    intent.putExtra(AddFoodService.NUMBER_EXTRA, number);
                    intent.putExtra(RECEIVER_TAG, mReceiver);
                    intent.putExtra(AddFoodService.INTENT_CODE, 1);
                    getActivity().startService(intent);

                }
            });

            return rootView;
        }
    }
}
