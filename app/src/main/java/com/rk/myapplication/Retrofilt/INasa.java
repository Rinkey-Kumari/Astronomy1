package com.rk.myapplication.Retrofilt;

import com.rk.myapplication.Model.APODModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface INasa {
    @GET("apod")
    Observable<APODModel> getAPOD(@Query("date") String Date,
                                    @Query("hd") boolean HDImage,
                                    @Query("api_key") String API_KEY
    );



}
