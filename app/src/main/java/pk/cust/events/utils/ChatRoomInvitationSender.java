package pk.cust.events.utils;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// this class will get the tokens from fireStore of same domain users and will send the invitation
public class ChatRoomInvitationSender {
    private static List<String> tokens = new ArrayList<>();

    public static void getTokensFromFireStore(String title, String body, String chatRoomId, String postId) {
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
                    sendNotification(title, body, chatRoomId, postId, tokens);

                })
                .addOnFailureListener(e -> {
                    Log.e("TAG", "Failed to retrieve tokens from Firestore", e);
                });
    }

    public static void sendNotification(String title, String body, String chatRoomId, String postId, List<String> deviceTokens) {

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        for (String token : deviceTokens) {

            JSONObject jsonNotify = new JSONObject();
            try {
                jsonNotify.put("title", "Ticket invitation!");
                jsonNotify.put("body", App.getString("user_name") + " sent you a ticket to join chat on their topic "
                        + title + "(" + body + ")");

                JSONObject dataJson = new JSONObject();
                dataJson.put("chatRoomId", chatRoomId);
                dataJson.put("chatPostId", postId);

                JSONObject messageJson = new JSONObject();
                messageJson.put("to", token);
                messageJson.put("notification", jsonNotify);
                messageJson.put("data", dataJson);

                RequestBody requestBody = RequestBody.create(mediaType, messageJson.toString());
                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(requestBody)
                        .addHeader("Authorization", "Bearer AAAAnp54E4E:APA91bF09wKMyPx9H-CrlwzbvIsbJ2oeyjHHwUlVyLRci4RhxH3t18js-br9INWW1gYDAmTYZLi3kusD-RPXncPqsKQV3BHEma8oUzP8qLErDZWmkOUTvLNQ-8ewcxSH3D8Y2rTCowie") // Replace with your FCM server key
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    // Handle unsuccessful response
                }
            } catch (JSONException | IOException e) {
                // Handle exception
            }
        }
    }
}
