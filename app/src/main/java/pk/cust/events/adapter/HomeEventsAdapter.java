package pk.cust.events.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import pk.cust.events.R;
import pk.cust.events.model.HomeEventsModel;

import java.util.ArrayList;

public class HomeEventsAdapter extends RecyclerView.Adapter<HomeEventsAdapter.ViewHolder> {

    Activity activity;
    ArrayList<HomeEventsModel> eventsModels;

    public HomeEventsAdapter(Activity activity, ArrayList<HomeEventsModel> eventsModels) {
        this.activity = activity;
        this.eventsModels = eventsModels;
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

        // Set like button state and count
        holder.likeButton.setImageResource(model.isLiked(model.getUserId()) ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);
        holder.likesCount.setText(String.valueOf(model.getLikesCount()));

    }

    @Override
    public int getItemCount() {
        return eventsModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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
    }

    private void updateLikeCount(String eventId, boolean newState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("posts").document(eventId);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);
            int newLikesCount = snapshot.getLong("likesCount").intValue() + (newState ? 1 : -1);
            transaction.update(eventRef, "likesCount", newLikesCount);
            return null;
        }).addOnSuccessListener(aVoid -> {
            // Transaction success
        }).addOnFailureListener(e -> {
            // Transaction failure
            Log.e("Firestore", "Transaction failed: " + e.getMessage());
        });
    }
}
