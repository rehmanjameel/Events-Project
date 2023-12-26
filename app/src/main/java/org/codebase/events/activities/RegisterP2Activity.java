package org.codebase.events.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import org.codebase.events.databinding.ActivityRegisterP2Binding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import org.codebase.events.utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterP2Activity extends AppCompatActivity {

    ActivityRegisterP2Binding binding;

    List<String> domainsItems = new ArrayList<>();
    List<String> interestsItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterP2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // control the device back button click
        controlBackPress();

        setUpDomains();
        setUpInterest();

        binding.signupId.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        binding.backArrow.setOnClickListener(view -> {
            startActivity(new Intent(this, RegistrationActivity.class));
            finish();
        });
    }

    private void setUpDomains() {
        // add domains in array list to show dropdown
        domainsItems.add("Science and Technology");
        domainsItems.add("Medicine and Healthcare");
        domainsItems.add("Business and Entrepreneurship");
        domainsItems.add("Education");
        domainsItems.add("Arts and Entertainment");
        domainsItems.add("Social Sciences");
        domainsItems.add("Communication and Media");
        domainsItems.add("Environmental and Sustainability");
        domainsItems.add("Sports and Recreation");
        domainsItems.add("Law and Justice");

        ArrayAdapter<String> domainAdapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, domainsItems);

        binding.domainsDropDown.setAdapter(domainAdapter);
    }

    private void setUpInterest() {
        // add interest in array list to show dropdown
        interestsItems.add("Science and Technology");
        interestsItems.add("Medicine and Healthcare");
        interestsItems.add("Business and Entrepreneurship");
        interestsItems.add("Education");
        interestsItems.add("Arts and Entertainment");
        interestsItems.add("Social Sciences");
        interestsItems.add("Communication and Media");
        interestsItems.add("Environmental and Sustainability");
        interestsItems.add("Sports and Recreation");
        interestsItems.add("Law and Justice");

        ArrayAdapter<String> gendersAdapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, interestsItems);

        binding.interestDropDown.setAdapter(gendersAdapter);
    }

    private void checkValidation() {

        String domainsDropDown = Objects.requireNonNull(binding.domainsDropDown.getText()).toString();
        String interestDropDown = Objects.requireNonNull(binding.interestDropDown.getText()).toString();
        String email = Objects.requireNonNull(binding.emailTIET.getText()).toString();
        String dob = Objects.requireNonNull(binding.dobTIET.getText()).toString();
        String password = Objects.requireNonNull(binding.passwordTIET.getText()).toString();


        if (domainsDropDown.equals("Select") && interestDropDown.equals("Select") && email.isEmpty() && password.isEmpty()
        && dob.isEmpty()) {
            binding.domainsDropDown.setError("Field required");
            binding.interestDropDown.setError("Field required");
            binding.dobTIET.setError("Field required");
            binding.emailTIET.setError("Field required");
            binding.passwordTIET.setError("Field required");


        } else if (password.isEmpty() || !Validator.isValidPasswordFormat(password)) {
            binding.passwordTIET.setError("Field required or password is weak");

        } else if (interestDropDown.isEmpty() || interestDropDown.equals("Select")) {
            binding.interestDropDown.setError("Field required");

        }
        else if (domainsDropDown.isEmpty() || domainsDropDown.equals("Select")) {
            binding.domainsDropDown.setError("Field required");

        }
        else if (dob.isEmpty()) {
            binding.dobTIET.setError("Field required");

        } else if (email.isEmpty() || !Validator.isValidMail(email)) {
            binding.emailTIET.setError("Field required or Invalid email");

        } else {
            Log.e("is it in the ", "else part of validation");
            registerUser(interestDropDown, domainsDropDown, dob, email, password);
            binding.progressbar.setVisibility(View.VISIBLE);
        }
    }

    private void registerUser(String interestDropDown, String domainsDropDown, String dob, String email, String password) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void controlBackPress() {
       getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPress();
            }
        });
    }

    private void backPress() {
        startActivity(new Intent(this, RegistrationActivity.class));
        finish();
    }

}