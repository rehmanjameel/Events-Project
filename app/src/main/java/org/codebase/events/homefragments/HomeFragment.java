package org.codebase.events.homefragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.codebase.events.R;
import org.codebase.events.adapter.EventsAdapter;
import org.codebase.events.databinding.FragmentHomeBinding;
import org.codebase.events.model.EventsModel;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    EventsAdapter eventsAdapter;
    ArrayList<EventsModel> eventsModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Ahmad", R.drawable.newyear, "topic"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Ahmad", R.drawable.event1, "topic"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Ahmad", R.drawable.football, "topic"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Ahmad", R.drawable.palestine, "topic"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Ahmad", R.drawable.exhibition, "topic"));

        binding.eventsRV.setLayoutManager(new LinearLayoutManager(requireActivity()));
        eventsAdapter = new EventsAdapter(requireActivity(), eventsModelArrayList);
        binding.eventsRV.setAdapter(eventsAdapter);

        return binding.getRoot();
    }
}