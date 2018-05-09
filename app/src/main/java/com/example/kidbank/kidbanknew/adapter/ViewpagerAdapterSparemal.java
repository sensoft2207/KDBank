package com.example.kidbank.kidbanknew.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.kidbank.kidbanknew.fragment.PageFragment;
import com.example.kidbank.kidbanknew.fragment.PageFragmentSparemal;

import java.util.List;

/**
 * Created by vishal on 22/3/18.
 */

public class ViewpagerAdapterSparemal extends FragmentStatePagerAdapter {

    private List<Integer> images;

    public ViewpagerAdapterSparemal(FragmentManager fm, List<Integer> imagesList) {
        super(fm);
        this.images = imagesList;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragmentSparemal.getInstance(images.get(position));
    }

    @Override
    public int getCount() {
        return images.size();
    }
}
