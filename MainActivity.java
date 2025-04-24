package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements RecentLocationsAdapter.OnLocationClickListener {
    private EditText editTextLocation;
    private RecyclerView recyclerViewRecentLocations;
    private RecentLocationsAdapter adapter;
    private ArrayList<String> recentLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Alusta käyttöliittymän komponentit
        editTextLocation = findViewById(R.id.txtEditlocation);
        recyclerViewRecentLocations = findViewById(R.id.recyclerViewRecentLocations);

        // Alusta RecyclerView ja sen adapteri
        recentLocations = new ArrayList<>();
        adapter = new RecentLocationsAdapter(this, recentLocations, this); // Lisää listener
        recyclerViewRecentLocations.setAdapter(adapter);
        recyclerViewRecentLocations.setLayoutManager(new LinearLayoutManager(this));
    }

    public void onFindBtnClick(View view) {
        Log.d("LUT", "Nappula toimii");
        Context context = this;

        MunicipalityDataRetriever mr = new MunicipalityDataRetriever();
        WeatherDataRetriever wr = new WeatherDataRetriever();

        String location = editTextLocation.getText().toString();

        // Lisää paikkakunta recentLocations-listaan (jos se ei jo ole listassa)
        addLocationToRecyclerView(location);

        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<MunicipalityData> populationData = mr.getData(context, location);
                WeatherData weatherData = wr.getWeatherData(location);

                if (populationData == null) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Siirry DisplayDataActivityyn ja välitä data
                        Intent intent = new Intent(MainActivity.this, DisplayDataActivity.class);
                        intent.putStringArrayListExtra("RECENT_LOCATIONS", recentLocations);
                        intent.putExtra("POPULATION_DATA", populationData);
                        intent.putExtra("WEATHER_DATA", weatherData);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void addLocationToRecyclerView(String location) {
        if (!location.isEmpty() && !recentLocations.contains(location)) {
            recentLocations.add(0, location); // Lisää alkuun
            if (recentLocations.size() > 3) {
                recentLocations.remove(recentLocations.size() - 1); // Poista ylimääräiset
            }
            adapter.notifyDataSetChanged(); // Päivitä RecyclerView
        }
    }

    @Override
    public void onLocationClick(String location) {
        Log.d("LUT", "RecyclerView item clicked: " + location);
        editTextLocation.setText(location); // Aseta klikattu kunta syöttökenttään
        onFindBtnClick(null); // Suorita haku klikatulle kunnalle
    }
}