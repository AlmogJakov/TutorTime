package com.project.tutortime.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import com.project.tutortime.firebase.teacherObj;
import com.project.tutortime.firebase.userObj;

import java.io.Serializable;

public class TutorAdapterItem implements Parcelable, Comparable<TutorAdapterItem> {
    userObj user;
    teacherObj teacher;
    String subName;

    public TutorAdapterItem(userObj user, teacherObj teacher, String subName) {
        this.user = user;
        this.teacher = teacher;
        this.subName = subName;
    }

    protected TutorAdapterItem(Parcel in) {
        subName = in.readString();
    }

    public static final Creator<TutorAdapterItem> CREATOR = new Creator<TutorAdapterItem>() {
        @Override
        public TutorAdapterItem createFromParcel(Parcel in) {
            return new TutorAdapterItem(in);
        }

        @Override
        public TutorAdapterItem[] newArray(int size) {
            return new TutorAdapterItem[size];
        }
    };

    public userObj getUser() {
        return user;
    }

    public teacherObj getTeacher() {
        return teacher;
    }

    public String getSubName() {
        return subName;
    }

    @Override
    public int compareTo(TutorAdapterItem o) {
        return Integer.compare(teacher.getSub().get(subName).getPrice(), o.teacher.getSub().get(o.subName).getPrice());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeStringArray(new String[] {this.user,
//                this.teacher,
//                this.subName});
        dest.writeString(subName);
    }
}
