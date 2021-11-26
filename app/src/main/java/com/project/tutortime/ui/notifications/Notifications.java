package com.project.tutortime.ui.notifications;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.tutortime.R;
import com.project.tutortime.databinding.NotificationsFragmentBinding;
import com.project.tutortime.databinding.TeacherCardFragmentBinding;
import com.project.tutortime.ui.teachercard.TeacherCardViewModel;

public class Notifications extends Fragment {

    private NotificationsViewModel NotificationsViewModel;
    private NotificationsFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = NotificationsFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        NotificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}