package act.sds.samsung.angelman.presentation.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.rd.PageIndicatorView;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.adapter.OnboardingImageAdapter;

public class OnboardingActivity extends AbstractActivity{
    public static int[] ONBOARDING_IMAGES = {
            R.drawable.onboarding_1,
            R.drawable.onboarding_2,
            R.drawable.onboarding_3,
            R.drawable.onboarding_4,
            R.drawable.onboarding_5,
    };
    private ImageView onboardingFinishButton;
    private PageIndicatorView onboardingIndicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AngelmanApplication angelmanApplication = (AngelmanApplication) getApplicationContext();
        if(angelmanApplication.isFirstLaunched()) {
            angelmanApplication.copyAssetImagesToImageFolder();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_onboarding);

            ViewPager onboardingViewPager = (ViewPager) findViewById(R.id.onboarding_view_pager);

            onboardingViewPager.setAdapter(new OnboardingImageAdapter(this));

            onboardingFinishButton = (ImageView) findViewById(R.id.onboarding_finish);
            onboardingIndicator = (PageIndicatorView) findViewById(R.id.onboarding_indicator);

            ImageView firstView = (ImageView) findViewById(R.id.onboaring_angelee);

            Glide.with(OnboardingActivity.this)
                    .load(R.drawable.angelee)
                    .asGif()
                    .crossFade()
                    .into(firstView);

            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout onboardingFirstPage = (RelativeLayout) findViewById(R.id.onboarding_first_page);
                    onboardingFirstPage.setVisibility(View.GONE);
                }
            }, 4000);

            onboardingFinishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToCategoryMenuActivity();
                }
            });

            onboardingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    showOrHideDeleteButtonByIndex(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }else{
            moveToCategoryMenuActivity();
        }
    }

    private void showOrHideDeleteButtonByIndex(int pos) {
        if(pos == ONBOARDING_IMAGES.length - 1){
            onboardingFinishButton.setVisibility(View.VISIBLE);
            onboardingIndicator.setVisibility(View.GONE);

        }else{
            onboardingFinishButton.setVisibility(View.GONE);
            onboardingIndicator.setVisibility(View.VISIBLE);
        }
    }

    private void moveToCategoryMenuActivity() {
        Intent intent = new Intent(this, CategoryMenuActivity.class);
        startActivity(intent);
        finish();
    }

}
