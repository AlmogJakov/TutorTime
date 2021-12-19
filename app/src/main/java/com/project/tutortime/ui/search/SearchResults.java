package com.project.tutortime.ui.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.Login;
import com.project.tutortime.R;
import com.project.tutortime.adapter.TutorAdapter;
import com.project.tutortime.firebase.subjectObj;

import java.util.ArrayList;
import java.util.Collections;

public class SearchResults extends AppCompatActivity {
    private static final String TAG = "SearchResults";
    private String typeResult, cityResult, subjectResult;
    private int maxResult;
    private int minResult;
    ListView listview;
    TextView sort;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    ArrayList<String> names = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_results);
        Button search = findViewById(R.id.buttonAcount);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }//
        });

        cityResult = getIntent().getStringExtra("cityResult");
        maxResult = getIntent().getIntExtra("maxResult", 300);
        minResult = getIntent().getIntExtra("minResult", 0);
        typeResult = getIntent().getStringExtra("typeResult");
        if (typeResult.equals("Online, Frontal"))typeResult = "both";
        subjectResult = getIntent().getStringExtra("subjectResult");
        System.out.println(cityResult);
        System.out.println(typeResult);
        double t = maxResult;
        maxResult = ((int)Math.ceil(t/20)) * 20;
        minResult = (minResult/20) *20;


        sort = findViewById(R.id.sort);
        String[] type = {"price: low to high", "price: high to low", "Rating: high to low"};
        boolean[] selectType = new boolean[type.length];
        setSpinner(sort, selectType, type);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int minR = minResult;
                for (int i = 0; minR!=maxResult+20; i++, minR+= 20){
                    String[] idOfTeacher = dataSnapshot.child("search").child(typeResult).child(subjectResult).child(cityResult).child(Integer.toString(minR)).getValue(String[].class);
                    if (idOfTeacher != null){
                        for (String s : idOfTeacher) {
                            String idUser = dataSnapshot.child("teachers").child(s).child("userID").getValue(String.class);
                            if (idUser != null){
                                String name = dataSnapshot.child("users").child(idUser).child("fName").getValue(String.class);
                                if (name != null){
                                    names.add(name);
                                }
                            }
                        }
                    }

                }

                listview = findViewById(R.id.featuresList);
                String[] values = new String[names.size()];
                values = names.toArray(values);
                //final TutorAdapter adapter = new TutorAdapter(SearchResults.this, values);
                //listview.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    private void setSpinner(TextView typeSpinner, boolean[] selectType, String[] type) {
        typeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchResults.this);
                builder.setTitle("Select Sort");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(type, selectType, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        for (int i = 0; i < selectType.length; i++) {
                            if (i!= which){
                                selectType[i]=false;
                            }
                        }
                        selectType[which] = true;
                        typeSpinner.setText(type[which]);
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }


    private void back() {
        finish();
    }
}
