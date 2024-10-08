package pk.cust.events.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import pk.cust.events.R;
import pk.cust.events.databinding.ActivityRegistrationBinding;
import pk.cust.events.services.EmailSenderService;
import pk.cust.events.utils.App;
import pk.cust.events.utils.RealPathUtil;
import pk.cust.events.utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding binding;

    List<String> genderItems = new ArrayList<>();

    private String imageRealPath = "";
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // control the device back button click
        controlBackPress();

        // add Genders in array list to show dropdown
        genderItems.add("Male");
        genderItems.add("Female");

        ArrayAdapter<String> gendersAdapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, genderItems);

        binding.genderDropDown.setAdapter(gendersAdapter);

        binding.nextButton.setOnClickListener(view -> {
            checkValidation();
        });

        binding.backArrow.setOnClickListener(view -> {
            backPress();
        });

        binding.loginText.setOnClickListener(view -> {
            backPress();

        });

        binding.selectImage.setOnClickListener(view -> {
            pickImage();
        });

        binding.sendCodeButton.setOnClickListener(v -> {
            sendEmailOtp();
        });

        binding.verifyCodeButton.setOnClickListener(v -> {
            if (App.getString("email_otp").equals(binding.emailCodeTIET.getText().toString())) {
                binding.otpLayout.setVisibility(View.GONE);
                binding.nextButton.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Email verified!", Toast.LENGTH_SHORT).show();
            } else if (binding.emailCodeTIET.getText().toString().isEmpty()) {
                binding.emailCodeTIET.setError("Field required...");
            } else {
                binding.emailCodeTIET.setError("Otp not matched!");
            }
        });
    }

    private void sendEmailOtp() {
        String email = Objects.requireNonNull(binding.emailTIET.getText()).toString();
        if (email.isEmpty()) {
            binding.emailTIET.setError("Field required");
        }
        else if (!Validator.isValidMail(email)) {
            binding.emailTIET.setError("Field required or Invalid email");

        } else {
            new EmailSenderService(this, email).sendEmail();
            startTimer();
        }
    }

    private void startTimer() {
        binding.sendCodeButton.setVisibility(View.GONE);
        binding.countTimerText.setVisibility(View.VISIBLE);
        binding.verifyCodeButton.setVisibility(View.VISIBLE);
        binding.emailCodeTIL.setVisibility(View.VISIBLE);
        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update TextView with remaining time
                binding.countTimerText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // Timer finished, show the button again
                binding.sendCodeButton.setVisibility(View.VISIBLE);
                binding.verifyCodeButton.setVisibility(View.GONE);
                binding.emailCodeTIL.setVisibility(View.GONE);
                binding.sendCodeButton.setText("Resend");
                // Optionally, reset the TextView to initial state
                App.saveString("email_otp", "");
                binding.countTimerText.setText("60");
            }
        };

        countDownTimer.start();
    }

    private void checkValidation() {

        String name = Objects.requireNonNull(binding.userNameTIET.getText()).toString();
        String gender = Objects.requireNonNull(binding.genderDropDown.getText()).toString();
        String phoneNo = Objects.requireNonNull(binding.phoneNoTIET.getText()).toString();
        String email = Objects.requireNonNull(binding.emailTIET.getText()).toString();


        if (name.isEmpty()
                && gender.equals("Select") && phoneNo.isEmpty() && email.isEmpty()) {
            binding.userNameTIET.setError("Field required");
            binding.phoneNoTIET.setError("Field required");
            binding.genderDropDown.setError("Field required");
            binding.emailTIET.setError("Field required");

        } else if (name.isEmpty()) {
            binding.userNameTIET.setError("Field required");

        } else if (gender.isEmpty() || gender.equals("Select")) {
            binding.genderDropDown.setError("Field required");

        } else if (phoneNo.isEmpty() || !Validator.isValidPakistanMobileNumber(phoneNo)) {
            binding.phoneNoTIET.setError("Field required or Invalid Phone no.");

        } else if (email.isEmpty()) {
            binding.emailTIET.setError("Field required");

        } else if (imageUri == null) {
            Toast.makeText(this, "Please select the image", Toast.LENGTH_LONG).show();
        } else {
            Log.e("is it in the ", "else part1 of validation");
            Intent intent = new Intent(this, RegisterP2Activity.class);
            intent.putExtra("user_name", name);
            intent.putExtra("gender", gender);
            intent.putExtra("phone_no", phoneNo);
            intent.putExtra("email", email);
            intent.putExtra("user_image", imageUri.toString());
            startActivity(intent);

//            binding.progressbar.setVisibility(View.GONE);
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.e("result", "" + result.getResultCode());
                    // There are no request codes
                    Intent intent = result.getData();
                    if (intent != null) {
                        imageUri = intent.getData();
                        imageRealPath = RealPathUtil.getRealPath(this, imageUri);

                        Log.e("realPath image", imageRealPath);
                        Log.e("image uri", imageUri.toString());
                        Glide.with(this)
                                .load(imageUri)
                                .centerCrop()
                                .error(R.drawable.baseline_account_circle_24)
                                .placeholder(R.drawable.baseline_account_circle_24)
                                .into(binding.selectImage);
                    }
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show();
                }
            });

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.wtf("here", "version");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                        == PackageManager.PERMISSION_DENIED) {
                    requestPermissions();
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    activityResultLauncher.launch(intent);
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    requestPermissions();
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    activityResultLauncher.launch(intent);
                }
            }
        } else {
            Log.wtf("Here", "Pick image");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncher.launch(intent);
        }
    }

    private ActivityResultLauncher<String[]> requestMultiplePermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                    Log.d("DEBUG", entry.getKey() + " = " + entry.getValue());
                }
                // Check if all required permissions are granted
                boolean allPermissionsGranted = true;
                for (Boolean granted : permissions.values()) {
                    if (!granted) {
                        allPermissionsGranted = false;
                        showDialogOK("Gallery Permission required for this app", (dialog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    requestPermissions();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    // proceed with logic by disabling the related features or quit the app.
                                    break;
                            }
                        });
                        break;
                    }
                }

                // If all permissions are granted, open the gallery
                if (allPermissionsGranted) {
                    pickImage();
                }
            }
    );

    private void requestPermissions() {
        // Launch the permission request
        String[] permissionsToRequest;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            permissionsToRequest = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }

        requestMultiplePermissions.launch(permissionsToRequest);
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
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
        Intent intent =  new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}