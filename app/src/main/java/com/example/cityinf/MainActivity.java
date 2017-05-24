package com.example.cityinf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.cityinf.CityDataClasses.CityDataActivity;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener
        , EditText.OnClickListener, TextWatcher {

    private final String PREFS_NAME = "isLoad";
    private SharedPreferences sharedPreferences;

    private ListView listView;
    private EditText search;
    private ImageButton btnSearch;
    private ImageButton btnBack;
    static Handler listHandler;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                setCountryList();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        };

        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        search = (EditText) findViewById(R.id.search);
        listView = (ListView) findViewById(R.id.DataList);
        btnSearch = (ImageButton) findViewById(R.id.search_btn);
        btnBack = (ImageButton) findViewById(R.id.back_btn);

        search.addTextChangedListener(this);
        listView.setOnItemClickListener(this);
        btnSearch.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        if (sharedPreferences.getBoolean(PREFS_NAME, true)) {
            CountryDataParse countryDataParse = null;
            progressBar.setVisibility(View.VISIBLE);
            try {
                countryDataParse = new CountryDataParse();
                countryDataParse.link(getContentResolver(), this);

                SharedPreferences.Editor ed = sharedPreferences.edit();
                ed.putBoolean(PREFS_NAME, false);
                ed.commit();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            countryDataParse.execute();
        }


        setCountryList();
    }


    private void setCountryList() {
        ArrayList<String> countries = new ArrayList<>();

        Cursor cursor = getContentResolver().query(CitiesDB.uriCountries, null, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            countries.add(cursor.getString(cursor.getColumnIndex(CitiesDB.COUNTRY)));
        }
        cursor.close();


        Collections.sort(countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn:
                search.setVisibility(View.VISIBLE);

                break;
            case R.id.back_btn:
                setCountryList();
                btnBack.setVisibility(View.INVISIBLE);
                break;
        }
    }

    void filterList() {
        Cursor cursor;
        String column;
        if (btnBack.getVisibility() == View.INVISIBLE) {
            cursor = getContentResolver().query(CitiesDB.uriCountries, new String[]{CitiesDB.COUNTRY},
                    null, null, null);
            column = CitiesDB.COUNTRY;
        } else {
            cursor = getContentResolver().query(CitiesDB.simpleUri, new String[]{CitiesDB.CITY},
                    null, null, null);
            column = CitiesDB.CITY;
        }
        ArrayList<String> data = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            if (cursor.getString(cursor.getColumnIndex(column)).toLowerCase()
                    .indexOf((search.getText().toString()).toLowerCase()) != -1)
                data.add(cursor.getString(cursor.getColumnIndex(column)));
        if (data.size() == 0)
            return;
        cursor.close();
        Collections.sort(data);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (btnBack.getVisibility() == View.INVISIBLE) {
            Cursor cursor = getContentResolver().query(CitiesDB.simpleUri, new String[]{CitiesDB.CITY},
                    CitiesDB.COUNTRY + " = ?", new String[]{listView.getItemAtPosition(position).toString()}, null);
            ArrayList<String> cities = new ArrayList<>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                cities.add(cursor.getString(cursor.getColumnIndex(CitiesDB.CITY)));
            cursor.close();
            Collections.sort(cities);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cities);
            listView.setAdapter(adapter);
            btnBack.setVisibility(View.VISIBLE);
        } else {
            Intent intent = new Intent(this, CityDataActivity.class);
            String cityIntent = listView.getItemAtPosition(position).toString();
            String result = "";
            for (String s : cityIntent.split(" ")) {
                result += s;
            }
            intent.putExtra("data", result);
            startActivity(intent);
        }

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (search.getText().length() < 1)
            setCountryList();
        else filterList();
        
    }
}