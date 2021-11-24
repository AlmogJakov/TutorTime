package com.project.tutortime.firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class firebaseBaseModel {
    protected DatabaseReference myRef;

    public firebaseBaseModel(){
        myRef= FirebaseDatabase.getInstance().getReference();
    }

}
