package com.project.tutortime.Model.firebase;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.R;

import java.util.HashMap;
import java.util.Map;

public class FireBaseUser extends FirebaseManager {
    FirebaseAuth fAuth;
    public void addUserToDB(String id, userObj user){
        myRef.child("users").child(id).setValue(user);
    }
    public void addUserToDB(String fName, String lName, String email, String city, String gender, String id){
        writeNewUser(fName,lName,email,city, gender, id);
    }

    public void writeNewUser(String fName, String lName, String email, String city, String gender, String id){
        userObj user = new userObj(fName,lName,email, city, gender);
        myRef.child("users").child(id).setValue(user);
    }

    public DatabaseReference getUserRef(){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return myRef.child("users").child(userID).getRef();
    }

    public void updateUserDetails(String firstName, String lastName, String city, String gender){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
       getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /* sort the list of service cities */
                //Collections.sort(listCities);
                        /* Make a list of all the RealTime DataBase commands to execute
                            (for the purpose of executing all the commands at once) */
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("users/" + userID + "/fName", firstName);
                childUpdates.put("users/" + userID + "/lName", lastName);
                childUpdates.put("users/" + userID + "/city", city);
                if (!gender.equals("")) {
                    childUpdates.put("users/" + userID + "/gender", gender);
                }
                myRef.updateChildren(childUpdates);
            }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       } );
    }

}
