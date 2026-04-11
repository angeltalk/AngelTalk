package angeltalk.plus.presentation.activity;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.presentation.custom.CardViewPagerLayout;
import angeltalk.plus.presentation.custom.CategoryMenuLayout;
import dagger.hilt.android.AndroidEntryPoint;

// Modern replacement for the WindowManager overlay approach. Android 12+ blocks
// TYPE_APPLICATION_OVERLAY from drawing above the keyguard, so child mode hosts
// the same CategoryMenuLayout/CardViewPagerLayout custom views inside an Activity
// that opts into setShowWhenLocked / setTurnScreenOn.
@AndroidEntryPoint
public class LockScreenActivity extends AppCompatActivity {

    private FrameLayout container;
    private CategoryMenuLayout categoryMenuLayout;
    private CardViewPagerLayout cardViewPagerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (km != null) {
                km.requestDismissKeyguard(this, null);
            }
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        container = new FrameLayout(this);
        container.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(container);

        showCategoryMenu();
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showCategoryMenu();
    }

    @Override
    public void onBackPressed() {
        // Lock screen — do not allow back to dismiss the AngelTalk surface.
    }

    private void showCategoryMenu() {
        removeCardViewPager();
        if (categoryMenuLayout == null) {
            categoryMenuLayout = new CategoryMenuLayout(this, null);
            categoryMenuLayout.setOnCategoryViewChangeListener(categoryViewChangeListener);
            container.addView(categoryMenuLayout, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
        }
        categoryMenuLayout.setLockAreaVisibleWithGone();
    }

    private void showCardViewPager(CategoryModel categoryModel) {
        if (cardViewPagerLayout == null) {
            cardViewPagerLayout = new CardViewPagerLayout(this, null);
            cardViewPagerLayout.setOnClickBackButtonListener(cardViewBackListener);
            container.addView(cardViewPagerLayout, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
        }
        cardViewPagerLayout.setCategoryData(categoryModel);
    }

    private void removeCardViewPager() {
        if (cardViewPagerLayout != null) {
            container.removeView(cardViewPagerLayout);
            cardViewPagerLayout = null;
        }
    }

    private final CategoryMenuLayout.OnCategoryViewChangeListener categoryViewChangeListener =
            new CategoryMenuLayout.OnCategoryViewChangeListener() {
                @Override
                public void onUnLock() {
                    finishAndRemoveTask();
                }

                @Override
                public void categoryClick(CategoryModel categoryModel) {
                    showCardViewPager(categoryModel);
                }
            };

    private final CardViewPagerLayout.OnClickBackButtonListener cardViewBackListener =
            new CardViewPagerLayout.OnClickBackButtonListener() {
                @Override
                public void clickBackButton() {
                    if (categoryMenuLayout != null) {
                        categoryMenuLayout.changeClickedState();
                    }
                    removeCardViewPager();
                }
            };
}
