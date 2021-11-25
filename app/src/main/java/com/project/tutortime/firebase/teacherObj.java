package com.project.tutortime.firebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class teacherObj implements Serializable {
    private String phoneNum;
    private List<subjectObj> sub;
    private String description;

    public teacherObj(){
    }

    public teacherObj(String phoneNum, String description, List<subjectObj> sub) {
        this.phoneNum=phoneNum;
        this.description=description;
        this.sub= new ArrayList<subjectObj>(sub);
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public List<subjectObj> getSub() {
        return sub;
    }

    public void setSub(List<subjectObj> sub) {
        this.sub = sub;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
