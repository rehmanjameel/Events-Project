package pk.cust.events.homefragments;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import pk.cust.events.adapter.FriendsAdapter;
import pk.cust.events.databinding.FragmentFriendsBinding;
import pk.cust.events.model.FriendsModel;

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

        setupUI(container);

        GridLayoutManager layoutManager = new GridLayoutManager(requireActivity(), 2);
        binding.friendsRV.setLayoutManager(layoutManager);

        getDataFromFireStore();

        binding.searchEditTextId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String query = editable.toString().trim();
                friendsAdapter.filterFriends(query);
            }
        });
        binding.progressbar.setVisibility(View.VISIBLE);

        return binding.getRoot();
    }

    public void hideSoftKeyboard() {
        //Hides the SoftKeyboard
        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {
        // Set up touch listener to remove focus from EditText and TextInputEditText.
        if (!(view instanceof EditText) && !(view instanceof TextInputEditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    // Remove focus from EditText and TextInputEditText
                    if (getActivity() != null && (getActivity().getCurrentFocus() instanceof EditText ||
                            getActivity().getCurrentFocus() instanceof TextInputEditText)) {
                        hideSoftKeyboard();
                        getActivity().getCurrentFocus().clearFocus();
                    }
                    return false;
                }
            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void getDataFromFireStore() {
        friendsModelArrayList.clear();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("TAG", document.getId() + " => " + document.getData() + document.get("email"));

                                binding.progressbar.setVisibility(View.GONE);

                                friendsModelArrayList.add(new FriendsModel(document.getId(),
                                        document.get("user_image").toString(),
                                        document.get("user_name").toString(), document.get("domain").toString(),
                                        document.getString("interest").toString(),
                                        document.getString("email").toString(),
                                        document.getString("phone_no").toString(),
                                        document.getString("dob").toString(),
                                        document.getString("address").toString(),
                                        document.getString("gender").toString()));
                            }

                            if (friendsModelArrayList.isEmpty()) {
                                binding.friendsRV.setVisibility(View.GONE);
                                binding.noDataText.setVisibility(View.VISIBLE);
                                binding.progressbar.setVisibility(View.GONE);

                            } else {
                                binding.friendsRV.setVisibility(View.VISIBLE);
                                binding.noDataText.setVisibility(View.GONE);
                                binding.progressbar.setVisibility(View.GONE);

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
