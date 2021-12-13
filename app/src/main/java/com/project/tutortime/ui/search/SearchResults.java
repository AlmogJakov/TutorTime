package com.project.tutortime.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


import com.project.tutortime.Login;
import com.project.tutortime.R;

public class SearchResults extends AppCompatActivity {
    private String  typeResult, cityResult, subjectResult;
    private int maxResult;
    private int minResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_results);
        Button search = findViewById(R.id.buttonAcount);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cityResult = getIntent().getStringExtra("cityResult");
        maxResult = getIntent().getIntExtra("maxResult",0);
        minResult = getIntent().getIntExtra("minResult",300);
        typeResult = getIntent().getStringExtra("typeResult");
        subjectResult = getIntent().getStringExtra("subjectResult");

        System.out.println(minResult);
        System.out.println(maxResult);
        System.out.println(typeResult);
        System.out.println(subjectResult);
        System.out.println(cityResult);
    }
}
