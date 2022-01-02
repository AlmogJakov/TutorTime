package com.project.tutortime.Model.firebase;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.tutortime.Controller.tutorprofile.TutorProfile;
import com.project.tutortime.Controller.tutorsublist.TutorSubList;
import com.project.tutortime.R;
import com.project.tutortime.View.LoadingScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireBaseTutor extends FirebaseManager {
    FireBaseUser u = new FireBaseUser();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    public String addTeacherToDB(String phoneNum, String description, List<String> serviceCities,
                                 List<subjectObj> sub, String imgUrl, rankObj rank){
        String userid = fAuth.getCurrentUser().getUid();
        //writeNewTeacher(phoneNum, description, userid, sub,  imgUrl);
        tutorObj teacher = new tutorObj(phoneNum, description, userid, serviceCities, imgUrl, rank);
        String teacherId = myRef.push().getKey();
        /* set user 'teacherID' variable */
        u.getUserRef().child("teacherID").setValue(teacherId);
        /* set the teacher object */
        myRef.child("teachers").child(teacherId).setValue(teacher);
        /* ADD ALL TEACHER SUBJECTS IN ONE COMMAND */
        new FireBaseUser().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /* Make a list of all the RealTime DataBase commands to execute
                 * (for the purpose of executing all the commands at once) */
                Map<String, Object> childUpdates = new HashMap<>();
                /* (add a command) add the subject to the Search Tree */
                for(subjectObj sList : sub) {
                    for (String aCity : serviceCities) {
                        if(sList.getType().equals("frontal") || sList.getType().equals("both")) {
                            childUpdates.put("search/" + sList.getType() + "/" + sList.getsName()
                                    + "/" + aCity + "/" + sList.getPrice() + "/"+teacherId, teacherId);
                        }
                    }
                    if(sList.getType().equals("online")){
                        childUpdates.put("search/" + sList.getType() + "/" + sList.getsName()
                                + "/" + sList.getPrice() + "/"+teacherId, teacherId);
                    }
                    /* (add a command) add the subject to the current teacher object */
                    childUpdates.put("teachers/" + teacherId + "/sub/" + sList.getsName(), sList);
                }
//                for (int i = 0; i < sub.size(); i++) {
//                    /* (add a command) add the subject to the Search Tree */
//                    childUpdates.put("search/" + sub.get(i).getType() + "/" + sub.get(i).getsName() +
//                            "/" + City + "/" + sub.get(i).getPrice() + "/" + teacherId, teacherId);
//                    /* (add a command) add the subject to the current teacher object */
//                    childUpdates.put("teachers/" + teacherId + "/sub/" + sub.get(i).getsName(), sub.get(i));
//                }
                /* Finally, execute all RealTime DataBase commands in one command (safely). */
                myRef.updateChildren(childUpdates);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        return teacherId;
    }

    public void setServiceCities(String teacherId, ArrayList<subjectObj> serviceCities){
        myRef.child("teachers").child(teacherId).child("serviceCities").setValue(serviceCities);
    }

    public void setSubList(String teacherId, ArrayList<subjectObj> listSub){
        myRef.child("teachers").child(teacherId).child("sub").setValue(listSub);
    }

    public void setPhoneNum(String teacherId, String pNum){
        myRef.child("teachers").child(teacherId).child("phoneNum").setValue(pNum);
    }

    public void setDescription(String teacherId, String des){
        myRef.child("teachers").child(teacherId).child("description").setValue(des);
    }

    public void setImgUrl(String teacherId, String imgUrl){
        myRef.child("teachers").child(teacherId).child("imgUrl").setValue(imgUrl);
    }
}