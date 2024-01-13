package com.example.talking_eye_guide_app.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.talking_eye_guide_app.Fragmets.CameraFragment;
import com.example.talking_eye_guide_app.Fragmets.MapFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {


    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
            default:
                return new MapFragment();
            case 1:
                return new CameraFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
