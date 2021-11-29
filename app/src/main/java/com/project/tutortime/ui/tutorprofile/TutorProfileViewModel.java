package com.project.tutortime.ui.tutorprofile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TutorProfileViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> mText;

    public TutorProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Tutor Profile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}