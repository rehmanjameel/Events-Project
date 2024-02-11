package pk.cust.events.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import pk.cust.events.databinding.ActivityMainBinding;

import android.os.Bundle;

import pk.cust.events.R;

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