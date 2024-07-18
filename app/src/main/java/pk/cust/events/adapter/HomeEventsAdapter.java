package pk.cust.events.adapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import pk.cust.events.R;
import pk.cust.events.model.HomeEventsModel;
import pk.cust.events.utils.App;
import pk.cust.events.utils.ChatRoomInvitationSender;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeEventsAdapter extends RecyclerView.Adapter<HomeEventsAdapter.ViewHolder> {

    Activity activity;
    ArrayList<HomeEventsModel> eventsModels;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference likeReference;
    String user_id;
    boolean testClick = false;

    public HomeEventsAdapter(Activity activity, ArrayList<HomeEventsModel> eventsModels) {
        this.activity = activity;
        this.eventsModels = eventsModels;
        user_id = App.getString("document_id");

    }

    @NonNull
    @Override
    public HomeEventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_events_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeEventsAdapter.ViewHolder holder, int position) {
        HomeEventsModel model = eventsModels.get(position);

        // Reset like button state
        holder.likeButton.setImageResource(R.drawable.baseline_favorite_border_24);
        holder.likeButton.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.white), PorterDuff.Mode.SRC_IN);

        RequestOptions requestOptions = new RequestOptions()
                .timeout(60000); // Set the timeout to 60 seconds (adjust as needed)

        Glide.with(activity)
                .load(model.getImageUrl())
                .error(R.drawable.baseline_account_circle_24)
                .placeholder(R.drawable.baseline_account_circle_24)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Handle load failure, show placeholder or error message
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Image loaded successfully
                        return false;
                    }
                })
                .into(holder.eventImage);

        Glide.with(activity)
                .load(model.getUserImage())
                .error(R.drawable.baseline_image_24)
                .placeholder(R.drawable.baseline_image_24)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Handle load failure, show placeholder or error message
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Image loaded successfully
                        return false;
                    }
                })
                .into(holder.personImage);

        holder.userName.setText(model.getUserName());

        holder.eventTopic.setText(model.getDescription());

        holder.getLikeButtonStatus(model.getPostId(), user_id);


        // Set click listener
        holder.likeButton.setOnClickListener(view -> {
            // Get the correct document reference for the post
            likeReference = db.collection("like").document(model.getPostId());

            likeReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.contains(user_id)) {
                                // User has already liked the post, remove the like
                                likeReference.update(user_id, FieldValue.delete());
                                holder.likeButton.setImageResource(R.drawable.baseline_favorite_border_24);
                                holder.likeButton.setColorFilter(ContextCompat.getColor(activity, R.color.white), PorterDuff.Mode.SRC_IN);

                                holder.getLikeButtonStatus(model.getPostId(), user_id);
                            } else {
                                // User has not liked the post, add the like
                                likeReference.update(user_id, true);
                                holder.likeButton.setImageResource(R.drawable.baseline_favorite_24);
                                holder.likeButton.setColorFilter(ContextCompat.getColor(activity, R.color.red), PorterDuff.Mode.SRC_IN);
                                holder.getLikeButtonStatus(model.getPostId(), user_id);
                                ChatRoomInvitationSender.getTokenForSingleUser(App.getString("user_name"),
                                        " liked your post ", model.getPostId());
                            }
                        } else {
                            // Document doesn't exist, meaning no likes for this post yet
                            // Add the first like
                            likeReference.set(new HashMap<String, Object>() {{
                                put(user_id, true);
                            }});
                            holder.likeButton.setImageResource(R.drawable.baseline_favorite_24);
                            holder.likeButton.setColorFilter(ContextCompat.getColor(activity, R.color.red), PorterDuff.Mode.SRC_IN);
                            holder.getLikeButtonStatus(model.getPostId(), user_id);
                        }
                    } else {
                        // Error occurred, handle it accordingly
                    }
                }
            });
        });

        Log.e("user names", model.getUserName() + ",.,."+App.getString("user_name"));
        if (!App.getString("user_name").equals(model.getUserName())) {
            Log.e("user names121", model.getUserName() + ",.,."+App.getString("user_name"));
            holder.chatImage.setVisibility(View.GONE);
        } else {
            holder.chatImage.setVisibility(View.VISIBLE);
        }

        // chat room functionality
        holder.chatImage.setOnClickListener(view -> {
            App.IS_CHAT_FROM_HOME = true;
            Bundle bundle = new Bundle();
            bundle.putString("user_name", model.getUserName());
            bundle.putString("user_image", model.getUserImage());
            bundle.putString("user_id", model.getUserId());
            bundle.putString("post_image", model.getImageUrl());
            bundle.putString("post_id", model.getPostId());
            bundle.putString("post_description", model.getDescription());
            bundle.putString("post_domain", model.getDomain());
            bundle.putLong("start_date_time", model.getStartDateTime());
            bundle.putString("end_date_time", model.getEndDateTime());

            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_eventDetailFragment, bundle);

        });


    }

    @Override
    public int getItemCount() {
        return eventsModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventLikeRef;

        TextView userName, eventTopic, likesCount;
        ImageView personImage, eventImage, chatImage, likeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.personName);
            personImage = itemView.findViewById(R.id.personImage);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventTopic = itemView.findViewById(R.id.eventTopic);
            chatImage = itemView.findViewById(R.id.discussionIcon);
            likeButton = itemView.findViewById(R.id.likeIcon); // Assuming it's an ImageView
            likesCount = itemView.findViewById(R.id.eventLikes);
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
                                likeButton.setImageResource(R.drawable.baseline_favorite_24);
                                likeButton.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.red), PorterDuff.Mode.SRC_IN);

                                int totalLikes = documentSnapshot.getData().size();
                                likesCount.setText(totalLikes + " likes");
                                saveTotalLikes(postKey, totalLikes);

                            } else {
                                // User has not liked the post
                                likeButton.setImageResource(R.drawable.baseline_favorite_border_24);
                                likeButton.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.white), PorterDuff.Mode.SRC_IN);

                                int totalLikes = documentSnapshot.getData().size();
                                likesCount.setText(totalLikes + " likes");
                                saveTotalLikes(postKey, totalLikes);

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

        private void saveTotalLikes(String postId, int totalLikes) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Update the token field for the user
            db.collection("posts").document(postId)
                    .update("total_likes", totalLikes)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("TAG", "Token saved successfully for user: " + postId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TAG", "Failed to save token for user: " + postId, e);
                    });
        }

    }

    public void removeItem(String postId) {
        for (int i = 0; i < eventsModels.size(); i++) {
            if (eventsModels.get(i).getPostId().equals(postId)) {
                eventsModels.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
}
