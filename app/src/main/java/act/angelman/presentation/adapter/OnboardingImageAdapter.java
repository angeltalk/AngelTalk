package act.angelman.presentation.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import act.angelman.R;
import act.angelman.presentation.activity.CategoryMenuActivity;


public class OnboardingImageAdapter extends PagerAdapter{

    int[] ONBOARDING_IMAGES = {
            R.drawable.img_onboarding_1,
            R.drawable.img_onboarding_2,
            R.drawable.img_onboarding_3,
            R.drawable.img_onboarding_4,
            R.drawable.img_onboarding_5,
    };

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

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_onboarding,container,false);

        ImageView onBoardingImage = ((ImageView) layout.findViewById(R.id.img_onboarding));
        ImageView onBoardingFinishAngelee = ((ImageView) layout.findViewById(R.id.onboarding_finish_angelee));
        ImageView onBoardingFinishButton = ((ImageView) layout.findViewById(R.id.onboarding_finish));

        onBoardingImage.setImageDrawable(context.getResources().getDrawable(ONBOARDING_IMAGES[position]));
        onBoardingFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCategoryMenuActivity();
            }
        });

        if(isLastPage(position)) {
            onBoardingFinishAngelee.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(R.drawable.angelee)
                    .asGif()
                    .crossFade()
                    .into(onBoardingFinishAngelee);
            onBoardingFinishButton.setVisibility(View.VISIBLE);

        } else {
            onBoardingFinishButton.setVisibility(View.GONE);
            onBoardingFinishAngelee.setVisibility(View.GONE);
        }

        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    private void moveToCategoryMenuActivity() {
        Intent intent = new Intent(context, CategoryMenuActivity.class);
        context.startActivity(intent);
        ((Activity)context).finish();
    }
    private boolean isLastPage(int position) {
        return position == ONBOARDING_IMAGES.length - 1;
    }

}
