package com.project.tutortime.Model.firebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class tutorObj implements Serializable {
    private String phoneNum;
    private HashMap<String,subjectObj> sub;
    private rankObj rank;
    private String description;
    private String userID;
    private List<String> serviceCities;
    String imgUrl;

    public tutorObj(){ }

    public tutorObj(String phoneNum, String description, String userid, List<String> service_cities,
                    String imgUrl, rankObj rank) {
        this.phoneNum=phoneNum;
        this.description=description;
        this.serviceCities = new ArrayList<String>(service_cities);
        //this.sub = new HashMap<>();
        this.userID = userid;
        this.imgUrl=imgUrl;
        this.rank = rank;
    }


    public rankObj getRank() {
        return rank;
    }

    public void setRank(rankObj rank) {
        this.rank = rank;
    }


    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public HashMap<String,subjectObj> getSub() {
        //if (this.sub==null) return new ArrayList<subjectObj>(sub);
        return this.sub;
    }

    public void setSub(HashMap<String,subjectObj> sub) {
        //if (this.sub==null) return;
        this.sub = sub;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUserID() {
        return userID;
    }

    public List<String> getServiceCities() {
        return serviceCities;
    }

    public void setServiceCities(List<String> serviceCities) {
        if (this.serviceCities==null) return;
        for (String s : serviceCities) {
            this.serviceCities.add(s);
        }
    }
}


