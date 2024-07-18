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
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            List<DocumentSnapshot> documents = snapshot.getDocuments();
                            for (DocumentSnapshot document : documents) {
                                String postId = document.getId();
                                String endDateTimeString = document.getString("end_date_time");

                                if (endDateTimeString != null) {
                                    try {
                                        long endDateTime = Long.parseLong(endDateTimeString);
                                        if (endDateTime <= currentTime) {
                                            String imageUrl = document.getString("imageUrl");

                                            // Delete the post document
                                            deletePostDocument(db, postId);

                                            // Delete the image from storage
                                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                                deleteImageFromStorage(storage, imageUrl);
                                            }

                                            deleteInvitations(db, postId);
                                            // Delete the like document
                                            deleteLikeDocument(db, postId);

                                            // Delete the chat document and its sub-collection
                                            deleteChatDocument(db, postId);
                                        }
                                    } catch (NumberFormatException e) {
                                        Log.e("tasks", "Failed to parse end_date_time: " + endDateTimeString, e);
                                    }
                                }
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

    private void deleteInvitations(FirebaseFirestore db, String postId) {
        db.collection("users").get().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               for (QueryDocumentSnapshot snapshot: task.getResult()) {
                   // Get the user's chatinvitation subcollection and find documents with the specified post_id
                   db.collection("users")
                           .document(snapshot.getId())
                           .collection("chatinvitation")
                           .whereEqualTo("post_id", postId)
                           .get()
                           .addOnCompleteListener(chatTask -> {
                               if (chatTask.isSuccessful()) {
                                   for (QueryDocumentSnapshot chatDoc : chatTask.getResult()) {
                                       // Delete each document with the specified post_id
                                       db.collection("users")
                                               .document(snapshot.getId())
                                               .collection("chatinvitation")
                                               .document(chatDoc.getId())
                                               .delete()
                                               .addOnSuccessListener(aVoid -> {
                                                   // Log success if needed
                                                   Log.d("deleteInvitations", "DocumentSnapshot successfully deleted!");
                                               })
                                               .addOnFailureListener(e -> {
                                                   // Log failure if needed
                                                   Log.w("deleteInvitations", "Error deleting document", e);
                                               });
                                   }
                               } else {
                                   // Log error if fetching chat invitations failed
                                   Log.w("deleteInvitations", "Error getting chat invitations.", chatTask.getException());
                               }
                           });
               }
           }  else {
               Log.w("deleteInvitations", "Error getting users.", task.getException());

           }
        });
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


