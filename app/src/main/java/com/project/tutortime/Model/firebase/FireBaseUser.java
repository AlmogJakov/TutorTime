package com.project.tutortime.Model.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class FireBaseUser extends FirebaseManager {
    FirebaseAuth fAuth;
    public void addUserToDB(String id, userObj user){
        myRef.child("users").child(id).setValue(user);
    }

    public DatabaseReference getUserRef(){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return myRef.child("users").child(userID).getRef();
    }

    public void setFName(String userId, String fName){
        myRef.child("users").child(userId).child("fName").setValue(fName);
    }

    public void setLName(String userId, String lName){
        myRef.child("users").child(userId).child("lName").setValue(lName);
    }

    public void setCity(String userId,  String city){
        myRef.child("users").child(userId).child("city").setValue(city);
    }
}
