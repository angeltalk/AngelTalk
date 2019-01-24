package angeltalk.plus.presentation.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import angeltalk.plus.AngelmanApplication;
import angeltalk.plus.R;
import angeltalk.plus.presentation.adapter.OnboardingImageAdapter;
import angeltalk.plus.presentation.manager.ApplicationManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import static angeltalk.plus.presentation.manager.ApplicationConstants.ONBOARDING_PERMISSION_REQUEST_CODE;
import static angeltalk.plus.presentation.manager.ApplicationConstants.OVERLAY_PERMISSION_REQUEST_CODE;
import static android.os.Build.VERSION_CODES.M;

public class OnboardingActivity extends AbstractActivity {

    @Inject
    ApplicationManager applicationManager;

    @BindView(R.id.onboarding_view_pager)
    public ViewPager onboardingViewPager;


    @BindView(R.id.onboarding_first_page)
    public RelativeLayout onboardingFirstPageLayout;


    @BindView(R.id.onboaring_angelee)
    ImageView onboardingAngeleeImageView;

    private WeakReference<OnboardingActivity> onboardingActivityReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);

        onboardingActivityReference = new WeakReference<>(this);

        if (applicationManager.isFirstLaunched()) {
            showOnboardingView();
            applicationManager.setNotFirstLaunched();
        } else {
            if(hasAllPermissions()) {
                showOnboardingView();
            } else {
                moveToCategoryMenuActivity();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ONBOARDING_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= M) {
                        checkDrawOverlayPermission();
                    } else {
                        moveToCategoryMenuActivity();
                    }
                }
                break;
            default:
                break;
        }
    }



    @TargetApi(Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {
        if (!Settings.canDrawOverlays(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
        } else {
            moveToCategoryMenuActivity();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                moveToCategoryMenuActivity();
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
            ActivityCompat.requestPermissions(onboardingActivityReference.get(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, ONBOARDING_PERMISSION_REQUEST_CODE);
        }
    };

    private boolean hasAllPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || (Build.VERSION.SDK_INT >= M && !Settings.canDrawOverlays(this));
    }
}
