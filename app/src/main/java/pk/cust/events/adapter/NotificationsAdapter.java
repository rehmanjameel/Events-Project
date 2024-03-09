package pk.cust.events.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pk.cust.events.R;
import pk.cust.events.model.NotificationsModel;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    Context context;
    ArrayList<NotificationsModel> modelNotification;

    public NotificationsAdapter(Context context, ArrayList<NotificationsModel> modelNotification) {
        this.context = context;
        this.modelNotification = modelNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NotificationsModel model = modelNotification.get(position);
        holder.name.setText(model.getName());
        holder.description.setText(model.getDescription());
    }

    @Override
    public int getItemCount() {
        return modelNotification.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.notificationSender);
            description = itemView.findViewById(R.id.notificationDescription);
        }
    }
}
