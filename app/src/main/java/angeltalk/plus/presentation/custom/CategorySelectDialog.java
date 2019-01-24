package angeltalk.plus.presentation.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import angeltalk.plus.R;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.presentation.util.DialogUtil;

public class CategorySelectDialog {

    private final AlertDialog dialog;
    private final Context context;
    private CategoryListAdapter categoryListAdapter;
    private final RecyclerView recyclerView;


    public CategorySelectDialog(Context context, List<CategoryModel> categoryList, View.OnClickListener positiveOnClickListener) {
        this.context = context;
        View innerView = ((Activity) context).getLayoutInflater().inflate(R.layout.category_select_dialog, null);
        recyclerView = (RecyclerView) innerView.findViewById(R.id.category_list_recycler_view);
        categoryListAdapter = new CategoryListAdapter(context, categoryList);
        recyclerView.setAdapter(categoryListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        dialog = DialogUtil.buildCustomDialog(context, innerView, positiveOnClickListener, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void show() {
        DialogUtil.show(context, dialog, 450);
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public CategoryModel getSelectItem() {
        return categoryListAdapter.getSelectItem();
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

        private void selectCategoryRadioButton(View selectedRadioButton) {
            View itemRadioButton;
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                itemRadioButton = recyclerView.getChildAt(i).findViewById(R.id.category_item_radio);
                ((AppCompatRadioButton) itemRadioButton).setChecked(itemRadioButton.equals(selectedRadioButton));
            }
            TextView confirmButton = (TextView) dialog.findViewById(R.id.confirm_button);
            confirmButton.setTextColor(context.getResources().getColor(R.color.simple_background_red));
            confirmButton.setEnabled(true);
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
            holder.categoryLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckPosition = position;
                    selectCategoryRadioButton(v.findViewById(R.id.category_item_radio));
                }
            });
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

            private RelativeLayout categoryLayout;
            private TextView categoryName;
            private AppCompatRadioButton selectRadioButton;

            public CategorySelectRecyclerViewHolder(View view) {
                super(view);
                this.categoryLayout = ((RelativeLayout) view.findViewById(R.id.category_item_holder_layout));
                this.categoryName = ((TextView) view.findViewById(R.id.category_item_name));
                this.selectRadioButton = ((AppCompatRadioButton) view.findViewById(R.id.category_item_radio));
            }
        }
    }
}
