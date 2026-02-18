package pk.custuni.eventsapp.utils;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// this class will get the tokens from fireStore of same domain users and will send the invitation
public class ChatRoomInvitationSender {
    private static List<String> tokens = new ArrayList<>();

    public static void getTokensFromFireStore(String title,
                                              String body,
                                              String chatRoomId,
                                              String postId) {

        FirebaseFirestore db =
                FirebaseFirestore.getInstance(FirebaseApp.getInstance(), "eventsdb");

        List<String> tokens = new ArrayList<>();

        db.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    for (QueryDocumentSnapshot document : querySnapshot) {

                        String userId = document.getId();
                        String token = document.getString("token");
                        String domain = document.getString("domain");

                        if (token != null &&
                                domain != null &&
                                !userId.equals(App.getString("document_id")) &&
                                domain.equals(App.getString("domain"))) {

                            tokens.add(token);
                        }
                    }

                    sendNotificationToBackend(title, body, chatRoomId, postId, tokens);

                })
                .addOnFailureListener(e ->
                        Log.e("TAG", "Failed to retrieve tokens", e));
    }

    public static void sendNotification(String title, String body, String chatRoomId, String postId, List<String> deviceTokens) {

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        for (String token : deviceTokens) {

            JSONObject jsonNotify = new JSONObject();
            try {
                if (title.equals("Chat Closed")) {
                    jsonNotify.put("title", title);
                    jsonNotify.put("body", App.getString("user_name") + " closed the chat of topic " + postId);
                } else {
                    jsonNotify.put("title", "Ticket invitation!");
                    jsonNotify.put("body", App.getString("user_name") + " sent you a ticket to join chat on their topic "
                            + title + "(" + body + ")");
                }

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
                        .addHeader("Authorization", "key=AAAAvapA33U:APA91bFkN0veraAmBrQJQW6ZFlM3YOtRdGjopa2LcVPezcnDBYGYJNonsy4bwIUKW5LNETUz06ByfTYXtcTF-s2j_hqugiHEwcTxDfZq4MpDRcsqGCfCz6f-JREzFUmuz6gRD-0ipgos") // Replace with your FCM server key
//                        .addHeader("Authorization", "Bearer AAAAnp54E4E:APA91bF09wKMyPx9H-CrlwzbvIsbJ2oeyjHHwUlVyLRci4RhxH3t18js-br9INWW1gYDAmTYZLi3kusD-RPXncPqsKQV3BHEma8oUzP8qLErDZWmkOUTvLNQ-8ewcxSH3D8Y2rTCowie") // Replace with your FCM server key
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("response", "response: " + response);
                Log.e("response", "requestbody: " + requestBody);
                if (!response.isSuccessful()) {
                    // Handle unsuccessful response
                }
            } catch (JSONException | IOException e) {
                // Handle exception
            }
        }
    }

    public static void getTokenForSingleUser(String title,
                                             String body,
                                             String postId) {

        FirebaseFirestore db =
                FirebaseFirestore.getInstance(FirebaseApp.getInstance(), "eventsdb");

        db.collection("posts")
                .document(postId)
                .get()
                .addOnSuccessListener(postSnapshot -> {

                    String userId = postSnapshot.getString("userId");
                    String description = postSnapshot.getString("description");

                    db.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener(userDocument -> {

                                String token = userDocument.getString("token");

                                if (token != null) {
                                    List<String> singleToken =
                                            Collections.singletonList(token);

                                    sendNotificationToBackend(
                                            title,
                                            body + description,
                                            null,
                                            postId,
                                            singleToken
                                    );
                                }
                            });

                });
    }


    public static void sendNotificationToSingleUser(String title, String body, String postDescription, String deviceToken) {

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");


            JSONObject jsonNotify = new JSONObject();
            try {
                    jsonNotify.put("title", title);
                    jsonNotify.put("body", body + postDescription);

//                JSONObject dataJson = new JSONObject();
//                dataJson.put("chatRoomId", chatRoomId);
//                dataJson.put("chatPostId", postId);

                JSONObject messageJson = new JSONObject();
                messageJson.put("to", deviceToken);
                messageJson.put("notification", jsonNotify);
//                messageJson.put("data", dataJson);

                RequestBody requestBody = RequestBody.create(mediaType, messageJson.toString());
                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(requestBody)
                        .addHeader("Authorization", "key=AAAAvapA33U:APA91bFkN0veraAmBrQJQW6ZFlM3YOtRdGjopa2LcVPezcnDBYGYJNonsy4bwIUKW5LNETUz06ByfTYXtcTF-s2j_hqugiHEwcTxDfZq4MpDRcsqGCfCz6f-JREzFUmuz6gRD-0ipgos") // Replace with your FCM server key
//                        .addHeader("Authorization", "Bearer AAAAnp54E4E:APA91bF09wKMyPx9H-CrlwzbvIsbJ2oeyjHHwUlVyLRci4RhxH3t18js-br9INWW1gYDAmTYZLi3kusD-RPXncPqsKQV3BHEma8oUzP8qLErDZWmkOUTvLNQ-8ewcxSH3D8Y2rTCowie") // Replace with your FCM server key
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

    private static void sendNotificationToBackend(String title,
                                                  String body,
                                                  String chatRoomId,
                                                  String postId,
                                                  List<String> tokens) {

        if (tokens == null || tokens.isEmpty()) return;

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        try {

            JSONObject root = new JSONObject();
            root.put("title", title);
            root.put("body", body);

            JSONArray tokenArray = new JSONArray();
            for (String token : tokens) {
                tokenArray.put(token);
            }
            root.put("tokens", tokenArray);

            JSONObject dataJson = new JSONObject();

            if (chatRoomId != null)
                dataJson.put("chatRoomId", chatRoomId);

            if (postId != null)
                dataJson.put("postId", postId);

            root.put("data", dataJson);

            RequestBody requestBody =
                    RequestBody.create(mediaType, root.toString());

            Request request = new Request.Builder()
                    .url("https://us-central1-loginfirebase-e6607.cloudfunctions.net/sendNotification")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            Log.e("BACKEND_CODE", String.valueOf(response.code()));
            Log.e("BACKEND_BODY", response.body().string());

        } catch (Exception e) {
            Log.e("BACKEND_ERROR", e.toString());
        }
    }

    public static void sendNotificationToBackend(
            String title,
            String body,
            List<String> tokens,
            JSONObject dataJson
    ) {

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        try {
            JSONObject root = new JSONObject();
            root.put("title", title);
            root.put("body", body);

            JSONArray tokenArray = new JSONArray();
            for (String token : tokens) {
                tokenArray.put(token);
            }

            root.put("tokens", tokenArray);
            root.put("data", dataJson);

            RequestBody requestBody =
                    RequestBody.create(mediaType, root.toString());

            Request request = new Request.Builder()
                    .url("https://us-central1-loginfirebase-e6607.cloudfunctions.net/sendNotification")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            Log.e("BACKEND_CODE", String.valueOf(response.code()));
            Log.e("BACKEND_BODY", response.body().string());

        } catch (Exception e) {
            Log.e("BACKEND_ERROR", e.toString());
        }
    }

}
