package act.sds.samsung.angelman.presentation.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
    private View view;

    public CategorySelectDialog(Context context, List<CategoryModel> categoryList, View.OnClickListener positiveOnClickListener, View.OnClickListener negativeOnClickListener) {

        View innerView = ((Activity) context).getLayoutInflater().inflate(R.layout.category_select_dialog, null);

        RecyclerView recyclerView = (RecyclerView) innerView.findViewById(R.id.category_list_recycler_view);
        adapter = new CategoryListAdapter(context, categoryList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

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

    class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategorySelectRecyclerViewHolder> {

        Context context;
        List<CategoryModel> categoryList;
        private int mCheckPostion = 0;

        CategoryListAdapter(Context context, List<CategoryModel> categoryModel) {
            this.context = context;
            this.categoryList = categoryModel;
        }

        public CategoryModel getSelectItem() {
            return categoryList.get(mCheckPostion);
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
            holder.selectRadioButton.setChecked(mCheckPostion == position);
            holder.selectRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == mCheckPostion) {
                        holder.selectRadioButton.setChecked(false);
                        mCheckPostion = -1;
                    } else {
                        mCheckPostion = position;
                        notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        class CategorySelectRecyclerViewHolder extends RecyclerView.ViewHolder {

            FontTextView categoryName;
            AppCompatRadioButton selectRadioButton;

            public CategorySelectRecyclerViewHolder(View view) {

                super(view);
                this.categoryName = ((FontTextView) view.findViewById(R.id.category_item_name));
                this.selectRadioButton = ((AppCompatRadioButton) view.findViewById(R.id.category_item_radio));
                selectRadioButton.setSupportButtonTintList(getColorList());
            }

            public ColorStateList getColorList() {
                return new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[]{
                                Color.rgb(222, 222, 222),
                                Color.rgb(120, 207, 199)
                        }
                );
            }
        }
    }
}
