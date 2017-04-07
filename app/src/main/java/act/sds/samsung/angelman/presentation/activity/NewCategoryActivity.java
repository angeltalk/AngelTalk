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

import java.io.UnsupportedEncodingException;
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
import act.sds.samsung.angelman.presentation.manager.ApplicationConstants;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.DialogUtil;
import act.sds.samsung.angelman.presentation.util.FontUtil;

import static act.sds.samsung.angelman.presentation.util.ResourceMapper.ColorState;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconState;

public class NewCategoryActivity extends AbstractActivity{

    private RecyclerView iconListView;
    private RecyclerView backgroundListView;

    private boolean dataChanged = false;

    @Inject
    CategoryRepository repository;

    @Inject
    ApplicationManager applicationManager;

    private TextView categoryTitleTextView;
    private EditText editCategoryTitle;
    private Button saveButton;
    private ImageView cancelButton;
    NewCategoryItemAdapter iconAdapter;
    NewCategoryItemAdapter backgroundAdapter;
    private RelativeLayout categoryHeader;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);

        cancelButton = (ImageView) findViewById(R.id.category_title_cancel);
        iconListView = (RecyclerView) findViewById(R.id.icon_list);
        backgroundListView = (RecyclerView) findViewById(R.id.color_list);
        ImageView categoryImage = (ImageView) findViewById(R.id.category_icon);
        RelativeLayout categoryColor = (RelativeLayout) findViewById(R.id.new_category_color);
        categoryHeader = (RelativeLayout) findViewById(R.id.new_category_header);

        LinearLayoutManager iconListLayoutManager = new LinearLayoutManager(this);
        iconListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        LinearLayoutManager backgroundListLayoutManager = new LinearLayoutManager(this);
        backgroundListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        iconListView.setLayoutManager(iconListLayoutManager);
        backgroundListView.setLayoutManager(backgroundListLayoutManager);

        List<CategoryItemModel> iconList = repository.getCategoryAllIconList();
        List<CategoryItemModel> backgroundList = repository.getCategoryAllBackgroundList();

        iconAdapter = new NewCategoryItemIconAdapter(categoryImage, sortIconListByStatus(iconList, IconState.USED.ordinal()));
        backgroundAdapter = new NewCategoryItemColorAdapter(categoryColor, sortIconListByStatus(backgroundList, ColorState.USED.ordinal()));

        iconAdapter.setCategoryChangeListener(categoryChangeListener);
        backgroundAdapter.setCategoryChangeListener(categoryChangeListener);

        iconListView.setAdapter(iconAdapter);
        backgroundListView.setAdapter(backgroundAdapter);

        categoryTitleTextView = (TextView) findViewById(R.id.category_title);
        saveButton = (Button)findViewById(R.id.new_category_save_button);
        categoryTitleTextView.setText(R.string.new_category_name);

        setFont();

        editCategoryTitle = (EditText) findViewById(R.id.edit_category_title);
        editCategoryTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    categoryHeader.requestFocus();
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(categoryHeader.getWindowToken(), 0);

                }
                return false;
            }
        });

        editCategoryTitle.addTextChangedListener(textChangeWatcher);

        ImageView leftArrowButton = (ImageView) findViewById(R.id.left_arrow_button);
        leftArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryModel model = new CategoryModel();
                model.title = categoryTitleTextView.getText().toString();
                model.icon = iconAdapter.getSelectedItem().type;
                model.color = backgroundAdapter.getSelectedItem().type;
                model.index = repository.saveNewCategoryItemAndReturnId(model);
                moveToNextActivity(model);
                finish();
            }
        });

        saveButton.setEnabled(false);
        saveButton.setTypeface(FontUtil.setFont(this, FontUtil.FONT_REGULAR));

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCategoryTitle.setText("");
                categoryTitleTextView.setText(R.string.new_category_name);
            }
        });
    }

    private void setFont() {
        FontUtil.setGlobalFont(getWindow().getDecorView(), FontUtil.FONT_REGULAR);
        categoryTitleTextView.setTypeface(FontUtil.setFont(this, FontUtil.FONT_MEDIUM));
    }

    @Override
    public void onBackPressed() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editCategoryTitle.getWindowToken(), 0);
        if(dataChanged) {
            View innerView = getLayoutInflater().inflate(R.layout.custom_confirm_dialog, null);
            TextView alertMessage = (TextView) innerView.findViewById(R.id.alert_message);
            alertMessage.setText(getString(R.string.inform_not_saved));

            alertDialog = DialogUtil.buildCustomDialog(NewCategoryActivity.this, innerView, positiveListener, negativeListener);
            alertDialog.show();
        } else {
            finish();
        }
    }

    private View.OnClickListener positiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            alertDialog.dismiss();
            finish();
        }
    };

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

                try {
                    if(editCategoryTitle.getText().toString().getBytes("euc-kr").length > 12){
                        s.delete(s.length()-1, s.length());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
}
