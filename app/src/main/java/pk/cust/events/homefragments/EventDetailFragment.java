package pk.cust.events.homefragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import pk.cust.events.R;
import pk.cust.events.activities.ProfileActivity;
import pk.cust.events.adapter.MessageAdapter;
import pk.cust.events.databinding.FragmentEventDetailBinding;
import pk.cust.events.model.MessageModel;
import pk.cust.events.utils.App;
import pk.cust.events.utils.ChatRoomInvitationSender;

@SuppressLint("SetTextI18n")
public class EventDetailFragment extends Fragment {

    private FragmentEventDetailBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference eventLikeRef;
    private String getChatRoomId = null;
    String chatRoomId;
    MessageAdapter messageAdapter;
    Bundle bundle;
    private List<MessageModel> messageList = new ArrayList<>(); // Declare and initialize messageList


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventDetailBinding.inflate(inflater, container, false);


        binding.postChatRV.setLayoutManager(new LinearLayoutManager(requireActivity()));

        bundle = getArguments();
        if (bundle != null) {
            updateData(bundle);

            if (App.IS_ACCEPTED_ROOM) {
                checkIfCurrentUserIsCreator(bundle.getString("chatRoomId"), bundle.getString("post_id"),
                        bundle.getString("user_id"));
            } else {
                checkIfCurrentUserIsCreator("chat_" + bundle.getString("post_id"), bundle.getString("post_id"),
                        bundle.getString("user_id"));
            }

        }

        if (App.IS_ROOM_SPACE) {

            binding.backArrow.setOnClickListener(view -> {
                App.IS_ROOM_SPACE = false;
                Navigation.findNavController(view).popBackStack(R.id.action_eventDetailFragment_to_eventsFragment, false);
                Navigation.findNavController(view).popBackStack();
            });

        } else if (App.IS_CHAT_FROM_HOME) {

            binding.backArrow.setOnClickListener(view -> {
                App.IS_CHAT_FROM_HOME = false;

                Navigation.findNavController(view).popBackStack(R.id.action_eventDetailFragment_to_homeFragment, false);
                Navigation.findNavController(view).popBackStack();
            });
        } else if (App.IS_PROFILE) {
            binding.backArrow.setOnClickListener(view -> {
                App.IS_PROFILE = false;
                Intent intent = new Intent(requireActivity(), ProfileActivity.class);
                startActivity(intent);
            });

        }


        if (App.IS_ACCEPTED_ROOM) {
            // Retrieve chat room ID from arguments
            if (getArguments() != null) {
                getChatRoomId = getArguments().getString("chatRoomId");
            }
            binding.backArrow.setOnClickListener(view -> {
                App.IS_ACCEPTED_ROOM = false;

                Navigation.findNavController(view).popBackStack(R.id.action_eventDetailFragment_to_chatRoomFragment, false);
                Navigation.findNavController(view).popBackStack();
            });
        }

        // Handle joining the chat room with the provided chatRoomId
        if (getChatRoomId != null) {
            // Implement logic to join the chat room with the provided chatRoomId
            // For example, establish a connection to the chat room and load messages
            // You might use a chat SDK or implement your own chat functionality here
            // For demonstration purposes, we'll simply log the chatRoomId
            // Chat room created successfully

            fetchChatMessages(getChatRoomId);
        }

        if (chatRoomId != null) {
            fetchChatMessages(chatRoomId);
        } else {
            if (!App.IS_ROOM_SPACE) {
                binding.chatTitle.setVisibility(View.GONE);
                fetchChatMessages("chat_" + bundle.getString("post_id"));
            }
        }
        binding.closeChat.setOnClickListener(v -> {
            updateCurrentUserInChatRoom("chat_" + bundle.getString("post_id"), bundle.getString("post_id"),
                    bundle.getString("user_id"));
        });

        // Set OnClickListener for the send button
        // Set OnClickListener for the send button
        binding.messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve message text from EditText
                String messageText = binding.mainEditTextId.getText().toString().trim();

                // Check if the message is not empty
                if (!TextUtils.isEmpty(messageText)) {
                    // Create a unique message ID
                    String messageId = "message_" + UUID.randomUUID().toString();

                    // Retrieve the current user's ID and name (replace with your actual implementation)
                    String senderId = App.getString("document_id"); // Replace with the current user's ID
                    String senderName = App.getString("user_name"); // Replace with the current user's name

                    // Get current timestamp
                    long timestamp = System.currentTimeMillis();

                    // Create a MessageModel object
                    MessageModel message = new MessageModel(messageId, senderId, senderName, messageText, timestamp);

                    // Send the message to Firestore
                    if (App.IS_ACCEPTED_ROOM) {

                        sendMessageToFirestore(message, getChatRoomId); // Pass the chat room ID
                    } else {
                        if (chatRoomId != null) {

                            sendMessageToFirestore(message, chatRoomId); // Pass the chat room ID
                        } else {

                            sendMessageToFirestore(message, "chat_" + bundle.getString("post_id")); // Pass the chat room ID
                        }

                    }

                    // Clear the EditText
                    binding.mainEditTextId.setText("");
                } else {
                    // Display a message indicating that the message is empty
                    Toast.makeText(requireActivity(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return binding.getRoot();
    }

    public void updateData(Bundle dataBundle) {
        if (dataBundle != null) {
            binding.personName.setText(dataBundle.getString("user_name"));
            binding.eventDomain.setText(dataBundle.getString("post_domain"));
            binding.eventTopic.setText(dataBundle.getString("post_description"));

            if (!Objects.requireNonNull(dataBundle.getString("end_date_time")).isEmpty()) {
                binding.endTime.setText("Event will close on: " +
                        App.convertMillisToDateTime(Long.parseLong(dataBundle.getString("end_date_time"))));
            } else {
                binding.endTime.setText("");
            }
            Glide.with(requireContext())
                    .load(dataBundle.getString("user_image"))
                    .error(R.drawable.baseline_broken_image_24)
                    .placeholder(R.drawable.profile)
                    .into(binding.personImage);

            RequestOptions requestOptions = new RequestOptions()
                    .timeout(60000); // Set the timeout to 60 seconds (adjust as needed)

            Glide.with(requireActivity())
                    .load(dataBundle.getString("post_image"))
                    .error(R.drawable.baseline_account_circle_24)
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .apply(requestOptions)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                            // Handle load failure, show placeholder or error message
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                            // Image loaded successfully
                            return false;
                        }
                    })
                    .into(binding.eventImage);

            getLikeButtonStatus(dataBundle.getString("post_id"),
                    dataBundle.getString("user_id"));


        }
    }

    // Method to check if the current user has liked a post
    public void getLikeButtonStatus(final String postKey, final String userId) {
        // Build the reference to the likes collection for the specified post ID
        eventLikeRef = db.collection("like").document(postKey);
        eventLikeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains(userId)) {
                            // User has liked the post
                            binding.likeIcon.setImageResource(R.drawable.baseline_favorite_24);
                            binding.likeIcon.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.red), PorterDuff.Mode.SRC_IN);

                            int totalLikes = Objects.requireNonNull(documentSnapshot.getData()).size();
                            binding.eventLikes.setText(totalLikes + " likes");
                        } else {
                            // User has not liked the post
                            binding.likeIcon.setImageResource(R.drawable.baseline_favorite_border_24);
                            binding.likeIcon.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.white), PorterDuff.Mode.SRC_IN);

                            int totalLikes = Objects.requireNonNull(documentSnapshot.getData()).size();
                            binding.eventLikes.setText(totalLikes + " likes");
                        }
                    } else {
                        Log.e("else", "likes document not exist");
                        // Document doesn't exist, meaning no likes for this post yet
                        // Handle accordingly, e.g., set default like button state
                    }
                } else {
                    Log.e("else", "task not succeeded");
                    // Error occurred, handle it accordingly
                }
            }
        });
    }

    private void createChatRoom(String postId, String currentUserId, String userName, String domain, String desc) {
        // Generate a unique chat room ID
        chatRoomId = "chat_" + postId;

        // Add current user and other participants to the chat room
        Map<String, Object> participants = new HashMap<>();
        participants.put(postId, true);
        participants.put("creator_id", currentUserId);
        participants.put("creator_name", userName);
//        participants.put(otherUserId, true);

        // Create the chat room document in Firestore
        DocumentReference chatRoomRef = db.collection("Chats").document(chatRoomId);
        chatRoomRef.set(participants)
                .addOnSuccessListener(aVoid -> {
                    // Chat room created successfully
                    binding.chatTitle.setVisibility(View.VISIBLE);
                    binding.postChatRV.setVisibility(View.VISIBLE);
                    binding.textLinearLayoutId.setVisibility(View.VISIBLE);

                    ChatRoomInvitationSender.getTokensFromFireStore(desc, domain, chatRoomId, postId);

                })
                .addOnFailureListener(e -> {
                    // Error creating chat room
                });
    }

    private void sendMessageToFirestore(MessageModel message, String chatRoomId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Chats").document(chatRoomId).collection("messages")
                .add(message)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Add the message to the local list of messages
                        fetchChatMessages(chatRoomId);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SendMessage", "Error sending message", e);
                    }
                });
    }


    private void fetchChatMessages(String chatRoomId) {
        // Example: Fetch chat messages from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Chats").document(chatRoomId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("ChatRoomFragment", "Error fetching chat messages", e);
                        return;
                    }

                    // Process the chat messages received from Firestore
                    List<MessageModel> chatMessages = new ArrayList<>();
                    if (querySnapshot != null) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            MessageModel message = document.toObject(MessageModel.class);
                            messageList.add(message);

                            chatMessages.add(message);
                        }
                    }

                    // Update the UI to display the chat messages
                    displayChatMessages(chatMessages);
                });
    }

    private void displayChatMessages(List<MessageModel> chatMessages) {
        // Initialize RecyclerView if not already initialized

        messageAdapter = new MessageAdapter(chatMessages, App.getString("document_id")); // Pass your user ID here
        binding.postChatRV.setAdapter(messageAdapter);
//      Notify the adapter that a new message has been added
        int position = chatMessages.size() - 1;

        // Scroll the RecyclerView to the bottom to show the new message
        binding.postChatRV.scrollToPosition(position);
        messageAdapter.notifyItemInserted(position);
    }

    // end the chat...
    private void updateCurrentUserInChatRoom(String chatRoomId, String postId, String currentUserId) {
        // Reference to the chat room document in Firestore
        DocumentReference chatRoomRef = db.collection("Chats").document(chatRoomId);

        // Update the value of currentUserId to false
        chatRoomRef.update(postId, false)
                .addOnSuccessListener(aVoid -> {
                    // Current user updated successfully
                    binding.textLinearLayoutId.setVisibility(View.GONE);
                    checkIfCurrentUserIsCreator(chatRoomId, postId, currentUserId);
                    ChatRoomInvitationSender.getTokensFromFireStore("Chat Closed", currentUserId, chatRoomId, bundle.getString("post_domain"));
                })
                .addOnFailureListener(e -> {
                    // Error updating current user
                });
    }

    private void checkIfCurrentUserIsCreator(String chatRoomId, String postId, String creatorId) {
        // Reference to the chat room document in Firestore
        DocumentReference chatRoomRef = db.collection("Chats").document(chatRoomId);

        // Retrieve the document data
        chatRoomRef.get().addOnSuccessListener(documentSnapshot -> {

            if (documentSnapshot.exists()) {
                Boolean isCreator = documentSnapshot.getBoolean(postId);
                String creatorid = documentSnapshot.getString("creator_id");
                String creator_name = documentSnapshot.getString("creator_name");

                Log.e("creator", creatorid + ",.,." + creator_name + ",.,." + creatorId);
                if (!creatorId.equals(App.getString("document_id"))) {
                    binding.textLinearLayoutId.setVisibility(View.VISIBLE);

                }
                // Check if the current user's ID exists as a key in the document
                if (documentSnapshot.contains(postId)) {
                    // Retrieve the boolean value associated with the current user's ID
                    binding.chatTitle.setVisibility(View.VISIBLE);
                    binding.postChatRV.setVisibility(View.VISIBLE);
                    if (isCreator != null && isCreator) {
                        // Current user is the creator of the chat room
                        // Show the option to close the chat
                        // For example:

                        if (App.IS_ROOM_SPACE || App.IS_PROFILE) {

                            Log.e("is room space", String.valueOf(App.IS_ROOM_SPACE));
                            binding.textLinearLayoutId.setVisibility(View.GONE);
                            binding.chatTitle.setVisibility(View.GONE);

                        } else {
                            Log.e("is room space", String.valueOf(App.IS_ROOM_SPACE));

                            binding.textLinearLayoutId.setVisibility(View.VISIBLE);

                        }
                    } else {
                        binding.textLinearLayoutId.setVisibility(View.GONE);
                        binding.chatTitle.setVisibility(View.GONE);
                    }
                } else {
                    binding.chatTitle.setVisibility(View.VISIBLE);
                    binding.postChatRV.setVisibility(View.VISIBLE);
                }
            } else {
                if (App.IS_CHAT_FROM_HOME) {

                    createChatRoom(bundle.getString("post_id"), bundle.getString("user_id"),
                            bundle.getString("user_name"), bundle.getString("post_domain"),
                            bundle.getString("post_description"));
                } else {
                    Log.e("chat: ", "not from home");
                }
            }
        }).addOnFailureListener(e -> {
            // Error retrieving chat room data
        });
    }


}