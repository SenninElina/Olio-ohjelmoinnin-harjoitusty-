package com.example.myapplication;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MunicipalityDataRetriever {

    public ArrayList<MunicipalityData> getData(Context context, String municipality) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode areas = null;

        try {
            areas = objectMapper.readTree(new URL("https://statfin.stat.fi/PxWeb/api/v1/en/StatFin/synt/statfin_synt_pxt_12dy.px"));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        for (JsonNode node : areas.get("variables").get(1).get("values")) {
            values.add(node.asText());
        }
        for (JsonNode node : areas.get("variables").get(1).get("valueTexts")) {
            keys.add(node.asText());
        }

        HashMap<String, String> municipalityCodes = new HashMap<>();

        for(int i = 0; i < keys.size(); i++) {
            municipalityCodes.put(keys.get(i), values.get(i));
        }

        String code = null;


        code = null;
        code = municipalityCodes.get(municipality);


        try {
            URL url = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/synt/statfin_synt_pxt_12dy.px");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JsonNode jsonInputString = objectMapper.readTree(context.getResources().openRawResource(R.raw.query_valisays_vaesto));
            // 0 = "Vuosi" , 1 = "Alue" , 2 = "Tiedot"
            ((ObjectNode) jsonInputString.get("query").get(1).get("selection")).putArray("values").add(code);

            byte[] input = objectMapper.writeValueAsBytes(jsonInputString);
            OutputStream os = con.getOutputStream();
            os.write(input, 0, input.length);


            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            JsonNode municipalityData = objectMapper.readTree(response.toString());

            ArrayList<String> years = new ArrayList<>();
            ArrayList<MunicipalityData> populationData = new ArrayList<>();

            for (JsonNode node : municipalityData.get("dimension").get("Vuosi").get("category").get("label")) {
                years.add(node.asText());
            }

            JsonNode populationValues = municipalityData.get("value");

            for (int i =0; i < years.size(); i++) {
                int populationChange = populationValues.get(i * 2).asInt();
                int population = populationValues.get(i * 2 + 1).asInt();

                populationData.add(new MunicipalityData(Integer.valueOf(years.get(i)), population, populationChange));
            }

            URL secondUrl = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/tyokay/statfin_tyokay_pxt_125s.px");

            HttpURLConnection secondCon = (HttpURLConnection) secondUrl.openConnection();
            secondCon.setRequestMethod("POST");
            secondCon.setRequestProperty("Content-Type", "application/json; utf-8");
            secondCon.setRequestProperty("Accept", "application/json");
            secondCon.setDoOutput(true);

            JsonNode secondJsonInputString = objectMapper.readTree(context.getResources().openRawResource(R.raw.query_tpov));
            // 0 = "Vuosi" , 1 = "Alue"
            ((ObjectNode) secondJsonInputString.get("query").get(1).get("selection")).putArray("values").add(code);

            byte[] secondInput = objectMapper.writeValueAsBytes(secondJsonInputString);
            OutputStream secondOs = secondCon.getOutputStream();
            secondOs.write(secondInput, 0, secondInput.length);


            BufferedReader secondBr = new BufferedReader(new InputStreamReader(secondCon.getInputStream(), "utf-8"));
            StringBuilder secondResponse = new StringBuilder();
            String secondLine = null;
            while ((secondLine = secondBr.readLine()) != null) {
                secondResponse.append(secondLine.trim());
            }

            JsonNode secondMunicipalityData = objectMapper.readTree(secondResponse.toString());

            JsonNode workPlaceValues = secondMunicipalityData.get("value");

            for (int i = 0; i < years.size(); i++) {
                double workPlace = workPlaceValues.get(i).asDouble();
                populationData.get(i).setWorkPlace(workPlace);
            }

            URL thirdUrl = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/tyokay/statfin_tyokay_pxt_115x.px");

            HttpURLConnection thirdCon = (HttpURLConnection) thirdUrl.openConnection();
            thirdCon.setRequestMethod("POST");
            thirdCon.setRequestProperty("Content-Type", "application/json; utf-8");
            thirdCon.setRequestProperty("Accept", "application/json");
            thirdCon.setDoOutput(true);

            JsonNode thirdJsonInputString = objectMapper.readTree(context.getResources().openRawResource(R.raw.query_tyollisyysaste));
            // 0 = "Alue" , 1 = "Vuosi"
            ((ObjectNode) thirdJsonInputString.get("query").get(0).get("selection")).putArray("values").add(code);

            byte[] thirdInput = objectMapper.writeValueAsBytes(thirdJsonInputString);
            OutputStream thirdOs = thirdCon.getOutputStream();
            thirdOs.write(thirdInput, 0, thirdInput.length);


            BufferedReader thirdBr = new BufferedReader(new InputStreamReader(thirdCon.getInputStream(), "utf-8"));
            StringBuilder thirdResponse = new StringBuilder();
            String thirdLine = null;
            while ((thirdLine = thirdBr.readLine()) != null) {
                thirdResponse.append(thirdLine.trim());
            }

            JsonNode thirdMunicipalityData = objectMapper.readTree(thirdResponse.toString());

            JsonNode employmentValues = thirdMunicipalityData.get("value");

            for (int i = 0; i < years.size(); i++) {
                double employment = employmentValues.get(i).asDouble();
                populationData.get(i).setEmployment(employment);
            }



//            Log.d("DEBUG", "PopulationData size: " + populationData.size());
//            for (MunicipalityData data : populationData) {
//                Log.d("DEBUG", "Vuosi: " + data.getYear() +
//                        ", Väestö: " + data.getPopulationChange() +
//                        ", Lisäys: " + data.getPopulation());
//            }
//            for (JsonNode node : municipalityData.get("value")) {
//                populations.add(node.asText());
//            }
//
//            ArrayList<MunicipalityData> populationData = new ArrayList<>();
//
//            for(int i = 0; i < years.size(); i++) {
//                populationData.add(new MunicipalityData(Integer.valueOf(years.get(i)), Integer.valueOf(populations.get(i))));
//            }

            return populationData;

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }

}

