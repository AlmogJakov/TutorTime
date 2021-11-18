package com.project.tutortime.firebase;

public class FireBaseUser extends firebaseBaseModel {
    public void addUserToDB(String fName, String lName, String email, String city, String id){
        writeNewUser(fName,lName,email,city, id);
    }
    public void writeNewUser(String fName, String lName, String email, String city, String id){
        userObj user = new userObj(fName,lName,email, city);
        myRef.child("users").child(id).setValue(user);

    }
}
