package com.example.newsreader_project;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LoadResource {

    private String resource;
    private List<String> titleList = new ArrayList<>();
    private List<String> urlList = new ArrayList<>();

    public LoadResource(String resource) {
        this.resource = resource;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<Void> loadAsync() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> load());
        return future;
    }

    public List<String> getTitleList() {
        return titleList;
    }
    public List<String> getUrlList() {
        return urlList;
    }

    public void load() {

        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(resource);

            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            int data = inputStreamReader.read();

            while (data != -1) {
                char current = (char) data;
                result += current;
                data = inputStreamReader.read();
            }

            JSONArray jsonArray = new JSONArray(result);

            int numberOfItems = 20;

            if (jsonArray.length() < 20) {
                numberOfItems = jsonArray.length();
            }


            for (int i = 0; i < numberOfItems; i++) {
                String articleId = jsonArray.getString(i);

                url = new URL("https://hacker-news.firebaseio.com/v0/item/" + articleId + ".json?print=pretty");
                urlConnection = (HttpURLConnection) url.openConnection();
                inputStream = urlConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                data = inputStreamReader.read();

                String articleInfo = "";

                while (data != -1) {
                    char current = (char) data;
                    articleInfo += current;
                    data = inputStreamReader.read();
                }

                JSONObject jsonObject = new JSONObject(articleInfo);

                String articleTitle = "";
                String articleUrl = "";

                if (!jsonObject.isNull("title") && !jsonObject.isNull("url")) {
                    articleTitle = jsonObject.getString("title");
                    articleUrl = jsonObject.getString("url");
                }
                titleList.add(articleTitle);
                urlList.add(articleUrl);
                Log.i("TAG", "titleList and urlList: " + articleTitle + ", " + articleUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
