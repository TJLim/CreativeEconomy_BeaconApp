package creativeeconomy.beacon.com.creativeeconomy_app.ce;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class CE_PagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;

    public CE_PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                CE_Greetings tabFragment1 = new CE_Greetings();
                Log.d("aaa", "        position2 : 0");
                return tabFragment1;
            case 1:
                CE_Intrdc tabFragment2 = new CE_Intrdc();
                Log.d("aaa", "        position2 : 1");
                return tabFragment2;
            case 2:
                CE_History tabFragment3 = new CE_History();
                Log.d("aaa", "        position2 : 2");
                return tabFragment3;
            case 3:
                CE_Fragment4 tabFragment4 = new CE_Fragment4();
                Log.d("aaa", "        position2 : 3");
                return tabFragment4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
