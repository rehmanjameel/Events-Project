package org.codebase.events.homefragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.codebase.events.R;
import org.codebase.events.adapter.EventsAdapter;
import org.codebase.events.databinding.FragmentEventsBinding;
import org.codebase.events.model.EventsModel;

import java.util.ArrayList;

public class EventsFragment extends Fragment {

   FragmentEventsBinding binding;
   EventsAdapter eventsAdapter;
   ArrayList<EventsModel> eventsModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventsBinding.inflate(inflater, container, false);

        eventsModelArrayList.add(new EventsModel(0, R.drawable.event1, "Event"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.exhibition, "Event"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.newyear, "Event"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.palestine, "Event"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.football, "Event"));
        eventsModelArrayList.add(new EventsModel(0, R.drawable.event1, "Event"));

        GridLayoutManager layoutManager = new GridLayoutManager(requireActivity(), 2);
        binding.eventsRV.setLayoutManager(layoutManager);

        eventsAdapter = new EventsAdapter(requireActivity(), eventsModelArrayList);
        binding.eventsRV.setAdapter(eventsAdapter);
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu);

                // Add option Menu Here
                // Initialise menu item search bar
                // with id and take its object
                MenuItem searchViewItem = menu.findItem(R.id.search_bar);
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

                // attach setOnQueryTextListener
                // to search view defined above
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    // Override onQueryTextSubmit method which is call when submit query is searched
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        // If the list contains the search query than filter the adapter
                        // using the filter method with the query as its argument
//                        if (list.contains(query)) {
//                            adapter.getFilter().filter(query);
//                        } else {
//                            // Search query not found in List View
//                            Toast.makeText(MainActivity.this, "Not found", Toast.LENGTH_LONG).show();
//                        }
                        return false;
                    }

                    // This method is overridden to filter the adapter according
                    // to a search query when the user is typing search
                    @Override
                    public boolean onQueryTextChange(String newText) {
//                        adapter.getFilter().filter(newText);
                        return false;
                    }
                });

            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                // Handle option Menu Here
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        return binding.getRoot();
    }





}