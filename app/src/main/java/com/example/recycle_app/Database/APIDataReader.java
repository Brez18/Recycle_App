package com.example.recycle_app.Database;

import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class APIDataReader {

    static String jsonResponse;
    public static void getData() {
        try {
            // Create a URL object with the API endpoint
            URL url = new URL("https://e-waste-22842-default-rtdb.firebaseio.com/registeruser.json");

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");
            // Get the response code
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                jsonResponse = response.toString();

            } else {
                System.out.println("Error: " + responseCode);
            }
            // Disconnect the connection
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int verifyData(String email, String password) throws JSONException {

        JSONObject jsonData = new JSONObject(jsonResponse);
        String obj_email,obj_password;

        int type_error = 0;// 0->couldn't find account 1->incorrect/invalid password
        for (Iterator<String> it = jsonData.keys(); it.hasNext(); ) {
            String key = it.next();

            obj_email = jsonData.getJSONObject(key).getString("EmailId");
            obj_password = jsonData.getJSONObject(key).getString("Password");

            if(obj_email.equals(email) && obj_password.equals(password)) {
                type_error = -1;
                break;
            }
            else if(obj_email.equals(email))
            {
                type_error = 1;
                break;
            }
        }

        return type_error;
    }
}
