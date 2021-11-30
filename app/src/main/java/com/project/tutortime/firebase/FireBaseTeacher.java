package com.project.tutortime.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FireBaseTeacher extends firebaseBaseModel{
    FireBaseUser u = new FireBaseUser();
    String teacherId;

    public void addTeacherToDB(String phoneNum, String description, String userid, List<subjectObj> sub, String imgUrl){
        writeNewTeacher(phoneNum, description, userid, sub,  imgUrl);
    }
    public void writeNewTeacher(String phoneNum, String description, String userid, List<subjectObj> sub, String imgUrl){
        teacherObj teacher = new teacherObj(phoneNum, description, userid, sub,  imgUrl);
        System.out.println(teacher.getDescription());
        String teacherId = myRef.push().getKey();
        u.getUserRef().child("teacherID").setValue(teacherId);
        myRef.child("teachers").child(teacherId).setValue(teacher);
    }

}