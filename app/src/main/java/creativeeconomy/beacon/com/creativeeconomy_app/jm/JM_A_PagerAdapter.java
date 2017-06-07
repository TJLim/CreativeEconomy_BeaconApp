package creativeeconomy.beacon.com.creativeeconomy_app.jm;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class JM_A_PagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;

    public JM_A_PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                JM_Yeomiji tabFragment1 = new JM_Yeomiji();
                return tabFragment1;
            case 1:
                JM_Cheonjeyeon tabFragment2 = new JM_Cheonjeyeon();
                return tabFragment2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
