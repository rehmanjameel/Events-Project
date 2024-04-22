package pk.cust.events.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import pk.cust.events.R;
import pk.cust.events.model.NotificationsModel;

public class ChatInvitationAdapter extends RecyclerView.Adapter<ChatInvitationAdapter.ViewHolder> {

    Context context;
    ArrayList<NotificationsModel> modelNotification;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onAcceptClick(String chatRoomId, String invitationId, String postId);
        void onRejectClick(String chatRoomId, String invitationId, String postId);
    }

    public ChatInvitationAdapter(Context context, ArrayList<NotificationsModel> modelNotification) {
        this.context = context;
        this.modelNotification = modelNotification;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public ChatInvitationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_invitation_layout, parent, false);
        return new ChatInvitationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatInvitationAdapter.ViewHolder holder, int position) {
        NotificationsModel model = modelNotification.get(position);
        holder.name.setText(model.getName());
        holder.description.setText(model.getDescription());

        Log.e("notifications get data012", model.getName() + " .,." + model.getDescription());


        holder.accept.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onAcceptClick(model.getChatId(), model.getId(), model.getPostId());
            }
        });

        holder.reject.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onRejectClick(model.getChatId(), model.getId(), model.getPostId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelNotification.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;
        MaterialButton accept, reject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.notificationSender);
            description = itemView.findViewById(R.id.notificationDescription);
            accept = itemView.findViewById(R.id.acceptButton);
            reject = itemView.findViewById(R.id.rejectButton);
        }



    }
}
