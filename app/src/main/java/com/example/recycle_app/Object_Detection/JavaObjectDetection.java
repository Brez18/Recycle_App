package com.example.recycle_app.Object_Detection;

import android.util.Log;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class JavaObjectDetection {

    public static String getKeyForArrayListValue(HashMap<String, List<String>> hashMap, String targetValue) {
        for (Map.Entry<String, List<String>> entry : hashMap.entrySet()) {
            List<String> list = entry.getValue();
            if (list != null && list.contains(targetValue)) {
                return entry.getKey();
            }
        }
        return null; // Return null if the value is not found in any ArrayList.
    }

    public void detect(File img_file){
        String API_TOKEN = "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";
        String API_URL = "https://api-inference.huggingface.co/models/facebook/detr-resnet-50";

        final HashMap<String, List<String>> categories = new HashMap<String, List<String>>() {{
            put("Rubber", Arrays.asList("sports ball", "baseball glove", "handbag"));
            put("Paper", Arrays.asList("book", "kite"));
            put("Plastic", Arrays.asList("knife", "elephant", "fork", "car", "bicycle", "train", "hot dog", "motorcycle", "spoon", "bowl", "scissors", "surfboard", "kite", "dog", "sandwich", "potted plant", "boat", "suitcase", "toothbrush", "bottle", "frisbee", "zebra", "bird", "airplane", "cat", "horse", "cow", "broccoli", "carrot", "cake", "bear"));
            put("Metal", Arrays.asList("fire hydrant", "toaster", "skis", "stop sign", "parking meter", "baseball bat", "tennis racket", "sink"));
            put("Wood", Arrays.asList("dining table", "skateboard", "snowboard", "bed"));
            put("E-waste", Arrays.asList("laptop", "cell phone", "microwave", "refrigerator", "keyboard", "remote", "oven"));
            put("None", Arrays.asList("person", "toilet"));
        }};

        try {
            // Set up the connection to the API endpoint
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
            connection.setDoOutput(true);

            // Read the image file
            FileInputStream fileInputStream = new FileInputStream(img_file);
            byte[] imageData = new byte[(int) img_file.length()];
            fileInputStream.read(imageData);
            fileInputStream.close();

            // Write the image data to the request body
            OutputStream os = connection.getOutputStream();
            os.write(imageData);
            os.flush();
            os.close();

            // Get the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();

                HashMap<String, HashMap<String, String>> outerHashMap = new HashMap<>();

                for(int i=0; i < jsonResponse.length(); i++) {
                    if (jsonResponse.startsWith("label", i)) {
                        i += 7;
                        String obj = jsonResponse.substring(++i, i + jsonResponse.substring(i).indexOf("\""));
                        String cat = getKeyForArrayListValue(categories, obj);
                        String qty;
                        if (outerHashMap.containsKey(obj)) {
                            qty = String.valueOf(Integer.parseInt(outerHashMap.get(obj).get("Quantity")) + 1);
                        } else {
                            qty = "1";
                        }

                        HashMap<String, String> innerHashMap = new HashMap<>();
                        innerHashMap.put("Category", cat);
                        innerHashMap.put("Quantity", qty);
                        outerHashMap.put(obj, innerHashMap);
                    }
                }
                Log.e("Detect", String.valueOf(outerHashMap));
            } else {
                Log.e("Detect", "Failed to make a POST request. HTTP error code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
