package pk.cust.events.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import pk.cust.events.R;
import pk.cust.events.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initialize firebase auth
        auth = FirebaseAuth.getInstance();

        // send email button click
        binding.sendEmail.setOnClickListener(v -> {
            validateField();
        });

        // go back to login activity
        binding.backArrow.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void validateField() {
        String email = binding.emailTIET.getText().toString();

        if (!TextUtils.isEmpty(email)) {
            sendEmail(email);
            binding.progressbar.setVisibility(View.VISIBLE);
        } else {
            binding.emailTIET.setError("Email required");
        }
    }

    private void sendEmail(String email) {

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();

                            onBackPressed();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();

                        }

                        binding.progressbar.setVisibility(View.GONE);
                    }
                });
    }

}