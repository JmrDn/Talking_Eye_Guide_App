package com.example.talking_eye_guide_app.Fragmets;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.talking_eye_guide_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class CameraFragment extends Fragment {
    private ImageView imageView;
    private RelativeLayout noCamera;
    private String previousValue = "";
    private boolean isCameraActive = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        initWidgets(view);
        noCamera.setVisibility(View.VISIBLE);
        setValueForPreviousValue();
        setUpCamera();


        return view;
    }

    private void setValueForPreviousValue() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("image").child("data").child("image");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    String base64String = snapshot.child("photo").getValue().toString();
                    previousValue = base64String;


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initWidgets(View view) {
        imageView = view.findViewById(R.id.imageview);
        noCamera = view.findViewById(R.id.noCamera_RelativeLayout);
    }

    private void setUpCamera() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("image").child("data").child("image");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    String base64String = snapshot.child("photo").getValue().toString();
                    if (!base64String.isEmpty()){
                        byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);

                        //Initialize bitmap
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                                bytes.length);

                        if(!base64String.equals(previousValue))
                            imageView.setImageBitmap(bitmap);



                        setImageVisibility(base64String, 60000);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setImageVisibility(String base64String, int i) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!previousValue.equals(base64String)){
                    noCamera.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);

                    previousValue = base64String;
                    setUpCamera();
                }
                else{
                    noCamera.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    isCameraActive = false;
                    setUpCamera();
                }
            }
        },10000);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}