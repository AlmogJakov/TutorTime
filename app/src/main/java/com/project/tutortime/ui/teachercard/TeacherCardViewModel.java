package com.project.tutortime.ui.teachercard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TeacherCardViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> mText;

    public TeacherCardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Teacher Card fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}