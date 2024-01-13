package org.codebase.events.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;

import org.codebase.events.R;
import org.codebase.events.databinding.ActivityProfileBinding;
import org.codebase.events.utils.App;

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