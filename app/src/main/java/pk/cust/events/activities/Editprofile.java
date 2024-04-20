package pk.cust.events.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import pk.cust.events.R;
import pk.cust.events.databinding.ActivityEditprofileBinding;
import pk.cust.events.utils.App;
import pk.cust.events.utils.RealPathUtil;
import pk.cust.events.utils.Validator;

public class Editprofile extends AppCompatActivity {

    ActivityEditprofileBinding binding;
    List<String> interestsTopicsList = new ArrayList<>();
    boolean[] selectedLanguage;
    private String imageRealPath = "";
    private Uri imageUri = null;
    ArrayList<Integer> langList = new ArrayList<>();


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    // instance for firebase storage and StorageReference
    private FirebaseStorage storage;
    private StorageReference storageReference;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditprofileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = storage.getReference();

        Glide.with(this)
                .load(App.getString("user_image"))
                .centerCrop()
                .error(R.drawable.baseline_account_circle_24)
                .placeholder(R.drawable.baseline_account_circle_24)
                .into(binding.selectImage);
//        imageUri = Uri.parse(App.getString("user_image"));

        binding.userNameTIET.setText(App.getString("user_name"));
        binding.interestDropDown.setText(App.getString("interest"));
        binding.phoneNoTIET.setText(App.getString("phone_no"));
        binding.currentAddressTIET.setText(App.getString("address"));

        // control the device back button click
        controlBackPress();

        setUpInterest();

        binding.updateButton.setOnClickListener(view -> {
            checkValidation();
        });

        binding.backArrow.setOnClickListener(view -> {
            backPress();
        });
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

        // initialize selected language array
        selectedLanguage = new boolean[interestsTopicsList.size()];

        binding.interestMenu.setEndIconOnClickListener(view -> {
            alertDialog();
        });

//        ArrayAdapter<String> gendersAdapter = new ArrayAdapter<>(this,
//                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, interestsTopicsList);

//        binding.interestDropDown.setAdapter(gendersAdapter);
//        binding.interestDropDown.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }

    private void alertDialog() {
        // Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(Editprofile.this);

        // set title
        builder.setTitle("Select Language");

        // set dialog non cancelable
        builder.setCancelable(false);

        builder.setMultiChoiceItems(
                interestsTopicsList.toArray(new CharSequence[interestsTopicsList.size()]),
                selectedLanguage,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position in lang list
                            langList.add(i);
                            // Sort array list
                            Collections.sort(langList);
                        } else {
                            // when checkbox unselected
                            // Remove position from langList
                            langList.remove(Integer.valueOf(i));
                        }
                    }
                });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Initialize string builder
                StringBuilder stringBuilder = new StringBuilder();
                // use for loop
                for (int j = 0; j < langList.size(); j++) {
                    // concat array value
                    stringBuilder.append(interestsTopicsList.get(langList.get(j)));
                    // check condition
                    if (j != langList.size() - 1) {
                        // When j value  not equal
                        // to lang list size - 1
                        // add comma
                        stringBuilder.append(", ");
                    }
                }
                // set text on textView
                binding.interestDropDown.setText(stringBuilder.toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss dialog
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // use for loop
                for (int j = 0; j < selectedLanguage.length; j++) {
                    // remove all selection
                    selectedLanguage[j] = false;
                    // clear language list
                    langList.clear();
                    // clear text view value
                    binding.interestDropDown.setText("");
                }
            }
        });
        // show dialog
        builder.show();
    }

    private void checkValidation() {

        String name = Objects.requireNonNull(binding.userNameTIET.getText()).toString();
        String interest = Objects.requireNonNull(binding.interestDropDown.getText()).toString();
        String phoneNo = Objects.requireNonNull(binding.phoneNoTIET.getText()).toString();
        String address = Objects.requireNonNull(binding.currentAddressTIET.getText()).toString();


        if (name.isEmpty()
                && interest.equals("Select") && phoneNo.isEmpty() && address.isEmpty()) {
            binding.userNameTIET.setError("Field required");
            binding.phoneNoTIET.setError("Field required");
            binding.interestMenu.setError("Field required");
            binding.currentAddressTIET.setError("Field required");

        } else if (name.isEmpty()) {
            binding.userNameTIET.setError("Field required");

        } else if (interest.isEmpty() || interest.equals("Select")) {
            binding.interestDropDown.setError("Field required");

        } else if (phoneNo.isEmpty() || !Validator.isValidPakistanMobileNumber(phoneNo)) {
            binding.phoneNoTIET.setError("Field required or Invalid Phone no.");

        } else if (address.isEmpty()) {
            binding.currentAddressTIET.setError("Field required");

        }
//        else if (imageUri == null) {
//            Toast.makeText(this, "Please select the image", Toast.LENGTH_LONG).show();
//        }
        else {

            Log.e("data for update", name + ",.," + phoneNo);
            uploadImage(name, interest, phoneNo, address);

            binding.progressbar.setVisibility(View.VISIBLE);
        }
    }

    private void uploadImage(String name, String interestDropDown, String phoneNo, String address) {
        // Defining the child of storageReference
        if (imageUri != null) {
            StorageReference ref
                    = storageReference.child("images/" + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getMetadata().getPath();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    updateUser(interestDropDown, name, phoneNo, address, uri.toString());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                            // Image uploaded successfully
                            // Dismiss dialog
//                                progressDialog.dismiss();
                            Toast.makeText(Editprofile.this,
                                    "Image Uploaded!!",
                                    Toast.LENGTH_SHORT).show();

                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
//                        progressDialog.dismiss();
                            Toast.makeText(Editprofile.this,
                                    "Failed " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            binding.progressbar.setVisibility(View.GONE);

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
            updateUser(interestDropDown, name, phoneNo, address, "");

//            Toast.makeText(this, "Something went wrong on uploading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserData(String userId, Map<String, Object> newData) {

        Log.e("user id in edit", userId);
        db.collection("users").document(userId)
                .set(newData)
                .addOnSuccessListener(aVoid -> {
                    // Data updated successfully
                    Log.d("TAG", "User data updated successfully.");
                    binding.progressbar.setVisibility(View.GONE);
                    getDataFromFireStore();
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Editprofile.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    // Error occurred while updating data
                    Log.e("TAG", "Error updating user data: " + e.getMessage());
                    Toast.makeText(this, "Profile not updated!", Toast.LENGTH_SHORT).show();
                    binding.progressbar.setVisibility(View.GONE);

                });
    }

    // Method to update user data with provided values
    private void updateUser(String interestDropDown, String name, String phoneNo, String address, String imageUri) {
        // Create a map to store the updated values
        Map<String, Object> newData = new HashMap<>();
        newData.put("user_name", name);
        newData.put("phone_no", phoneNo);
        newData.put("address", address);
        if (imageUri != null) {
            newData.put("user_image", imageUri);
        } else {
            newData.put("user_image", App.getString("user_image"));
        }
        newData.put("interest", interestDropDown);
        newData.put("gender", App.getString("gender"));
        newData.put("dob", App.getString("dob"));
        newData.put("domain", App.getString("domain"));
        newData.put("email", App.getString("email"));

        // Update user data in Firestore
        updateUserData(App.getString("document_id"), newData);
    }

    private void getDataFromFireStore() {
        db.collection("users")
                .whereEqualTo("email", App.getString("email"))
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
                            }
                        } else {
                            binding.progressbar.setVisibility(View.GONE);
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
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
        Intent intent =  new Intent(this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}