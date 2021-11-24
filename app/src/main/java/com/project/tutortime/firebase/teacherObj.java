package com.project.tutortime.firebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class teacherObj implements Serializable {
    private int phoneNum;
    private List<subjectObj> sub;
    public String id;

    public teacherObj(){
    }

    public teacherObj(int phoneNum, List<subjectObj> sub) {
        this.phoneNum=phoneNum;
        this.sub= new ArrayList<subjectObj>();
    }

    public int getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(int phoneNum) {
        this.phoneNum = phoneNum;
    }

    public List<subjectObj> getSub() {
        return sub;
    }

    public void setSub(List<subjectObj> sub) {
        this.sub = sub;
    }

    public String getId(String id) {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
