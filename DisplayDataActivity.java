package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DisplayDataActivity extends AppCompatActivity {
    private TextView txtPopulationData;
    private TextView txtWeatherData;
    private TextView txtCoronaData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        // Alusta TextView-komponentit
        txtPopulationData = findViewById(R.id.txtPopulation);
        txtWeatherData = findViewById(R.id.txtWeather);
        txtCoronaData = findViewById(R.id.txtCorona);

        // Hae väkiluvut Intentistä
        ArrayList<MunicipalityData> populationData = (ArrayList<MunicipalityData>) getIntent().getSerializableExtra("POPULATION_DATA");

        StringBuilder s = new StringBuilder();
        for (MunicipalityData data : populationData) {
            s.append(data.getYear()).append(": \n")
                    .append("  Väestö: ").append(data.getPopulation()).append("\n")
                    .append("  Väestön lisäys: ").append(data.getPopulationChange()).append("\n")
                    .append("  Työpaikkaomavaraisuus: ").append(data.getWorkPlace()).append("%\n")
                    .append("  Työllisyysaste, 18-64-vuotiaat: ").append(data.getEmployment()).append("%\n\n");
        }
        txtPopulationData.setText(s.toString());

        // Hae säätiedot Intentistä
        WeatherData weatherData = (WeatherData) getIntent().getSerializableExtra("WEATHER_DATA");

        StringBuilder weatherText = new StringBuilder();
        weatherText.append(weatherData.getName()).append("\n")
                .append("Sää nyt: ").append(weatherData.getMain()).append(" (").append(weatherData.getDescription()).append(")\n")
                .append("Lämpötila: ").append(weatherData.getTemperatureCelsius()).append(" °C\n")
                .append("Tuulennopeus: ").append(weatherData.getWindSpeed()).append(" m/s");

        txtWeatherData.setText(weatherText.toString());

        // Hae koronatiedot
        String municipality = getIntent().getStringExtra("LOCATION_NAME");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            CoronaDataRetriever coronaDataRetriever = new CoronaDataRetriever();
            int deaths = coronaDataRetriever.getDeathsForMunicipality(municipality);

            runOnUiThread(() -> {
                if (deaths != -1) {
                    StringBuilder coronaText = new StringBuilder();
                    coronaText.append("Kunta: ").append(municipality).append("\n")
                            .append("Koronakuolemien yhteismäärä: ").append(deaths).append("\n");

                    txtCoronaData.setText(coronaText.toString());
                } else {
                    txtCoronaData.setText("Koronatietoja ei saatavilla kyseiselle kunnalle.");
                }
            });
        });
    }

    // Käsittelijä "Palaa"-napille
    public void onBackButtonClick(View view) {
        finish(); // Sulje tämä aktiviteetti ja palaa MainActivityyn
    }
}