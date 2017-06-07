package creativeeconomy.beacon.com.creativeeconomy_app.jm;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class JM_D_PagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;

    public JM_D_PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                JM_DulleC1 tabFragment1 = new JM_DulleC1();
                return tabFragment1;
            case 1:
                JM_DulleC2 tabFragment2 = new JM_DulleC2();
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
