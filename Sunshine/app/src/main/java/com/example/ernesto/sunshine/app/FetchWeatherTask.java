package com.example.ernesto.sunshine.app;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ernesto on 20/01/2016.
 */
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


}