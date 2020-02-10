package com.openclassroom.go4lunch.ui;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class PlaceRequest extends AsyncTask<String, Integer, JSONArray> {
    String nextPageToken;
    @Override
    protected JSONArray doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            String line;
            StringBuilder stringBuilder = new StringBuilder("");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            if (jsonObject.has("next_page_token")) {
                nextPageToken = jsonObject.getString("next_page_token");
            } else {
                nextPageToken = "";
            }
            return jsonObject.getJSONArray("results");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }


}