package pk.cust.events.homefragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import java.util.ArrayList;

import pk.cust.events.adapter.NotificationsAdapter;
import pk.cust.events.databinding.FragmentNotificationsBinding;
import pk.cust.events.model.NotificationsModel;
import pk.cust.events.utils.App;

public class NotificationsFragment extends Fragment {

    FragmentNotificationsBinding binding;

    private ArrayList<NotificationsModel> notificationsModelList = new ArrayList<>();
    private NotificationsAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        // Initialize firebase authentication
        mAuth = FirebaseAuth.getInstance();

        // create Firebase FireStore instance
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();


        binding.notificationsRV.setLayoutManager(new LinearLayoutManager(requireActivity()));

        binding.backArrow.setOnClickListener(view -> {
            requireActivity().onBackPressed();
        });

        getNotifications();

        return binding.getRoot();
    }

    private void getNotifications() {
        notificationsModelList.clear();
        String currentStudentDocumentId = App.getString("document_id");
        db.collection("users").document(currentStudentDocumentId).collection("notifications")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot snapshot :
                                task.getResult()) {
                            if (snapshot != null) {
                                String title = snapshot.getString("title");
                                String description = snapshot.getString("body");

                                Log.e("notifications get data", title + " .,." + description);
                                Log.e("notifications get data", title + " .,." + snapshot.getId());

                                notificationsModelList.add(new NotificationsModel(snapshot.getId(), title, description));
                            }

                        }

                        adapter = new NotificationsAdapter(requireActivity(), notificationsModelList);
                        binding.notificationsRV.setAdapter(adapter);
                    }
                });

    }
}