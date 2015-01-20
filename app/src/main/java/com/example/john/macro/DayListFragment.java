package com.example.john.macro;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.john.macro.data.MacroContract;
import com.example.john.macro.data.MacroContract.DayEntry;

import java.util.Date;

/**
 * Created by John on 12/27/2014.
 */
public class DayListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String LOG_TAG = DayListFragment.class.getSimpleName();
    private static DayListAdapter mAdapter;
    private ListView mListView;
    public static final int MACRO_LOADER = 0;

    private static final String[] MACRO_COLUMNS =
        {
            DayEntry.TABLE_NAME + "." + DayEntry._ID,
            DayEntry.COLUMN_DATETEXT,
            DayEntry.COLUMN_CALORIES,
            DayEntry.COLUMN_FAT,
            DayEntry.COLUMN_PROTEIN,
            DayEntry.COLUMN_CARBOHYDRATE,
            DayEntry.COLUMN_FOODS
        };

    public static final int COL_ID = 0;
    public static final int COL_DATE = 1;
    public static final int COL_CALORIES = 2;
    public static final int COL_FAT = 3;
    public static final int COL_PROTEIN = 4;
    public static final int COL_CARBOHYDRATE = 5;
    public static final int COL_FOODS = 6;

    public DayListFragment() {
    }

    public void onStart()
    {
        super.onStart();
    }


    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MACRO_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_days);


        mAdapter = new DayListAdapter(getActivity(), null, 0);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mAdapter.getCursor();

                if(cursor != null && cursor.moveToPosition(position))
                {
                    ((Callback) getActivity()).onItemSelected(cursor.getString(COL_DATE));
                }
            }
        });


        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getLoaderManager().restartLoader(MACRO_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        String startDate = MacroContract.getDbDateString(new Date());
        String sortOrder = DayEntry.COLUMN_DATETEXT + " DESC";

        Uri dayForDateUri = DayEntry.buildDayUriWithDate(startDate);

        return new CursorLoader(
                getActivity(),
                dayForDateUri,
                MACRO_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mAdapter.swapCursor(null);

    }

    public interface Callback
    {
        public void onItemSelected(String date);
    }
}