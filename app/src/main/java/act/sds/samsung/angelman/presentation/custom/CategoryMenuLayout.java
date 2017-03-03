package act.sds.samsung.angelman.presentation.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;
import act.sds.samsung.angelman.presentation.adapter.CategoryAdapter;
import jp.wasabeef.blurry.Blurry;

public class CategoryMenuLayout extends LinearLayout {

    @Inject
    CategoryRepository categoryRepository;

    private OnCategoryViewChangeListener onCategoryViewChangeListener;
    private View subject;
    protected RelativeLayout lockContainer;
    private TextView lockGuide;
    private ImageView lockButton;

    private static final int CLICKED = 0;
    private static final int NON_CLICKED = 1;

    private int clickState = NON_CLICKED;

    public CategoryMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        ((AngelmanApplication) context.getApplicationContext()).getAngelmanComponent().inject(this);

        subject = inflate(context, R.layout.category_menu_layout, this);
        lockContainer = (RelativeLayout) subject.findViewById(R.id.lock_container);
        lockGuide = (TextView) subject.findViewById(R.id.lock_guide);
        lockButton = (ImageView) subject.findViewById(R.id.lock_image);

        getAllCategoryList(context);
        setLockView(context);

    }

    public void setLockAreaVisibleWithGone() {
        lockContainer.setVisibility(GONE);
        lockGuide.setVisibility(GONE);
        lockButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_disabled));
        clickState = NON_CLICKED;
    }

    private void getAllCategoryList(final Context context) {
        final GridView categoryList = (GridView) findViewById(R.id.category_list);

        ArrayList<CategoryModel> categoryAllList = categoryRepository.getCategoryAllList();
        final CategoryAdapter categoryAdapter = new CategoryAdapter(context, categoryAllList);
        categoryList.setAdapter(categoryAdapter);

        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(onCategoryViewChangeListener != null && clickState == NON_CLICKED) {
                    changeClickedState();
                    onCategoryViewChangeListener.categoryClick(((CategoryModel) categoryList.getItemAtPosition(position)));
                }
            }
        });
    }

    private void setLockView(final Context context) {
        lockButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                lockButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_enabled));
                lockGuide.setVisibility(VISIBLE);

                lockContainer.setVisibility(VISIBLE);

                Blurry.with(context)
                        .sampling(6)
                        .capture(subject)
                        .into(((ImageView) findViewById(R.id.blurredView)));

                view.startDrag(null, new ImageDragShadowBuilder(lockButton), view, 0);

                return true;
            }
        });

        lockButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    setLockAreaVisibleWithGone();
                }
                return false;
            }
        });

        lockContainer.setOnDragListener(new View.OnDragListener() {
            private boolean isInLockArea(View v, float eventX, float eventY) {
                int centerX = v.getRight()/2;
                int centerY = v.getBottom();
                int radius = v.getRight()/2;

                return Math.pow(eventX-centerX, 2) + Math.pow(eventY-centerY, 2) < Math.pow(radius, 2);
            }
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        if (isInLockArea(v, event.getX(), event.getY())) {
                            setLockAreaVisibleWithGone();
                        } else {
                            onCategoryViewChangeListener.onUnLock();
                        }
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        setLockAreaVisibleWithGone();
                        break;
                }
                return true;
            }

        });
    }

    public void setOnCategoryViewChangeListener(OnCategoryViewChangeListener onCategoryViewChangeListener){
        this.onCategoryViewChangeListener = onCategoryViewChangeListener;
    }

    public interface OnCategoryViewChangeListener {
        void onUnLock();
        void categoryClick(CategoryModel categoryModel);
    }

    public void changeClickedState(){
        if(clickState == NON_CLICKED){
            clickState = CLICKED;
        } else {
            clickState = NON_CLICKED;
        }
    }
}
