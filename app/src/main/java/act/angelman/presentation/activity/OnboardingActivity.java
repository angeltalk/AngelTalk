package act.angelman.presentation.activity;

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

import javax.inject.Inject;

import act.angelman.AngelmanApplication;
import act.angelman.R;
import act.angelman.presentation.adapter.OnboardingImageAdapter;
import act.angelman.presentation.manager.ApplicationManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class OnboardingActivity extends AbstractActivity {
    public static int[] ONBOARDING_IMAGES = {
            R.drawable.onboarding_1,
            R.drawable.onboarding_2,
            R.drawable.onboarding_3,
            R.drawable.onboarding_4,
            R.drawable.onboarding_5,
    };

    @Inject
    ApplicationManager applicationManager;

    @BindView(R.id.onboarding_view_pager)
    public ViewPager onboardingViewPager;

    @BindView(R.id.onboarding_finish)
    public ImageView onboardingFinishButton;

    @BindView(R.id.onboarding_indicator)
    public PageIndicatorView onboardingIndicatorView;

    @BindView(R.id.onboarding_first_page)
    public RelativeLayout onboardingFirstPageLayout;

    @BindView(R.id.onboaring_angelee)
    public ImageView onboardingAngeleeImageView;

    private AngelmanApplication angelmanApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);

        angelmanApplication = (AngelmanApplication) getApplicationContext();
        if (applicationManager.isFirstLaunched()) {
            showOnboardingView();
            applicationManager.setNotFirstLaunched();
        } else {
            moveToCategoryMenuActivity();
        }
    }

    @OnClick(R.id.onboarding_finish)
    public void onClickOnboardingFinish(View v) {
        applicationManager.setChildMode();
        moveToCategoryMenuActivity();
    }

    @OnPageChange(R.id.onboarding_view_pager)
    public void onPageSelectedOnboardingViewPager(int position) {
        if (isLastPage(position)) {
            onboardingFinishButton.setVisibility(View.VISIBLE);
            onboardingIndicatorView.setVisibility(View.GONE);

        } else {
            onboardingFinishButton.setVisibility(View.GONE);
            onboardingIndicatorView.setVisibility(View.VISIBLE);
        }
    }

    private void showOnboardingView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);
        initContentView();

        onboardingViewPager.setAdapter(new OnboardingImageAdapter(this));
    }
    private void initContentView() {
        Glide.with(OnboardingActivity.this)
                .load(R.drawable.angelee)
                .asGif()
                .crossFade()
                .into(onboardingAngeleeImageView);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onboardingFirstPageLayout.setVisibility(View.GONE);
            }
        }, 4000);
    }

    private boolean isLastPage(int position) {
        return position == ONBOARDING_IMAGES.length - 1;
    }

    private void moveToCategoryMenuActivity() {
        Intent intent = new Intent(this, CategoryMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
