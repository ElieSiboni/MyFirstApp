package com.example.myfirstapp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DogApi {

    @GET("/api/breeds/list/all")
    Call<RestDogResponse> getDogResponse();
}
