package pk.cust.events.homefragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import pk.cust.events.R;
import pk.cust.events.databinding.FragmentUserDetailBinding;


public class UserDetailFragment extends Fragment {

   private FragmentUserDetailBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserDetailBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            binding.personName.setText(getArguments().getString("user_name"));
            binding.friendDomain.setText(getArguments().getString("user_domain"));
            binding.friendInterest.setText(getArguments().getString("user_interest"));
            binding.friendEmail.setText(getArguments().getString("user_email"));

            Glide.with(requireContext())
                    .load(getArguments().getString("user_image"))
                    .error(R.drawable.baseline_broken_image_24)
                    .placeholder(R.drawable.profile)
                    .into(binding.personImage);
        }

        binding.backArrow.setOnClickListener(view -> {
            Navigation.findNavController(view).popBackStack(R.id.action_userDetailFragment_to_friendsFragment, false);
            Navigation.findNavController(view).popBackStack();
        });

        return binding.getRoot();

    }
}