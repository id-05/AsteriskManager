package com.asteriskmanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class AsteriskServerItemAdapter extends FragmentPagerAdapter {
    public AsteriskServerItemAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new DashboardFragment();
            case 0:
                Bundle bundle = new Bundle();
                //bundle.putInt("serverid", server.id);
                //SeachFragment frag = new SeachFragment();
                //frag.setArguments(bundle);
                return new CliFragment();
//            case 1:
//                return new PatientsFragment();
            case 2:
                return new ChannelFragment();
//            case 3:
//                return new SeriesFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
