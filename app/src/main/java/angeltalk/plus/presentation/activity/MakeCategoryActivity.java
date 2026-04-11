package angeltalk.plus.presentation.activity;

import dagger.hilt.android.AndroidEntryPoint;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.inject.Inject;

import angeltalk.plus.AngelmanApplication;
import angeltalk.plus.R;
import angeltalk.plus.domain.model.CategoryItemModel;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.domain.repository.CategoryRepository;
import angeltalk.plus.presentation.adapter.NewCategoryItemAdapter;
import angeltalk.plus.presentation.adapter.NewCategoryItemColorAdapter;
import angeltalk.plus.presentation.adapter.NewCategoryItemIconAdapter;
import angeltalk.plus.presentation.custom.CustomConfirmDialog;
import angeltalk.plus.presentation.manager.ApplicationConstants;
import angeltalk.plus.presentation.manager.ApplicationManager;
import static angeltalk.plus.presentation.util.ResourceMapper.ColorState;
import static angeltalk.plus.presentation.util.ResourceMapper.IconState;
@AndroidEntryPoint
public class MakeCategoryActivity extends AbstractActivity{

    @Inject
    CategoryRepository repository;

    @Inject
    ApplicationManager applicationManager;

    public ImageView cancelButton;

    public RecyclerView iconListView;

    public RecyclerView backgroundListView;

    public ImageView categoryImage;

    public RelativeLayout categoryColor;

    public RelativeLayout categoryHeader;

    public TextView categoryTitleTextView;

    public ImageView newCategorySaveButton;

    public EditText editCategoryTitle;

    private boolean dataChanged = false;
    private NewCategoryItemAdapter iconAdapter;
    private NewCategoryItemAdapter backgroundAdapter;
    private CustomConfirmDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);
        bindViews();
        initListView();

        this.changeEnabledStatusOfNewCategorySaveButton(false);
    }

    @Override
    public void onBackPressed() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editCategoryTitle.getWindowToken(), 0);
        if(dataChanged) {
            View innerView = getLayoutInflater().inflate(R.layout.custom_confirm_dialog, null);
            TextView alertMessage = (TextView) innerView.findViewById(R.id.alert_message);
            alertMessage.setText(getString(R.string.inform_not_saved));
            alertDialog  = new CustomConfirmDialog(this, getString(R.string.inform_not_saved), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    finish();
                }
            });
            alertDialog.show();
        } else {
            finish();
        }
    }
    // TODO(phase-6): @OnClick wiring removed — wire setOnClickListener in bindViews()
    public void onClickLeftArrowButton(View v) {
        onBackPressed();
    }
    // TODO(phase-6): @OnClick wiring removed — wire setOnClickListener in bindViews()
    public void onClickNewCategorySaveButton(View v) {
        CategoryModel model = new CategoryModel();
        model.title = categoryTitleTextView.getText().toString();
        model.icon = iconAdapter.getSelectedItem().type;
        model.color = backgroundAdapter.getSelectedItem().type;
        model.index = repository.saveNewCategoryItemAndReturnId(model);
        moveToNextActivity(model);
        finish();
    }
    // TODO(phase-6): @OnClick wiring removed — wire setOnClickListener in bindViews()
    public void onClickCategoryTitleCancel(View v) {
        editCategoryTitle.setText("");
        categoryTitleTextView.setText(R.string.new_category_name);
    }
    // TODO(phase-6): @OnEditorAction removed — wire EditorActionListener in bindViews()
    public boolean onEditorActionCategoryTitle(TextView view, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE){
            categoryHeader.requestFocus();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(categoryHeader.getWindowToken(), 0);

        }
        return false;
    }
    // TODO(phase-6): @OnTextChanged removed — wire TextWatcher in bindViews()
    public void onTextChangedCategoryTitle(CharSequence s, int start, int before, int count) {
        dataChanged = true;
    }
    // TODO(phase-6): @OnTextChanged removed — wire TextWatcher in bindViews()
    public void afterTextChangedCategoryTitle(Editable s) {
        if (editCategoryTitle.getText().length() > 0) {
            categoryTitleTextView.setText(editCategoryTitle.getText().toString());

            this.changeEnabledStatusOfNewCategorySaveButton(true);
            cancelButton.setVisibility(View.VISIBLE);

            try {
                if(editCategoryTitle.getText().toString().getBytes("euc-kr").length > 12){
                    s.delete(s.length()-1, s.length());
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            categoryTitleTextView.setText(R.string.new_category_name);
            this.changeEnabledStatusOfNewCategorySaveButton(false);
            cancelButton.setVisibility(View.INVISIBLE);

            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(categoryHeader.getWindowToken(), 0);
        }
    }

    @NonNull
    private LinearLayoutManager getHorizontalListLayoutManager() {
        LinearLayoutManager iconListLayoutManager = new LinearLayoutManager(this);
        iconListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        return iconListLayoutManager;
    }

    private void initListView() {
        List<CategoryItemModel> iconList = repository.getCategoryAllIconList();
        iconListView.setLayoutManager(getHorizontalListLayoutManager());
        iconAdapter = new NewCategoryItemIconAdapter(categoryImage, sortIconListByStatus(iconList, IconState.USED.ordinal()));
        iconAdapter.setCategoryChangeListener(new NewCategoryItemAdapter.CategoryChangeListener() {
            @Override
            public void categoryChanged() {
                dataChanged = true;
            }});
        iconListView.setAdapter(iconAdapter);

        List<CategoryItemModel> backgroundList = repository.getCategoryAllBackgroundList();
        backgroundListView.setLayoutManager(getHorizontalListLayoutManager());
        backgroundAdapter = new NewCategoryItemColorAdapter(categoryColor, sortIconListByStatus(backgroundList, ColorState.USED.ordinal()));
        backgroundAdapter.setCategoryChangeListener(new NewCategoryItemAdapter.CategoryChangeListener() {
                @Override
                public void categoryChanged() {
                    dataChanged = true;
                }});
        backgroundListView.setAdapter(backgroundAdapter);
    }

    private void moveToNextActivity(CategoryModel categoryModel) {
        Intent intent = new Intent(getApplicationContext(), CardViewPagerActivity.class);
        applicationManager.setCategoryModel(categoryModel);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ApplicationConstants.CATEGORY_COLOR, categoryModel.color);
        getApplicationContext().startActivity(intent);
    }

    private List<CategoryItemModel> sortIconListByStatus(List<CategoryItemModel> list, int usedStateByType){
        List<CategoryItemModel> usedItem = Lists.newArrayList();
        List<CategoryItemModel> unusedItem = Lists.newArrayList();

        for(int i = 0 ; i < list.size() ; i++){
            if(list.get(i).status == usedStateByType){
                usedItem.add(list.get(i));
            } else {
                unusedItem.add(list.get(i));
            }
        }
        unusedItem.addAll(usedItem);

        return unusedItem;

    }

    private void changeEnabledStatusOfNewCategorySaveButton(boolean isEnabled) {
        if (isEnabled) {
            newCategorySaveButton.setEnabled(true);
            newCategorySaveButton.setImageAlpha(255);
        } else {
            newCategorySaveButton.setEnabled(false);
            newCategorySaveButton.setImageAlpha(77);
        }
    }
    private void bindViews() {
        cancelButton = findViewById(R.id.category_title_cancel);
        iconListView = findViewById(R.id.icon_list);
        backgroundListView = findViewById(R.id.color_list);
        categoryImage = findViewById(R.id.category_icon);
        categoryColor = findViewById(R.id.new_category_color);
        categoryHeader = findViewById(R.id.new_category_header);
        categoryTitleTextView = findViewById(R.id.category_title);
        newCategorySaveButton = findViewById(R.id.new_category_save_button);
        editCategoryTitle = findViewById(R.id.edit_category_title);
        findViewById(R.id.left_arrow_button).setOnClickListener(v -> onClickLeftArrowButton(v));
        findViewById(R.id.new_category_save_button).setOnClickListener(v -> onClickNewCategorySaveButton(v));
        findViewById(R.id.category_title_cancel).setOnClickListener(v -> onClickCategoryTitleCancel(v));

        editCategoryTitle.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextChangedCategoryTitle(s, start, before, count);
            }
            @Override public void afterTextChanged(Editable s) {
                afterTextChangedCategoryTitle(s);
            }
        });
        editCategoryTitle.setOnEditorActionListener((view, actionId, event) ->
                onEditorActionCategoryTitle(view, actionId, event));
    }
}
