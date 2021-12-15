package com.project.tutortime.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.R;
import com.project.tutortime.TutorAdapter;
import com.project.tutortime.databinding.FragmentHomeBinding;
import com.project.tutortime.firebase.userObj;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    DatabaseReference  myRef = FirebaseDatabase.getInstance().getReference();
    TextView hello;
    ListView listview;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });


        hello = binding.helloName;
        listview = binding.featuresList;

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("users").child(userID).child("fName").getValue(String.class);
                //hello.append(" " + name + ",");
                hello.setText("שלום " + name + ",");
                if (!dataSnapshot.child("search").exists()) return;
                //long counter = dataSnapshot.child("search").getChildrenCount();
                //String teacher = dataSnapshot.child("search").getChildren();
                //DatabaseReference refer = dataSnapshot.getRef();
                //Query query = refer.
                //System.out.println("Search childs: "+counter);
                String[] values = new String[] { "Tutor1", "Tutor2", "Tutor3"};
                final TutorAdapter adapter = new TutorAdapter(getContext(), values);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        return root;
        }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}