package pk.cust.events.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import pk.cust.events.databinding.ActivityMainBinding;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import pk.cust.events.R;
import pk.cust.events.utils.App;

public class MainActivity extends AppCompatActivity {

   ActivityMainBinding binding;
    private NavController navController; // Declare the NavController

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Find the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController(); // Initialize the NavController
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int destinationId = 0;

            if (item.getItemId() == R.id.navigation_home) {
                destinationId = R.id.homeFragment;
            } else if (item.getItemId() == R.id.eventMenu) {
                destinationId = R.id.eventsFragment;
            } else if (item.getItemId() == R.id.chatRoom) {
                destinationId = R.id.chatRoomFragment;
            } else if (item.getItemId() == R.id.diversity) {
                destinationId = R.id.friendsFragment;
            } else if (item.getItemId() == R.id.profileMenu) {
                destinationId = R.id.notificationsFragment;
            }

            if (destinationId != 0) {
                navController.navigate(destinationId);
            }

            return true;
        });

        // get fcm token
        getFCMToken();

    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    Log.e("tokeen", token);
//                    Toast.makeText(MainActivity.this, "token: " + token, Toast.LENGTH_SHORT).show();

                    saveTokenToFireStore(App.getString("document_id"), token);

                }
            }
        });
    }

    // Assume 'userId' is the user's unique ID and 'token' is the FCM token
    private void saveTokenToFireStore(String userId, String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Update the token field for the user
        db.collection("users").document(userId)
                .update("token", token)
                .addOnSuccessListener(aVoid -> {
                    Log.d("TAG", "Token saved successfully for user: " + userId);
                })
                .addOnFailureListener(e -> {
                    Log.e("TAG", "Failed to save token for user: " + userId, e);
                });
    }

    @Override
    public void onBackPressed() {

        // Handle back navigation using NavController
        if (navController.getCurrentDestination() != null) {
            int currentDestinationId = navController.getCurrentDestination().getId();

            if (currentDestinationId == R.id.homeFragment) {
                // If on HomeFragment, exit the app
                super.onBackPressed();
                finish();
            } else {
                // Pop the back stack
                if (currentDestinationId != R.id.homeFragment) {
                    // If there's no more fragment to pop, navigate to HomeFragment
                    binding.bottomNavigation.setSelectedItemId(R.id.navigation_home);
                }
            }
        }

    }
}