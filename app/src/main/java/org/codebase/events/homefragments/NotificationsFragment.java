package org.codebase.events.homefragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.codebase.events.R;
import org.codebase.events.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    FragmentNotificationsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}