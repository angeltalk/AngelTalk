package act.angelman.presentation.adapter;

import android.widget.RelativeLayout;

import java.util.List;

import act.angelman.domain.model.CategoryItemModel;

import static act.angelman.presentation.util.ResourceMapper.ColorState;
import static act.angelman.presentation.util.ResourceMapper.getCategoryColorResourceId;


public class NewCategoryItemColorAdapter extends NewCategoryItemAdapter {
    private RelativeLayout categoryColor;

    public NewCategoryItemColorAdapter(RelativeLayout categoryColor, List<CategoryItemModel> categoryItemList) {
        super(categoryItemList);
        this.categoryColor = categoryColor;
    }

    @Override
    protected boolean isUsedItem(int position) {
        return categoryItemList.get(position).status == ColorState.USED.ordinal();
    }

    @Override
    protected void setInitialPosition(NewCategoryItemAdapter.NewCategoryItemViewHolder holder) {
        selectItem(holder, getCategoryColorResourceId(categoryItemList.get(selectedPosition).type, ColorState.SELECT.ordinal()), selectedPosition);
        categoryColor.setBackgroundResource(getCategoryColorResourceId(categoryItemList.get(selectedPosition).type, ColorState.MENU.ordinal()));
    }

    @Override
    protected void changeItem(NewCategoryItemAdapter.NewCategoryItemViewHolder holder) {
        int currentPosition = holder.getAdapterPosition();
        selectItem(holder, getCategoryColorResourceId(categoryItemList.get(currentPosition).type, ColorState.SELECT.ordinal()), currentPosition);
        unselectItem(getCategoryColorResourceId(categoryItemList.get(selectedPosition).type, ColorState.UNSELECT.ordinal()));
        categoryColor.setBackgroundResource(getCategoryColorResourceId(categoryItemList.get(currentPosition).type, ColorState.MENU.ordinal()));
    }

    @Override
    protected void selectItem(NewCategoryItemViewHolder holder, int categoryIconResourceId, int selectedPosition) {
        super.selectItem(holder, categoryIconResourceId, selectedPosition);
        categoryItemList.get(selectedPosition).status = ColorState.SELECT.ordinal();
    }

    @Override
    protected void unselectItem(int categoryIconResourceId) {
        super.unselectItem(categoryIconResourceId);
        categoryItemList.get(selectedPosition).status = ColorState.UNSELECT.ordinal();
    }

    @Override
    protected void setImageResourceByTypeAndStatus(NewCategoryItemAdapter.NewCategoryItemViewHolder holder, int position) {
        holder.categoryItem.setImageResource(getCategoryColorResourceId(categoryItemList.get(position).type, categoryItemList.get(position).status));
    }
}
