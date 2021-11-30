package com.project.tutortime.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class FireBaseUser extends firebaseBaseModel {
    FirebaseAuth fAuth;
    public void addUserToDB(String fName, String lName, String email, String city, String id){
        writeNewUser(fName,lName,email,city, id);
    }
    public void writeNewUser(String fName, String lName, String email, String city, String id){
        userObj user = new userObj(fName,lName,email, city);
        myRef.child("users").child(id).setValue(user);
    }

    public DatabaseReference getUserRef(){
        String userID = fAuth.getInstance().getCurrentUser().getUid();
        return myRef.child("users").child(userID).getRef();
    }
}
