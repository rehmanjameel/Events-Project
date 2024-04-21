package pk.cust.events.homefragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import pk.cust.events.R;
import pk.cust.events.activities.ProfileActivity;
import pk.cust.events.databinding.FragmentEventDetailBinding;
import pk.cust.events.utils.App;
import pk.cust.events.utils.ChatRoomInvitationSender;

public class EventDetailFragment extends Fragment {

    private FragmentEventDetailBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference eventLikeRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventDetailBinding.inflate(inflater, container, false);



        Bundle bundle = getArguments();
        updateData(bundle);

        if (!App.IS_PROFILE) {
            binding.backArrow.setOnClickListener(view -> {
                Navigation.findNavController(view).popBackStack(R.id.action_eventDetailFragment_to_eventsFragment, false);
                Navigation.findNavController(view).popBackStack();
            });
        } else if (App.IS_CHAT_FROM_HOME) {
            binding.backArrow.setOnClickListener(view -> {
                Navigation.findNavController(view).popBackStack(R.id.action_eventDetailFragment_to_homeFragment, false);
                Navigation.findNavController(view).popBackStack();
            });
        } else {
            binding.backArrow.setOnClickListener(view -> {
                Intent intent = new Intent(requireActivity(), ProfileActivity.class);
                startActivity(intent);
                requireActivity().finish();
            });

        }

        return binding.getRoot();
    }



    public void updateData(Bundle dataBundle) {
        if (getArguments() != null) {
            binding.personName.setText(dataBundle.getString("user_name"));
            binding.eventDomain.setText(dataBundle.getString("post_domain"));
            binding.eventTopic.setText(dataBundle.getString("post_description"));

            Glide.with(requireContext())
                    .load(dataBundle.getString("user_image"))
                    .error(R.drawable.baseline_broken_image_24)
                    .placeholder(R.drawable.profile)
                    .into(binding.personImage);

            Glide.with(requireContext())
                    .load(dataBundle.getString("post_image"))
                    .error(R.drawable.baseline_broken_image_24)
                    .placeholder(R.drawable.baseline_broken_image_24)
                    .into(binding.eventImage);

            getLikeButtonStatus(dataBundle.getString("post_id"),
                    dataBundle.getString("user_id"));

            createChatRoom(dataBundle.getString("post_id"), dataBundle.getString("user_id"),
                    dataBundle.getString("user_name"), dataBundle.getString("post_domain"),
                    dataBundle.getString("post_description"));
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

                            int totalLikes = documentSnapshot.getData().size();
                            binding.eventLikes.setText(totalLikes + " likes");
                        } else {
                            // User has not liked the post
                            binding.likeIcon.setImageResource(R.drawable.baseline_favorite_border_24);
                            binding.likeIcon.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.white), PorterDuff.Mode.SRC_IN);

                            int totalLikes = documentSnapshot.getData().size();
                            binding.eventLikes.setText(totalLikes + " likes");
                        }
                    } else {
                        // Document doesn't exist, meaning no likes for this post yet
                        // Handle accordingly, e.g., set default like button state
                    }
                } else {
                    // Error occurred, handle it accordingly
                }
            }
        });
    }

    private void createChatRoom(String postId, String currentUserId, String userName, String domain, String desc) {
        // Generate a unique chat room ID
        String chatRoomId = "chat_" + postId;

        // Add current user and other participants to the chat room
        Map<String, Object> participants = new HashMap<>();
        participants.put(currentUserId, true);
//        participants.put(otherUserId, true);

        // Create the chat room document in Firestore
        DocumentReference chatRoomRef = db.collection("Chats").document(chatRoomId);
        chatRoomRef.set(participants)
                .addOnSuccessListener(aVoid -> {
                    // Chat room created successfully
                    binding.chatTitle.setVisibility(View.VISIBLE);
                    binding.postChatRV.setVisibility(View.VISIBLE);
                    binding.textLinearLayoutId.setVisibility(View.VISIBLE);
                    ChatRoomInvitationSender.getTokensFromFireStore(userName, domain, desc, chatRoomId);

                })
                .addOnFailureListener(e -> {
                    // Error creating chat room
                });
    }

}