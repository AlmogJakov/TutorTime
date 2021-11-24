package com.project.tutortime.firebase;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class FireBaseTeacher extends firebaseBaseModel{
    FireBaseUser u = new FireBaseUser();

    public void addTeacherToDB(int phoneNum, List<subjectObj> sub){
        writeNewTeacher(phoneNum,sub);
    }
    public void writeNewTeacher(int phoneNum, List<subjectObj> sub){
        teacherObj teacher = new teacherObj(phoneNum, sub);
        String teacherId= myRef.push().getKey();
        teacher.setId(teacherId);
        u.getUserRef().child("teacherID").setValue(teacherId);
        myRef.child("teachers").child(teacherId).setValue(teacher);
    }
}
