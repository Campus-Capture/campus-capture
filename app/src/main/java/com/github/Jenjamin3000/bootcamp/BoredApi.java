package com.github.Jenjamin3000.bootcamp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BoredApi {
    @GET("activity")
    public Call<BoredActivityData> getActivity();
}
