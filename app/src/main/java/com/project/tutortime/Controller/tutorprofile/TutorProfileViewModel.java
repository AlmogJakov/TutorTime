package com.project.tutortime.Controller.tutorprofile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TutorProfileViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<String> mText;

    public TutorProfileViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is Tutor Profile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}