package creativeeconomy.beacon.com.creativeeconomy_app.main;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import creativeeconomy.beacon.com.creativeeconomy_app.R;

public class MainTab1 extends android.support.v4.app.Fragment {

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_tab_1, container, false);
    }
}