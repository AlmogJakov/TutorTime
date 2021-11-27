package com.project.tutortime.firebase;

import java.io.Serializable;

public class subjectObj implements Serializable {
    public enum SubName {
        HINT("Select subject"),
        Math("Math"),
        English("English"),
        Physics("Physics"),
        Biology("Biology"),
        Literature("Literature"),
        History("History"),
        Chemistry("Chemistry"),
        Music("Music"),
        ComputerScience("Computer Science"),
        Citizenship("Citizenship"),
        Bible("Bible");
        private String friendlyName;

        private SubName(String friendlyName){
            this.friendlyName = friendlyName;
        }

        @Override public String toString(){
            return friendlyName;
        }
    }
    public enum Type {
        HINT("Select learning type"),
        online("online"),
        frontal("frontal"),
        onlinefrontal("online/frontal");
        private String friendlyName;

        private Type(String friendlyName){
            this.friendlyName = friendlyName;
        }

        @Override public String toString(){
            return friendlyName;
        }
    }
    private String sName;
    private String type;
    private int price;
    private String experience;

    public subjectObj(){
    }

    public subjectObj(String sName, String type, int price, String experience){
        this.sName=sName;
        this.type=type;
        this.price=price;
        this.experience=experience;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    @Override
    public String toString() {
        return "Subject: " + sName + "\n" +
                "Type: " + type + "\n" +
                "Price: " + price + "\n" +
                "Experience: " + experience + "\n" ;
    }
}
