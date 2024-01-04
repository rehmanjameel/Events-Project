package org.codebase.events.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.codebase.events.R;
import org.codebase.events.databinding.ActivityLoginBinding;
import org.codebase.events.utils.App;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (App.isLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // control the device back button click
        controlBackPress();

        // create Firebase FireStore instance
        db = FirebaseFirestore.getInstance();

        binding.signupId.setOnClickListener(view -> {
            startActivity(new Intent(this, RegistrationActivity.class));
        });

        binding.loginUser.setOnClickListener(view -> {
            checkValidation();
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

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData() + document.get("email"));
                                if (userName.equals(document.get("email")) && password.equals(document.get("password"))) {
                                    binding.progressbar.setVisibility(View.GONE);
                                    App.saveLogin(true);
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        } else {
                            binding.progressbar.setVisibility(View.GONE);
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
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