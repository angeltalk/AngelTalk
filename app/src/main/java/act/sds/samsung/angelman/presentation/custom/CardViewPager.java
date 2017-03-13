package act.sds.samsung.angelman.presentation.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class CardViewPager extends ViewPager {

    public CardViewPager(Context context) {
        super(context);
        setViewPagerMargin();
    }

    public CardViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setViewPagerMargin();
    }

    private void setViewPagerMargin() {
        setClipToPadding(false);
        setOffscreenPageLimit(1);
    }
}
