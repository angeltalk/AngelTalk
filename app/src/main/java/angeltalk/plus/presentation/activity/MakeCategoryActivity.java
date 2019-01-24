package angeltalk.plus.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

import static angeltalk.plus.presentation.util.ResourceMapper.ColorState;
import static angeltalk.plus.presentation.util.ResourceMapper.IconState;
import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;

public class MakeCategoryActivity extends AbstractActivity{

    @Inject
    CategoryRepository repository;

    @Inject
    ApplicationManager applicationManager;

    @BindView(R.id.category_title_cancel)
    public ImageView cancelButton;

    @BindView(R.id.icon_list)
    public RecyclerView iconListView;

    @BindView(R.id.color_list)
    public RecyclerView backgroundListView;

    @BindView(R.id.category_icon)
    public ImageView categoryImage;

    @BindView(R.id.new_category_color)
    public RelativeLayout categoryColor;

    @BindView(R.id.new_category_header)
    public RelativeLayout categoryHeader;

    @BindView(R.id.category_title)
    public TextView categoryTitleTextView;

    @BindView(R.id.new_category_save_button)
    public ImageView newCategorySaveButton;

    @BindView(R.id.edit_category_title)
    public EditText editCategoryTitle;

    private boolean dataChanged = false;
    private NewCategoryItemAdapter iconAdapter;
    private NewCategoryItemAdapter backgroundAdapter;
    private CustomConfirmDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        setContentView(R.layout.activity_new_category);
        ButterKnife.bind(this);
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

    @OnClick(R.id.left_arrow_button)
    public void onClickLeftArrowButton(View v) {
        onBackPressed();
    }

    @OnClick(R.id.new_category_save_button)
    public void onClickNewCategorySaveButton(View v) {
        CategoryModel model = new CategoryModel();
        model.title = categoryTitleTextView.getText().toString();
        model.icon = iconAdapter.getSelectedItem().type;
        model.color = backgroundAdapter.getSelectedItem().type;
        model.index = repository.saveNewCategoryItemAndReturnId(model);
        moveToNextActivity(model);
        finish();
    }

    @OnClick(R.id.category_title_cancel)
    public void onClickCategoryTitleCancel(View v) {
        editCategoryTitle.setText("");
        categoryTitleTextView.setText(R.string.new_category_name);
    }

    @OnEditorAction(R.id.edit_category_title)
    public boolean onEditorActionCategoryTitle(TextView view, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE){
            categoryHeader.requestFocus();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(categoryHeader.getWindowToken(), 0);

        }
        return false;
    }

    @OnTextChanged(R.id.edit_category_title)
    public void onTextChangedCategoryTitle(CharSequence s, int start, int before, int count) {
        dataChanged = true;
    }

    @OnTextChanged(value = R.id.edit_category_title, callback = AFTER_TEXT_CHANGED)
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
}
