package com.example.cityinf;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class CountryDataParse extends AsyncTask<String, String, String> {
    private URL url = new URL(" https://raw.githubusercontent.com/David-Haim" +
            "/CountriesToCitiesJSON/master/countriesToCities.json");


    private HttpURLConnection httpURLConnection;
    private BufferedReader reader = null;
    private String resultJson = "";
    private ContentResolver contentResolver;
    private Context activity;

    CountryDataParse() throws MalformedURLException {
    }

    public void link(ContentResolver c, Context activity) {
        this.contentResolver = c;
        this.activity = (MainActivity) activity;
    }

    @Override
    protected String doInBackground(String... params) {


        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            resultJson = buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultJson;


    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            CitiesDB.fastInsert(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
