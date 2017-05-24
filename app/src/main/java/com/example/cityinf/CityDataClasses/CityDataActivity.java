package com.example.cityinf.CityDataClasses;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.cityinf.R;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CityDataActivity extends AppCompatActivity {

    RecyclerView rv;
    ArrayList<Geoname> geonames;
    private Retrofit retrofit;
    static RespApi respApi;
    ResponseJSON resp;
    Handler handlerResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_data_activity);


        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        handlerResponse = new Handler() {

            @Override
            public void publish(LogRecord record) {
                RVAdapter adapter = new RVAdapter(resp.getGeonames());
                rv.setAdapter(adapter);
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        };

        RVAdapter.link(this);
        resp = (ResponseJSON) onRetainCustomNonConfigurationInstance();
        if (resp == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.geonames.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            respApi = retrofit.create(RespApi.class);

            String path = "http://api.geonames.org/wikipediaSearchJSON?q="
                    + getIntent().getStringExtra("data") + "&maxRows=10&username=lotos44";

            Call<ResponseJSON> call = respApi.getData(path);

            call.enqueue(new Callback<ResponseJSON>() {
                @Override
                public void onResponse(Call<ResponseJSON> call, retrofit2.Response<ResponseJSON> response) {
                    resp = response.body();
                    handlerResponse.publish(null);
                }

                @Override
                public void onFailure(Call<ResponseJSON> call, Throwable t) {
                    Log.i("TAG", "");
                }
            });
        }else handlerResponse.publish(null);


    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return resp;
    }

    public static RespApi getApi() {
        return respApi;
    }

}
