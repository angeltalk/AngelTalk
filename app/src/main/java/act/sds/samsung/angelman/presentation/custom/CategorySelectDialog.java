package act.sds.samsung.angelman.presentation.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.presentation.util.ContentsUtil;
import act.sds.samsung.angelman.presentation.util.DialogUtil;

public class CategorySelectDialog {


    private static final int DEFAULT_WIDTH = 350;
    private static final int DEFAULT_HEIGHT = 450;
    private final AlertDialog dialog;
    private CategoryListAdapter adapter;
    private final RecyclerView recyclerView;


    public CategorySelectDialog(Context context, List<CategoryModel> categoryList, View.OnClickListener positiveOnClickListener, View.OnClickListener negativeOnClickListener) {

        View innerView = ((Activity) context).getLayoutInflater().inflate(R.layout.category_select_dialog, null);

        recyclerView = (RecyclerView) innerView.findViewById(R.id.category_list_recycler_view);
        adapter = new CategoryListAdapter(context, categoryList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        adapter.selectCategoryRadioButton(0);

        dialog = DialogUtil.buildCustomDialog(context, innerView, positiveOnClickListener, negativeOnClickListener);
        dialog.show();
        dialog.getWindow().setLayout((int) ContentsUtil.convertDpToPixel(DEFAULT_WIDTH, context), (int) ContentsUtil.convertDpToPixel(DEFAULT_HEIGHT, context));
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public CategoryModel getSelectItem() {
        return adapter.getSelectItem();
    }

    public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategorySelectRecyclerViewHolder> {

        private Context context;
        private List<CategoryModel> categoryList;
        private int mCheckPosition = -1;
        private View view;

        CategoryListAdapter(Context context, List<CategoryModel> categoryModel) {
            this.context = context;
            this.categoryList = categoryModel;
        }

        public CategoryModel getSelectItem() {
            return (mCheckPosition < 0) ? null : categoryList.get(mCheckPosition);
        }

        public void selectCategoryRadioButton(int position) {
            selectCategoryRadioButton(recyclerView.getChildAt(position).findViewById(R.id.category_item_radio));
        }

        private void selectCategoryRadioButton(View selectedRadioButton) {
            View itemRadioButton;
            for(int i=0; i<recyclerView.getChildCount(); i++) {
                itemRadioButton = recyclerView.getChildAt(i).findViewById(R.id.category_item_radio);
                ((AppCompatRadioButton) itemRadioButton).setChecked(itemRadioButton.equals(selectedRadioButton));
            }
            ((FontTextView) dialog.findViewById(R.id.confirm)).setTextColor(context.getResources().getColor(R.color.simple_background_red));
        }

        @Override
        public CategorySelectRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_list, parent, false);
            return new CategorySelectRecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CategorySelectRecyclerViewHolder holder, final int position) {
            CategoryModel categoryItem = categoryList.get(position);
            holder.categoryName.setText(categoryItem.title);
            holder.selectRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckPosition = position;
                    selectCategoryRadioButton(v);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        class CategorySelectRecyclerViewHolder extends RecyclerView.ViewHolder {

            private FontTextView categoryName;
            private AppCompatRadioButton selectRadioButton;

            public CategorySelectRecyclerViewHolder(View view) {
                super(view);
                this.categoryName = ((FontTextView) view.findViewById(R.id.category_item_name));
                this.selectRadioButton = ((AppCompatRadioButton) view.findViewById(R.id.category_item_radio));
            }
        }
    }
}
