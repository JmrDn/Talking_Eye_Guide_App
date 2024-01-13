package com.example.talking_eye_guide_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CreateAccount extends AppCompatActivity {

    TextView loginHereBtn;
    FirebaseAuth firebaseAuth;
    AppCompatButton createAccBtn;
    EditText email, fullName, userName, password, confirmPassword;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;
    boolean passwordVisible, confirmPasswordVisible;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        loginHereBtn = findViewById(R.id.loginHere_Textview);
        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email_Edittext);
        fullName = findViewById(R.id.fullName_Edittext);
        userName = findViewById(R.id.userName_Edittext);
        password = findViewById(R.id.password_Edittext);
        confirmPassword = findViewById(R.id.confirmPassword_Edittext);
        progressBar = findViewById(R.id.progressbar);
        firebaseFirestore = FirebaseFirestore.getInstance();
        createAccBtn = findViewById(R.id.createAcc_Button);

        progressBar.setVisibility(View.GONE);
        passwordHideMethod();


        loginHereBtn.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), Login.class));
        });

        createAccBtn.setOnClickListener(v->{
            progressBar.setVisibility(View.VISIBLE);
            createAccBtn.setVisibility(View.GONE);
            String emailString = email.getText().toString();
            String fullNameString = fullName.getText().toString();
            String userNameString = userName.getText().toString();
            String passwordString = password.getText().toString();
            String confirmPasswordString = confirmPassword.getText().toString();

            if (emailString.isEmpty()){
                email.setError("Enter email");
                progressBar.setVisibility(View.GONE);
                createAccBtn.setVisibility(View.VISIBLE);
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()){
                email.setError("Enter valid email");
                progressBar.setVisibility(View.GONE);
                createAccBtn.setVisibility(View.VISIBLE);
            }
            else if (fullNameString.isEmpty()){
                fullName.setError("Enter full name");
                progressBar.setVisibility(View.GONE);
                createAccBtn.setVisibility(View.VISIBLE);
            }
            else if (userNameString.isEmpty()){
                userName.setError("Enter username");
                progressBar.setVisibility(View.GONE);
                createAccBtn.setVisibility(View.VISIBLE);
            }
            else if (passwordString.isEmpty()){
                password.setError("Enter password");
                progressBar.setVisibility(View.GONE);
                createAccBtn.setVisibility(View.VISIBLE);
            }
            else if (passwordString.length() < 8){
                password.setError("Your password must be at least 8 characters long");
                progressBar.setVisibility(View.GONE);
                createAccBtn.setVisibility(View.VISIBLE);

            }
            else if (confirmPasswordString.isEmpty()){
                confirmPassword.setError("Enter password");
                progressBar.setVisibility(View.GONE);
                createAccBtn.setVisibility(View.VISIBLE);
            }
            else if (!confirmPasswordString.equals(passwordString)){
                password.setError("Password not match");
                confirmPassword.setError("Password not match");
                progressBar.setVisibility(View.GONE);
                createAccBtn.setVisibility(View.VISIBLE);
            }
            else {
                progressBar.setVisibility(View.VISIBLE);
                createAccBtn.setVisibility(View.GONE);
                createAccount(emailString, confirmPasswordString, fullNameString, userNameString);
            }
        });


    }

    @SuppressLint("ClickableViewAccessibility")
    private void passwordHideMethod() {
        password.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                final int Right1 = 2;

                if (motionEvent.getAction()== MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX()>= password.getRight()-password.getCompoundDrawables()[Right1].getBounds().width()){
                        int selection = password.getSelectionEnd();
                        if (passwordVisible){
                            //set drawable image here
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_off_24, 0);
                            // for hide password
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }
                        else {

                            //set drawable image here
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_24, 0);
                            // for show password
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;

                        }
                        password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        confirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int Right1 = 2;

                if (motionEvent.getAction()== MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX()>= confirmPassword.getRight()-confirmPassword.getCompoundDrawables()[Right1].getBounds().width()){
                        int selection = confirmPassword.getSelectionEnd();
                        if (confirmPasswordVisible){
                            //set drawable image here
                            confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_off_24, 0);
                            // for hide password
                            confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            confirmPasswordVisible = false;
                        }
                        else {

                            //set drawable image here
                            confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_24, 0);
                            // for show password
                            confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            confirmPasswordVisible = true;

                        }
                        confirmPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void createAccount(String email, String confirmPassword, String fullName, String userName) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,confirmPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.VISIBLE);
                            createAccBtn.setVisibility(View.GONE);


                           saveUserCredentials(email,fullName,userName);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Failed to create account", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            createAccBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    private void saveUserCredentials(String email, String fullName, String userName) {

        String userId = FirebaseAuth.getInstance().getUid();

       if (userId != null){

           HashMap<String, Object> userDetails = new HashMap<>();
           userDetails.put("email", email);
           userDetails.put("fullName", fullName);
           userDetails.put("userName", userName);

           FirebaseFirestore.getInstance().collection("Users").document(userId)
                   .set(userDetails)
                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                               Log.d("TAG", "User credentials saved");
                               firebaseAuth.signOut();
                               Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_LONG).show();
                               startActivity(new Intent(getApplicationContext(), Login.class));
                           }
                           else
                               Log.d("TAG", task.getException().getMessage().toString());
                       }
                   });


       }
    }
}