package pk.cust.events.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import pk.cust.events.R;
import pk.cust.events.databinding.ActivityProfileBinding;
import pk.cust.events.utils.App;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Glide.with(this)
                .load(App.getString("user_image"))
                .centerCrop()
                .error(R.drawable.baseline_account_circle_24)
                .placeholder(R.drawable.baseline_account_circle_24)
                .into(binding.profilePicture);

        Log.e("user name of profile", App.getString("uer_name"));
        binding.profileName.setText(App.getString("uer_name"));
        binding.domainName.setText(App.getString("domain"));
        binding.interestName.setText(App.getString("interest"));
        binding.personEmail.setText(App.getString("email"));
        binding.personMobile.setText(App.getString("phone_no"));
        binding.personDob.setText(App.getString("dob"));
        binding.personGender.setText(App.getString("gender"));
        binding.personAddress.setText(App.getString("address"));

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
    }
}