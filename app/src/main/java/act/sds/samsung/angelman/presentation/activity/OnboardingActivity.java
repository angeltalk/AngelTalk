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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

import static butterknife.OnPageChange.Callback.PAGE_SELECTED;

public class OnboardingActivity extends AbstractActivity{
    public static int[] ONBOARDING_IMAGES = {
            R.drawable.onboarding_1,
            R.drawable.onboarding_2,
            R.drawable.onboarding_3,
            R.drawable.onboarding_4,
            R.drawable.onboarding_5,
    };

    @BindView(R.id.onboarding_finish)
    public ImageView onboardingFinishButton;

    @BindView(R.id.onboarding_indicator)
    public PageIndicatorView onboardingIndicator;

    @BindView(R.id.onboarding_view_pager)
    public ViewPager onboardingViewPager;

    @BindView(R.id.onboaring_angelee)
    public ImageView firstView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(((AngelmanApplication) getApplicationContext()).isFirstLaunched()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_onboarding);

            ButterKnife.setDebug(true);
            ButterKnife.bind(this);

            onboardingViewPager.setAdapter(new OnboardingImageAdapter(this));

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

        }else{
            moveToCategoryMenuActivity();
        }
    }

    @OnClick(R.id.onboarding_finish)
    public void onClickOnboardingFinishButton (View view) {
        moveToCategoryMenuActivity();
    }

    private void moveToCategoryMenuActivity() {
        Intent intent = new Intent(this, CategoryMenuActivity.class);
        startActivity(intent);
        finish();
    }

    @OnPageChange(value = R.id.onboarding_view_pager, callback = PAGE_SELECTED)
    public void onPageSelectedOnboardingViewPager(int position) {
        showOrHideDeleteButtonByIndex(position);
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

}
