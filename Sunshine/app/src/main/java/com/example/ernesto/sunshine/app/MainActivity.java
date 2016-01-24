package com.example.ernesto.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> mForecastAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String[] forecastArray={
                "Lunes - Soleado, 30/25 ",
                "Martes - Soleado, 30/25 ",
                "Miercoles - Soleado, 30/25 ",
                "Jueves - Soleado, 30/25 ",
                "Viernes - Soleado, 30/25 ",
                "Sabado - Soleado, 30/25 ",
                "Domingo - Soleado, 30/25 ",
                "Lunes - Soleado, 30/25 "
        };

        List<String> weekForecast=new ArrayList<>(Arrays.asList(forecastArray));

        mForecastAdapter= new ArrayAdapter<String>(getApplicationContext(),
                                                    R.layout.list_item_forecast,
                                                    R.id.list_item_forecast_textview,
                                                    weekForecast);
        ListView listView=(ListView) findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //String forecast = mForecastAdapter.getItem(position);
                Log.v("Data", "Positionn " + position);
                //Toast.makeText(MainActivity.this, forecast, Toast.LENGTH_LONG).show();
            }
        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.forecastfragment, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fresh) {
            FetchWeatherTask fw=new FetchWeatherTask();
            fw.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG= FetchWeatherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=Piura,PE" +
                        "&mode=json&units=metric&cnt=7&appid=16c6666049da9569f6d455a4b2b3fc7f");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                }
                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "FORECAST JSON String: " + forecastJsonStr);

                //Convertir Json a Texto

                JSONObject forecastJson= new JSONObject(forecastJsonStr);
                JSONArray weatherarray=forecastJson.getJSONArray("list");

                String[] resultados=new String[weatherarray.length()];



                for(int i=0;i<resultados.length;i++){
                    String day; //dia 1
                    String description; //weather: main, description
                    String high; //temp
                    String low; //temp

                    JSONObject dayForecast=weatherarray.getJSONObject(i);

                    JSONObject weatherObject= dayForecast.getJSONArray("weather").getJSONObject(0);
                    description=weatherObject.getString("main") + "-" + weatherObject.getString("description");

                    JSONObject temperatureObject=dayForecast.getJSONObject("temp");
                    high=temperatureObject.getString("max");
                    low=temperatureObject.getString("min");
                    resultados[i]="Dia: " + i+ "" + description + " " + high + " " +low;

                    Log.v(LOG_TAG,"Forecast entry: " + resultados[i]);
                }
                return  resultados;

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mForecastAdapter.clear();
                for(String dayForecastStr : result) {
                    mForecastAdapter.add(dayForecastStr);
                }
                //Log.v("Cuanta data", String.valueOf(mForecastAdapter.getCount()));
                // New data is back from the server.  Hooray!
            }
        }
    }

}
