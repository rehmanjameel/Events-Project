package pk.cust.events.adapter;

import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import pk.cust.events.R;
import pk.cust.events.model.MessageModel;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<MessageModel> messageList;
    private String myUserId;

    public MessageAdapter(List<MessageModel> messageList, String myUserId) {
        this.messageList = messageList;
        this.myUserId = myUserId;
    }

    public void setMessageList(List<MessageModel> messageList) {
//        this.messageList.clear();
//        this.messageList.addAll(messageList);
//        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel message = messageList.get(position);
        Log.e("bindholder", message.getMessageText());
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textSenderName, textTime;
        LinearLayout layoutMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textSenderName = itemView.findViewById(R.id.text_sender_name);
            textMessage = itemView.findViewById(R.id.text_message);
            textTime = itemView.findViewById(R.id.text_time);
            layoutMessage = itemView.findViewById(R.id.layout_message);
        }

        public void bind(MessageModel message) {
            textMessage.setText(message.getMessageText());
            textTime.setText(formatTime(message.getTimestamp()));
            Log.e("is rv1", "entered here?" + message.getMessageText());

            // If the message is sent by the current user, set sender name as "Me"
            if (message.getSenderId().equals(myUserId)) {
                textSenderName.setVisibility(View.GONE);
                layoutMessage.setBackgroundResource(R.drawable.bg_message_sent);
            } else {
                textSenderName.setText(message.getSenderName());
                textSenderName.setVisibility(View.VISIBLE);
                layoutMessage.setBackgroundResource(R.drawable.bg_message_received);
            }
        }

        private String formatTime(long timestamp) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);

            Calendar today = Calendar.getInstance();
            today.setTimeInMillis(System.currentTimeMillis());

            SimpleDateFormat timeFormat;
            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                    && calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                // Today's message
                timeFormat = new SimpleDateFormat("hh:mma", Locale.getDefault());
                return "Today, " + timeFormat.format(calendar.getTime());
            } else {
                // Other days
                timeFormat = new SimpleDateFormat("EEE, hh:mma", Locale.getDefault());
                return timeFormat.format(calendar.getTime());
            }
        }

    }
}


