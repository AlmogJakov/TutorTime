package com.project.tutortime.ui.search;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.project.tutortime.SetTutorProfile;
import com.project.tutortime.firebase.subjectObj;


public class Search extends Fragment {
    FirebaseAuth fAuth;
    private DatabaseReference mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String userID = fAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userID).child("isTeacher").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getValue()!= null) {
                    if (dataSnapshot.getValue(Integer.class) == 0) {
                        Toast.makeText(getActivity(), "Your a Student.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Your a Teacher.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        View v = inflater.inflate(R.layout.fragment_search, container, false);
        Button addBtn = v.findViewById(R.id.buttonAcount);

        Spinner nameSpinner = v.findViewById(R.id.selectSub);
        nameSpinner.setAdapter(new ArrayAdapter<>
                (this.getActivity(), android.R.layout.simple_spinner_item, subjectObj.SubName.values()));

        Spinner typeSpinner = v.findViewById(R.id.learn);
        typeSpinner.setAdapter(new ArrayAdapter<>
                (this.getActivity(), android.R.layout.simple_spinner_item, subjectObj.Type.values()));

        Spinner spinner = (Spinner) v.findViewById(R.id.selectCity);
        ArrayAdapter<String> adapter = getAdapter();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] cities = getResources().getStringArray(R.array.Cities);
        adapter.add("Choose City");
        adapter.addAll(cities);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return v;

    }

    private ArrayAdapter<String> getAdapter() {
        return new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == 0) { // Hint
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(0)); }
                return v; }
            @Override
            public int getCount() { return super.getCount(); }
            @Override /* Disable selection of the Hint (first selection) */
            public boolean isEnabled(int position) { return (position != 0); }
            @Override /* Set the color of the Hint (first selection) to Grey */
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView)view;
                if (position == 0) tv.setTextColor(Color.GRAY); else tv.setTextColor(Color.BLACK);
                return view; }
        };
    }


}