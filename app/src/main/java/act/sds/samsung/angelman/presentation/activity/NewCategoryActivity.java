package act.sds.samsung.angelman.presentation.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;

import java.util.List;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CategoryItemModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;
import act.sds.samsung.angelman.presentation.adapter.NewCategoryItemAdapter;
import act.sds.samsung.angelman.presentation.adapter.NewCategoryItemColorAdapter;
import act.sds.samsung.angelman.presentation.adapter.NewCategoryItemIconAdapter;
import act.sds.samsung.angelman.presentation.util.DialogUtil;
import act.sds.samsung.angelman.presentation.util.FontUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static act.sds.samsung.angelman.presentation.util.ResourceMapper.ColorState;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconState;

public class NewCategoryActivity extends AbstractActivity {

    private boolean dataChanged = false;

    @Inject
    CategoryRepository repository;

    @BindView(R.id.category_title)
    public TextView categoryTitleTextView;

    @BindView(R.id.edit_category_title)
    public EditText editCategoryTitle;

    @BindView(R.id.new_category_save_button)
    public Button saveButton;

    @BindView(R.id.category_title_cancel)
    public ImageView cancelButton;

    @BindView(R.id.icon_list)
    public RecyclerView iconListView;

    @BindView(R.id.color_list)
    public RecyclerView backgroundListView;

    @BindView(R.id.new_category_header)
    public RelativeLayout categoryHeader;

    private AlertDialog alertDialog;
    private NewCategoryItemAdapter iconAdapter;
    private NewCategoryItemAdapter backgroundAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);
        ButterKnife.bind(this);

        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);

        iconAdapter = createIconAdapter();
        iconAdapter.setCategoryChangeListener(categoryChangeListener);
        iconListView.setAdapter(iconAdapter);

        backgroundAdapter = createBackgroundAdapter();
        backgroundAdapter.setCategoryChangeListener(categoryChangeListener);
        backgroundListView.setAdapter(backgroundAdapter);

        categoryTitleTextView.setText(R.string.new_category_name);
        setRegularFont();

        editCategoryTitle.addTextChangedListener(textChangeWatcher);

        ImageView leftArrowButton = (ImageView) findViewById(R.id.left_arrow_button);
        leftArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        saveButton.setEnabled(false);
        saveButton.setTypeface(FontUtil.setFont(this, FontUtil.FONT_REGULAR));

    }

    private NewCategoryItemColorAdapter createBackgroundAdapter() {
        RelativeLayout categoryColor = (RelativeLayout) findViewById(R.id.new_category_color);
        LinearLayoutManager backgroundListLayoutManager = new LinearLayoutManager(this);
        backgroundListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        backgroundListView.setLayoutManager(backgroundListLayoutManager);
        List<CategoryItemModel> backgroundList = repository.getCategoryAllBackgroundList();
        return new NewCategoryItemColorAdapter(categoryColor, sortIconListByStatus(backgroundList, ColorState.USED.ordinal()));
    }

    private NewCategoryItemIconAdapter createIconAdapter() {
        ImageView categoryImage = (ImageView) findViewById(R.id.category_icon);
        LinearLayoutManager iconListLayoutManager = new LinearLayoutManager(this);
        iconListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        iconListView.setLayoutManager(iconListLayoutManager);
        List<CategoryItemModel> iconList = repository.getCategoryAllIconList();
        return new NewCategoryItemIconAdapter(categoryImage, sortIconListByStatus(iconList, IconState.USED.ordinal()));

    }

    private void setRegularFont() {
        FontUtil.setGlobalFont(getWindow().getDecorView(), FontUtil.FONT_REGULAR);
        categoryTitleTextView.setTypeface(FontUtil.setFont(this, FontUtil.FONT_MEDIUM));
    }


    private View.OnClickListener positiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            alertDialog.dismiss();
            finish();
        }
    };


    @Override
    public void onBackPressed() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editCategoryTitle.getWindowToken(), 0);
        if (dataChanged) {
            View innerView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
            TextView alertMessage = (TextView) innerView.findViewById(R.id.alert_message);
            alertMessage.setText(getString(R.string.inform_not_saved));
            alertDialog = DialogUtil.buildCustomDialog(NewCategoryActivity.this, innerView, positiveListener, negativeListener);
            alertDialog.show();
        } else {
            finish();
        }
    }


    @OnEditorAction(R.id.edit_category_title)
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            categoryHeader.requestFocus();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(categoryHeader.getWindowToken(), 0);
        }
        return false;
    }

    @OnClick(R.id.new_category_save_button)
    public void onClickSaveButton(View v) {
        CategoryModel model = new CategoryModel();
        model.title = categoryTitleTextView.getText().toString();
        model.icon = iconAdapter.getSelectedItem().type;
        model.color = backgroundAdapter.getSelectedItem().type;
        int id = repository.saveNewCategoryItemAndReturnId(model);
        model.index = id;
        moveToNextActivity(model);
        finish();
    }

    @OnClick(R.id.category_title_cancel)
    public void onClickCancelButton(View v) {
        editCategoryTitle.setText("");
        categoryTitleTextView.setText(R.string.new_category_name);
    }


    private View.OnClickListener negativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            alertDialog.dismiss();
        }
    };

    private TextWatcher textChangeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            dataChanged = true;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (editCategoryTitle.getText().length() > 0) {
                categoryTitleTextView.setText(editCategoryTitle.getText().toString());
                saveButton.setEnabled(true);
                saveButton.setTextColor(getResources().getColor(R.color.white));
                cancelButton.setVisibility(View.VISIBLE);
            } else {
                categoryTitleTextView.setText(R.string.new_category_name);
                saveButton.setEnabled(false);
                saveButton.setTextColor(getResources().getColor(R.color.white_32));
                cancelButton.setVisibility(View.INVISIBLE);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(categoryHeader.getWindowToken(), 0);
            }
        }
    };

    private NewCategoryItemAdapter.CategoryChangeListener categoryChangeListener = new NewCategoryItemAdapter.CategoryChangeListener() {
        @Override
        public void categoryChanged() {
            dataChanged = true;
        }
    };

    private void moveToNextActivity(CategoryModel categoryModel) {
        Intent intent = new Intent(getApplicationContext(), CardViewPagerActivity.class);
        ((AngelmanApplication) getApplicationContext()).setCategoryModel(categoryModel);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(CardViewPagerActivity.CATEGORY_COLOR, categoryModel.color);
        getApplicationContext().startActivity(intent);
    }

    private List<CategoryItemModel> sortIconListByStatus(List<CategoryItemModel> list, int usedStateByType) {
        List<CategoryItemModel> usedItem = Lists.newArrayList();
        List<CategoryItemModel> unUsedItem = Lists.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).status == usedStateByType) {
                usedItem.add(list.get(i));
            } else {
                unUsedItem.add(list.get(i));
            }
        }
        unUsedItem.addAll(usedItem);
        return unUsedItem;

    }
}
