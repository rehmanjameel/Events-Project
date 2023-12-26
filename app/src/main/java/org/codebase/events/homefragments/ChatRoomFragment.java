package org.codebase.events.homefragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.codebase.events.R;
import org.codebase.events.databinding.FragmentChatRoomBinding;

public class ChatRoomFragment extends Fragment {

    FragmentChatRoomBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}