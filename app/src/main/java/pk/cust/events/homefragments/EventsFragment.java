package pk.cust.events.homefragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import pk.cust.events.adapter.EventsAdapter;
import pk.cust.events.databinding.FragmentEventsBinding;
import pk.cust.events.model.EventsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class EventsFragment extends Fragment {

   FragmentEventsBinding binding;
   EventsAdapter eventsAdapter;
   ArrayList<EventsModel> eventsModelArrayList = new ArrayList<>();
    List<String> domainsItems = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    DocumentReference eventLikeRef;
    int totalLikes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventsBinding.inflate(inflater, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        setupUI(binding.relativeLayout);
        GridLayoutManager layoutManager = new GridLayoutManager(requireActivity(), 2);
        binding.eventsRV.setLayoutManager(layoutManager);


        binding.searchEditTextId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String query = editable.toString().trim();
                eventsAdapter.filter(query); // Call filter method in adapter
            }
        });

        binding.progressbar.setVisibility(View.VISIBLE);
        getDataFromFireStore();

        setUpDomains();
        return binding.getRoot();
    }

    private void setUpDomains() {
        // add domains in array list to show dropdown
        domainsItems.add("Science and Technology");
        domainsItems.add("Medicine and Healthcare");
        domainsItems.add("Business and Entrepreneurship");
        domainsItems.add("Education");
        domainsItems.add("Arts and Entertainment");
        domainsItems.add("Social Sciences");
        domainsItems.add("Communication and Media");
        domainsItems.add("Environmental and Sustainability");
        domainsItems.add("Sports and Recreation");
        domainsItems.add("Law and Justice");

        ArrayAdapter<String> domainAdapter = new ArrayAdapter<>(requireActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, domainsItems);
        binding.domainsDropDown.setAdapter(domainAdapter);

        binding.domainsDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedWord = domainsItems.get(position);
                eventsAdapter.filter(selectedWord);
            }
        });    }

    public void hideSoftKeyboard()
    {
        //Hides the SoftKeyboard
        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {
        // Set up touch listener to remove focus from EditText and TextInputEditText.
        if (!(view instanceof EditText) && !(view instanceof TextInputEditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    // Remove focus from EditText and TextInputEditText
                    if (getActivity() != null && (getActivity().getCurrentFocus() instanceof EditText ||
                            getActivity().getCurrentFocus() instanceof TextInputEditText)) {
                        hideSoftKeyboard();
                        getActivity().getCurrentFocus().clearFocus();
                    }
                    return false;
                }
            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }


    private void getDataFromFireStore() {
        eventsModelArrayList.clear();
        // Example code to retrieve posts with user details
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Task<Object>> tasks = new ArrayList<>(); // List to store user detail tasks

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userId = document.getString("userId");
                        String description = document.getString("description");
                        String imageUrl = document.getString("imageUrl");
                        String postId = document.getId();

                        Task<Object> userTask = db.collection("users")
                                .document(userId)
                                .get()
                                .continueWithTask(userDocumentTask -> {
                                    if (userDocumentTask.isSuccessful()) {
                                        DocumentSnapshot userDocument = userDocumentTask.getResult();
                                        if (userDocument.exists()) {
                                            String userName = userDocument.getString("user_name");
                                            String userImage = userDocument.getString("user_image");
                                            String userDomain = userDocument.getString("domain");
                                            int totalLikes = 0;
                                            if (document.contains("total_likes")) {
                                                totalLikes = document.getLong("total_likes").intValue();
                                            }
                                            // Add event only if total likes > 0
                                            if (totalLikes > 1) {
                                                eventsModelArrayList.add(new EventsModel(userId, userName, userImage, imageUrl, description, userDomain, postId));
                                            }
                                        } else {
                                            Log.e("Error", "User document does not exist");
                                        }
                                    } else {
                                        Exception e = userDocumentTask.getException();
                                        Log.e("Error", "Error getting user document: " + (e != null ? e.getMessage() : ""));
                                    }
                                    return null;
                                });

                        tasks.add(userTask);
                    }

                    // When all user detail tasks are complete, update the UI
                    Tasks.whenAllComplete(tasks)
                            .addOnCompleteListener(task -> {
                                binding.progressbar.setVisibility(View.GONE);
                                if (eventsModelArrayList.isEmpty()) {
                                    binding.eventsRV.setVisibility(View.GONE);
                                    binding.noDataText.setVisibility(View.VISIBLE);
                                } else {
                                    binding.eventsRV.setVisibility(View.VISIBLE);
                                    binding.noDataText.setVisibility(View.GONE);
                                    eventsAdapter = new EventsAdapter(requireActivity(), eventsModelArrayList);
                                    binding.eventsRV.setAdapter(eventsAdapter);
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    binding.progressbar.setVisibility(View.GONE);
                    // Handle failure
                });
    }


    private void getLikeCount(String postId) {
        // Build the reference to the likes collection for the specified post ID
        eventLikeRef = db.collection("like").document(postId);
        eventLikeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    totalLikes = documentSnapshot.getData().size();
                    // Check if the post has more than 10 likes
//                    if (totalLikes > 0) {
//                        eventsModelArrayList.add(new EventsModel(userId, userName, userImage, imageUrl, description, userDomain, postId));
//                    }
                } else {
                    // Document doesn't exist, meaning no likes for this post yet
                    // Handle accordingly, e.g., set default like button state
                }
            } else {
                // Error occurred, handle it accordingly
            }
        });
    }


}