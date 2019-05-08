package com.example.franc.mxh.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.franc.mxh.Fragment.Login_User_Fragment;
import com.example.franc.mxh.Fragment.Register_User_Fragment;


public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Fragment[] fragments;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments =new Fragment[]{
                new Login_User_Fragment(),
                new Register_User_Fragment()
        };
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
