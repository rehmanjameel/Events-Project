package pk.cust.events.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import pk.cust.events.homefragments.EventDetailFragment;
import pk.cust.events.model.EventsModel;
import pk.cust.events.utils.App;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    EventsAdapter eventsAdapter;
    ArrayList<EventsModel> eventsModelArrayList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private boolean expanded = false;

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



        binding.editButton.setOnClickListener(view -> {
            //            Log.e("is it in the ", "else part1 of validation");
            Intent intent = new Intent(this, Editprofile.class);
            startActivity(intent);
        });
        binding.backArrow.setOnClickListener(view -> {
            App.IS_PROFILE = false;
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        binding.iconDropDown.setOnClickListener(view -> {

            if (!expanded) {
                binding.minmaxLayout.setVisibility(View.VISIBLE);
                expanded = true;
                binding.iconDropDown.setImageResource(R.drawable.baseline_keyboard_double_arrow_up_24);
            } else {
                binding.minmaxLayout.setVisibility(View.GONE);
                expanded = false;
                binding.iconDropDown.setImageResource(R.drawable.baseline_keyboard_double_arrow_down_24);

            }
        });

        binding.logoutButton.setOnClickListener(view -> {

            logout();
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                eventsAdapter.setOnItemClickListener(new EventsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Bundle data) {
                        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

                        // Create a new instance of the fragment
                        EventDetailFragment fragment = new EventDetailFragment();
                        fragment.setArguments(data);

                        // Replace the current fragment with the new one
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null) // Optional: Add to back stack for navigation
                                .commit();

                        // Make the fragment_container visible
                    }
                });

            }
        }, 3000);

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

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the message show for the Alert time
        builder.setMessage("Do you want to logout?");

        // Set Alert Title
        builder.setTitle("Alert !");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        builder.setIcon(R.drawable.baseline_warning_amber_24);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will close
            App.logout();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this)
                .load(App.getString("user_image"))
                .centerCrop()
                .error(R.drawable.baseline_account_circle_24)
                .placeholder(R.drawable.baseline_account_circle_24)
                .into(binding.profilePicture);

        Log.e("user name of profile", App.getString("user_name"));
        Log.e("user image of profile", App.getString("user_image"));
        binding.profileName.setText(App.getString("user_name"));
        binding.domainName.setText(App.getString("domain"));
        binding.interestName.setText(App.getString("interest"));
        binding.personEmail.setText(App.getString("email"));
        binding.personMobile.setText(App.getString("phone_no"));
        binding.personDob.setText(App.getString("dob"));
        binding.personGender.setText(App.getString("gender"));
        binding.personAddress.setText(App.getString("address"));
    }



}