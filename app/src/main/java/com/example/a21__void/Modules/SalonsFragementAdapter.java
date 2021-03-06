package com.example.a21__void.Modules;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.CardView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASANDA on 2018/07/10.
 * for Pandaphic
 */
public class SalonsFragementAdapter extends FragmentStatePagerAdapter {
    private List<SalonsPreviewFragment>  fragments;

    public SalonsFragementAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void Add(SalonsPreviewFragment fragment){
        this.fragments.add(fragment);
        this.notifyDataSetChanged();
    }

    public int get(LatLng position) {
        for(int pos = 0; pos < this.fragments.size();pos++){
            if(fragments.get(pos).getSalonObject().getPosition().equals(position))
                return pos;
        }
        return -1;
    }

    public void clear(){
        this.fragments.clear();
    }

    public CardView getCardViewAt(int currentItem) {

            return (currentItem >= 0 && currentItem < this.getCount()) ? this.fragments.get(currentItem).getCard() : null;

    }
}
