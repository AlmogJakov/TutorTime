package com.project.tutortime.ui.userprofile;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.tutortime.MainActivity;
import com.project.tutortime.R;
import com.project.tutortime.databinding.FragmentMyProfileBinding;
import com.project.tutortime.databinding.FragmentMySubListBinding;
import com.project.tutortime.firebase.FireBaseUser;
import com.project.tutortime.firebase.subjectObj;
import com.project.tutortime.ui.mysublist.MySubListViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyProfile extends Fragment {

    private MyProfileViewModel MyProfileViewModel;
    private FragmentMyProfileBinding binding;

    EditText fname, lname;
    Spinner citySpinner, genderSpinner;
    Button saveProfile;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    String teacherID;

    public static MyProfile newInstance() {
        return new MyProfile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        MyProfileViewModel = new ViewModelProvider(this).get(MyProfileViewModel.class);
        binding = FragmentMyProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fname = binding.myFName;
        lname = binding.myLName;
        citySpinner = binding.spinnerCity;
        genderSpinner = binding.spinnerGender;
        saveProfile = binding.btnSaveProfile;

        /* Select City Spinner Code () */
        ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] select_gender = getResources().getStringArray(R.array.Gender);
        a.add("Choose Gender");
        a.addAll(select_gender);
        genderSpinner.setAdapter(a);
        genderSpinner.setSelection(0); //display hint

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] cities = getResources().getStringArray(R.array.Cities);
        adapter.add("Choose City");
        adapter.addAll(cities);
        citySpinner.setAdapter(adapter);
        citySpinner.setSelection(0); //display hint
        /* END Select City Spinner Code () */

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String firstName = fname.getText().toString().trim();
                String lastName = lname.getText().toString().trim();
                String city = citySpinner.getSelectedItem().toString();
                String gender = genderSpinner.getSelectedItem().toString();
                if (TextUtils.isEmpty(firstName)) {
                    fname.setError("First name is required.");
                    return; }
                if (TextUtils.isEmpty(lastName)) {
                    lname.setError("Last name is required.");
                    return; }
                if (citySpinner.getSelectedItemPosition() == 0) {
                    TextView errorText = (TextView) citySpinner.getSelectedView();
                    errorText.setError("City is required.");
                    Toast.makeText(getActivity(), "City is required.",
                            Toast.LENGTH_SHORT).show();
                    return; }
                new FireBaseUser().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        /* sort the list of service cities */
                        //Collections.sort(listCities);
                        /* get teacher ID */
                        teacherID = dataSnapshot.child("teacherID").getValue(String.class);
                        /* Make a list of all the RealTime DataBase commands to execute
                            (for the purpose of executing all the commands at once) */
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("users/" + userID + "/fName", firstName);
                        childUpdates.put("users/" + userID + "/lName", lastName);
                        childUpdates.put("users/" + userID + "/city", city);
                        childUpdates.put("users/" + userID + "/gender", gender);

                        myRef.updateChildren(childUpdates);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
                goToTutorMain(getActivity());
            }
        });




        return root;
    }

    //    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        MyProfileViewModel = new ViewModelProvider(this).get(MyProfileViewModel.class);
//        // TODO: Use the ViewModel
//    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fname.setText(dataSnapshot.child("users").child(userID).child("fName").getValue(String.class));
                lname.setText(dataSnapshot.child("users").child(userID).child("lName").getValue(String.class));
                teacherID = dataSnapshot.child("users").child(userID).child("teacherID").getValue(String.class);
                String currCity = dataSnapshot.child("users").child(userID).
                        child("city").getValue(String.class);
                String[] cities = getResources().getStringArray(R.array.Cities);
                for (int i = 0; i < cities.length; i++) {
                    if (citySpinner.getItemAtPosition(i).equals(currCity)) {
                        citySpinner.setSelection(i);
                        break;
                    }
                }
                String currGender = dataSnapshot.child("users").child(userID).
                        child("gender").getValue(String.class);
                String[] arrGender = getResources().getStringArray(R.array.Gender);
                for (int i = 0; i < arrGender.length; i++) {
                    if (genderSpinner.getItemAtPosition(i).equals(currGender)) {
                        genderSpinner.setSelection(i);
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "onCreate error. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* get fragment activity (to do actions on the activity) */
    private void goToTutorMain(Activity currentActivity) {
        //Activity currentActivity = getContext();
        /* were logging in as tutor (tutor status value = 1).
         * therefore, pass 'Status' value (1) to MainActivity. */
        final ArrayList<Integer> arr = new ArrayList<Integer>();
        arr.add(0);
        //Intent intent = new Intent(SetTutorProfile.this, MainActivity.class);
        Intent intent = new Intent(currentActivity, MainActivity.class);
        /* disable returning to SetTutorProfile class after opening main
         * activity, since we don't want the user to re-choose Profile
         * -> because the tutor profile data still exists with no use!
         * (unless we implementing method to remove the previous data) */
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("status",arr);
        /* finish last activities to prevent last MainActivity to run with Customer view */
        currentActivity.finishAffinity();
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }

}