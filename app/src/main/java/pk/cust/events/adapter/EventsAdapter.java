package pk.cust.events.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;
import pk.cust.events.R;
import pk.cust.events.model.EventsModel;
import pk.cust.events.utils.App;

import java.util.ArrayList;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<EventsModel> eventsList;
    private ArrayList<EventsModel> filteredList;

    private OnItemClickListener mListener;

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(Bundle data);
    }

    // Setter method for the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public EventsAdapter(Context context, ArrayList<EventsModel> eventsList) {
        this.context = context;
        this.eventsList = eventsList;
        this.filteredList = new ArrayList<>(eventsList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventsModel model = filteredList.get(position);

        Glide.with(context)
                .load(model.getEventImage())
                .error(R.drawable.baseline_image_24)
                .placeholder(R.drawable.baseline_image_24)
                .into(holder.eventImage);

        Glide.with(context)
                .load(model.getPersonImage())
                .error(R.drawable.baseline_image_24)
                .placeholder(R.drawable.baseline_image_24)
                .into(holder.personImage);

        holder.eventTitle.setText(model.getEventTitle());
        holder.eventDomain.setText(model.getEventDomain());
        holder.personName.setText(model.getPersonName());
        Log.e("profile logs", String.valueOf(App.IS_PROFILE));

        if (App.IS_PROFILE) {
            holder.personImage.setVisibility(View.GONE);
        }

        holder.eventCard.setOnClickListener(view -> {
            App.saveBoolean("is_room_space", true);
            Log.e("profile logs", String.valueOf(App.IS_PROFILE));
            App.IS_ROOM_SPACE = true;
            Log.e("roomspace logs", String.valueOf(App.IS_ROOM_SPACE));

            Bundle bundle = getBundle(model);
            if (App.IS_PROFILE) {
                if (mListener != null) {
                    mListener.onItemClick(bundle);
                }
            } else {
                Navigation.findNavController(view).navigate(R.id.action_eventsFragment_to_eventDetailFragment, bundle);
            }
        });
    }

    private static @NonNull Bundle getBundle(EventsModel model) {
        Bundle bundle = new Bundle();
        bundle.putString("user_name", model.getPersonName());
        bundle.putString("user_image", model.getPersonImage());
        bundle.putString("user_id", model.getId());
        bundle.putString("post_image", model.getEventImage());
        bundle.putString("post_id", model.getPostId());
        bundle.putString("post_description", model.getEventTitle());
        bundle.putString("post_domain", model.getEventDomain());
        bundle.putLong("start_date_time", model.getStartDateTime());
        bundle.putString("end_date_time", model.getEndDateTime());
        return bundle;
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {
        filteredList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(eventsList);
        } else {
            for (EventsModel model : eventsList) {
                if (model.getEventDomain().equalsIgnoreCase(query)) {
                    filteredList.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView eventImage;
        CircleImageView personImage;
        TextView eventTitle, eventDomain, personName;
        CardView eventCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventDomain = itemView.findViewById(R.id.eventDomain);
            personImage = itemView.findViewById(R.id.personImage);
            personName = itemView.findViewById(R.id.personName);

            eventCard = itemView.findViewById(R.id.eventCardClick);
        }



    }
}
