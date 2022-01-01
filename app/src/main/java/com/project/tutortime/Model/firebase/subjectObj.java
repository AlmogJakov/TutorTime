package com.project.tutortime.Model.firebase;

import java.io.Serializable;

public class subjectObj implements Serializable {
    public enum SubName {
        HINT("Select subject"),
        Math("Math"),
        English("English"),
        hebrew("hebrew"),
        Arabic("Arabic"),
        Spanish("Spanish"),
        Physics("Physics"),
        Biology("Biology"),
        Literature("Literature"),
        History("History"),
        Chemistry("Chemistry"),
        Music("Music"),
        ComputerScience("Computer Science"),
        Citizenship("Citizenship"),
        Bible("Bible");
        private final String friendlyName;

        SubName(String friendlyName){
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
        both("both");
        private final String friendlyName;

        Type(String friendlyName){
            this.friendlyName = friendlyName;
        }

        @Override public String toString(){
            return friendlyName;
        }
    }

    public enum Prices {
        HINT("Select Price"),
        twenty ("20"),
        forty("40"),
        sixty("60"),
        eighty("80"),
        hundred("100"),
        hundredAndTwenty("120"),
        hundredAndForty("140"),
        hundredAndSixty("160"),
        hundredAndEighty("180"),
        TwoHundred("200"),
        TwoHundredAndTwenty("220"),
        TwoHundredAndForty("240"),
        TwoHundredAndSixty("260"),
        TwoHundredAndEighty("280"),
        ThreeHundred("300");
        private final String friendlyName;

        Prices(String friendlyName){
            this.friendlyName = friendlyName;
        }

        @Override public String toString(){
            return friendlyName;
        }
    }
    public int getPricesEnumPosition(int num) {
        if (num == 20) return 1;
        else if (num == 40) return 2;
        else if (num == 60) return 3;
        else if (num == 80) return 4;
        else if (num == 100) return 5;
        else if (num == 120) return 6;
        else if (num == 140) return 7;
        else if (num == 160) return 8;
        else if (num == 180) return 9;
        else if (num == 200) return 10;
        else if (num == 220) return 11;
        else if (num == 240) return 12;
        else if (num == 260) return 13;
        else if (num == 280) return 14;
        else if (num == 300) return 15;
        return 0;
    }


    private String sName;
    private String type;
    private String key;
    private int price;
    private String experience;

    public subjectObj(){ }

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
