package act.sds.samsung.angelman.presentation.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.data.firebase.FirebaseSynchronizer;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;
import act.sds.samsung.angelman.presentation.adapter.CategoryAdapter;
import act.sds.samsung.angelman.presentation.util.DialogUtil;
import act.sds.samsung.angelman.presentation.util.FontUtil;
import act.sds.samsung.angelman.presentation.util.ImageUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static act.sds.samsung.angelman.R.string.delete_category;

public class CategoryMenuActivity extends AbstractActivity {

    public static String SCREEN_SERVICE_NAME = "act.sds.samsung.angelman.presentation.service.ScreenService";
    private static final String VOC_WEB_URL = "https://docs.google.com/forms/d/1N8sSXRWc0HHVIQSXgtcO60bj_U_3cXh7Hfl5Nlxp1OE/edit";

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CardRepository cardRepository;


    @Inject
    FirebaseSynchronizer firebaseSynchronizer;


    @BindView(R.id.category_list)
    public GridView categoryGrid;

    @BindView(R.id.logo_angeltalk)
    public ImageView logoButton;

    @BindView(R.id.category_delete_button)
    public TextView deleteButton;

    protected CategoryAdapter categoryAdapter;
    private ImageUtil imageUtil;
    private AlertDialog dialog;

    private PopupWindow easterEggPopup;

    public enum CategoryMenuStatus {
        NONE, CATEGORY_DEFAULT, CATEGORY_DELETABLE
    }

    int selectedCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ButterKnife.bind(this);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);

        deleteButton.setTypeface(FontUtil.setFont(this, FontUtil.FONT_REGULAR));

        final GestureDetector logoGestureDetector = getGestureDetector();

        logoButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return logoGestureDetector.onTouchEvent(event);
            }
        });

        selectedCategoryId = -1;

        AngelmanApplication angelmanApplication = (AngelmanApplication) getApplicationContext();
        if (angelmanApplication.isFirstLaunched() && !isServiceRunningCheck()) {
            angelmanApplication.setNotFirstLaunched();
            angelmanApplication.setChildMode();
        }

        imageUtil = ImageUtil.getInstance();

        List<CategoryModel> categoryAllList = categoryRepository.getCategoryAllList();

        categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryAllList, true);
        categoryGrid.setAdapter(categoryAdapter);

        changeToDefault();

        //syncWithServer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<CategoryModel> categoryAllList = categoryRepository.getCategoryAllList();
        categoryAdapter.setCategoryList(categoryAllList);
        setCategoryItemClick(categoryGrid, categoryAllList);
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
        Uri webpage = Uri.parse(VOC_WEB_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @OnClick(R.id.category_delete_button)
    public void onClickCategoryDeleteButton(View v) {
        CategoryMenuStatus status = CategoryMenuStatus.CATEGORY_DEFAULT;
        switch (categoryAdapter.getCategoryMenuStatus()) {
            case CATEGORY_DEFAULT:
                status = CategoryMenuStatus.CATEGORY_DELETABLE;
                ((TextView) findViewById(R.id.category_delete_button)).setText(R.string.complete);
                break;
            case CATEGORY_DELETABLE:
                status = CategoryMenuStatus.CATEGORY_DEFAULT;
                ((TextView) findViewById(R.id.category_delete_button)).setText(R.string.delete);
                break;
        }
        categoryAdapter.changeCategoryItemsStatus(status);
    }

    @NonNull
    private GestureDetector getGestureDetector() {
        GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
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

        return gestureDetector;
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

    void removeSavedResourceFiles(String imagePath, String voicePath) {
        imageUtil.removeFile(imagePath);
        imageUtil.removeFile(voicePath);
    }


    private View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

    private void changeToDefault() {
        ((TextView) findViewById(R.id.category_delete_button)).setText(R.string.delete);
        categoryAdapter.changeCategoryItemsStatus(CategoryMenuStatus.CATEGORY_DEFAULT);
    }

    private void setCategoryItemClick(final GridView gridView, final List<CategoryModel> categoryList) {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CardView cardView = (CardView) view;
                TextView titleView = (TextView) cardView.findViewById(R.id.category_title);
                selectedCategoryId = categoryAdapter.getItem(position).index;

                String titleString = titleView.getText().toString();
                if (categoryAdapter.getCategoryMenuStatus() == CategoryMenuStatus.CATEGORY_DELETABLE) {
                    showDeleteAlertDialog(titleString);
                } else if (categoryAdapter.getCategoryMenuStatus() == CategoryMenuStatus.CATEGORY_DEFAULT) {
                    if (titleString.equals(getResources().getString(R.string.new_category))) {
                        moveToNewCategoryActivity();
                    } else {
                        CategoryModel categoryModel = categoryList.get(position);
                        moveToCategoryViewPager(categoryModel);
                    }
                }
            }
        });
    }

    private void moveToNewCategoryActivity() {
        Intent intent = new Intent(this, NewCategoryActivity.class);
        startActivity(intent);
    }

    private void moveToCategoryViewPager(CategoryModel categoryModel) {
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
                    removeSavedResourceFiles(cardModel.imagePath, cardModel.voicePath);
                }
            }

            cardRepository.deleteSingleCardsWithCategory(selectedCategoryId);
            categoryRepository.deleteCategory(selectedCategoryId);
            categoryAdapter.removeItem(selectedCategoryId);

            dialog.dismiss();

            if (categoryAdapter.getCount() == 0) {
                changeToDefault();
                moveToNewCategoryActivity();
            }

        }
    };

    private void showDeleteAlertDialog(String categoryName) {
        String message;
        if (categoryAdapter.getCount() == 1) {
            message = getResources().getString(R.string.one_left_delete_category);
        } else {
            message = String.format(getResources().getString(delete_category), categoryName);
        }
        View innerView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView alertMessage = (TextView) innerView.findViewById(R.id.alert_message);
        alertMessage.setText(message);

        dialog = DialogUtil.buildCustomDialog(CategoryMenuActivity.this, innerView, deleteCategoryClickListener, cancelClickListener);
        dialog.show();
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains(SCREEN_SERVICE_NAME)) {
                return true;
            }
        }
        return false;
    }


    private void syncWithServer(){
        List<CardModel> singleCardAllList = cardRepository.getSingleCardAllList();
        List<CategoryModel> categoryAllList = categoryRepository.getCategoryAllList();

        firebaseSynchronizer.uploadDataToFirebase(categoryAllList ,singleCardAllList);
    }
}
