package com.example.talking_eye_guide_app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.talking_eye_guide_app.BottomNav.HistoryFragment;
import com.example.talking_eye_guide_app.BottomNav.HomeFragment;
import com.example.talking_eye_guide_app.BottomNav.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private final String defaultNav = "Home";
    Ringtone ringtone;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();
        setUpAlertMessage();
        createNotification();

        Fragment selectedFragment = null;

        if (defaultNav.equals("Home")){
            selectedFragment = new HomeFragment();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if(itemId == R.id.Home){
                    selectedFragment = new HomeFragment();
                }
                else if (itemId == R.id.History){
                    selectedFragment = new HistoryFragment();
                }
                else if (itemId == R.id.Profile){
                    selectedFragment = new ProfileFragment();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
                return true;
            }
        });

        
       
    }

    private void createNotification() {
        //If version is greater than version oreo, notification proceeds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("Alert Message",
                    "Alert Message", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100,1000,200,340});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager =  getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void playRingtone(){
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, soundUri);
        ringtone.play();
    }

    private void stopRingtone(){
        if (ringtone != null && ringtone.isPlaying()){
            ringtone.stop();
        }
    }

    private void getNotify(){
        Context context = this;
        if (context != null){

            String alertMessage = "Emergency Button Activated";
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Alert Message");
            builder.setContentTitle("Alert!");
            builder.setSmallIcon(R.drawable.applogo);
            builder.setAutoCancel(true);
            builder.setContentText(alertMessage);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setVibrate(new long[] {100,1000,200,340});
            builder.setContentIntent(pendingIntent);


            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.notify(0, builder.build());
        }
    }

    private void setUpAlertMessage() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    boolean alert = (boolean) snapshot.child("alarm").getValue();

                    if (alert){
                        playRingtone();
                        getNotify();
                        showAlertDialog();
                    }
                    else{
                        stopRingtone();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "Failed to retrieve data");
            }
        });
    }

    private void showAlertDialog() {
        Dialog alertDialog = new Dialog(this);

        alertDialog.setCancelable(false);
        alertDialog.setContentView(R.layout.alert_dialog);
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.white_bg_with_radius));
        alertDialog.show();

        AppCompatButton okBtn = alertDialog.findViewById(R.id.ok_Button);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("alarm");

                db.setValue(false);
                stopRingtone();
                alertDialog.dismiss();

            }
        });
    }

    private void initWidgets() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}