package com.project.tutortime.adapter;

import com.project.tutortime.firebase.teacherObj;
import com.project.tutortime.firebase.userObj;

public class TutorAdapterItem {
    userObj user;
    teacherObj teacher;
    String subName;
    public TutorAdapterItem(userObj user, teacherObj teacher, String subName) {
        this.user = user;
        this.teacher = teacher;
        this.subName = subName;
    }
}
