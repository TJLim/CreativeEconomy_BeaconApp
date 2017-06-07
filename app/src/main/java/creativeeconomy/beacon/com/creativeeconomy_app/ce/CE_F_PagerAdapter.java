package creativeeconomy.beacon.com.creativeeconomy_app.ce;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class CE_F_PagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;

    public CE_F_PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                CE_Jspace tabFragment1 = new CE_Jspace();
                Log.d("aaa", "        position2 : 0");
                return tabFragment1;
            case 1:
                CE_Reservation tabFragment2 = new CE_Reservation();
                Log.d("aaa", "        position2 : 1");
                return tabFragment2;
            case 2:
                CE_Equipment tabFragment3 = new CE_Equipment();
                Log.d("aaa", "        position2 : 2");
                return tabFragment3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
