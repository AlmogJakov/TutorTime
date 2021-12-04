package com.project.tutortime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.firebase.teacherObj;
import com.project.tutortime.firebase.userObj;

public class LoadingScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        /* DISABLE landscape orientation  */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        /* Unnecessary teacher remover
//        * Should be removed! for developers ONLY!
//        * this next code purpose is to remove teachers from database that has no userID pointing at them */
//        FirebaseAuth fAuth = FirebaseAuth.getInstance();
//        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                DataSnapshot users = dataSnapshot.child("users");
//                DataSnapshot teachers = dataSnapshot.child("teachers");
//                for (DataSnapshot snapshot : teachers.getChildren()) {
//                    teacherObj teacher = snapshot.getValue(teacherObj.class);
//                    String usrID = teacher.getUserID();
//                    String teacherID = snapshot.getKey();
//                    if (users.child(usrID) == null || ((users.child(usrID).child("teacherID") != null) &&
//                            (!users.child(usrID).child("teacherID").getValue(String.class).equals(teacherID)))) {
//                        //System.out.println(users.child(usrID).child("teacherID").getValue(String.class));
//                        //System.out.println(teacherID);
//                        snapshot.getRef().removeValue();
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) { }
//        });
//        /* END Unnecessary teacher remover */
    }
}