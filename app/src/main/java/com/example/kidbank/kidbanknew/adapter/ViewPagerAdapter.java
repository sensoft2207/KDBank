package com.example.kidbank.kidbanknew.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.kidbank.kidbanknew.Model.KidTaskData;
import com.example.kidbank.kidbanknew.fragment.PageFragment;

import java.util.List;

/**
 * Created by vishal on 7/3/18.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<KidTaskData> images;

    public ViewPagerAdapter(FragmentManager fm, List<KidTaskData> imagesList) {
        super(fm);
        this.images = imagesList;
    }

    @Override
    public Fragment getItem(int position) {

        KidTaskData kd = images.get(position);

        return PageFragment.getInstance(kd.getTaskImage(),kd.getTask_id());
    }

    @Override
    public int getCount() {
        return images.size();
    }
}
