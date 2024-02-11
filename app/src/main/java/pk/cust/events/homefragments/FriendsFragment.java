package pk.cust.events.homefragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pk.cust.events.adapter.EventsAdapter;
import pk.cust.events.databinding.FragmentFriendsBinding;
import pk.cust.events.model.EventsModel;

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

//        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Ahmad"));
//        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Maha"));
//        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Usama"));
//        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Wahid"));
//        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Maham"));
//        eventsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Asma"));

        GridLayoutManager layoutManager = new GridLayoutManager(requireActivity(), 2);
        binding.eventsRV.setLayoutManager(layoutManager);

        if (eventsModelArrayList.size() == 0) {
            binding.eventsRV.setVisibility(View.GONE);
            binding.noDataText.setVisibility(View.VISIBLE);
        } else {
            binding.eventsRV.setVisibility(View.VISIBLE);
            binding.noDataText.setVisibility(View.GONE);

            eventsAdapter = new EventsAdapter(requireActivity(), eventsModelArrayList);
            binding.eventsRV.setAdapter(eventsAdapter);
        }

        return binding.getRoot();
    }
}