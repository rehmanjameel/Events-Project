package pk.cust.events.homefragments;

import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import pk.cust.events.R;
import pk.cust.events.databinding.FragmentEventDetailBinding;
import pk.cust.events.utils.App;

public class EventDetailFragment extends Fragment {

    private FragmentEventDetailBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference eventLikeRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventDetailBinding.inflate(inflater, container, false);




        return binding.getRoot();
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
}