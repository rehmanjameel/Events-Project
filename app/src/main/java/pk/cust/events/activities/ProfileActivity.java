package pk.cust.events.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import pk.cust.events.R;
import pk.cust.events.adapter.EventsAdapter;
import pk.cust.events.databinding.ActivityProfileBinding;
import pk.cust.events.model.EventsModel;
import pk.cust.events.utils.App;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    EventsAdapter eventsAdapter;
    ArrayList<EventsModel> eventsModelArrayList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.personalEventsRV.setLayoutManager(layoutManager);

        Glide.with(this)
                .load(App.getString("user_image"))
                .centerCrop()
                .error(R.drawable.baseline_account_circle_24)
                .placeholder(R.drawable.baseline_account_circle_24)
                .into(binding.profilePicture);

        Log.e("user name of profile", App.getString("user_name"));
        binding.profileName.setText(App.getString("user_name"));
        binding.domainName.setText(App.getString("domain"));
        binding.interestName.setText(App.getString("interest"));
        binding.personEmail.setText(App.getString("email"));
        binding.personMobile.setText(App.getString("phone_no"));
        binding.personDob.setText(App.getString("dob"));
        binding.personGender.setText(App.getString("gender"));
        binding.personAddress.setText(App.getString("address"));

//        ImageView backArrow = findViewById(R.id.backArrow);
//        backArrow.setOnClickListener(view -> {
//            onBackPressed();
//        });
        binding.backArrow.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.logoutButton.setOnClickListener(view -> {
            App.logout();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        getDataFromFireStore();
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

                        if (userId.equals(App.getString("document_id"))) {
                            eventsModelArrayList.add(new EventsModel(userId, "", "", imageUrl, description, "", document.getId()));

                        }
                        // Retrieve additional user details from the 'users' collection
//                        Task<Object> userTask = db.collection("users")
//                                .document(userId)
//                                .get()
//                                .onSuccessTask(userDocument -> {
//                                    String userName = userDocument.getString("user_name");
//                                    String userImage = userDocument.getString("user_image");
//                                    String userDomain = userDocument.getString("domain");
//                                    Log.e("user name from posts", userName + ",.,." + userImage);
//
//                                    eventsModelArrayList.add(new EventsModel(userId, userName, userImage, imageUrl, description, userDomain, document.getId()));
//
//                                    Log.e("user name from posts123", userName + ",.,." + userImage);
//
//                                    return Tasks.forResult(null);
//                                })
//                                .addOnFailureListener(e -> {
//                                    binding.progressbar.setVisibility(View.GONE);
//                                    // Handle failure
//                                });
//
//                        tasks.add(userTask);
                    }

                    // When all user detail tasks are complete, update the UI
                    Tasks.whenAllComplete(tasks)
                            .addOnCompleteListener(task -> {
                                binding.progressbar.setVisibility(View.GONE);

                                if (eventsModelArrayList.isEmpty()) {
                                    binding.personalEventsRV.setVisibility(View.GONE);
                                    binding.noDataText.setVisibility(View.VISIBLE);
                                    binding.progressbar.setVisibility(View.GONE);

                                } else {
                                    binding.personalEventsRV.setVisibility(View.VISIBLE);
                                    binding.noDataText.setVisibility(View.GONE);
                                    Log.e("events list", String.valueOf(eventsModelArrayList.size()));
                                    eventsAdapter = new EventsAdapter(this, eventsModelArrayList);
                                    binding.personalEventsRV.setAdapter(eventsAdapter);
                                    binding.progressbar.setVisibility(View.GONE);

                                }


                            });
                })
                .addOnFailureListener(e -> {
                    binding.progressbar.setVisibility(View.GONE);
                    // Handle failure
                });
    }
}