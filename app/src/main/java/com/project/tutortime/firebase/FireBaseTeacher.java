package com.project.tutortime.firebase;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class FireBaseTeacher extends firebaseBaseModel{
    FireBaseUser u = new FireBaseUser();

    public void addTeacherToDB(String phoneNum, String description, List<subjectObj> sub){
        writeNewTeacher(phoneNum, description, sub);
    }
    public void writeNewTeacher(String phoneNum, String description, List<subjectObj> sub){
        teacherObj teacher = new teacherObj(phoneNum, description, sub);
        System.out.println(teacher.getDescription());

        String teacherId= myRef.push().getKey();
        u.getUserRef().child("teacherID").setValue(teacherId);
        myRef.child("teachers").child(teacherId).setValue(teacher);
        //u.getUserRef().child("isTeacher").setValue(1);
    }
}
