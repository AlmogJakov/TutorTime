package com.project.tutortime.firebase;

import java.util.ArrayList;
import java.util.List;

public class FireBaseTeacher extends firebaseBaseModel{
    FireBaseUser u = new FireBaseUser();

    public String addTeacherToDB(String phoneNum, String description, String userid, List<subjectObj> sub, String imgUrl){
        //writeNewTeacher(phoneNum, description, userid, sub,  imgUrl);
        teacherObj teacher = new teacherObj(phoneNum, description, userid, sub,  imgUrl);
        System.out.println(teacher.getDescription());
        String teacherId = myRef.push().getKey();
        u.getUserRef().child("teacherID").setValue(teacherId);
        myRef.child("teachers").child(teacherId).setValue(teacher);
        return teacherId;
    }

    public void setSubList(String teacherId, ArrayList<subjectObj> listSub){
        myRef.child("teachers").child(teacherId).child("sub").setValue(listSub);
    }
}