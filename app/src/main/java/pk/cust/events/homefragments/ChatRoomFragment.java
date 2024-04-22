package pk.cust.events.homefragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import pk.cust.events.R;
import pk.cust.events.adapter.ChatInvitationAdapter;
import pk.cust.events.adapter.EventsAdapter;
import pk.cust.events.databinding.FragmentChatRoomBinding;
import pk.cust.events.model.EventsModel;
import pk.cust.events.model.HomeEventsModel;
import pk.cust.events.model.NotificationsModel;
import pk.cust.events.utils.App;

public class ChatRoomFragment extends Fragment {

    FragmentChatRoomBinding binding;

    private ArrayList<NotificationsModel> notificationsModelList = new ArrayList<>();
    private ChatInvitationAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    String currentStudentDocumentId;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatRoomBinding.inflate(inflater, container, false);

        binding.notificationsRV.setLayoutManager(new LinearLayoutManager(requireActivity()));

        // Initialize firebase authentication
        mAuth = FirebaseAuth.getInstance();

        // create Firebase FireStore instance
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        currentStudentDocumentId = App.getString("document_id");

        getNotifications(currentStudentDocumentId);

        binding.backArrow.setOnClickListener(view -> {
            requireActivity().onBackPressed();
        });


        binding.progressbar.setVisibility(View.VISIBLE);


        return binding.getRoot();
    }

    private void getNotifications(String userId) {
        notificationsModelList.clear();
        db.collection("users").document(userId).collection("chatinvitation")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot snapshot :
                                task.getResult()) {
                            if (snapshot != null) {
                                String title = snapshot.getString("title");
                                String description = snapshot.getString("body");
                                String chatId = snapshot.getString("chat_room_Id");
                                String postId = snapshot.getString("post_id");
                                boolean isStatus = Boolean.TRUE.equals(snapshot.getBoolean("is_status"));
                                if (!isStatus) {

                                    String invitationId = snapshot.getId();

                                    Log.e("notifications get data0", title + " .,." + description);
                                    Log.e("notifications get data21", title + " .,." + snapshot.getId());

                                    NotificationsModel notificationsModel = new NotificationsModel();

                                    notificationsModel.setId(invitationId);
                                    notificationsModel.setName(title);
                                    notificationsModel.setDescription(description);
                                    notificationsModel.setChatId(chatId);
                                    notificationsModel.setPostId(postId);
                                    notificationsModelList.add(notificationsModel);
                                    binding.progressbar.setVisibility(View.GONE);
                                }
                            }

                        }
                        Log.e("notifications get data023", String.valueOf(notificationsModelList.size()));

                        adapter = new ChatInvitationAdapter(requireActivity(), notificationsModelList);
                        binding.notificationsRV.setAdapter(adapter);


                    }
                });

    }


    private void joinChatRoom(String chatRoomId, View view, String postId) {
        // Navigate to the chat room screen with the provided chatRoomId
        db.collection("posts").document(postId)
                .get()
                .addOnSuccessListener(document -> {
                    List<Task<Object>> tasks = new ArrayList<>(); // List to store user detail tasks


//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userId = document.getString("userId");
                        String description = document.getString("description");
                        String imageUrl = document.getString("imageUrl");
//                        String postId = document.getId();

//                    }
                    // Retrieve additional user details from the 'users' collection
                    Task<Object> userTask = db.collection("users")
                            .document(userId)
                            .get()
                            .onSuccessTask(userDocument -> {
                                String userName = userDocument.getString("user_name");
                                String userImage = userDocument.getString("user_image");
                                String userDomain = userDocument.getString("domain");
                                Log.e("user name from posts", userName + ",.,." + userImage);

                                Bundle bundle = new Bundle();
                                bundle.putString("chatRoomId", chatRoomId);
                                bundle.putString("user_name", userName);
                                bundle.putString("user_image", userImage);
                                bundle.putString("user_id", userId);
                                bundle.putString("post_image", imageUrl);
                                bundle.putString("post_id", postId);
                                bundle.putString("post_description", description);
                                bundle.putString("post_domain", userDomain);

                                App.IS_ACCEPTED_ROOM = true;
                                // Assuming you're using Navigation Component to navigate
                                Navigation.findNavController(view)
                                        .navigate(R.id.action_chatRoomFragment_to_eventDetailFragment, bundle);
                                Log.e("user name from posts123", userName + ",.,." + userImage);

                                return Tasks.forResult(null);
                            })
                            .addOnFailureListener(e -> {
//                                binding.progressbar.setVisibility(View.GONE);
                                // Handle failure
                            });

                    tasks.add(userTask);

                })
                .addOnFailureListener(e -> {
                    binding.progressbar.setVisibility(View.GONE);

//                    binding.progressbar.setVisibility(View.GONE);
                    // Handle failure
                });

    }


    private void updateInvitationAcceptStatus(String userId, String invitationId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Update the token field for the user
        db.collection("users").document(userId).collection("chatinvitation").document(invitationId)
                .update("is_status", true)
                .addOnSuccessListener(aVoid -> {
                    getNotifications(userId);
                    binding.progressbar.setVisibility(View.GONE);
                    Log.d("TAG", "Token saved successfully for user: " + userId);
                })
                .addOnFailureListener(e -> {
                    binding.progressbar.setVisibility(View.GONE);

                    Log.e("TAG", "Failed to save token for user: " + userId, e);
                });


    }

    @Override
    public void onResume() {
        super.onResume();

        // Set item click listener
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {


                adapter.setOnItemClickListener(new ChatInvitationAdapter.OnItemClickListener() {
                    @Override
                    public void onAcceptClick(String chatRoomId, String invitationId, String postId) {
                        // Handle accept click
                        joinChatRoom(chatRoomId, requireView(), postId);
                        binding.progressbar.setVisibility(View.VISIBLE);
//                        updateInvitationAcceptStatus(currentStudentDocumentId, invitationId);
                    }

                    @Override
                    public void onRejectClick(String chatRoomId, String invitationId, String postId) {
                        // Handle reject click
                        // Remove invitation or handle rejection logic
                        binding.progressbar.setVisibility(View.VISIBLE);
                        updateInvitationAcceptStatus(currentStudentDocumentId, invitationId);

                    }
                });
            }
        }, 3000);
    }
}