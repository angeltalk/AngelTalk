package act.angelman.presentation.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
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

import act.angelman.AngelmanApplication;
import act.angelman.R;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.model.CategoryModel;
import act.angelman.domain.repository.CardRepository;
import act.angelman.domain.repository.CategoryRepository;
import act.angelman.network.transfer.CardTransfer;
import act.angelman.presentation.adapter.CategoryAdapter;
import act.angelman.presentation.custom.CustomConfirmDialog;
import act.angelman.presentation.manager.ApplicationConstants;
import act.angelman.presentation.manager.ApplicationManager;
import act.angelman.presentation.manager.NotificationActionManager;
import act.angelman.presentation.receiver.NotificationActionReceiver;
import act.angelman.presentation.util.FileUtil;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static act.angelman.R.string.delete_category;

public class CategoryMenuActivity extends AbstractActivity  implements NavigationView.OnNavigationItemSelectedListener{

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CardRepository cardRepository;

    @Inject
    CardTransfer cardTransfer;

    @Inject
    ApplicationManager applicationManager;

    @BindView(R.id.category_list)
    public GridView categoryGridView;

    @BindView(R.id.logo_angeltalk)
    public ImageView logoButton;

    @BindView(R.id.category_delete_button)
    public TextView categoryDeleteButton;

    @BindString(R.string.voc_web_url)
    public String vocWebUrl;

    @BindString(R.string.new_category_button_text)
    public String newCategoryString;

    @BindString(R.string.one_left_delete_category)
    public String oneLeftDeleteCategory;

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Uri webpage = Uri.parse(VOC_WEB_URL);

        if (id == R.id.privacy) {
            webpage = Uri.parse(PRIVACY_POLICY);
        } else if (id == R.id.usages) {
            webpage = Uri.parse(TERMS_OF_SERVICE);
        } else if (id == R.id.oss) {
            webpage = Uri.parse(OSS_LICENSE);
        } else if (id == R.id.voc) {
            webpage = Uri.parse(VOC_WEB_URL);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public enum CategoryMenuStatus {
        NONE, CATEGORY_DEFAULT, CATEGORY_DELETABLE
    }

    protected CategoryAdapter categoryAdapter;
    private CustomConfirmDialog dialog;
    private int selectedCategoryId = -1;
    private PopupWindow easterEggPopup;
    private GestureDetector logoGestureDetector;

    private DrawerLayout drawer;

    private static final String VOC_WEB_URL = "https://docs.google.com/forms/d/1N8sSXRWc0HHVIQSXgtcO60bj_U_3cXh7Hfl5Nlxp1OE/edit";
    private static final String PRIVACY_POLICY="https://docs.google.com/document/d/14AnlKoswSa_5b1ThWbEKU-HlDWE4Z34TgnzccVqjEB8/edit#heading=h.1nrbepsiaovj";
    private static final String TERMS_OF_SERVICE="https://docs.google.com/document/d/18Z1y7jHtTVuDSMK7X82eblYUEEePihhqMDCJQ6KI988/edit#heading=h.q1s8qq8d9h44";
    private static final String OSS_LICENSE="https://docs.google.com/document/d/1fRievNVqIlK3QovqmoETGSBhISivNeGiAa7rGQfE3nU/edit?usp=sharing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ButterKnife.bind(this);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        ImageView sendVoc = ((ImageView) findViewById(R.id
                .send_voc));
        sendVoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        initEasterEggPopup();
        setCategoryGridView();
        launchNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCategoryGridView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        changeCategoryMenuStatus(CategoryMenuStatus.CATEGORY_DEFAULT);
    }

    @Override
    public void onBackPressed() {
        if (categoryAdapter.getCategoryMenuStatus() == CategoryMenuStatus.CATEGORY_DELETABLE) {
            changeCategoryMenuStatus(CategoryMenuStatus.CATEGORY_DEFAULT);
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

    private void setCategoryGridView() {
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
                Log.e("error", "category is not set");
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
                        moveToCategoryViewPagerActivity(categoryModel, null);
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
        dialog = new CustomConfirmDialog(this, message, deleteCategoryClickListener);
        dialog.show();
    }

    private void moveToNewCategoryActivity() {
        Intent intent = new Intent(this, MakeCategoryActivity.class);
        startActivity(intent);
    }

    private void moveToCategoryViewPagerActivity(CategoryModel categoryModel , String intentKey) {
        Intent intent = new Intent(getApplicationContext(), CardViewPagerActivity.class);
        applicationManager.setCategoryModel(categoryModel);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ApplicationConstants.CATEGORY_COLOR, categoryModel.color);
        intent.putExtra(intentKey, true);
        getApplicationContext().startActivity(intent);
    }

    private View.OnClickListener deleteCategoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<CardModel> singleCardList = cardRepository.getSingleCardListWithCategoryId(selectedCategoryId);

            for (CardModel cardModel : singleCardList) {
                FileUtil.removeFile(cardModel.contentPath);
                FileUtil.removeFile(cardModel.voicePath);
                FileUtil.removeFile(cardModel.thumbnailPath);
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

    private void launchNotification() {
        NotificationActionManager notificationActionManager = new NotificationActionManager(this);
        notificationActionManager.generateNotification(new Intent(this, NotificationActionReceiver.class));
    }
}
