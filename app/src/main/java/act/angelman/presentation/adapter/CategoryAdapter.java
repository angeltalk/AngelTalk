package act.angelman.presentation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

import java.util.List;

import act.angelman.R;
import act.angelman.domain.model.CategoryModel;
import act.angelman.presentation.activity.CategoryMenuActivity;
import act.angelman.presentation.util.ResourceMapper;
import act.angelman.presentation.util.ResourceMapper.IconType;
import act.angelman.presentation.util.ResourcesUtil;

import static act.angelman.presentation.util.ResourceMapper.IconState.DEFAULT;

public class CategoryAdapter extends BaseAdapter {

    private static CategoryModel newCategoryModel = null;
    private List<CategoryModel> categoryList;
    private Context context;
    private CategoryMenuActivity.CategoryMenuStatus categoryMenuStatus;

    private static final int CARD_MAX_SIZE = 6;

    private boolean isMotherMode = false;

    public CategoryAdapter(Context context, List<CategoryModel> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        categoryMenuStatus = CategoryMenuActivity.CategoryMenuStatus.NONE;
    }

    public CategoryAdapter(Context context, List<CategoryModel> categoryList, boolean isMotherMode) {
        this.context = context;
        this.categoryList = categoryList;
        categoryMenuStatus = CategoryMenuActivity.CategoryMenuStatus.CATEGORY_DEFAULT;
        this.isMotherMode = isMotherMode;
    }

    @Override
    public int getCount() {
        if (addCategoryVisible())
            return categoryList.size() + 1;
        return categoryList.size();
    }

    @Override
    public CategoryModel getItem(int position) {
        if (addCategoryVisible() && position == categoryList.size())
            return newCategoryModel;

        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final CardView cardViewItem;

        if (categoryMenuStatus == CategoryMenuActivity.CategoryMenuStatus.CATEGORY_DELETABLE) {
            cardViewItem = makeCard(position, parent);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cardViewItem.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                }
            }, 1);

        } else {

            if (isAddNewCategoryInPosition(position)) {
                cardViewItem = (CardView) LayoutInflater.from(context).inflate(R.layout.category_card, parent, false);
                addNewCategoryModel(cardViewItem);
            }else{
                if (view == null) {
                    cardViewItem = makeCard(position, parent);
                } else if (isNotSameCardWithPosition(view, position)) {
                    cardViewItem = makeCard(position, parent);
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

    private boolean isNotSameCardWithPosition(View view, int position) {
        CardView cardView = (CardView) view;
        TextView categoryTitle = (TextView) cardView.findViewById(R.id.category_title);
        if (categoryList.size() < position) {
            return false;
        }
        return categoryTitle.getText().toString().equals(context.getResources().getString(R.string.new_category)) || !categoryList.get(position).title.equals(categoryTitle.getText().toString());
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

    private CardView makeCard(int position, ViewGroup parent) {
        CardView cardViewItem = (CardView) LayoutInflater.from(context).inflate(R.layout.category_card, parent, false);
        TextView categoryTitle = (TextView) cardViewItem.findViewById(R.id.category_title);
        ImageView categoryIcon = (ImageView) cardViewItem.findViewById(R.id.category_icon);
        RelativeLayout categoryItemLayout = (RelativeLayout) cardViewItem.findViewById(R.id.category_item_layout);

        CategoryModel categoryModel = categoryList.get(position);
        categoryItemLayout.setBackground(getResourceDrawable(ResourcesUtil.getCardViewLayoutBackgroundBy(categoryModel.color)));
        final IconType[] values = IconType.values();
        categoryIcon.setImageDrawable(getResourceDrawable(ResourceMapper.getCategoryIconResourceId(values[categoryModel.icon].ordinal(), DEFAULT.ordinal())));

        categoryTitle.setText(categoryModel.title);
        categoryTitle.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_medium)));

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
        newCategoryModel.title = context.getResources().getString(R.string.new_category_button_text);

        ImageView categoryIcon = (ImageView) cardViewItem.findViewById(R.id.category_icon);
        RelativeLayout categoryItemLayout = (RelativeLayout) cardViewItem.findViewById(R.id.category_item_layout);
        TextView categoryTitle = (TextView) cardViewItem.findViewById(R.id.category_title);

        categoryItemLayout.setBackground(context.getResources().getDrawable(R.drawable.drop_shadow_dashgap));
        categoryItemLayout.setAlpha(0.7f);
        categoryIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_category));
        categoryTitle.setText(newCategoryModel.title);
        categoryTitle.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_medium)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cardViewItem.setElevation(0f);
        }
        return newCategoryModel;
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

    public void setCategoryList(List<CategoryModel> categoryList) {
        this.categoryList = categoryList;
        this.notifyDataSetChanged();
    }

    private int getCardViewHeightSize() {
        int dmW = context.getResources().getDisplayMetrics().widthPixels;
        return (int) (dmW * 0.4f);
    }

    private Drawable getResourceDrawable(int id) {
        if (id > 0)
            return context.getResources().getDrawable(id);
        else
            return context.getResources().getDrawable(R.color.white);
    }
}
