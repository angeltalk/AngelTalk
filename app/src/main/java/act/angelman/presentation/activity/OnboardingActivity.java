package act.angelman.presentation.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import act.angelman.AngelmanApplication;
import act.angelman.R;
import act.angelman.presentation.adapter.OnboardingImageAdapter;
import act.angelman.presentation.manager.ApplicationManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OnboardingActivity extends AbstractActivity {

    @Inject
    ApplicationManager applicationManager;

    @BindView(R.id.onboarding_view_pager)
    public ViewPager onboardingViewPager;


    @BindView(R.id.onboarding_first_page)
    public RelativeLayout onboardingFirstPageLayout;


    @BindView(R.id.onboaring_angelee)
    ImageView onboardingAngeleeImageView;

    private AngelmanApplication angelmanApplication;
    private Activity activity;

    @VisibleForTesting
    static final int PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);

        angelmanApplication = (AngelmanApplication) getApplicationContext();
        activity = this;

        if (applicationManager.isFirstLaunched()) {
            applicationManager.setNotFirstLaunched();
            showOnboardingView();
        } else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                showOnboardingView();
            } else {
                moveToCategoryMenuActivity();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    moveToCategoryMenuActivity();
                } else {
                    return;
                }
                return;
            }
        }
    }

    private void showOnboardingView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);

        if(applicationManager.isFirstLaunched()) {
            initContentView();
        } else {
            onboardingFirstPageLayout.setVisibility(View.GONE);
        }

        OnboardingImageAdapter onboardingImageAdapter = new OnboardingImageAdapter(this);
        onboardingImageAdapter.setPermissionButtonOnClickListener(onPermissionButtonOnClickListener);
        onboardingViewPager.setAdapter(onboardingImageAdapter);

        if(!applicationManager.isFirstLaunched()) {
            onboardingViewPager.setCurrentItem(4);
        }
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

    private void moveToCategoryMenuActivity() {
        Intent intent = new Intent(this, CategoryMenuActivity.class);
        startActivity(intent);
        finish();
    }

    private View.OnClickListener onPermissionButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

        }
    };
}
