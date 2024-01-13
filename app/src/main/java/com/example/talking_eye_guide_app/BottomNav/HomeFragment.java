package com.example.talking_eye_guide_app.BottomNav;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.talking_eye_guide_app.Adapter.MyViewPagerAdapter;
import com.example.talking_eye_guide_app.Fragmets.CameraFragment;
import com.example.talking_eye_guide_app.Fragmets.MapFragment;
import com.example.talking_eye_guide_app.R;
import com.example.talking_eye_guide_app.Utils.DateAndTimeFormatUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;


public class HomeFragment extends Fragment {

    private RelativeLayout locationLayout, cameraLayout;
    private TextView locationTV, cameraTV;
    private double latitude = 0;
    private double longitude = 0;
    private String previousLatitude ="";
    private String previousLongitude = "";
    private String date = "";
    private String time = "";
    private ImageButton audioBtn;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        iniWidgets(view);
        setUpDefaultTabLayout();
        setUpTabLayout();
        setTodayLocation();
        getDataAverage();

        audioBtn.setOnClickListener(v->{

            String phoneNumber = "09608332131";

            // Create the intent with the action ACTION_DIAL
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);

            // Set the data (phone number) for the intent
            dialIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(dialIntent);
            if (dialIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                // Start the dialer activity

            }
        });



        return  view;
    }

    private void getDataAverage() {
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId != null){
            Date currentDateAndTime = new Date();
            String dateForDocumentName = DateAndTimeFormatUtils.dateForDocumentName(currentDateAndTime);
            CollectionReference collectionReference =  FirebaseFirestore.getInstance().collection("Users")
                    .document(userId).collection("daily_history")
                    .document(dateForDocumentName).collection(dateForDocumentName);

            if(collectionReference != null){

                Query query = collectionReference.orderBy("date_and_time", Query.Direction.DESCENDING);

                query.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if(task.isSuccessful()){

                                    QuerySnapshot queryDocumentSnapshots = task.getResult();

                                    if (!queryDocumentSnapshots.isEmpty()){

                                        if(queryDocumentSnapshots.getDocuments().get(0).exists()){
                                            DocumentSnapshot latestDocument = queryDocumentSnapshots.getDocuments().get(0);

                                            latitude = (double)latestDocument.get("latitude");
                                            longitude = (double) latestDocument.get("longitude");
                                            date = latestDocument.getString("date");
                                            time = latestDocument.getString("time");

                                        }
                                        else{
                                            latitude = 0;
                                            longitude = 0;
                                        }

                                        setDataAverage(date, time, latitude, longitude, userId);

                                    }


                                }

                            }
                        });
            }


        }

    }

    private void setDataAverage(String date, String time, double latitude, double longitude, String userId) {
        Date currentDate = new Date();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users")
                .document(userId).collection("daily_history")
                .document(DateAndTimeFormatUtils.dateForDocumentName(currentDate));

        HashMap<String, Object> averageDocument = new HashMap<>();
        averageDocument.put("date", date);
        averageDocument.put("time", time);
        averageDocument.put("latitude", latitude);
        averageDocument.put("longitude", longitude);
        averageDocument.put("dateId", DateAndTimeFormatUtils.dateForDocumentName(currentDate));


        if(documentReference != null){
            documentReference.set(averageDocument)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG", "Average set");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "Average set failed");
                        }
                    });
        }
    }

    private void setTodayLocation() {
        String userId = FirebaseAuth.getInstance().getUid();

        Date currentDateAndTime = new Date();
        String timeStamp = DateAndTimeFormatUtils.dateAndTime(currentDateAndTime);
        String date = DateAndTimeFormatUtils.dateFormat(currentDateAndTime);
        String time = DateAndTimeFormatUtils.timeFormat(currentDateAndTime);
        String dateForDocumentName = DateAndTimeFormatUtils.dateForDocumentName(currentDateAndTime);

        CollectionReference collectionReference =  FirebaseFirestore.getInstance().collection("Users")
                .document(userId).collection("daily_history")
                .document(dateForDocumentName).collection(dateForDocumentName);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("84953").child("location");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (snapshot.exists()){
                    String latitudeString = snapshot.child("latitude").getValue().toString();
                    String longitudeString = snapshot.child("longitude").getValue().toString();

                    latitude = Double.parseDouble(latitudeString);
                    longitude = Double.parseDouble(longitudeString);

                    HashMap<String, Object> todayLocation = new HashMap<>();

                    if(!latitudeString.equals(previousLatitude) &&
                    !longitudeString.equals(previousLongitude)){
                        if (latitudeString != "0" && longitudeString != "0"){
                            todayLocation.put("date_and_time", timeStamp);
                            todayLocation.put("date", date);
                            todayLocation.put("time", time);
                            todayLocation.put("latitude", latitude);
                            todayLocation.put("longitude", longitude);
                        }

                    }


                    if (todayLocation != null){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                collectionReference.add(todayLocation)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }
                        },10000);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "Database error: " + error.getMessage());
            }
        });
    }

    private void setUpDefaultTabLayout() {
        locationLayout.setBackgroundResource(R.drawable.selected_tab);
        locationTV.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

        cameraLayout.setBackgroundResource(R.drawable.unselected_tab);
        cameraTV.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));

        Fragment fragment = new MapFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment).commit();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpTabLayout() {

       if (getContext()!= null){
           locationLayout.setOnTouchListener(new View.OnTouchListener() {

               @Override
               public boolean onTouch(View v, MotionEvent event) {
                   locationLayout.setBackgroundResource(R.drawable.selected_tab);
                   locationTV.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                   cameraLayout.setBackgroundResource(R.drawable.unselected_tab);
                   cameraTV.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));

                   Fragment fragment = new MapFragment();
                   requireActivity().getSupportFragmentManager().beginTransaction()
                           .replace(R.id.frameLayout, fragment).commit();

                   return false;
               }
           });

           cameraLayout.setOnTouchListener(new View.OnTouchListener() {

               @Override
               public boolean onTouch(View v, MotionEvent event) {
                   cameraLayout.setBackgroundResource(R.drawable.selected_tab);
                   cameraTV.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                   locationLayout.setBackgroundResource(R.drawable.unselected_tab);
                   locationTV.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));

                   Fragment fragment = new CameraFragment();
                   requireActivity().getSupportFragmentManager().beginTransaction()
                           .replace(R.id.frameLayout, fragment).commit();

                   return false;
               }
           });
       }
    }

    private void iniWidgets(View view) {


        locationLayout = view.findViewById(R.id.locationLayout);
        cameraLayout = view.findViewById(R.id.cameraLayout);

        locationTV = view.findViewById(R.id.locationTextview);
        cameraTV = view.findViewById(R.id.camera_Textview);

        audioBtn = view.findViewById(R.id.audio_Button);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}