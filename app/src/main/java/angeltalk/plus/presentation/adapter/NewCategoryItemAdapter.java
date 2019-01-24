package angeltalk.plus.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import angeltalk.plus.R;
import angeltalk.plus.domain.model.CategoryItemModel;

import static angeltalk.plus.presentation.util.ResourceMapper.IconState;


abstract public class NewCategoryItemAdapter extends RecyclerView.Adapter<NewCategoryItemAdapter.NewCategoryItemViewHolder> {
    private static final int INITIAL_POSITION = -1;
    List<CategoryItemModel> categoryItemList;
    int selectedPosition = INITIAL_POSITION;
    NewCategoryItemViewHolder selectedHolder;
    private CategoryChangeListener categoryChangeListener;

    NewCategoryItemAdapter(List<CategoryItemModel> categoryItemList) {
        this.categoryItemList = categoryItemList;
    }

    @Override
    public NewCategoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_icon, parent, false);
        return new NewCategoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NewCategoryItemViewHolder holder, final int position) {
        setImageResourceByTypeAndStatus(holder, position);

        if (isUsedItem(position)) {
            holder.categoryItem.setEnabled(false);
            holder.categoryItem.setOnClickListener(null);
        } else {
            holder.categoryItem.setEnabled(true);
            holder.categoryItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeSelected(holder);
                    notifyCategoryChanged();
                    notifyDataSetChanged();
                }
            });

            if (selectedPosition == INITIAL_POSITION) {
                selectedPosition = 0;
                setInitialPosition(holder);
                selectedHolder = holder;
            }
        }
    }

    private void notifyCategoryChanged() {
        if(categoryChangeListener != null) {
            categoryChangeListener.categoryChanged();
        }
    }

    abstract protected boolean isUsedItem(int position);

    abstract protected void setInitialPosition(NewCategoryItemViewHolder holder);

    abstract protected void changeItem(NewCategoryItemViewHolder holder);

    abstract protected void setImageResourceByTypeAndStatus(NewCategoryItemViewHolder holder, int position);

    private void changeSelected(NewCategoryItemViewHolder holder) {
        int currentPosition = holder.getAdapterPosition();
        if (currentPosition != selectedPosition) {
            changeItem(holder);
            selectedPosition = currentPosition;
            selectedHolder = holder;
        }
    }

    protected void selectItem(NewCategoryItemViewHolder holder, int categoryIconResourceId, int selectedPosition) {
        holder.categoryItem.setImageResource(categoryIconResourceId);
    }

    protected void unselectItem(int categoryIconResourceId) {
        if (selectedPosition > INITIAL_POSITION) {
            categoryItemList.get(selectedPosition).status = IconState.UNSELECT.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        return categoryItemList.size();
    }

    public class NewCategoryItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView categoryItem;

        NewCategoryItemViewHolder(View itemView) {
            super(itemView);
            categoryItem = (ImageView) itemView.findViewById(R.id.category_item_image);
        }
    }

    public void setCategoryChangeListener(CategoryChangeListener categoryChangeListener){
        this.categoryChangeListener = categoryChangeListener;
    }

    public CategoryItemModel getSelectedItem() {
        return categoryItemList.get(selectedPosition);
    }

    public interface CategoryChangeListener {
        void categoryChanged();
    }
}
