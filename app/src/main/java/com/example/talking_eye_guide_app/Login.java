package com.example.talking_eye_guide_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    TextView createAccBtn;
    EditText email, password;
    AppCompatButton loginBtn;
    ProgressBar progressBar;
    boolean passwordVisible;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        createAccBtn = findViewById(R.id.createAccount_Textview);
        firebaseAuth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.login_Button);
        email = findViewById(R.id.email_Edittext);
        password = findViewById(R.id.password_Edittext);
        progressBar = findViewById(R.id.progressbar);

        progressBar.setVisibility(View.GONE);



        passwordHideMethod();

        createAccBtn.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(),CreateAccount.class));
        });

        loginBtn.setOnClickListener(v->{
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
            String emailString = email.getText().toString();
            String passwordString = password.getText().toString();

            if (emailString.isEmpty()){
                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);
                email.setError("Enter email");
            }
            else if (passwordString.isEmpty()){
                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);
                password.setError("Enter password");
            }
            else {
                progressBar.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.GONE);
                login(emailString, passwordString);
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
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.lock_icon,0, R.drawable.baseline_visibility_off_24, 0);
                            // for hide password
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }
                        else {

                            //set drawable image here
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.lock_icon,0, R.drawable.baseline_visibility_24, 0);
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
    }

    private void login(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressBar.setVisibility(View.VISIBLE);
                        loginBtn.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "You have successfully log in", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressBar.setVisibility(View.GONE);
                        loginBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}