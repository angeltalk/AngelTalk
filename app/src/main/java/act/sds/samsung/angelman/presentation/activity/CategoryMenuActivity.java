package act.sds.samsung.angelman.presentation.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.data.synchronizer.FirebaseSynchronizer;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;
import act.sds.samsung.angelman.presentation.adapter.CategoryAdapter;
import act.sds.samsung.angelman.presentation.custom.CustomConfirmDialog;
import act.sds.samsung.angelman.presentation.util.FileUtil;
import act.sds.samsung.angelman.presentation.util.ImageUtil;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static act.sds.samsung.angelman.AngelmanApplication.PRIVATE_PREFERENCE_NAME;
import static act.sds.samsung.angelman.R.string.delete_category;

public class CategoryMenuActivity extends AbstractActivity {

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CardRepository cardRepository;

    @Inject
    FirebaseSynchronizer firebaseSynchronizer;

    @BindView(R.id.category_list)
    public GridView categoryGridView;

    @BindView(R.id.logo_angeltalk)
    public ImageView logoButton;

    @BindView(R.id.category_delete_button)
    public TextView categoryDeleteButton;

    @BindString(R.string.voc_web_url)
    public String vocWebUrl;

    @BindString(R.string.new_category)
    public String newCategoryString;

    @BindString(R.string.one_left_delete_category)
    public String oneLeftDeleteCategory;

    public enum CategoryMenuStatus {
        NONE, CATEGORY_DEFAULT, CATEGORY_DELETABLE
    }

    protected CategoryAdapter categoryAdapter;
    private CustomConfirmDialog dialog;
    private int selectedCategoryId = -1;
    private PopupWindow easterEggPopup;
    private GestureDetector logoGestureDetector;

    private static NotificationManager notificationManager;
    private static Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ButterKnife.bind(this);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);

        initEasterEggPopup();
        initCategoryGridView();
        launchWidgetButton();
        //syncWithServer();
    }

    @Override
    public void onBackPressed() {
        if (categoryAdapter.getCategoryMenuStatus() == CategoryMenuStatus.CATEGORY_DELETABLE) {
            categoryAdapter.changeCategoryItemsStatus(CategoryMenuStatus.CATEGORY_DEFAULT);
        } else if (easterEggPopup != null) {
            easterEggPopup.dismiss();
            easterEggPopup = null;
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.send_voc)
    public void onClickSendVoc(View v) {
        Uri webpage = Uri.parse(vocWebUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @OnClick(R.id.category_delete_button)
    public void onClickCategoryDeleteButton(View v) {
        if (categoryAdapter.getCategoryMenuStatus().equals(CategoryMenuStatus.CATEGORY_DEFAULT)) {
            changeCategoryMenuStatus(CategoryMenuStatus.CATEGORY_DELETABLE);
        } else {
            changeCategoryMenuStatus(CategoryMenuStatus.CATEGORY_DEFAULT);
        }
    }

    @OnTouch(R.id.logo_angeltalk)
    public boolean onTouch(View v, MotionEvent event) {
        return logoGestureDetector.onTouchEvent(event);
    }

    private void initEasterEggPopup() {
        logoGestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                showEasterEggPopup();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });
    }

    private void initCategoryGridView() {
        List<CategoryModel> categoryAllList = categoryRepository.getCategoryAllList();
        categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryAllList, true);
        categoryGridView.setAdapter(categoryAdapter);
        categoryAdapter.setCategoryList(categoryAllList);
        setCategoryItemClickListener(categoryGridView, categoryAllList);
        changeCategoryMenuStatus(CategoryMenuStatus.CATEGORY_DEFAULT);
    }

    private void changeCategoryMenuStatus(CategoryMenuStatus categoryMenuStatus) {
        switch (categoryMenuStatus) {
            case CATEGORY_DEFAULT:
                categoryDeleteButton.setText(R.string.delete);
                break;
            case CATEGORY_DELETABLE:
                categoryDeleteButton.setText(R.string.complete);
                break;
            default:
                Log.e("EEEE", "category is not set");
                break;
        }
        categoryAdapter.changeCategoryItemsStatus(categoryMenuStatus);
    }

    private void showEasterEggPopup() {
        View view = getLayoutInflater().inflate(R.layout.easter_egg, null);
        ImageView easterEggClose = (ImageView) view.findViewById(R.id.easter_egg_close);

        easterEggClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                easterEggPopup.dismiss();
            }
        });
        easterEggPopup = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        easterEggPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void setCategoryItemClickListener(final GridView gridView, final List<CategoryModel> categoryList) {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoryId = categoryAdapter.getItem(position).index;

                String titleString = ((TextView) view.findViewById(R.id.category_title)).getText().toString();
                if (categoryAdapter.getCategoryMenuStatus() == CategoryMenuStatus.CATEGORY_DELETABLE) {
                    showDeleteConfirmDialog(titleString);
                } else if (categoryAdapter.getCategoryMenuStatus() == CategoryMenuStatus.CATEGORY_DEFAULT) {
                    if (titleString.equals(newCategoryString)) {
                        moveToNewCategoryActivity();
                    } else {
                        CategoryModel categoryModel = categoryList.get(position);
                        moveToCategoryViewPagerActivity(categoryModel);
                    }
                }
            }
        });
    }

    private void showDeleteConfirmDialog(String categoryName) {
        String message;
        if (categoryAdapter.getCount() == 1) {
            message = oneLeftDeleteCategory;
        } else {
            message = String.format(getResources().getString(delete_category), categoryName);
        }
        dialog = new CustomConfirmDialog(this, message, deleteCategoryClickListener, cancelClickListener);
    }

    private void moveToNewCategoryActivity() {
        Intent intent = new Intent(this, NewCategoryActivity.class);
        startActivity(intent);
    }

    private void moveToCategoryViewPagerActivity(CategoryModel categoryModel) {
        Intent intent = new Intent(getApplicationContext(), CardViewPagerActivity.class);
        ((AngelmanApplication) getApplicationContext()).setCategoryModel(categoryModel);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(CardViewPagerActivity.CATEGORY_COLOR, categoryModel.color);
        getApplicationContext().startActivity(intent);
    }

    private View.OnClickListener deleteCategoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<CardModel> singleCardList = cardRepository.getSingleCardListWithCategoryId(selectedCategoryId);

            for (CardModel cardModel : singleCardList) {
                if (cardModel.imagePath.contains(ImageUtil.IMAGE_FOLDER)) {
                    FileUtil.removeFile(cardModel.imagePath);
                    FileUtil.removeFile(cardModel.voicePath);
                }
            }

            cardRepository.deleteSingleCardsWithCategory(selectedCategoryId);
            categoryRepository.deleteCategory(selectedCategoryId);
            categoryAdapter.removeItem(selectedCategoryId);

            dialog.dismiss();

            if (categoryAdapter.getCount() == 0) {
                changeCategoryMenuStatus(CategoryMenuStatus.CATEGORY_DEFAULT);
                moveToNewCategoryActivity();
            }
        }
    };

    private View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

    private void syncWithServer() {
        List<CardModel> singleCardAllList = cardRepository.getSingleCardAllList();
        List<CategoryModel> categoryAllList = categoryRepository.getCategoryAllList();

        firebaseSynchronizer.uploadDataToFirebase(categoryAllList, singleCardAllList);
    }


    public static class widgetButtonListner extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences preferences = context.getSharedPreferences(PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
            boolean isChildMode = preferences.getBoolean("childMode", false);

            AngelmanApplication.changeChildMode(context, !isChildMode);
            RemoteViews notificationView = new RemoteViews(context.getPackageName(), isChildMode ? R.layout.layout_widget_off : R.layout.layout_widget);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            notificationView.setOnClickPendingIntent(R.id.btn_change_mode, pendingIntent);

            notification = new Notification(R.drawable.angelee, null, System.currentTimeMillis());
            notification.contentView = notificationView;
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notificationManager.notify(1, notification);
        }
    }

    private void launchWidgetButton() {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        AngelmanApplication angelmanApplication = (AngelmanApplication) getApplicationContext();
        RemoteViews notificationView = new RemoteViews(getPackageName(), angelmanApplication.isChildMode() ? R.layout.layout_widget : R.layout.layout_widget_off);

        Intent switchIntent = new Intent(this, widgetButtonListner.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, switchIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.btn_change_mode, pendingIntent);

        notification = new Notification(R.drawable.angelee, null, System.currentTimeMillis());
        notification.contentView = notificationView;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(1, notification);
    }
}
