package pk.cust.events.services;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.ArrayList;
import java.util.HashMap;

import pk.cust.events.activities.MainActivity;
import pk.cust.events.utils.App;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static final ArrayList<Long> alreadyNotifiedTimestamps = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains data payload.
        if (!remoteMessage.getData().isEmpty()) {
            // Handle data payload of the message.
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            Log.e("notifications1", title + ",.,." + message);

            // Process the message (e.g., show notification).
            if (title != null && message != null) {
//                NotificationHelper.showNotification(App.context, title, message);
                Log.e("notifications 0", title + ",.,." + message);

//                saveUserNotifications(title, message);
            }

        }

        if (!isDuplicate(remoteMessage.getSentTime())) {
            // send notificaiton here

            // Check if message contains notification payload.
            if (remoteMessage.getNotification() != null) {
                // Handle notification payload of the message.
                String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();

                if (title.equals("Chat Closed")) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                Log.e("notifications2", title + ",.,." + body);

                // Process the notification (e.g., show notification).
                if (title != null && body != null) {
                    NotificationHelper.showNotification(App.context, title, body);
                    Log.e("notifications3", title + ",.,." + body);

                    saveUserNotifications(title, body);
                    String chatRoomId;
                    String postId;
                    // Check if the message contains data payload
                    if (remoteMessage.getData().size() > 0) {
                        chatRoomId = remoteMessage.getData().get("chatRoomId");
                        postId = remoteMessage.getData().get("chatPostId");
                        Log.e("chatroom id", chatRoomId);

                        // Handle the chat room ID here and navigate user to the chat room
                        if (!title.equals("Chat Closed")) {
                            if (chatRoomId != null) {
                                saveUserPostInvitation(title, body, chatRoomId, postId);
                            }
                        }
                    }
                }
            }
        }
    }

    // Workaround for Firebase duplicate pushes
    private boolean isDuplicate(long timestamp) {
        if (alreadyNotifiedTimestamps.contains(timestamp)) {
            alreadyNotifiedTimestamps.remove(timestamp);
            return true;
        } else {
            alreadyNotifiedTimestamps.add(timestamp);
        }

        return false;
    }

    private void saveUserNotifications(String title, String body) {
        HashMap<String, Object> userNotification = new HashMap<>();
        userNotification.put("title", title);
        userNotification.put("body", body);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Replace "currentStudentDocumentId" with the actual document ID of the current student
        String currentStudentDocumentId = App.getString("document_id"); // Implement this method to get the document ID of the current student
        if (currentStudentDocumentId != null) {
            db.collection("users").document(currentStudentDocumentId).collection("notifications")
                    .add(userNotification)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.e("TAG", "Notification added with ID: " + documentReference.getId());
                            Toast.makeText(App.getContext(), "Notification added successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("TAG", "Failed to add notification", e);
                            Toast.makeText(App.getContext(), "Failed to add notification", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e("TAG", "Current student document ID is null");
        }
    }

    private void saveUserPostInvitation(String title, String body, String chatRoomId, String postId) {
        HashMap<String, Object> userNotification = new HashMap<>();
        userNotification.put("title", title);
        userNotification.put("body", body);
        userNotification.put("chat_room_Id", chatRoomId);
        userNotification.put("post_id", postId);
        userNotification.put("is_status", false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Replace "currentStudentDocumentId" with the actual document ID of the current student
        String currentStudentDocumentId = App.getString("document_id"); // Implement this method to get the document ID of the current student
        if (currentStudentDocumentId != null) {

            db.collection("users").document(currentStudentDocumentId).collection("chatinvitation")
                    .add(userNotification)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.e("TAG", "Notification added with ID: " + documentReference.getId());
                            Toast.makeText(App.getContext(), "Invitation received!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("TAG", "Failed to add notification", e);
                            Toast.makeText(App.getContext(), "Failed to add notification", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e("TAG", "Current student document ID is null");
        }
    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
//        Toast.makeText(App.context, token + ",.", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Refreshed token: " + token);
    }

    private void sendNotification(String title, String messageBody) {
        // Implement your notification logic here.
        // You can show a notification using NotificationCompat.Builder.
    }
}

