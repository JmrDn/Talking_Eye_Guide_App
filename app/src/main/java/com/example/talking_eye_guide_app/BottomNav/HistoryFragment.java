package com.example.talking_eye_guide_app.BottomNav;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talking_eye_guide_app.Adapter.HistoryAdapter;
import com.example.talking_eye_guide_app.Model.HistoryModel;
import com.example.talking_eye_guide_app.R;
import com.example.talking_eye_guide_app.Utils.DateAndTimeFormatUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private ArrayList<HistoryModel> list;

    private Toolbar toolbar;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initWidgets(view);
        setUpRecyclerview();
        setUpToolbar();


        return view;
    }

    private void setUpToolbar() {

    }

    private void initWidgets(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
        toolbar = view.findViewById(R.id.toolbar);
    }

    private void setUpRecyclerview() {
        if (getContext()!= null){
            list = new ArrayList<>();
            adapter = new HistoryAdapter(getContext(), list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

           String userId = FirebaseAuth.getInstance().getUid();
           if (userId != null){
               CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users").document(userId)
                       .collection("daily_history");

               if (collectionReference != null){
                   collectionReference.get()
                           .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                   if(task.isSuccessful()){
                                       QuerySnapshot querySnapshot = task.getResult();

                                       if (!querySnapshot.isEmpty() && querySnapshot != null){
                                            list.clear();

                                            for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                                if (documentSnapshot.exists()){
                                                    String date = documentSnapshot.getString("date");
                                                    String time = documentSnapshot.getString("time");
                                                    double latitude = (double) documentSnapshot.get("latitude");
                                                    double longitude = (double) documentSnapshot.get("longitude");
                                                    String location = getCompleteAddressString(latitude, longitude);
                                                    String dateFormatted = DateAndTimeFormatUtils.wordDateFormat(date);

                                                    list.add(new HistoryModel(location, time, dateFormatted));

                                                    if (adapter!= null)
                                                        adapter.notifyDataSetChanged();

                                                }
                                            }
                                       }
                                   }
                                   else{
                                       Log.d("TAG", task.getException().getMessage());
                                   }
                               }
                           });
               }
           }
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }
}