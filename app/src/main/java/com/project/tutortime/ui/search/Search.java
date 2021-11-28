package com.project.tutortime.ui.search;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.ChooseOne;
import com.project.tutortime.Login;
import com.project.tutortime.MainActivity;
import com.project.tutortime.R;
import com.project.tutortime.databinding.SearchFragmentBinding;
import com.project.tutortime.databinding.TeacherCardFragmentBinding;
import com.project.tutortime.ui.teachercard.TeacherCardViewModel;

public class Search extends Fragment {
    private SearchViewModel SearchViewModel;
    private SearchFragmentBinding binding;
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

        SearchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = SearchFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSearch;
        SearchViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}