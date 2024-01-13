package com.example.talking_eye_guide_app.BottomNav;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;


import com.example.talking_eye_guide_app.Login;
import com.example.talking_eye_guide_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    private AppCompatButton logoutBtn;
    private TextView nameTV, emailTV;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initWidgets(view);
        setUpUserInfo();

        logoutBtn.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), Login.class));
        });
        return view;
    }

    private void setUpUserInfo() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null){
            FirebaseFirestore.getInstance().collection("Users").document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()){
                                    String name = documentSnapshot.getString("fullName");
                                    String email = documentSnapshot.getString("email");

                                    nameTV.setText(name);
                                    emailTV.setText(email);
                                }
                            }
                        }
                    });
        }
    }

    private void initWidgets(View view) {
        logoutBtn = view.findViewById(R.id.logout_Btn);

        nameTV = view.findViewById(R.id.name_Textview);
        emailTV = view.findViewById(R.id.email_Textview);
    }
}