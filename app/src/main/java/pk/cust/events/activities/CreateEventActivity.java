package pk.cust.events.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pk.cust.events.R;
import pk.cust.events.databinding.ActivityCreateEventBinding;
import pk.cust.events.homefragments.EventDetailFragment;
import pk.cust.events.model.HomeEventsModel;
import pk.cust.events.utils.App;
import pk.cust.events.utils.RealPathUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {

    ActivityCreateEventBinding binding;

    private String imageRealPath = "";
    private Uri imageUri = null;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private List<String> tokens = new ArrayList<>();
    private App app = new App();
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.selectImage.setOnClickListener(view -> pickImage());

        // Example code to add a post to Firestore
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        binding.post.setOnClickListener(view -> checkValidation());

        calendar = Calendar.getInstance();

        binding.endTimeTIL.setEndIconOnClickListener(v -> {
            showTimePicker();
        });

        binding.endDateTIL.setEndIconOnClickListener(v -> {
            showDatePicker();
        });

    }

    private void checkValidation() {
        String description = binding.descriptionTIET.getText().toString();
        String endDate = binding.endDateTIET.getText().toString();
        String endTime = binding.endTimeTIET.getText().toString();

        if (description.isEmpty()) {
            binding.descriptionTIET.setError("Field required");
        }
//        else if (endDate.isEmpty()) {
//            binding.endDateTIET.setError("Field required");
//
//        } else if (endTime.isEmpty()) {
//            binding.endTimeTIET.setError("Field required");
//        }
        else if (imageUri == null) {
            Toast.makeText(this, "Please select the image", Toast.LENGTH_LONG).show();
        } else {
            binding.progressbar.setVisibility(View.VISIBLE);
            uploadImage(description);
        }
    }

    private void uploadImage(String description) {
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
                                    postEvent(description, uri.toString());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                            // Image uploaded successfully
                            // Dismiss dialog
//                                progressDialog.dismiss();
                            Toast.makeText(CreateEventActivity.this,
                                    "Image Uploaded!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.progressbar.setVisibility(View.GONE);
                            // Error, Image not uploaded
//                        progressDialog.dismiss();
                            Toast.makeText(CreateEventActivity.this,
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

    private void postEvent(String description, String postImage) {
        if (currentUser != null) {
            String userId = App.getString("document_id");

            Map<String, Object> post = new HashMap<>();
            post.put("userId", userId);
            post.put("description", description);
            post.put("imageUrl", postImage);
            post.put("start_date_time", System.currentTimeMillis());
            if (!binding.endDateTIET.getText().toString().isEmpty() ||
                    !binding.endTimeTIET.getText().toString().isEmpty()) {
                post.put("end_date_time", String.valueOf(app.convertDateTimeToMilliseconds(binding.endDateTIET.getText().toString(),
                        binding.endTimeTIET.getText().toString())));
            } else {
                post.put("end_date_time", "");
            }
            // Add other fields as needed

            db.collection("posts")
                    .add(post)
                    .addOnSuccessListener(documentReference -> {
                        // Get the newly added post's details
                        documentReference.get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // Retrieve the post details
                                        Map<String, Object> postDetails = documentSnapshot.getData();

                                        // Retrieve the post details
                                        String userIdRetrieved = documentSnapshot.getString("userId");
                                        String descriptionRetrieved = documentSnapshot.getString("description");
                                        String imageUrlRetrieved = documentSnapshot.getString("imageUrl");
                                        long startDateTime = documentSnapshot.getLong("start_date_time");
                                        String endDateTime = documentSnapshot.getString("end_date_time");

                                        // Log or use the post details as needed
                                        Log.d("PostDetails", "Post: " + postDetails);
                                        Log.d("ebd time date", "Post: " + endDateTime);

                                        db.collection("users")
                                                .document(userId)
                                                .get()
                                                .onSuccessTask(userDocument -> {
                                                    String userName = userDocument.getString("user_name");
                                                    String userImage = userDocument.getString("user_image");
                                                    String userDomain = userDocument.getString("domain");

                                                    Log.e("post image", imageUrlRetrieved);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("user_name", userName);
                                                    bundle.putString("user_image", userImage);
                                                    bundle.putString("user_id", userIdRetrieved);
                                                    bundle.putString("post_image", imageUrlRetrieved);
                                                    bundle.putString("post_id", documentReference.getId());
                                                    bundle.putString("post_description", descriptionRetrieved);
                                                    bundle.putString("post_domain", userDomain);
                                                    bundle.putLong("start_date_time", startDateTime);
                                                    bundle.putString("end_date_time", endDateTime);

                                                    App.IS_CHAT_FROM_HOME = true;

                                                    EventDetailFragment detailFragment = new EventDetailFragment();
                                                    detailFragment.setArguments(bundle);
                                                    getSupportFragmentManager().beginTransaction()
                                                            .add(android.R.id.content, detailFragment).commit();

                                                    return Tasks.forResult(null);
                                                })
                                                .addOnFailureListener(e -> {
                                                    binding.progressbar.setVisibility(View.GONE);
                                                    // Handle failure
                                                });
                                        if (!endDateTime.isEmpty()) {
                                            getTokensFromFirestore(App.getString("user_name"), description + ". This event will end on " +
                                                    App.convertMillisToDateTime(Long.parseLong(endDateTime)));
                                        } else
                                            getTokensFromFirestore(App.getString("user_name"), description + ".\n This event will stay permanently...");

                                        Toast.makeText(this, "Post uploaded successfully!", Toast.LENGTH_LONG).show();
                                        // onBackPressed();
                                    } else {
                                        Log.d("PostDetails", "No such document");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("PostDetails", "Failed to retrieve post details: ", e);
                                });
                        // Post added successfully
                    })
                    .addOnFailureListener(e -> {
                        binding.progressbar.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Failed to post" + e, Toast.LENGTH_LONG).show();
                        // Handle failure
                    });
        }
    }

    private void getTokensFromFirestore(String title, String body) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String userId = document.getId();
                        String token = document.getString("token");
                        String domain = document.getString("domain");
                        if (token != null && domain != null && !userId.equals(App.getString("document_id"))) {
                            Log.e("user token and id", userId + " ,.," + token);
                            if (domain.equals(App.getString("domain"))) {
                                tokens.add(token);
                            }
                            // Send notification using 'token' for each user
                        } else {
                            Log.d("TAG", "No token found for user: " + userId);
                        }
                    }
                    sendNotify(title, body, tokens);

                })
                .addOnFailureListener(e -> {
                    Log.e("TAG", "Failed to retrieve tokens from Firestore", e);
                });
    }

    private void sendNotify(String title, String body, List<String> deviceTokens) {

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        for (String token : deviceTokens) {
            Log.e("user token and id", " ,.," + token);

            JSONObject jsonNotify = new JSONObject();
            try {
                jsonNotify.put("title", title + " creates new event");
                jsonNotify.put("body", body);

                JSONObject messageJson = new JSONObject();
                messageJson.put("to", token);
                messageJson.put("notification", jsonNotify);

                RequestBody requestBody = RequestBody.create(mediaType, messageJson.toString());
                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(requestBody)
                        .addHeader("Authorization", "Bearer AAAAnp54E4E:APA91bF09wKMyPx9H-CrlwzbvIsbJ2oeyjHHwUlVyLRci4RhxH3t18js-br9INWW1gYDAmTYZLi3kusD-RPXncPqsKQV3BHEma8oUzP8qLErDZWmkOUTvLNQ-8ewcxSH3D8Y2rTCowie") // Replace with your FCM server key
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("response", response.toString());

            } catch (JSONException | IOException e) {
                Log.e("error", e.toString());
            }
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
                                .error(R.drawable.baseline_account_circle_24)
                                .placeholder(R.drawable.baseline_account_circle_24)
                                .into(binding.postImage);
                        binding.post.setEnabled(true);
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

    private void showTimePicker() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minuteOfHour);
            String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
            binding.endTimeTIET.setText(time);
            Toast.makeText(CreateEventActivity.this, time, Toast.LENGTH_SHORT).show();
            Toast.makeText(CreateEventActivity.this, "date time " + app.convertDateTimeToMilliseconds(binding.endDateTIET.getText().toString(),
                    binding.endTimeTIET.getText().toString()), Toast.LENGTH_SHORT).show();
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, month1);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, month1 + 1, dayOfMonth);
            binding.endDateTIET.setText(date);
            Toast.makeText(CreateEventActivity.this, date, Toast.LENGTH_SHORT).show();
        }, year, month, day);

        datePickerDialog.show();
    }

}