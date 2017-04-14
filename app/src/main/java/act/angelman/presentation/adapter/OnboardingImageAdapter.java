package act.angelman.presentation.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import static act.angelman.presentation.activity.OnboardingActivity.ONBOARDING_IMAGES;

public class OnboardingImageAdapter extends PagerAdapter{

    private Context context;

    public OnboardingImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return ONBOARDING_IMAGES.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = new View(context);
        view.setBackground(context.getResources().getDrawable(ONBOARDING_IMAGES[position]));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }
}
