package pk.cust.events.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import pk.cust.events.utils.App;

public class DeleteExpiredEventsWorker extends Worker {

    public DeleteExpiredEventsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        long currentTime = System.currentTimeMillis();

        db.collection("posts")
                .whereLessThanOrEqualTo("end_date_time", currentTime)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            List<DocumentSnapshot> documents = snapshot.getDocuments();
                            for (DocumentSnapshot document : documents) {
                                String postId = document.getId();
                                String imageUrl = document.getString("imageUrl");

                                // Delete the post document
                                deletePostDocument(db, postId);

                                // Delete the image from storage
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    deleteImageFromStorage(storage, imageUrl);
                                }

                                // Delete the like document
                                deleteLikeDocument(db, postId);

                                // Delete the chat document and its sub-collection
                                deleteChatDocument(db, postId);
                            }
                        } else {
                            Log.e("tasks", "No documents found to delete.");
                        }
                    } else {
                        Log.e("tasks", "Query failed: ", task.getException());
                    }
                });

        return Result.success();
    }

    private void deletePostDocument(FirebaseFirestore db, String postId) {
        db.collection("posts").document(postId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.e("tasks", "Successfully deleted document: " + postId);
                    Toast.makeText(App.getContext(), "Successfully deleted document: " + postId, Toast.LENGTH_SHORT).show();
                    sendBroadcast(postId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(App.getContext(), "Document not deleted: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("tasks", "Document not deleted: " + e.getMessage());
                });
    }

    private void deleteImageFromStorage(FirebaseStorage storage, String imageUrl) {
        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
        imageRef.delete()
                .addOnSuccessListener(aVoid -> Log.e("tasks", "Successfully deleted image: " + imageUrl))
                .addOnFailureListener(e -> Log.e("tasks", "Image not deleted: " + e.getMessage()));
    }

    private void deleteLikeDocument(FirebaseFirestore db, String postId) {
        db.collection("like").document(postId).delete()
                .addOnSuccessListener(aVoid -> Log.e("tasks", "Successfully deleted like document: " + postId))
                .addOnFailureListener(e -> Log.e("tasks", "Like document not deleted: " + e.getMessage()));
    }

    private void deleteChatDocument(FirebaseFirestore db, String postId) {
        DocumentReference chatDocRef = db.collection("Chats").document("chat_" + postId);
        chatDocRef.collection("messages").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot messagesSnapshot = task.getResult();
                        if (messagesSnapshot != null && !messagesSnapshot.isEmpty()) {
                            List<Task<Void>> deleteTasks = new ArrayList<>();
                            for (DocumentSnapshot message : messagesSnapshot.getDocuments()) {
                                deleteTasks.add(message.getReference().delete());
                            }
                            // Wait for all deletes to complete
                            Tasks.whenAll(deleteTasks)
                                    .addOnSuccessListener(aVoid -> {
                                        // Now delete the chat document itself
                                        chatDocRef.delete()
                                                .addOnSuccessListener(aVoid1 -> Log.e("tasks", "Successfully deleted chat document: " + postId))
                                                .addOnFailureListener(e -> Log.e("tasks", "Chat document not deleted: " + e.getMessage()));
                                    })
                                    .addOnFailureListener(e -> Log.e("tasks", "Failed to delete messages: " + e.getMessage()));
                        } else {
                            // No messages to delete, delete the chat document directly
                            chatDocRef.delete()
                                    .addOnSuccessListener(aVoid -> Log.e("tasks", "Successfully deleted chat document: " + postId))
                                    .addOnFailureListener(e -> Log.e("tasks", "Chat document not deleted: " + e.getMessage()));
                        }
                    } else {
                        Log.e("tasks", "Failed to get messages: ", task.getException());
                    }
                });
    }

    private void sendBroadcast(String postId) {
        Intent intent = new Intent("pk.cust.events.ACTION_POST_DELETED");
        intent.putExtra("postId", postId);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}


