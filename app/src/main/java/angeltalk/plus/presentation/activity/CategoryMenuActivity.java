package angeltalk.plus.presentation.activity;

import dagger.hilt.android.AndroidEntryPoint;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import javax.inject.Inject;

import angeltalk.plus.AngelmanApplication;
import angeltalk.plus.R;
import angeltalk.plus.domain.model.CardModel;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.domain.repository.CardRepository;
import angeltalk.plus.domain.repository.CategoryRepository;
import angeltalk.plus.network.transfer.CardTransfer;
import angeltalk.plus.presentation.adapter.CategoryAdapter;
import angeltalk.plus.presentation.custom.CustomConfirmDialog;
import angeltalk.plus.presentation.manager.ApplicationConstants;
import angeltalk.plus.presentation.manager.ApplicationManager;
import angeltalk.plus.presentation.manager.NotificationActionManager;
import angeltalk.plus.presentation.receiver.NotificationActionReceiver;
import angeltalk.plus.presentation.util.FileUtil;
import static angeltalk.plus.R.string.delete_category;

@AndroidEntryPoint
public class CategoryMenuActivity extends AbstractActivity  implements NavigationView.OnNavigationItemSelectedListener{

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CardRepository cardRepository;

    @Inject
    CardTransfer cardTransfer;

    @Inject
    ApplicationManager applicationManager;

    @Inject
    NotificationActionManager notificationActionManager;

    public GridView categoryGridView;

    public ImageView logoButton;

    public ImageView categoryDeleteButton;

    NavigationView navigationView;

    public String vocWebUrl;

    public String newCategoryString;

    public String oneLeftDeleteCategory;

    public DrawerLayout drawer;

    public enum CategoryMenuStatus {
        NONE, CATEGORY_DEFAULT, CATEGORY_DELETABLE
    }

    protected CategoryAdapter categoryAdapter;
    private CustomConfirmDialog dialog;
    private int selectedCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN));
        setContentView(R.layout.activity_main_menu);
        bindViews();
        initNavigationView();
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
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        Uri webpage;

        if (id == R.id.privacy) {
            webpage = Uri.parse(getString(R.string.privacy_web_url));
        } else if (id == R.id.usages) {
            webpage = Uri.parse(getString(R.string.terms_web_url));
        } else if (id == R.id.oss) {
            webpage = Uri.parse(getString(R.string.oss_web_url));
        } else if (id == R.id.voc) {
            webpage = Uri.parse(getString(R.string.voc_web_url));
        }else{
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }
    // TODO(phase-6): @OnClick wiring removed — wire setOnClickListener in bindViews()
    public void onClickDrawerMenu(View v) {
        drawer.openDrawer(GravityCompat.START);
    }
    // TODO(phase-6): @OnClick wiring removed — wire setOnClickListener in bindViews()
    public void onClickCategoryDeleteButton(View v) {
        if (categoryAdapter.getCategoryMenuStatus().equals(CategoryMenuStatus.CATEGORY_DEFAULT)) {
            changeCategoryMenuStatus(CategoryMenuStatus.CATEGORY_DELETABLE);
        } else {
            changeCategoryMenuStatus(CategoryMenuStatus.CATEGORY_DEFAULT);
        }
    }

    private void initNavigationView() {
        Glide.with(CategoryMenuActivity.this)
                .load(R.drawable.angelee)


                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(((ImageView) navigationView.getHeaderView(0).findViewById(R.id.slide_menu_angel)));

        drawer.closeDrawer(GravityCompat.START);
        navigationView.setNavigationItemSelectedListener(this);
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
                categoryDeleteButton.setImageDrawable(getDrawable(R.drawable.btn_delete_dark));
                break;
            case CATEGORY_DELETABLE:
                categoryDeleteButton.setImageDrawable(getDrawable(R.drawable.btn_confirm_dark));
                break;
            default:
                Log.e("error", "category is not set");
                break;
        }
        categoryAdapter.changeCategoryItemsStatus(categoryMenuStatus);
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
        notificationActionManager.generateNotification(new Intent(this, NotificationActionReceiver.class));
    }
    private void bindViews() {
        vocWebUrl = getString(R.string.voc_web_url);
        newCategoryString = getString(R.string.new_category_button_text);
        oneLeftDeleteCategory = getString(R.string.one_left_delete_category);
        categoryGridView = findViewById(R.id.category_list);
        logoButton = findViewById(R.id.logo_angeltalk);
        categoryDeleteButton = findViewById(R.id.category_delete_button);
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        findViewById(R.id.drawer_meun).setOnClickListener(v -> onClickDrawerMenu(v));
        findViewById(R.id.category_delete_button).setOnClickListener(v -> onClickCategoryDeleteButton(v));
    }
}
