package com.example.myapplication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CoronaDataRetriever {

    public int getDeathsForMunicipality(String municipality) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Hae data THL:n API:sta
            URL url = new URL("https://sampo.thl.fi/pivot/prod/fi/epirapo/covid19case/fact_epirapo_covid19case.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parsi JSON-data
            JsonNode rootNode = objectMapper.readTree(response.toString());

            // Kuntien ja viikkojen indeksit
            Map<String, Integer> municipalityIndexMap = parseMunicipalityIndices(rootNode);
            int totalWeeks = parseTotalWeeks(rootNode);

            // Etsi kunnan indeksi
            Integer municipalityIndex = municipalityIndexMap.get(municipality);
            if (municipalityIndex == null) {
                return -1; // Kuntaa ei löytynyt
            }

            // Lasketaan kaikki kuolemat yhteen
            int totalDeaths = 0;
            for (int week = 0; week < totalWeeks; week++) {
                int dataIndex = (municipalityIndex * totalWeeks) + week;
                JsonNode valueNode = rootNode.path("value").get(String.valueOf(dataIndex));
                if (valueNode != null && !valueNode.asText().isEmpty()) {
                    totalDeaths += valueNode.asInt(0);
                }
            }

            return totalDeaths;

        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Palauta -1 virhetilanteessa
        }
    }

    private Map<String, Integer> parseMunicipalityIndices(JsonNode rootNode) {
        Map<String, Integer> municipalityIndexMap = new HashMap<>();
        JsonNode municipalities = rootNode.path("dataset").path("dimension").path("hcdmunicipality2020").path("category").path("label");

        municipalities.fields().forEachRemaining(entry -> {
            String key = entry.getKey(); // Kunnan tunniste (esim. "445131")
            String label = entry.getValue().asText(); // Kunnan nimi (esim. "Ahvenanmaa")
            JsonNode indexNode = rootNode.path("dataset").path("dimension").path("hcdmunicipality2020").path("category").path("index").get(key);
            if (indexNode != null) {
                municipalityIndexMap.put(label, indexNode.asInt());
            }
        });

        return municipalityIndexMap;
    }

    private int parseTotalWeeks(JsonNode rootNode) {
        JsonNode weeks = rootNode.path("dataset").path("dimension").path("dateweek20200101").path("category").path("index");
        return weeks.size();
    }
}