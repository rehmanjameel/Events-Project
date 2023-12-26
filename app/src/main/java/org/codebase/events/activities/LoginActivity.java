package org.codebase.events.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.codebase.events.R;
import org.codebase.events.databinding.ActivityLoginBinding;
import org.codebase.events.utils.App;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // control the device back button click
        controlBackPress();

        binding.signupId.setOnClickListener(view -> {
            startActivity(new Intent(this, RegistrationActivity.class));
        });

        binding.loginUser.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }

    private void checkValidation() {

        String userName = Objects.requireNonNull(binding.userNameTIET.getText()).toString();
        String password = Objects.requireNonNull(binding.passwordTIET.getText()).toString();

        if (userName.isEmpty() && password.isEmpty()
        ) {
            binding.userNameTIET.setError("Field required");
            binding.passwordTIET.setError("Field required");

        } else if (userName.isEmpty()) {
            binding.userNameTIET.setError("Field required");

        } else if (password.isEmpty()) {
            binding.passwordTIET.setError("Field required or password is weak");

        } else {
            Log.e("is it in the ", "else part of validation");
            if (App.isNetworkAvailable()) {
                binding.progressbar.setVisibility(View.VISIBLE);
                loginUser(userName, password);
            } else {
                displayToast("Check internet connection!");
                binding.progressbar.setVisibility(View.GONE);
            }

        }
    }

    private void loginUser(String userName, String password) {
    }

    private void displayToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void controlBackPress() {
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPress();
            }
        });
    }

    private void backPress() {
        finish();
    }
}