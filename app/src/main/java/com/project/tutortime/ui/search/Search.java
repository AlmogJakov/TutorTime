package com.project.tutortime.ui.search;

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
import com.project.tutortime.databinding.SearchFragmentBinding;
import com.project.tutortime.databinding.TeacherCardFragmentBinding;
import com.project.tutortime.ui.teachercard.TeacherCardViewModel;

public class Search extends Fragment {

    private SearchViewModel SearchViewModel;
    private SearchFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = SearchFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSearch;
        SearchViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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