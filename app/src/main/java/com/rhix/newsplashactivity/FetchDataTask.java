package com.rhix.newsplashactivity;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FetchDataTask {
    private final GridViewAdapter adapter;
    private final ExecutorService executorService;
    private final Handler handler;

    public FetchDataTask(GridViewAdapter adapter) {
        this.adapter = adapter;
        this.executorService = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void execute(String urlString) {
        executorService.execute(() -> {
            List<FoodModel> itemList = new ArrayList<>();

            Log.d("Running", "Retrieving...");

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Parse the JSON string
                JSONObject jsonObject = new JSONObject(response.toString());

                // Get the products array
                JSONArray productsArray = jsonObject.getJSONArray("products");

                // Iterate over the jsonProductsArray to retrieve product details
                for (int i = 0; i < productsArray.length(); i++) {
                    JSONObject product = productsArray.getJSONObject(i);

                    // Extract product details from each object
                    int foodId = product.getInt("id");
                    String foodName = product.getString("product_name");
                    int foodQuantity = product.getInt("product_quantity");
                    String foodImage = product.getString("product_image");

                    // Add each product to the itemList
                    itemList.add(new FoodModel(foodId, foodName, String.valueOf(foodQuantity), foodImage));
                }

                reader.close();
                connection.disconnect();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            // Update the UI on the main thread
            handler.post(() -> {
                adapter.setData(itemList);
                adapter.notifyDataSetChanged();
            });
        });
    }
}

