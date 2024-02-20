package pk.cust.events.homefragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import pk.cust.events.activities.LoginActivity;
import pk.cust.events.activities.MainActivity;
import pk.cust.events.adapter.EventsAdapter;
import pk.cust.events.adapter.FriendsAdapter;
import pk.cust.events.databinding.FragmentFriendsBinding;
import pk.cust.events.model.EventsModel;
import pk.cust.events.model.FriendsModel;
import pk.cust.events.utils.App;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    FragmentFriendsBinding binding;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    FriendsAdapter friendsAdapter;
    ArrayList<FriendsModel> friendsModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFriendsBinding.inflate(inflater, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

//        friendsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Ahmad"));
//        friendsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Maha"));
//        friendsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Usama"));
//        friendsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Wahid"));
//        friendsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Maham"));
//        friendsModelArrayList.add(new EventsModel(0, R.drawable.profile, "Asma"));

        GridLayoutManager layoutManager = new GridLayoutManager(requireActivity(), 2);
        binding.friendsRV.setLayoutManager(layoutManager);

        getDataFromFireStore();
        return binding.getRoot();
    }

    private void getDataFromFireStore() {
        db.collection("users")
                .whereEqualTo("domain", App.getString("domain"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("TAG", document.getId() + " => " + document.getData() + document.get("email"));

//                                App.saveString("document_id", document.getId());
//                                Log.e("email", document.get("user_name") + ",.," + document.get("email"));
//                                App.saveString("uer_name", String.valueOf(document.get("user_name")));
//                                App.saveString("gender", String.valueOf(document.get("gender")));
//                                App.saveString("phone_no", String.valueOf(document.get("phone_no")));
//                                App.saveString("address", String.valueOf(document.get("address")));
//                                App.saveString("user_image", String.valueOf(document.get("user_image")));
//                                App.saveString("dob", String.valueOf(document.get("dob")));
//                                App.saveString("domain", String.valueOf(document.get("domain")));
//                                App.saveString("interest", String.valueOf(document.get("interest")));
//                                App.saveString("email", String.valueOf(document.get("email")));

                                binding.progressbar.setVisibility(View.GONE);

                                friendsModelArrayList.add(new FriendsModel(document.getId(),
                                        document.get("user_image").toString(),
                                        document.get("user_name").toString()));
//                                if (userName.equals(document.get("email")) && password.equals(document.get("password"))) {
//                                }
                            }
                            if (friendsModelArrayList.size() == 0) {
                                binding.friendsRV.setVisibility(View.GONE);
                                binding.noDataText.setVisibility(View.VISIBLE);
                            } else {
                                binding.friendsRV.setVisibility(View.VISIBLE);
                                binding.noDataText.setVisibility(View.GONE);

                                friendsAdapter = new FriendsAdapter(requireActivity(), friendsModelArrayList);
                                binding.friendsRV.setAdapter(friendsAdapter);
                            }
                        } else {
                            binding.progressbar.setVisibility(View.GONE);
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
