package com.example.john.macro;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.john.macro.data.MacroContract;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity implements DayListFragment.Callback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            /*
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.day_list_container, new DayListFragment())
                    .commit();
            */

            setMacros();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onItemSelected(String date)
    {
        Intent intent = new Intent(this, DailySummaryActivity.class)
                .putExtra(DailySummaryActivity.DATE_KEY, date);
        startActivity(intent);


    }

    public void setMacros()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30);
        String startDate = MacroContract.getDbDateString(cal.getTime());
        String[] selectionArgs = {startDate};


        Cursor cursor = getContentResolver().query(
                MacroContract.DayEntry.CONTENT_URI,
                null,
                MacroContract.DayEntry.COLUMN_DATETEXT + " >= ?",
                selectionArgs,
                MacroContract.DayEntry.COLUMN_DATETEXT + " DESC");

        double fat = 0;
        double protein = 0;
        double carb = 0;

        if(cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                fat += cursor.getFloat(DayListFragment.COL_FAT);
                protein += cursor.getFloat(DayListFragment.COL_PROTEIN);
                carb += cursor.getFloat(DayListFragment.COL_CARBOHYDRATE);
            }
        }

        ((MacroGraphView)findViewById(R.id.macro_graph_container)).setMacros(fat, protein, carb);

    }
}
