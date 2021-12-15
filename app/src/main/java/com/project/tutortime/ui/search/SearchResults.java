package com.project.tutortime.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.project.tutortime.Login;
import com.project.tutortime.R;

import java.util.ArrayList;

public class SearchResults extends AppCompatActivity {
    private static final String TAG = "SearchResults";
    private String typeResult, cityResult, subjectResult;
    private int maxResult;
    private int minResult;
    RecyclerView r;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    ArrayList<String> strings = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_results);
        Button search = findViewById(R.id.buttonAcount);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }//
        });

        cityResult = getIntent().getStringExtra("cityResult");
        maxResult = getIntent().getIntExtra("maxResult", 0);
        minResult = getIntent().getIntExtra("minResult", 300);
        typeResult = getIntent().getStringExtra("typeResult");
        subjectResult = getIntent().getStringExtra("subjectResult");
        System.out.println(cityResult);
        System.out.println(typeResult);

        strings.add("cityResult");
        strings.add("typeResult");
        strings.add("subjectResult");
        r = findViewById(R.id.recycleCard);
        r.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new MainAdapter(strings);
        r.setLayoutManager(layoutManager);
        r.setAdapter(adapter);

    }
}
