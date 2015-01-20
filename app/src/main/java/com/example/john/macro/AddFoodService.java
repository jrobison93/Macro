package com.example.john.macro;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by John on 12/29/2014.
 */
public class AddFoodService extends IntentService
{
    private static final String LOG_TAG = AddFoodService.class.getSimpleName();

    private static String baseSearchUrl = "http://api.data.gov/usda/ndb/search/?";
    private static String baseReportUrl = "http://api.data.gov/usda/ndb/reports/?";
    private static String api_key_param = "api_key";
    private static String api_key = "W09VAIajmzgJkCI63Pglky3DViYlQsISod7t5aRW";
    private static String format_param = "format";
    private static String format = "json";
    private static String search_param = "q";
    private static String max_rows_param = "max";
    private static int max_rows = 10;
    private static String ndbno_param = "ndbno";

    private static String[] names;
    private static String[] numbers;

    public static final String QUERY_EXTRA = "query";
    public static final String NUMBER_EXTRA = "num";
    public static final String NAME_KEY = "names";
    public static final String NUMBER_KEY = "numbers";
    public static final String CAL_KEY = "calories";
    public static final String FAT_KEY = "fat";
    public static final String PROT_KEY = "protein";
    public static final String CARB_KEY = "carb";
    public static final String ERROR_KEY = "fileNotFound";

    public static final String INTENT_CODE = "code";

    public AddFoodService()
    {
        super("Macro");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        int code = intent.getIntExtra(INTENT_CODE, 0);

        if(code == 0)
        {
            String queryString = intent.getStringExtra(QUERY_EXTRA);
            ResultReceiver receiver = intent.getParcelableExtra(AddFoodActivity.RECEIVER_TAG);

            searchForFoods(queryString, receiver);
        }
        else if(code == 1)
        {
            String numberString = intent.getStringExtra(NUMBER_EXTRA);
            ResultReceiver receiver = intent.getParcelableExtra(AddFoodActivity.RECEIVER_TAG);

            getFood(numberString, receiver);
        }

    }

    private void searchForFoods(String queryString, ResultReceiver receiver)
    {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String foodsJsonStr = null;

        try
        {
            Uri builtUri = Uri.parse(baseSearchUrl).buildUpon()
                    .appendQueryParameter(format_param, "json")
                    .appendQueryParameter(search_param, queryString)
                    .appendQueryParameter(max_rows_param, Integer.toString(max_rows))
                    .appendQueryParameter(api_key_param, api_key)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null)
            {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while((line = reader.readLine()) != null)
            {
                buffer.append(line);
                buffer.append("\n");
            }

            if(buffer.length() == 0)
            {
                return;
            }

            foodsJsonStr = buffer.toString();

        }
        catch (FileNotFoundException e)
        {
            Bundle returnVals = new Bundle();
            returnVals.putStringArrayList(NAME_KEY, new ArrayList<String>());
            returnVals.putStringArrayList(NUMBER_KEY, new ArrayList<String>());
            returnVals.putBoolean(ERROR_KEY, true);

            receiver.send(AddFoodActivity.SEARCH_CODE, returnVals);

            return;
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "Error", e);
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (final IOException e)
                {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        if(foodsJsonStr != null) {

            final String list_param = "list";
            final String item_param = "item";

            final String name_param = "name";
            final String ndbno_param = "ndbno";

            try {

                JSONObject foodsJson = new JSONObject(foodsJsonStr);
                JSONObject foodList = foodsJson.getJSONObject(list_param);

                JSONArray foodArray = foodList.getJSONArray(item_param);

                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> numbers = new ArrayList<>();

                for (int i = 0; i < foodArray.length(); i++) {
                    String name;
                    String number;

                    JSONObject item = foodArray.getJSONObject(i);

                    name = item.getString(name_param);
                    number = item.getString(ndbno_param);

                    names.add(name);
                    numbers.add(number);
                }

                Bundle returnVals = new Bundle();
                returnVals.putStringArrayList(NAME_KEY, names);
                returnVals.putStringArrayList(NUMBER_KEY, numbers);
                returnVals.putBoolean(ERROR_KEY, false);

                receiver.send(AddFoodActivity.SEARCH_CODE, returnVals);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }

    public void getFood(String numberString, ResultReceiver receiver)
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String foodsJsonStr = null;

        try
        {
            Uri builtUri = Uri.parse(baseReportUrl).buildUpon()
                    .appendQueryParameter(format_param, "json")
                    .appendQueryParameter(ndbno_param, numberString)
                    .appendQueryParameter(api_key_param, api_key)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null)
            {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while((line = reader.readLine()) != null)
            {
                buffer.append(line);
                buffer.append("\n");
            }

            if(buffer.length() == 0)
            {
                return;
            }

            foodsJsonStr = buffer.toString();

        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "Error", e);
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (final IOException e)
                {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        final String report_param = "report";
        final String food_param = "food";
        final String nutrients_param = "nutrients";

        final String measures_param = "measures";
        final String name_param = "name";
        final String value_param = "value";


        try
        {
            JSONObject foodsJson = new JSONObject(foodsJsonStr);
            JSONObject report = foodsJson.getJSONObject(report_param);
            JSONObject food = report.getJSONObject(food_param);

            String name = food.getString(name_param);

            JSONArray nutrients = food.getJSONArray(nutrients_param);

            double calories = 0;
            double fat = 0;
            double protein = 0;
            double carb = 0;



            for(int i = 0; i < nutrients.length(); i++)
            {
                JSONObject element = nutrients.getJSONObject(i);
                switch (element.getString(name_param))
                {
                    case "Energy":
                        calories = element.getDouble(value_param);
                        break;
                    case "Protein":
                        protein = element.getDouble(value_param);
                        break;
                    case "Total lipid (fat)":
                        fat = element.getDouble(value_param);
                        break;
                    case "Carbohydrate, by difference":
                        carb = element.getDouble(value_param);
                        break;
                    default:
                        break;
                }
            }

            Bundle returnVals = new Bundle();
            returnVals.putDouble(CAL_KEY, calories);
            returnVals.putDouble(FAT_KEY, fat);
            returnVals.putDouble(PROT_KEY, protein);
            returnVals.putDouble(CARB_KEY, carb);
            returnVals.putString(NAME_KEY, name);


            receiver.send(AddFoodActivity.FOOD_CODE, returnVals);

        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }
}
