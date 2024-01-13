package org.codebase.events.homefragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.codebase.events.R;
import org.codebase.events.activities.ProfileActivity;
import org.codebase.events.adapter.HomeEventsAdapter;
import org.codebase.events.databinding.FragmentHomeBinding;
import org.codebase.events.model.HomeEventsModel;
import org.codebase.events.utils.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    HomeEventsAdapter eventsAdapter;
    ArrayList<HomeEventsModel> eventsModelArrayList = new ArrayList<>();
    FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        Glide.with(this)
                .load(Uri.parse(App.getString("user_image")).buildUpon().scheme("https").build())
                .centerCrop()
                .error(R.drawable.baseline_account_circle_24)
                .placeholder(R.drawable.baseline_account_circle_24)
                .into(binding.profilePicture);

        binding.profilePicture.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        eventsModelArrayList.add(new HomeEventsModel(0, R.drawable.profile, "Ahmad", R.drawable.newyear, "New year"));
        eventsModelArrayList.add(new HomeEventsModel(0, R.drawable.profile, "Ahmad", R.drawable.event1, "University event"));
        eventsModelArrayList.add(new HomeEventsModel(0, R.drawable.profile, "Ahmad", R.drawable.football, "Football match"));
        eventsModelArrayList.add(new HomeEventsModel(0, R.drawable.profile, "Ahmad", R.drawable.palestine, "Palestine situation"));
        eventsModelArrayList.add(new HomeEventsModel(0, R.drawable.profile, "Ahmad", R.drawable.exhibition, "Dubai IT exhibition"));

        binding.eventsRV.setLayoutManager(new LinearLayoutManager(requireActivity()));
        eventsAdapter = new HomeEventsAdapter(requireActivity(), eventsModelArrayList);
        binding.eventsRV.setAdapter(eventsAdapter);

        db = FirebaseFirestore.getInstance();

        addDataToFireStore();

        return binding.getRoot();
    }

    private void addDataToFireStore() {

    }
}