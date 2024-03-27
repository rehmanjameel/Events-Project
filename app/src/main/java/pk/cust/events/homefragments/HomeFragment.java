package pk.cust.events.homefragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import pk.cust.events.R;
import pk.cust.events.activities.CreateEventActivity;
import pk.cust.events.activities.ProfileActivity;
import pk.cust.events.adapter.HomeEventsAdapter;
import pk.cust.events.databinding.FragmentHomeBinding;
import pk.cust.events.model.HomeEventsModel;
import pk.cust.events.utils.App;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    HomeEventsAdapter eventsAdapter;
    ArrayList<HomeEventsModel> eventsModelArrayList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        RequestOptions requestOptions = new RequestOptions()
                .timeout(60000); // Set the timeout to 60 seconds (adjust as needed)
        Glide.with(this)
                .load(Uri.parse(App.getString("user_image")))
                .centerCrop()
                .error(R.drawable.baseline_account_circle_24)
                .placeholder(R.drawable.baseline_account_circle_24)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Handle load failure, show placeholder or error message
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Image loaded successfully
                        return false;
                    }
                })
                .into(binding.profilePicture);

        binding.profilePicture.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        binding.eventsRV.setLayoutManager(new LinearLayoutManager(requireActivity()));

        binding.createEventButton.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), CreateEventActivity.class);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    private void getDataFromFireStore() {
        eventsModelArrayList.clear();
        // Example code to retrieve posts with user details
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Task<Object>> tasks = new ArrayList<>(); // List to store user detail tasks

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userId =  document.getString("userId");
                        String description = document.getString("description");
                        String imageUrl = document.getString("imageUrl");

                        Log.e("userid from posts", userId + ",.,." + description + ",.,." + imageUrl);

                        // Retrieve additional user details from the 'users' collection
                        Task<Object> userTask = db.collection("users")
                                .document(userId)
                                .get()
                                .onSuccessTask(userDocument -> {
                                    String userName = userDocument.getString("user_name");
                                    String userImage = userDocument.getString("user_image");
                                    String userDomain = userDocument.getString("domain");
                                    Log.e("user name from posts", userName + ",.,." + userImage);


                                    if (userDomain.equals(App.getString("domain"))) {
                                        HomeEventsModel homeEventsModel = new HomeEventsModel();
                                        homeEventsModel.setUserId(userId);
                                        homeEventsModel.setPostId(document.getId());
                                        homeEventsModel.setUserName(userName);
                                        homeEventsModel.setUserImage(userImage);
                                        homeEventsModel.setDescription(description);
                                        homeEventsModel.setUserImage(imageUrl);
                                        homeEventsModel.setLikesCount(0);
                                        eventsModelArrayList.add(homeEventsModel);
                                    }
                                    Log.e("user name from posts123", userName + ",.,." + userImage);

                                    return Tasks.forResult(null);
                                })
                                .addOnFailureListener(e -> {
                                    binding.progressbar.setVisibility(View.GONE);
                                    // Handle failure
                                });

                        tasks.add(userTask);
                    }

                    // When all user detail tasks are complete, update the UI
                    Tasks.whenAllComplete(tasks)
                            .addOnCompleteListener(task -> {
                                binding.progressbar.setVisibility(View.GONE);

                                Log.e("events list", String.valueOf(eventsModelArrayList.size()));
                                eventsAdapter = new HomeEventsAdapter(requireActivity(), eventsModelArrayList);
                                binding.eventsRV.setAdapter(eventsAdapter);
                            });
                })
                .addOnFailureListener(e -> {
                    binding.progressbar.setVisibility(View.GONE);
                    // Handle failure
                });
    }

    // Method to get the likes count for a post
    private Task<Integer> fetchLikesCount(String postId, String userId) {
        DocumentReference postRef = db.collection("posts").document(postId).collection("likes").document(userId);

        return postRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    long likesCount = documentSnapshot.getLong("dummyField"); // Field name doesn't matter, just need to get a non-null document
                    return (int) likesCount; // Return likes count
                } else {
                    return 0; // Return 0 if "likes" subcollection doesn't exist
                }
            } else {
                return 0; // Return 0 on failure
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.progressbar.setVisibility(View.VISIBLE);
        getDataFromFireStore();
    }
}