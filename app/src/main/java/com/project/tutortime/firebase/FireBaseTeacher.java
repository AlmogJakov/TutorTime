package com.project.tutortime.firebase;

import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FireBaseTeacher extends firebaseBaseModel{
    FireBaseUser u = new FireBaseUser();

    public String addTeacherToDB(String phoneNum, String description, String userid, List<String> ServiceCities,
                                 List<subjectObj> sub, String imgUrl){
        //writeNewTeacher(phoneNum, description, userid, sub,  imgUrl);
        teacherObj teacher = new teacherObj(phoneNum, description, userid, ServiceCities, imgUrl);
        /* get teacher ID */
        String teacherId = myRef.push().getKey();
        /* set user 'teacherID' variable */
        u.getUserRef().child("teacherID").setValue(teacherId);
        /* set the teacher object */
        myRef.child("teachers").child(teacherId).setValue(teacher);
        /* ADD ALL TEACHER SUBJECTS IN ONE COMMAND */
        new FireBaseUser().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String City = dataSnapshot.child("city").getValue(String.class);
                /* Make a list of all the RealTime DataBase commands to execute
                 * (for the purpose of executing all the commands at once) */
                Map<String, Object> childUpdates = new HashMap<>();
                for (int i = 0; i < sub.size(); i++) {
                    /* (add a command) add the subject to the Search Tree */
                    childUpdates.put("search/" + sub.get(i).getType() + "/" + sub.get(i).getsName() + "/" + City + "/" + sub.get(i).getPrice() + "/" + teacherId, teacherId);
                    /* (add a command) add the subject to the current teacher object */
                    childUpdates.put("teachers/" + teacherId + "/sub/" + sub.get(i).getsName(), sub.get(i));
                }
                /* Finally, execute all RealTime DataBase commands in one command (safely). */
                myRef.updateChildren(childUpdates);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        return teacherId;
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