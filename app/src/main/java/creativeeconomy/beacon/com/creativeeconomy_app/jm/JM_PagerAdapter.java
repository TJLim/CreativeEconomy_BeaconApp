package creativeeconomy.beacon.com.creativeeconomy_app.jm;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class JM_PagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;

    public JM_PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                JM_Intrdc tabFragment1 = new JM_Intrdc();
                return tabFragment1;
            case 1:
                JM_Usinginfo tabFragment2 = new JM_Usinginfo();
                return tabFragment2;
            case 2:
                JM_Infocenter tabFragment3 = new JM_Infocenter();
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
