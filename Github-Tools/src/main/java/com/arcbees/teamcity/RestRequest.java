package com.arcbees.teamcity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class RestRequest {
    public static JsonElement fetchJson(String username, String password, String urlEndpoint) throws IOException {
        URL url = new URL(urlEndpoint);
        URLConnection urlConnection = url.openConnection();

        if (username != null && password != null) {
            String authString = username + ":" + password;
            byte[] encodedBasicAuth = Base64.encodeBase64(authString.getBytes());
            String encodedBasicAuthStr = new String(encodedBasicAuth);
            
            urlConnection.setRequestProperty("Authorization", "Basic " + encodedBasicAuthStr);
        }

        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.connect();

        JsonReader reader = new JsonReader(new InputStreamReader(urlConnection.getInputStream()));
        JsonParser parser = new JsonParser();

        return parser.parse(reader);
    }
}
