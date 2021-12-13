package com.project.tutortime.ui.search;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.R;
import com.project.tutortime.firebase.subjectObj;

import java.util.ArrayList;
import java.util.Collections;


public class Search extends Fragment {
    FirebaseAuth fAuth;
    private DatabaseReference mDatabase;
    Button addBtn;
    Spinner subjectSpin;
    TextView typeSpinner;
    TextView citySpinner;
    EditText minPrice, maxPrice;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);
        addBtn = v.findViewById(R.id.buttonAcount);

        String[] type = {"Online", "Frontal"};
        typeSpinner = v.findViewById(R.id.learn);
        boolean[] selectType = new boolean[type.length];
        ArrayList<Integer> listType = new ArrayList<>();
        setSpinner(typeSpinner, selectType, listType, type);

        subjectSpin = v.findViewById(R.id.selectSub);
        subjectSpin.setAdapter(new ArrayAdapter<>
                (this.getActivity(), android.R.layout.simple_spinner_item, subjectObj.SubName.values()));

        citySpinner = v.findViewById(R.id.selectCity);
        String[] cities = getResources().getStringArray(R.array.Cities);
        boolean[] selectCity = new boolean[cities.length];
        ArrayList<Integer> listCity = new ArrayList<>();
        setSpinner(citySpinner,selectCity,listCity, cities);

        maxPrice = v.findViewById(R.id.max);
        minPrice = v.findViewById(R.id.min);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typeResult = typeSpinner.getText().toString().trim();
                String cityResult = citySpinner.getText().toString().trim();
                String subjectResult = subjectSpin.getSelectedItem().toString();
                String minResult = minPrice.getText().toString().trim();
                String maxResult = maxPrice.getText().toString().trim();
                int min=0, max=300;
                try{
                    min = Integer.parseInt(minResult);
                    max = Integer.parseInt(maxResult);
                }
                catch (NumberFormatException ignored){

                }

                Intent intent = new Intent(getActivity(), SearchResults.class);
                intent.putExtra("typeResult", typeResult);
                intent.putExtra("cityResult", cityResult);
                intent.putExtra("subjectResult", subjectResult);
                intent.putExtra("minResult", min);
                intent.putExtra("maxResult", max);
                startActivity(intent);
            }
        });
        return v;

    }

    private void setSpinner(TextView typeSpinner, boolean[] selectType, ArrayList<Integer> list, String[] type) {
        typeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select type");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(type, selectType, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            list.add(which);
                            Collections.sort(list);
                        } else {
                            list.remove((Integer) which);
                        }
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < list.size(); i++) {
                            stringBuilder.append(type[list.get(i)]);
                            if (i != list.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        typeSpinner.setText(stringBuilder.toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < selectType.length; i++) {
                            selectType[i] = false;
                            list.clear();
                            typeSpinner.setText("");
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private ArrayAdapter<String> getAdapter() {
        return new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == 0) { // Hint
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(0));
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount();
            }

            @Override /* Disable selection of the Hint (first selection) */
            public boolean isEnabled(int position) {
                return (position != 0);
            }

            @Override /* Set the color of the Hint (first selection) to Grey */
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) tv.setTextColor(Color.GRAY);
                else tv.setTextColor(Color.BLACK);
                return view;
            }
        };
    }

    private void almog() {
        fAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String userID = fAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userID).child("isTeacher").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getValue(Integer.class) == 0) {
                        Toast.makeText(getActivity(), "Your a Student.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Your a Teacher.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}