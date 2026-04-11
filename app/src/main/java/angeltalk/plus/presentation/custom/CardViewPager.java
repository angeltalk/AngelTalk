package angeltalk.plus.presentation.custom;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;

public class CardViewPager extends ViewPager {

    public CardViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setViewPagerMargin();
    }

    private void setViewPagerMargin() {
        setClipToPadding(false);
        setOffscreenPageLimit(1);
    }
}
