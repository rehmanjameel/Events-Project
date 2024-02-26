package pk.cust.events.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

import pk.cust.events.R;
import pk.cust.events.model.EventsModel;
import pk.cust.events.model.FriendsModel;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    Context context;
    ArrayList<FriendsModel> friendsList;
    ArrayList<FriendsModel> filteredList;

    public FriendsAdapter(Context context, ArrayList<FriendsModel> friendsList) {
        this.context = context;
        this.friendsList = friendsList;
        this.filteredList = new ArrayList<>(friendsList);
    }

    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_layout, parent, false);
        return new FriendsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
        FriendsModel model = filteredList.get(position);

        Glide.with(context)
                .load(model.getFriendImage())
                .error(R.drawable.baseline_image_24)
                .placeholder(R.drawable.baseline_image_24)
                .into(holder.friendImage);

        holder.friendName.setText(model.getFriendName());
        holder.friendDomain.setText(model.getFriendDomain());
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filterFriends(String query) {
        filteredList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(friendsList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (FriendsModel friendsModel: friendsList) {
                if (friendsModel.getFriendName().toLowerCase().contains(lowerCaseQuery) ||
                friendsModel.getFriendDomain().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(friendsModel);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView friendImage;
        TextView friendName, friendDomain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friendImage = itemView.findViewById(R.id.friendImage);
            friendName = itemView.findViewById(R.id.friendName);
            friendDomain = itemView.findViewById(R.id.friendDomain);
        }
    }
}
