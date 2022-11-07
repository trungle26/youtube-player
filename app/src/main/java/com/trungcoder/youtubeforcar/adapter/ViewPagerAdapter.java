package com.trungcoder.youtubeforcar.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.trungcoder.youtubeforcar.fragment.page1.page1;
import com.trungcoder.youtubeforcar.fragment.page2.page2;
import com.trungcoder.youtubeforcar.fragment.page3.page3;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch(position){
            case 1:
                return new page2();
            case 2:
                return new page3();
            default:
                return new page1();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

