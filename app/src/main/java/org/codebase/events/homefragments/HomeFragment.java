package org.codebase.events.homefragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.codebase.events.R;
import org.codebase.events.adapter.HomeEventsAdapter;
import org.codebase.events.databinding.FragmentHomeBinding;
import org.codebase.events.model.HomeEventsModel;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    HomeEventsAdapter eventsAdapter;
    ArrayList<HomeEventsModel> eventsModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        eventsModelArrayList.add(new HomeEventsModel(0, R.drawable.profile, "Ahmad", R.drawable.newyear, "New year"));
        eventsModelArrayList.add(new HomeEventsModel(0, R.drawable.profile, "Ahmad", R.drawable.event1, "University event"));
        eventsModelArrayList.add(new HomeEventsModel(0, R.drawable.profile, "Ahmad", R.drawable.football, "Football match"));
        eventsModelArrayList.add(new HomeEventsModel(0, R.drawable.profile, "Ahmad", R.drawable.palestine, "Palestine situation"));
        eventsModelArrayList.add(new HomeEventsModel(0, R.drawable.profile, "Ahmad", R.drawable.exhibition, "Dubai IT exhibition"));

        binding.eventsRV.setLayoutManager(new LinearLayoutManager(requireActivity()));
        eventsAdapter = new HomeEventsAdapter(requireActivity(), eventsModelArrayList);
        binding.eventsRV.setAdapter(eventsAdapter);

        return binding.getRoot();
    }
}