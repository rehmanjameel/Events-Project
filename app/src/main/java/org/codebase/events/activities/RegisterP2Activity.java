package org.codebase.events.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.codebase.events.databinding.ActivityRegisterP2Binding;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.codebase.events.utils.Validator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RegisterP2Activity extends AppCompatActivity {

    ActivityRegisterP2Binding binding;

    List<String> domainsItems = new ArrayList<>();
    List<String> interestsTopicsList = new ArrayList<>();
    String userName, gender, phoneNo, address, userImage;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    // instance for firebase storage and StorageReference
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterP2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // control the device back button click
        controlBackPress();

        setUpDomains();

        setUpInterest();

        binding.signupId.setOnClickListener(view -> {
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
            checkValidation();
        });

        if (getIntent() != null) {
            userName = getIntent().getStringExtra("user_name");
            gender = getIntent().getStringExtra("gender");
            phoneNo = getIntent().getStringExtra("phone_no");
            address = getIntent().getStringExtra("address");
            userImage = getIntent().getStringExtra("user_image");
        }

        // create Firebase FireStore instance
        db = FirebaseFirestore.getInstance();

        binding.dobTIL.setEndIconOnClickListener(view -> {
            clickDatePicker();
        });

        binding.backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegistrationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void setUpDomains() {
        // add domains in array list to show dropdown
        domainsItems.add("Science and Technology");
        domainsItems.add("Medicine and Healthcare");
        domainsItems.add("Business and Entrepreneurship");
        domainsItems.add("Education");
        domainsItems.add("Arts and Entertainment");
        domainsItems.add("Social Sciences");
        domainsItems.add("Communication and Media");
        domainsItems.add("Environmental and Sustainability");
        domainsItems.add("Sports and Recreation");
        domainsItems.add("Law and Justice");

        ArrayAdapter<String> domainAdapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, domainsItems);

        binding.domainsDropDown.setAdapter(domainAdapter);
    }

    private void setUpInterest() {
        // add interest in array list to show dropdown
        interestsTopicsList.add("Technology");
        interestsTopicsList.add("Entertainment");
        interestsTopicsList.add("Books and Literature");
        interestsTopicsList.add("Sports and Fitness");
        interestsTopicsList.add("Travel");
        interestsTopicsList.add("Food and Cooking");
        interestsTopicsList.add("Fashion and Beauty");
        interestsTopicsList.add("Art and Creativity");
        interestsTopicsList.add("Science and Nature");
        interestsTopicsList.add("Health and Wellness");
        interestsTopicsList.add("Business and Finance");
        interestsTopicsList.add("Hobbies and Crafts");
        interestsTopicsList.add("Politics and Current Affairs");
        interestsTopicsList.add("Education");
        interestsTopicsList.add("Parenting");
        interestsTopicsList.add("Social Causes");
        interestsTopicsList.add("Pets");
        interestsTopicsList.add("History and Heritage");
        interestsTopicsList.add("Family & Relationships");
        interestsTopicsList.add("Mindfulness and Spirituality");

        ArrayAdapter<String> gendersAdapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, interestsTopicsList);

        binding.interestDropDown.setAdapter(gendersAdapter);
    }

    private void checkValidation() {

        String domainsDropDown = Objects.requireNonNull(binding.domainsDropDown.getText()).toString();
        String interestDropDown = Objects.requireNonNull(binding.interestDropDown.getText()).toString();
        String email = Objects.requireNonNull(binding.emailTIET.getText()).toString();
        String dob = Objects.requireNonNull(binding.dobTIET.getText()).toString();
        String password = Objects.requireNonNull(binding.passwordTIET.getText()).toString();


        if (domainsDropDown.equals("Select") && interestDropDown.equals("Select") && email.isEmpty() && password.isEmpty()
                && dob.isEmpty()) {
            binding.domainsDropDown.setError("Select Domain");
            binding.interestDropDown.setError("Select Interest");
            binding.dobTIET.setError("Field required");
            binding.emailTIET.setError("Field required");
            binding.passwordTIET.setError("Field required");

        } else if (interestDropDown.isEmpty() || interestDropDown.equals("Select")) {
            binding.interestDropDown.setError("Field required");

        } else if (domainsDropDown.isEmpty() || domainsDropDown.equals("Select")) {
            binding.domainsDropDown.setError("Field required");

        } else if (dob.isEmpty()) {
            binding.dobTIET.setError("Field required");

        } else if (email.isEmpty() || !Validator.isValidMail(email)) {
            binding.emailTIET.setError("Field required or Invalid email");

        } else if (password.isEmpty() || !Validator.isValidPasswordFormat(password)) {
            binding.passwordTIET.setError("Field required or password is weak");

        } else {
            Log.e("is it in the ", "else part2 of validation");
            binding.progressbar.setVisibility(View.VISIBLE);
            saveEmailPassword(email, password, interestDropDown, domainsDropDown, dob);
        }
    }

    private void saveEmailPassword(String email, String password, String interestDropDown, String domainsDropDown, String dob) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                uploadImage(email, interestDropDown, domainsDropDown, dob);
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            binding.progressbar.setVisibility(View.GONE);

                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterP2Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void uploadImage(String email, String interestDropDown, String domainsDropDown, String dob) {
        // Defining the child of storageReference
        if (userImage != null) {
            StorageReference ref
                    = storageReference.child("images/" + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(Uri.parse(userImage))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getMetadata().getPath();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    registerUser(interestDropDown, domainsDropDown, dob, email, uri.toString());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                            // Image uploaded successfully
                            // Dismiss dialog
//                                progressDialog.dismiss();
                            Toast.makeText(RegisterP2Activity.this,
                                    "Image Uploaded!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
//                        progressDialog.dismiss();
                            Toast.makeText(RegisterP2Activity.this,
                                    "Failed " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
//                .addOnProgressListener(
//                        new OnProgressListener<UploadTask.TaskSnapshot>() {
//
//                            // Progress Listener for loading
//                            // percentage on the dialog box
//                            @Override
//                            public void onProgress(
//                                    UploadTask.TaskSnapshot taskSnapshot)
//                            {
//                                double progress
//                                        = (100.0
//                                        * taskSnapshot.getBytesTransferred()
//                                        / taskSnapshot.getTotalByteCount());
//                                progressDialog.setMessage(
//                                        "Uploaded "
//                                                + (int)progress + "%");
//                            }
//                        });

        } else {
            Toast.makeText(this, "Something went wrong on uploading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(String interestDropDown, String domainsDropDown, String dob, String email, String imageUri) {

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("user_name", userName);
        user.put("gender", gender);
        user.put("phone_no", phoneNo);
        user.put("address", address);
        user.put("user_image", imageUri);
        user.put("dob", dob);
        user.put("domain", domainsDropDown);
        user.put("interest", interestDropDown);
        user.put("email", email);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                        binding.progressbar.setVisibility(View.GONE);
                        Intent intent = new Intent(RegisterP2Activity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressbar.setVisibility(View.GONE);
                        Log.w("TAG", "Error adding document", e);
                    }
                });
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
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void clickDatePicker() {
        final Calendar myCalendar = Calendar.getInstance();
        int year = myCalendar.get(Calendar.YEAR);
        int month = myCalendar.get(Calendar.MONTH);
        int day = myCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    binding.dobTIET.setText(selectedDate);
                },
                year, month, day
        );

//        myCalendar.add(Calendar.YEAR, 18);
//        datePickerDialog.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
        datePickerDialog.show();
    }
}