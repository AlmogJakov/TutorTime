package com.project.tutortime.Model.firebase;

import java.io.Serializable;
import java.util.HashMap;

public class rankObj implements Serializable {
    private HashMap<String, Integer> userRating;
    private float avgRank;

    public rankObj(){}

    public rankObj(HashMap<String, Integer> userRating, float avgRank) {
        this.userRating = userRating;
        this.avgRank = avgRank;
    }

    public HashMap<String, Integer> getUserRating() {
        return userRating;
    }

    public void setUserRating(HashMap<String, Integer> userRating) {
        this.userRating = userRating;
    }

    public float getAvgRank() {
        return avgRank;
    }

    public void setAvgRank(float avgRank) {
        this.avgRank = avgRank;
    }
}
