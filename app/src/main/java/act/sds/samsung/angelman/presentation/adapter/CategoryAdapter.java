package act.sds.samsung.angelman.presentation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.presentation.activity.CategoryMenuActivity;
import act.sds.samsung.angelman.presentation.util.FontUtil;
import act.sds.samsung.angelman.presentation.util.ResourceMapper;
import act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconState.DEFAULT;

public class CategoryAdapter extends BaseAdapter {

    private static final int CARD_MAX_SIZE = 6;
    private static CategoryModel newCategoryModel = null;
    private Context context;
    private List<CategoryModel> categoryList;
    private CategoryMenuActivity.CategoryMenuStatus categoryMenuStatus;
    private boolean isMotherMode = false;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        categoryMenuStatus = CategoryMenuActivity.CategoryMenuStatus.NONE;
    }

    public CategoryAdapter(Context context, ArrayList<CategoryModel> categoryList, boolean isMotherMode) {
        this.context = context;
        this.categoryList = categoryList;
        categoryMenuStatus = CategoryMenuActivity.CategoryMenuStatus.CATEGORY_DEFAULT;
        this.isMotherMode = isMotherMode;
    }

    @Override
    public int getCount() {
        if (addCategoryVisible()) {
            return categoryList.size() + 1;
        } else {
            return categoryList.size();
        }
    }

    @Override
    public CategoryModel getItem(int position) {
        if (addCategoryVisible() && position == categoryList.size()) {
            return newCategoryModel;
        } else {
            return categoryList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final CardView cardViewItem;

        if (categoryMenuStatus == CategoryMenuActivity.CategoryMenuStatus.CATEGORY_DELETABLE) {
            cardViewItem = getCardView(position, parent);
            startDeletableCardAnimation(cardViewItem);
        } else {
            if (isAddNewCategoryInPosition(position)) {
                cardViewItem = (CardView) LayoutInflater.from(context).inflate(R.layout.category_card, parent, false);
                addNewCategoryModel(cardViewItem);
            } else {
                if (view == null || isNotSameCardWithPosition(view, position)) {
                    cardViewItem = getCardView(position, parent);
                } else {
                    cardViewItem = (CardView) view;
                }
            }
            cardViewItem.getLayoutParams().height = cardViewItem.getLayoutParams().width = getCardViewHeightSize();
            cardViewItem.clearAnimation();
        }
        setRemovableButtonVisible(cardViewItem);
        return cardViewItem;
    }

    private void startDeletableCardAnimation(final CardView cardViewItem) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cardViewItem.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
            }
        }, 1);
    }

    private boolean isNotSameCardWithPosition(View view, int position) {
        CardView cardView = (CardView) view;
        TextView categoryTitle = (TextView) cardView.findViewById(R.id.category_title);
        if (categoryList.size() < position) {
            return false;
        }
        return categoryTitle.getText().toString().equals(context.getResources().getString(R.string.new_category))
                || !categoryList.get(position).title.equals(categoryTitle.getText().toString());
    }

    public void removeItem(int category) {
        for (int index = 0; index < categoryList.size(); index++) {
            CategoryModel categoryModel = categoryList.get(index);
            if (categoryModel.index == category) {
                categoryList.remove(index);
            }
        }
        this.notifyDataSetChanged();
    }

    private CardView getCardView(int position, ViewGroup parent) {
        CardView cardViewItem = (CardView) LayoutInflater.from(context).inflate(R.layout.category_card, parent, false);
        TextView categoryTitle = (TextView) cardViewItem.findViewById(R.id.category_title);
        categoryTitle.setTypeface(FontUtil.setFont(context, FontUtil.FONT_MEDIUM));
        ImageView categoryIcon = (ImageView) cardViewItem.findViewById(R.id.category_icon);
        RelativeLayout categoryItemLayout = (RelativeLayout) cardViewItem.findViewById(R.id.category_item_layout);

        CategoryModel categoryModel = categoryList.get(position);
        categoryItemLayout.setBackground(getResourceDrawable(ResourcesUtil.getCardViewLayoutBackgroundBy(categoryModel.color)));
        final IconType[] values = IconType.values();
        categoryIcon.setImageDrawable(getResourceDrawable(ResourceMapper.getCategoryIconResourceId(values[categoryModel.icon].ordinal(), DEFAULT.ordinal())));

        categoryTitle.setText(categoryModel.title);
        cardViewItem.getLayoutParams().height = cardViewItem.getLayoutParams().width = getCardViewHeightSize();
        return cardViewItem;
    }

    private CategoryModel addNewCategoryModel(CardView cardViewItem) {
        if (newCategoryModel == null) {
            newCategoryModel = new CategoryModel();
        }
        newCategoryModel.index = -1;
        newCategoryModel.color = Color.TRANSPARENT;
        newCategoryModel.icon = R.drawable.ic_add_category;
        newCategoryModel.title = context.getResources().getString(R.string.new_category);

        ImageView categoryIcon = (ImageView) cardViewItem.findViewById(R.id.category_icon);
        RelativeLayout categoryItemLayout = (RelativeLayout) cardViewItem.findViewById(R.id.category_item_layout);
        TextView cardTitle = (TextView) cardViewItem.findViewById(R.id.category_title);

        categoryItemLayout.setBackground(context.getResources().getDrawable(R.drawable.drop_shadow_dashgap));
        categoryItemLayout.setAlpha(0.7f);
        categoryIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_category));
        cardTitle.setText(newCategoryModel.title);
        cardTitle.setTypeface(FontUtil.setFont(context, FontUtil.FONT_DEMILIGHT));
        hideCardViewShadow(cardViewItem);
        return newCategoryModel;
    }

    private void hideCardViewShadow(CardView cardViewItem) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cardViewItem.setElevation(0f);
        }
    }

    private void setRemovableButtonVisible(CardView cardViewItem) {
        switch (categoryMenuStatus) {
            case CATEGORY_DEFAULT:
                cardViewItem.findViewById(R.id.delete_button).setVisibility(View.GONE);
                break;
            case CATEGORY_DELETABLE:
                cardViewItem.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean addCategoryVisible() {
        return categoryList.size() != CARD_MAX_SIZE && isMotherMode && categoryMenuStatus == CategoryMenuActivity.CategoryMenuStatus.CATEGORY_DEFAULT;
    }

    private boolean isAddNewCategoryInPosition(int position) {
        return isMotherMode && position == categoryList.size() && categoryMenuStatus == CategoryMenuActivity.CategoryMenuStatus.CATEGORY_DEFAULT;
    }

    public void changeCategoryItemsStatus(CategoryMenuActivity.CategoryMenuStatus status) {
        categoryMenuStatus = status;
        this.notifyDataSetChanged();
    }

    public CategoryMenuActivity.CategoryMenuStatus getCategoryMenuStatus() {
        return categoryMenuStatus;
    }

    public void setCategoryList(ArrayList<CategoryModel> categoryList) {
        this.categoryList = categoryList;
        this.notifyDataSetChanged();
    }

    private int getCardViewHeightSize() {
        int dmH = ((AngelmanApplication) context.getApplicationContext()).getScreenHeightPixel();

        if (dmH > 2000)
            return 600;
        else if (dmH < 1300) {
            return 300;
        } else {
            return 440;
        }
    }

    private Drawable getResourceDrawable(int id) {
        if (id > 0) {
            return context.getResources().getDrawable(id);
        } else {
            return context.getResources().getDrawable(R.color.white);
        }
    }
}
