package com.example.talking_eye_guide_app.Fragmets;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import com.example.talking_eye_guide_app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap gMap;
    private FusedLocationProviderClient providerClient;
    Marker marker;
    private RelativeLayout noLocationLayout;
    private ImageButton focusUserBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initWidgets(view);
        noLocationLayout.setVisibility(View.GONE);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        supportMapFragment.getMapAsync(this);

        mapInitialize();

        return view;
    }

    private void initWidgets(View view) {
        noLocationLayout = view.findViewById(R.id.noLocation_RelativeLayout);
        focusUserBtn = view.findViewById(R.id.focusUser_Button);
    }

    private void mapInitialize() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(16);
        locationRequest.setFastestInterval(3000);


        providerClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    private  void refresh (int milliseconds){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               onMapReady(gMap);
            }
        }, milliseconds);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        gMap = googleMap;


        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if (ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        gMap.setMyLocationEnabled(true);
                        gMap.getUiSettings().setCompassEnabled(true);
                        providerClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
//                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("84953").child("location");
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()){
                                            String latitudeString = snapshot.child("latitude").getValue().toString();
                                            String longitudeString = snapshot.child("longitude").getValue().toString();

                                            if (latitudeString.equals("0") && longitudeString.equals("0")){
                                                noLocationLayout.setVisibility(View.VISIBLE);

                                                focusUserBtn.setVisibility(View.GONE);
                                            }
                                            else {

                                                if(getContext()!= null){
                                                    noLocationLayout.setVisibility(View.GONE);
                                                    focusUserBtn.setVisibility(View.VISIBLE);
                                                    double latitude = Double.parseDouble(latitudeString);
                                                    double longitude = Double.parseDouble(longitudeString);

                                                    if (marker != null){
                                                        marker.remove();
                                                    }

                                                    LatLng latLng1 = new LatLng(latitude, longitude);
                                                    MarkerOptions markerOptions = new MarkerOptions();
                                                    markerOptions.title("Guide Bot");
                                                    markerOptions.position(latLng1).icon(setIcon((Activity) getContext(), R.drawable.mapmarker));

                                                    marker = gMap.addMarker(markerOptions);
                                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng1, 17);



                                                    //Focus user/blind location
                                                    focusUserBtn.setOnClickListener(v->{
                                                        gMap.animateCamera(cameraUpdate);
                                                    });

                                                    if (!gMap.getUiSettings().isZoomControlsEnabled()){
                                                        gMap.animateCamera(cameraUpdate);
                                                    }

                                                }

                                            }


                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                        Log.d("Database", "Failed to retrieve data");

                                    }
                                });


                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getContext(), "Permission " + permissionDeniedResponse.getPermissionName() + " was denied", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();


    }

    public BitmapDescriptor setIcon (Activity context, int drawableId){
        Drawable drawable = ActivityCompat.getDrawable(context, drawableId);
        drawable.setBounds(0,0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }
    private void getDirections(String from, String to){
        try {
            Uri uri = Uri.parse("https://www.google.com/maps/dir/" + from + "/" + to);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
        catch (ActivityNotFoundException e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}