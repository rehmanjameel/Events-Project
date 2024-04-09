package pk.cust.events.adapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
//
        holder.userName.setText(model.getUserName());

        holder.eventTopic.setText(model.getDescription());

        holder.getLikeButtonStatus(model.getPostId(), user_id);

        holder.likeButton.setOnClickListener(view -> {
            testClick = true;
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

                                holder.getLikeButtonStatus(model.getPostId(), user_id);
                            } else {
                                // User has not liked the post, add the like
                                likeReference.update(user_id, true);
                                holder.likeButton.setImageResource(R.drawable.baseline_favorite_24);
                                holder.likeButton.setColorFilter(ContextCompat.getColor(activity, R.color.red), PorterDuff.Mode.SRC_IN);
                            }
                        } else {
                            // Document doesn't exist, meaning no likes for this post yet
                            // Add the first like
                            likeReference.set(new HashMap<String, Object>() {{
                                put(user_id, true);
                                holder.likeButton.setImageResource(R.drawable.baseline_favorite_24);
                                holder.likeButton.setColorFilter(ContextCompat.getColor(activity, R.color.red), PorterDuff.Mode.SRC_IN);

                            }});
                        }
                    } else {
                        // Error occurred, handle it accordingly
                    }
                }
            });


        });


    }

    // Method to update like button icon based on the like state
    private void updateLikeButtonIcon(ImageView likeButton, boolean isLiked) {
        if (isLiked) {
            likeButton.setImageResource(R.drawable.baseline_favorite_24); // Set filled heart icon when liked
        } else {
            likeButton.setImageResource(R.drawable.baseline_favorite_border_24); // Set outline heart icon when not liked
        }
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
                            //count the number of likes

                            if (documentSnapshot.contains(userId)) {
                                // User has liked the post
                                likeButton.setImageResource(R.drawable.baseline_favorite_24); // Set filled heart icon when liked
                                likeButton.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.red), PorterDuff.Mode.SRC_IN);
                                int totalLikes = documentSnapshot.getData().size();
                                likesCount.setText(totalLikes + " likes");
                            } else {
                                // User has not liked the post
                                likeButton.setImageResource(R.drawable.baseline_favorite_border_24); // Set outline heart icon when not liked
                                likeButton.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.white), PorterDuff.Mode.SRC_IN);

                                int totalLikes = documentSnapshot.getData().size();
                                likesCount.setText(totalLikes + " likes");
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

    }
}
