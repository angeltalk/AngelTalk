package act.sds.samsung.angelman.presentation.adapter;

import android.widget.ImageView;

import java.util.List;

import act.sds.samsung.angelman.domain.model.CategoryItemModel;

import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconState;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.getCategoryIconResourceId;


public class NewCategoryItemIconAdapter extends NewCategoryItemAdapter {
    private ImageView categoryImage;

    public NewCategoryItemIconAdapter(ImageView categoryImage, List<CategoryItemModel> categoryItemList) {
        super(categoryItemList);
        this.categoryImage = categoryImage;
    }

    @Override
    protected boolean isUsedItem(int position) {
        return categoryItemList.get(position).status == IconState.USED.ordinal();
    }

    @Override
    protected void setInitialPosition(NewCategoryItemViewHolder holder) {
        selectItem(holder, getCategoryIconResourceId(categoryItemList.get(selectedPosition).type, IconState.SELECT.ordinal()), selectedPosition);
        categoryImage.setImageResource(getCategoryIconResourceId(categoryItemList.get(selectedPosition).type, IconState.DEFAULT.ordinal()));
    }

    @Override
    protected void changeItem(NewCategoryItemViewHolder holder) {
        int currentPosition = holder.getAdapterPosition();
        selectItem(holder, getCategoryIconResourceId(categoryItemList.get(currentPosition).type, IconState.SELECT.ordinal()), currentPosition);
        unselectItem(getCategoryIconResourceId(categoryItemList.get(selectedPosition).type, IconState.UNSELECT.ordinal()));
        categoryImage.setImageResource(getCategoryIconResourceId(categoryItemList.get(currentPosition).type, IconState.DEFAULT.ordinal()));
    }

    @Override
    protected void selectItem(NewCategoryItemViewHolder holder, int categoryIconResourceId, int selectedPosition) {
        super.selectItem(holder, categoryIconResourceId, selectedPosition);
        categoryItemList.get(selectedPosition).status = IconState.SELECT.ordinal();
    }

    @Override
    protected void unselectItem(int categoryIconResourceId) {
        super.unselectItem(categoryIconResourceId);
        selectedHolder.categoryItem.setImageResource(categoryIconResourceId);
    }

    @Override
    protected void setImageResourceByTypeAndStatus(NewCategoryItemViewHolder holder, int position) {
        holder.categoryItem.setImageResource(getCategoryIconResourceId(categoryItemList.get(position).type, categoryItemList.get(position).status));
    }
}
