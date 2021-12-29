package com.project.tutortime.firebase;

import java.io.Serializable;
import java.util.Date;

public class userObj implements Serializable {
    private String fName;
    private String lName;
    private String email;
    private String city;
    private String teacherID;
    private String gender;
    private int IsTeacher;
    private long lSeen;


    public userObj(){
    }

    public userObj(String fName, String lName, String email, String city, String gender){
        this.fName=fName;
        this.lName=lName;
        this.email=email;
        this.city=city;
        this.gender=gender;
        this.IsTeacher=-1;
    }

    public long getlSeen() {
        return lSeen;
    }

    public void setlSeen(long lSeen) {
        this.lSeen = lSeen;
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

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public int getIsTeacher() {
        return IsTeacher;
    }

    public void setIsTeacher(int teacherInd) {
        this.IsTeacher = teacherInd;
    }
}
