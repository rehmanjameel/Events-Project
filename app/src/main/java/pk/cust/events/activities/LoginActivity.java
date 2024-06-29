package pk.cust.events.activities;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import pk.cust.events.databinding.ActivityLoginBinding;
import pk.cust.events.utils.App;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

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

        // Initialize firebase authentication
        mAuth = FirebaseAuth.getInstance();

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

        binding.forgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
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

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                getDataFromFireStore(currentUser.getEmail());
                            }
                        } else {
                            binding.progressbar.setVisibility(View.GONE);

                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getDataFromFireStore(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("TAG", document.getId() + " => " + document.getData() + document.get("email"));

                                App.saveString("document_id", document.getId());
                                Log.e("email", document.get("user_name") + ",.," + document.get("email"));
                                App.saveString("user_name", String.valueOf(document.get("user_name")));
                                App.saveString("gender", String.valueOf(document.get("gender")));
                                App.saveString("phone_no", String.valueOf(document.get("phone_no")));
                                App.saveString("address", String.valueOf(document.get("address")));
                                App.saveString("user_image", String.valueOf(document.get("user_image")));
                                App.saveString("dob", String.valueOf(document.get("dob")));
                                App.saveString("domain", String.valueOf(document.get("domain")));
                                App.saveString("interest", String.valueOf(document.get("interest")));
                                App.saveString("email", String.valueOf(document.get("email")));

                                binding.progressbar.setVisibility(View.GONE);
                                App.saveLogin(true);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

//                                if (userName.equals(document.get("email")) && password.equals(document.get("password"))) {
//                                }
                            }
                        } else {
                            binding.progressbar.setVisibility(View.GONE);
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

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