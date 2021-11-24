package com.project.tutortime.firebase;

import java.io.Serializable;

public class userObj implements Serializable {
    private String fName;
    private String lName;
    private String email;
    private String city;
    private String teacherID;


    public userObj(){
    }

    public userObj(String fName, String lName, String email, String city){
        this.fName=fName;
        this.lName=lName;
        this.email=email;
        this.city=city;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }


}
