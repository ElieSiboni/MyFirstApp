package com.example.myfirstapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://dog.ceo/";

    private RecyclerView recyclerView;
    private ListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("application_android", Context.MODE_PRIVATE);
        gson = new GsonBuilder()
                .setLenient()
                .create();

        List<Dogs> DogsList = getDataFromCache();

        if (DogsList != null) {
            showList(DogsList);
        } else {
                makeApiCall();
            }
        }


    private List<Dogs> getDataFromCache() {
        String jsonDogs = sharedPreferences.getString(Constants.KEY_DOGS_LIST, null);

        if (jsonDogs == null) {
            return null;
        } else {
            Type listType = new TypeToken<List<Dogs>>() {}.getType();
            return gson.fromJson(jsonDogs, listType);
        }
    }

    private void showList(List<Dogs> DogsList) {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ListAdapter(DogsList);
        recyclerView.setAdapter(mAdapter);
    }

    private void makeApiCall() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        DogApi dogapi = retrofit.create(DogApi.class);

        Call<RestDogResponse> call = dogapi.getDogResponse();
        call.enqueue(new Callback<RestDogResponse>() {
            @Override
            public void onResponse(Call<RestDogResponse> call, Response<RestDogResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    List<Dogs> DogsList = response.body().getMessage();
                    savedList(DogsList);
                    showList(DogsList);
                } else {
                    showError();    
                }
            }

            @Override
            public void onFailure(Call<RestDogResponse> call, Throwable t) {
                showError();
            }
        });
    }

    private void savedList(List<Dogs> DogsList) {
        String jsonString = gson.toJson(DogsList);
        sharedPreferences
                .edit()
                .putString(Constants.KEY_DOGS_LIST, jsonString)
                .apply();

        Toast.makeText(getApplicationContext(), "Liste sauvegardée", Toast.LENGTH_SHORT).show();
    }

    private void showError() {
        Toast.makeText(getApplicationContext(), "API Error", Toast.LENGTH_SHORT).show();
    }
}