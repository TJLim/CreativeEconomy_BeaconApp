package creativeeconomy.beacon.com.creativeeconomy_app.ce;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class CE_B_PagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;

    public CE_B_PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                CE_Smarttour tabFragment1 = new CE_Smarttour();
                Log.d("aaa", "        position2 : 0");
                return tabFragment1;
            case 1:
                CE_Smartfactory tabFragment2 = new CE_Smartfactory();
                Log.d("aaa", "        position2 : 1");
                return tabFragment2;
            case 2:
                CE_Growthsupport tabFragment3 = new CE_Growthsupport();
                Log.d("aaa", "        position2 : 2");
                return tabFragment3;
            case 3:
                CE_Industrysupport tabFragment4 = new CE_Industrysupport();
                Log.d("aaa", "        position2 : 3");
                return tabFragment4;
            case 4:
                CE_Arttoy tabFragment5 = new CE_Arttoy();
                Log.d("aaa", "        position2 : 4");
                return tabFragment5;
            case 5:
                CE_Jejuconcert tabFragment6 = new CE_Jejuconcert();
                Log.d("aaa", "        position2 : 5");
                return tabFragment6;


            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
