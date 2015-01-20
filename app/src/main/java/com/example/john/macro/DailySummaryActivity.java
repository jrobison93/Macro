package com.example.john.macro;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.john.macro.data.MacroContract;


public class DailySummaryActivity extends ActionBarActivity {

    public static final String DATE_KEY = "date";

    private static String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_summary);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


        date = getIntent().getStringExtra(DATE_KEY);


    }

    @Override
    protected void onPause()
    {
        Bundle savedInstanceState = new Bundle();
        savedInstanceState.putString(DATE_KEY, date);

        onSaveInstanceState(savedInstanceState);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_summary, menu);
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

    public void onAddFoodClick(View view)
    {
        Intent intent = new Intent(this, AddFoodActivity.class)
                .putExtra(DATE_KEY, date);
        startActivity(intent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String LOG_TAG = PlaceholderFragment.class.getSimpleName();
        private static DailySummaryAdapter mAdapter;
        private ListView mListView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_daily_summary, container, false);
            TextView dateView = (TextView)(rootView.findViewById(R.id.summary_date_textview));
            dateView.setText(Utility.formatDate(date));

            Cursor cursor = getActivity().getContentResolver().query(
                    MacroContract.DayEntry.CONTENT_URI,
                    null,
                    MacroContract.DayEntry.COLUMN_DATETEXT + " = ?",
                    new String[]{date},
                    null
            );

            cursor.moveToFirst();

            double calories = cursor.getDouble(DayListFragment.COL_CALORIES);
            double fat = cursor.getDouble(DayListFragment.COL_FAT);
            double protein = cursor.getDouble(DayListFragment.COL_PROTEIN);
            double carb = cursor.getDouble(DayListFragment.COL_CARBOHYDRATE);

            MacroGraphView graph = (MacroGraphView) (rootView.findViewById(R.id.summary_graph));
            graph.setMacros(fat, protein, carb);

            TextView calView = (TextView)(rootView.findViewById(R.id.summary_calorie_textview));
            calView.setText("Calories: " + calories);

            TextView fatView = (TextView)(rootView.findViewById(R.id.summary_fat_textview));
            fatView.setText(Utility.formatFat(getActivity(), fat));

            TextView proteinView = (TextView)(rootView.findViewById(R.id.summary_protein_textview));
            proteinView.setText(Utility.formatProtein(getActivity(), protein));

            TextView carbView = (TextView)(rootView.findViewById(R.id.summary_carb_textview));
            carbView.setText(Utility.formatCarb(getActivity(), carb));


            String[] foods = Utility.deserialize(cursor.getString(DayListFragment.COL_FOODS), Utility.FOOD_DIVIDER);
            if(foods.length != 0 && foods[0] != "")
            {
                TextView noFoods = (TextView)(rootView.findViewById(R.id.no_foods_textview));
                noFoods.setHeight(0);

                mListView = (ListView) rootView.findViewById(R.id.foods_listview);
                mAdapter = new DailySummaryAdapter(getActivity(), foods);

                mListView.setAdapter(mAdapter);
            }
            else
            {
                TextView noFoods = (TextView)(rootView.findViewById(R.id.no_foods_textview));
                ViewGroup.LayoutParams params = noFoods.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                noFoods.setLayoutParams((params));
            }

            cursor.close();



            return rootView;
        }
    }
}
