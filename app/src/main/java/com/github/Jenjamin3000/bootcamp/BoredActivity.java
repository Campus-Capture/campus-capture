package com.github.Jenjamin3000.bootcamp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BoredActivity extends AppCompatActivity {

    BoredApi boredApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bored);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.boredapi.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        boredApi = retrofit.create(BoredApi.class);

        Button fetchActivityButton = findViewById(R.id.fetchActivityButton);

        fetchActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateActivityData();
            }
        });

        updateActivityData();

    }

    void updateActivityData(){

        TextView activityText = findViewById(R.id.activityText);

        boredApi.getActivity().enqueue(new Callback<BoredActivityData>() {
            @Override
            public void onResponse(Call<BoredActivityData> call, Response<BoredActivityData> response) {
                BoredActivityData data = response.body();
                activityText.setText(data.activity);
            }

            @Override
            public void onFailure(Call<BoredActivityData> call, Throwable throwable) {
                activityText.setText("Error fetching activity");
                System.out.println(throwable);
            }
        });
    }
}