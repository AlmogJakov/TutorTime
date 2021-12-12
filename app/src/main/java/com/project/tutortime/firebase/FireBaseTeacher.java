package com.project.tutortime.firebase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FireBaseTeacher extends firebaseBaseModel{
    FireBaseUser u = new FireBaseUser();

    public String addTeacherToDB(String phoneNum, String description, String userid, List<String> ServiceCities,
                                 List<subjectObj> sub, String imgUrl){
        //writeNewTeacher(phoneNum, description, userid, sub,  imgUrl);
        teacherObj teacher = new teacherObj(phoneNum, description, userid, ServiceCities, sub,  imgUrl);
        String teacherId = myRef.push().getKey();
        u.getUserRef().child("teacherID").setValue(teacherId);
        myRef.child("teachers").child(teacherId).setValue(teacher);
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