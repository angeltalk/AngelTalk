package act.sds.samsung.angelman.presentation.custom;

import android.content.Context;
import android.graphics.PixelFormat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import act.sds.samsung.angelman.domain.model.CategoryModel;

import static act.sds.samsung.angelman.presentation.util.ApplicationManager.PRIVATE_PREFERENCE_NAME;
import static android.content.Context.WINDOW_SERVICE;

public class ChildModeManager {
    private WindowManager mWindowManager;

    private TelephonyManager telephonyManager;
    private static MyPhoneStateListener phoneListener;

    private CategoryMenuLayout categoryMenuLayout;
    private CardViewPagerLayout cardViewPagerLayout;
    private Context context;

    public ChildModeManager(Context context) {
        this.context = context;
        mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        setTelephonyManager(context);
    }

    public void createAndAddCategoryMenu() {

        if (categoryMenuLayout == null) {
            categoryMenuLayout = new CategoryMenuLayout(context, null);
            mWindowManager.addView(categoryMenuLayout, params);
            categoryMenuLayout.setOnCategoryViewChangeListener(onCategoryViewChangeListener);
        }
        categoryMenuLayout.setLockAreaVisibleWithGone();
    }

    public void removeAllView() {
        removeCardViewPager();
        removeCategoryMenu();
    }

    private void removeCategoryMenu() {
        if (categoryMenuLayout != null) {
            mWindowManager.removeView(categoryMenuLayout);
            categoryMenuLayout.destroyDrawingCache();
        }
        categoryMenuLayout = null;
    }

    private void removeCardViewPager() {
        if (cardViewPagerLayout != null) {
            mWindowManager.removeView(cardViewPagerLayout);
            cardViewPagerLayout.destroyDrawingCache();
        }
        cardViewPagerLayout = null;
    }

    private void createCardViewPager(CategoryModel categoryModel) {
        if(cardViewPagerLayout == null) {
            cardViewPagerLayout = new CardViewPagerLayout(context, null);
            cardViewPagerLayout.setOnClickBackButtonListener(onClickBackButtonListener);
            mWindowManager.addView(cardViewPagerLayout, params);
        }
        cardViewPagerLayout.setCategoryData(categoryModel);
    }

    private void setTelephonyManager(Context context) {
        if(telephonyManager == null) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if(phoneListener == null) {
                phoneListener = new MyPhoneStateListener();
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        private boolean isCalled = false;
        public void onCallStateChanged(int state, String incomingNumber) {
            processByPhoneStatus(state);
        }

        private synchronized void processByPhoneStatus(int state) {
            if(context.getSharedPreferences(PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean("childMode", true)) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    isCalled = true;
                    removeAllView();
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    if (isCalled) {
                        isCalled = false;
                        createAndAddCategoryMenu();
                    }
                }
            }
        }
    }

    private CategoryMenuLayout.OnCategoryViewChangeListener onCategoryViewChangeListener = new CategoryMenuLayout.OnCategoryViewChangeListener() {
        @Override
        public void onUnLock() {
            removeAllView();
        }

        @Override
        public void categoryClick(CategoryModel categoryModel) {
            createCardViewPager(categoryModel);
        }
    };

    private CardViewPagerLayout.OnClickBackButtonListener onClickBackButtonListener = new CardViewPagerLayout.OnClickBackButtonListener() {
        @Override
        public void clickBackButton() {
            categoryMenuLayout.changeClickedState();
            removeCardViewPager();
        }
    };

    private final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
            PixelFormat.TRANSLUCENT
    );

    public CategoryMenuLayout getCategoryMenuLayout() {
        return categoryMenuLayout;
    }

}
