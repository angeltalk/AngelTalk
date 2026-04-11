package angeltalk.plus.presentation.activity;

import dagger.hilt.android.AndroidEntryPoint;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
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
import static angeltalk.plus.presentation.manager.ApplicationConstants.ONBOARDING_PERMISSION_REQUEST_CODE;
import static angeltalk.plus.presentation.manager.ApplicationConstants.OVERLAY_PERMISSION_REQUEST_CODE;
import static android.os.Build.VERSION_CODES.M;

@AndroidEntryPoint
public class OnboardingActivity extends AbstractActivity {

    @Inject
    ApplicationManager applicationManager;

    public ViewPager onboardingViewPager;


    public RelativeLayout onboardingFirstPageLayout;


    ImageView onboardingAngeleeImageView;

    private WeakReference<OnboardingActivity> onboardingActivityReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        if (requestCode == ONBOARDING_PERMISSION_REQUEST_CODE) {
            boolean allGranted = grantResults.length > 0;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                if (Build.VERSION.SDK_INT >= M) {
                    checkDrawOverlayPermission();
                } else {
                    moveToCategoryMenuActivity();
                }
            }
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
        bindViews();
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
            ActivityCompat.requestPermissions(
                    onboardingActivityReference.get(),
                    requiredDangerousPermissions(),
                    ONBOARDING_PERMISSION_REQUEST_CODE);
        }
    };

    /**
     * The storage model changed twice since this app was written:
     *   - API 29 (Q): scoped storage deprecates WRITE_EXTERNAL_STORAGE
     *   - API 33 (Tiramisu): READ_EXTERNAL_STORAGE replaced by READ_MEDIA_IMAGES/VIDEO/AUDIO
     * Returns true when any dangerous permission the app still needs is missing,
     * or when the SYSTEM_ALERT_WINDOW overlay isn't granted.
     */
    private boolean hasAllPermissions() {
        for (String perm : requiredDangerousPermissions()) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return Build.VERSION.SDK_INT >= M && !Settings.canDrawOverlays(this);
    }

    private String[] requiredDangerousPermissions() {
        java.util.List<String> perms = new java.util.ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.READ_MEDIA_IMAGES);
            perms.add(Manifest.permission.READ_MEDIA_VIDEO);
            perms.add(Manifest.permission.READ_MEDIA_AUDIO);
            perms.add(Manifest.permission.POST_NOTIFICATIONS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            perms.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            perms.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        perms.add(Manifest.permission.READ_PHONE_STATE);
        perms.add(Manifest.permission.RECORD_AUDIO);
        return perms.toArray(new String[0]);
    }
    private void bindViews() {
        onboardingViewPager = findViewById(R.id.onboarding_view_pager);
        onboardingFirstPageLayout = findViewById(R.id.onboarding_first_page);
        onboardingAngeleeImageView = findViewById(R.id.onboaring_angelee);
    }
}
