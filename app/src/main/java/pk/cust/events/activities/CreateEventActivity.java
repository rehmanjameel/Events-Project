package pk.cust.events.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import pk.cust.events.utils.App;
import pk.cust.events.utils.RealPathUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    }

    private void checkValidation() {
        String description = binding.descriptionTIET.getText().toString();

        if (description.isEmpty()) {
            binding.descriptionTIET.setError("Field required");
        } else if (imageUri == null) {
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
            // Add other fields as needed

            db.collection("posts")
                    .add(post)
                    .addOnSuccessListener(documentReference -> {
                        getTokensFromFirestore(App.getString("user_name"), description);
                        Toast.makeText(this, "Post uploaded successfully!", Toast.LENGTH_LONG).show();
                        onBackPressed();
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
                        Log.e("user token and id", userId + " ,.," + token);
                        if (token != null) {
                            tokens.add(token);
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
            JSONObject jsonNotify = new JSONObject();
            try {
                jsonNotify.put("title", App.getString("user_name") + " sends " + title);
                jsonNotify.put("body", body);

                JSONObject messageJson = new JSONObject();
                messageJson.put("to", token);
                messageJson.put("notification", jsonNotify);

                RequestBody requestBody = RequestBody.create(mediaType, messageJson.toString());
                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(requestBody)
                        .addHeader("Authorization", "Bearer AAAAar65774:APA91bE6BwdQQoG53I0hL03gTjcm2GHD1wgle5WxHwvXNSgL-JiMZKvZQXvII3dTuBjha3CRz4V2fUAnpPe24L38eOj2NhbXbA-yI8thSxEOZzOCVNxQKtRrBHHVf92TdxfMiadQvfV4") // Replace with your FCM server key
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
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                requestPermissions();
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
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
        String[] permissionsToRequest = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
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
}