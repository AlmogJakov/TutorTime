package com.project.tutortime.ui.search;

import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.ChooseOne;
import com.project.tutortime.Login;
import com.project.tutortime.MainActivity;
import com.project.tutortime.R;

public class SearchViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> mText;

    public SearchViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Search fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}