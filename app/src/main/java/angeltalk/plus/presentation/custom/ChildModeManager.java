package angeltalk.plus.presentation.custom;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import angeltalk.plus.domain.model.CategoryModel;

import static angeltalk.plus.presentation.manager.ApplicationConstants.PRIVATE_PREFERENCE_NAME;
import static android.content.Context.WINDOW_SERVICE;

public class ChildModeManager {
    private WindowManager mWindowManager;

    private TelephonyManager telephonyManager;
    private static MyPhoneStateListener phoneListener;
    private static TelephonyCallback telephonyCallback;

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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mWindowManager.addView(categoryMenuLayout, paramsAndroidOreoHigher);
            } else {
                mWindowManager.addView(categoryMenuLayout, paramsAndroidOreoLower);
            }
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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mWindowManager.addView(cardViewPagerLayout, paramsAndroidOreoHigher);
            } else {
                mWindowManager.addView(cardViewPagerLayout, paramsAndroidOreoLower);
            }

        }
        cardViewPagerLayout.setCategoryData(categoryModel);
    }

    private void setTelephonyManager(Context context) {
        if (telephonyManager != null) return;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // READ_PHONE_STATE is a dangerous permission on API 23+. On API 31+ the
        // listen() / registerTelephonyCallback() call throws SecurityException without it.
        // Defer registration if it's not granted — call-state handling degrades to a
        // no-op instead of crashing on first launch.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w("ChildModeManager", "READ_PHONE_STATE not granted — skipping phone state listener");
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (telephonyCallback == null) {
                    telephonyCallback = new MyTelephonyCallback();
                    telephonyManager.registerTelephonyCallback(
                            context.getMainExecutor(), telephonyCallback);
                }
            } else {
                if (phoneListener == null) {
                    phoneListener = new MyPhoneStateListener();
                    telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                }
            }
        } catch (SecurityException e) {
            Log.w("ChildModeManager", "Failed to register phone state listener", e);
            phoneListener = null;
            telephonyCallback = null;
        }
    }

    @SuppressLint("MissingPermission")
    private synchronized void processByPhoneStatus(int state) {
        if (context.getSharedPreferences(PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean("childMode", true)) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                wasCalled = true;
                removeAllView();
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                if (wasCalled) {
                    wasCalled = false;
                    createAndAddCategoryMenu();
                }
            }
        }
    }

    private boolean wasCalled = false;

    @SuppressWarnings("deprecation")
    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            processByPhoneStatus(state);
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private class MyTelephonyCallback extends TelephonyCallback
            implements TelephonyCallback.CallStateListener {
        @Override
        public void onCallStateChanged(int state) {
            processByPhoneStatus(state);
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

    private final WindowManager.LayoutParams paramsAndroidOreoHigher = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
    );

    private final WindowManager.LayoutParams paramsAndroidOreoLower = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
    );

    public CategoryMenuLayout getCategoryMenuLayout() {
        return categoryMenuLayout;
    }

}
