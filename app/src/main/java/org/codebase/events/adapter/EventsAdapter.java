package org.codebase.events.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.codebase.events.R;
import org.codebase.events.model.EventsModel;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    Activity activity;
    ArrayList<EventsModel> eventsModels;

    public EventsAdapter(Activity activity, ArrayList<EventsModel> eventsModels) {
        this.activity = activity;
        this.eventsModels = eventsModels;
    }

    @NonNull
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.ViewHolder holder, int position) {
        EventsModel model = eventsModels.get(position);

        Glide.with(activity)
                .load(model.getEventImage())
                .error(R.drawable.baseline_account_circle_24)
                .placeholder(R.drawable.baseline_account_circle_24)
                .into(holder.eventImage);

        Glide.with(activity)
                .load(model.getUserImage())
                .error(R.drawable.baseline_image_24)
                .placeholder(R.drawable.baseline_image_24)
                .into(holder.personImage);

        holder.userName.setText(model.getName());

        holder.eventTopic.setText(model.getEventTopic());

    }

    @Override
    public int getItemCount() {
        return eventsModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName, eventTopic;
        ImageView personImage, eventImage, chatImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.personName);
            personImage = itemView.findViewById(R.id.personImage);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventTopic = itemView.findViewById(R.id.eventTopic);
            chatImage = itemView.findViewById(R.id.discussionIcon);
        }
    }
}
