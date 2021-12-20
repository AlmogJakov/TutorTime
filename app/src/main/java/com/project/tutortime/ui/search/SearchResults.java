package com.project.tutortime.ui.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import androidx.cardview.widget.CardView;
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
import com.project.tutortime.adapter.TutorAdapterItem;
import com.project.tutortime.firebase.subjectObj;
import com.project.tutortime.firebase.teacherObj;
import com.project.tutortime.firebase.userObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchResults extends AppCompatActivity {
    private static final String TAG = "SearchResults";
    private String typeResult, cityResult, subjectResult;
    private int maxResult;
    private int minResult;
    private int kindOfSort;
    ListView listview;
    TextView sort;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    List<TutorAdapterItem> teachersToShow = new ArrayList<>();

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

        // get the variables
        cityResult = getIntent().getStringExtra("cityResult");
        maxResult = getIntent().getIntExtra("maxResult", 300);
        minResult = getIntent().getIntExtra("minResult", 0);
        typeResult = getIntent().getStringExtra("typeResult");
        if (typeResult.equals("Online, Frontal"))typeResult = "both";
        else if (typeResult.equals("Frontal"))typeResult = "frontal";
        else typeResult = "online";
        subjectResult = getIntent().getStringExtra("subjectResult");
        kindOfSort = getIntent().getIntExtra("sort", 0);

        double t = maxResult;
        maxResult = ((int)Math.ceil(t/20)) * 20;
        minResult = (minResult/20) *20;


        sort = findViewById(R.id.sort);
        String[] type = {"price: low to high", "price: high to low", "Rating: high to low"};
        boolean[] selectType = new boolean[type.length];
        selectType[kindOfSort] = true;
        setSpinner(sort, selectType, type);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int minR = minResult;
                for (int i = 0; minR!=maxResult+20; i++, minR+= 20){
                    Iterable<DataSnapshot> idOfTeacher = dataSnapshot.child("search").child(typeResult).child(subjectResult).child(cityResult).child(Integer.toString(minR)).getChildren();
//                    ArrayList<String> idOfTeacher = idOfTeacher1.;
                        for (DataSnapshot s1 : idOfTeacher) {
                            String s = s1.getKey();
                            if (s != null){
                                teacherObj teacher = dataSnapshot.child("teachers").child(s).getValue(teacherObj.class);
                                if (teacher != null){
                                    userObj user = dataSnapshot.child("users").child(teacher.getUserID()).getValue(userObj.class);
                                    teachersToShow.add(new TutorAdapterItem(user,teacher,subjectResult));
                                }
//                                String idUser = dataSnapshot.child("teachers").child(s).child("userID").getValue(String.class);
//                                if (idUser != null){
//                                    String name = dataSnapshot.child("users").child(idUser).child("fName").getValue(String.class);
//                                    if (name != null){
//                                        System.out.println(name);
//                                        names.add(name);
//                                    }
//                                }
                            }
                        }

                }

                listview = findViewById(R.id.featuresList);
                final TutorAdapter adapter = new TutorAdapter(SearchResults.this, teachersToShow);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), TeacherCard.class);
                        intent.putExtra("user", teachersToShow.get(position).getUser());
                        intent.putExtra("teacher", teachersToShow.get(position).getTeacher());
                        intent.putExtra("sub", teachersToShow.get(position).getSubName());
                        startActivity(intent);
                    }
                });
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

                        // when 'which' is:
                        //                0 - sort teacher cards by price from low to high
                        //                1 - sort teacher cards by price from high to low
                        //                2 - sort teacher cards by rank from high to low
                        reload(which);

                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    private void reload(int sortBy) {
        Intent intent = new Intent(this, TeacherCard.class);
        intent.putExtra("typeResult", typeResult);
        intent.putExtra("cityResult", cityResult);
        intent.putExtra("subjectResult", subjectResult);
        intent.putExtra("minResult", minResult);
        intent.putExtra("maxResult", maxResult);
        intent.putExtra("sort", sortBy);
        finish();
        startActivity(intent);
    }


    private void back() {
        finish();
    }



    public static class teacher{
        String fName, lName, description, subject;
        int price;
        public teacher(String _fName, String _lName, String _description, String _subject, int _price){
            fName = _fName;
            lName = _lName;
            description = _description;
            subject = _subject;
            price = _price;
        }

    }
    private void setRandomTeacher(){
        String[] name = {"itay", "noa", "almog", "chen", "shira", "edi", "orna", "sharon", "tal", "roi", "bar"};
//        int[]
    }
}
