package org.codebase.events.homefragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.codebase.events.R;
import org.codebase.events.adapter.EventsAdapter;
import org.codebase.events.databinding.FragmentFriendsBinding;
import org.codebase.events.model.EventsModel;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    FragmentFriendsBinding binding;

    EventsAdapter eventsAdapter;
    ArrayList<EventsModel> eventsModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFriendsBinding.inflate(inflater, container, false);

        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Ahmad"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Maha"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Usama"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Wahid"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Maham"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Asma"));

        GridLayoutManager layoutManager = new GridLayoutManager(requireActivity(), 2);
        binding.eventsRV.setLayoutManager(layoutManager);

        eventsAdapter = new EventsAdapter(requireActivity(), eventsModelArrayList);
        binding.eventsRV.setAdapter(eventsAdapter);

        return binding.getRoot();
    }
}