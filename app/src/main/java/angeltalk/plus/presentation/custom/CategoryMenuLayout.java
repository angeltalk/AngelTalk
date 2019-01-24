package angeltalk.plus.presentation.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.annotation.VisibleForTesting;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import angeltalk.plus.AngelmanApplication;
import angeltalk.plus.R;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.domain.repository.CategoryRepository;
import angeltalk.plus.presentation.adapter.CategoryAdapter;

import static angeltalk.plus.R.id.category_list;
import static android.content.Context.VIBRATOR_SERVICE;


public class CategoryMenuLayout extends LinearLayout {

    @Inject
    CategoryRepository categoryRepository;

    private OnCategoryViewChangeListener onCategoryViewChangeListener;
    private View subject;
    private TextView lockLongPressGuide;
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
        lockLongPressGuide = (TextView) subject.findViewById(R.id.lock_long_press_guide);
        lockButton = (ImageView) subject.findViewById(R.id.lock_image);

        initClockTextStyle(context);
        getAllCategoryList(context);
        setLockView(context);

        if(hasNavigationBar(context)) {
            setSmallerMarginLayout();
        }
    }

    public void setLockAreaVisibleWithGone() {
        lockLongPressGuide.setVisibility(GONE);
        lockButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_disabled));
        clickState = NON_CLICKED;
    }

    private void getAllCategoryList(final Context context) {
        final GridView categoryList = (GridView) findViewById(category_list);

        List<CategoryModel> categoryAllList = categoryRepository.getCategoryAllList();
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

    @VisibleForTesting void setLockView(Context context) {

        final Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockLongPressGuide.setVisibility(VISIBLE);
                vibrator.vibrate(200);
                lockLongPressGuide.postDelayed(new Runnable() {
                    public void run() {
                        lockLongPressGuide.setVisibility(GONE);
                    }
                }, 1100);
            }
        });

        lockButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                lockButton.setVisibility(GONE);
                lockLongPressGuide.setVisibility(GONE);
                onCategoryViewChangeListener.onUnLock();
                return true;
            }
        });
    }

    @VisibleForTesting void setSmallerMarginLayout() {
        DisplayMetrics dm = getResources().getDisplayMetrics();

        ConstraintLayout.LayoutParams lp = ((ConstraintLayout.LayoutParams) subject.findViewById(R.id.clock_layout).getLayoutParams());
        lp.topMargin = Math.round(10 * dm.density);
        lp.bottomMargin = Math.round(2 * dm.density);
        subject.findViewById(R.id.clock_layout).setLayoutParams(lp);

        int p = Math.round(4*dm.density);
        subject.findViewById(R.id.lock_image).setPadding(p,p,p,p);
    }

    private boolean hasNavigationBar(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        double dmRatio = (double)dm.heightPixels/dm.widthPixels;

        return !hasMenuKey && !hasBackKey && (dmRatio < 1.72);
    }

    private void initClockTextStyle(Context context) {
        TextClock clockDate = (TextClock) subject.findViewById(R.id.clock_date);
        TextClock clockAmpm = (TextClock) subject.findViewById(R.id.clock_ampm);
        TextClock clockTime = (TextClock) subject.findViewById(R.id.clock_time);

        Typeface typeFaceBlack = Typeface.createFromAsset(context.getAssets(), getContext().getString(R.string.default_font_path));
        clockDate.setTypeface(typeFaceBlack);
        clockAmpm.setTypeface(typeFaceBlack);
        clockTime.setTypeface(typeFaceBlack);
    }
}
