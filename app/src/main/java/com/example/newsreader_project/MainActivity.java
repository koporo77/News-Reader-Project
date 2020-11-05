package com.example.newsreader_project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    List<String> titleList = new ArrayList<>();
    List<String> urlList = new ArrayList<>();

    ArrayAdapter arrayAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        titleList.add("hello world1");
//        titleList.add("hello world2");
//        titleList.add("hello world3");
//        titleList.add("hello world4");

        ListView listView = findViewById(R.id.listview);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titleList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NewActivity.class);
                intent.putExtra("url", urlList.get(position));
                startActivity(intent);
            }
        });

        LoadResource loadResource = new LoadResource("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        CompletableFuture<Void> future = loadResource.loadAsync();
        future.thenRun(() -> {
            titleList.clear();

            urlList = loadResource.getUrlList();

            List<String> titleListGot = loadResource.getTitleList();
            for (int i = 0; i < titleListGot.size(); i++) {
                titleList.add(titleListGot.get(i));
            }
            arrayAdapter.notifyDataSetChanged();

            Log.i("TAG", "titleList and urlList: " + titleList + ", " + urlList);
        });
    }

}