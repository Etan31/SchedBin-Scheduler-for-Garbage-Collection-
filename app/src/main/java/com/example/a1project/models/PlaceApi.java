package com.example.a1project.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

public class PlaceApi {

    private String API_KEY;

    public PlaceApi() {
        loadApiKey();
    }

    private void loadApiKey() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
            API_KEY = properties.getProperty("API_KEY");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<String> autoComplete(String input){
        ArrayList<String> arrayList=new ArrayList();
        HttpURLConnection connection=null;
        StringBuilder jsonResult=new StringBuilder();

        

        try {
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
            sb.append("input=" + input);
            sb.append("&key=" + API_KEY);

            // Add location and radius parameters 
            sb.append("&location=11.50007310, 125.49999080");
            sb.append("&radius=5000");

            URL url = new URL(sb.toString());
            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());

            int read;

            char[] buff = new char[1024];
            while ((read = inputStreamReader.read(buff)) != -1) {
                jsonResult.append(buff, 0, read);
            }

            Log.d("JSon", jsonResult.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonResult.toString());
            JSONArray prediction = jsonObject.getJSONArray("predictions");
            for (int i = 0; i < prediction.length(); i++) {
                arrayList.add(prediction.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayList;
    }
}
