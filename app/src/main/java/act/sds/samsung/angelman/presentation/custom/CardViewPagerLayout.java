package act.sds.samsung.angelman.presentation.custom;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;
import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.presentation.adapter.CardImageAdapter;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class CardViewPagerLayout extends RelativeLayout {

    @Inject
    CardRepository cardRepository;

    private List<CardModel> allCardListInSelectedCategory;
    private View subject;
    public CardViewPager cardViewPager;
    private Context context;
    private OnClickBackButtonListener onClickBackButtonListener;
    private RequestManager glideRequestManager;
    private View backButton;
    private TextView categoryItemTextView;
    private CardImageAdapter cardImageAdapter;

    private ViewPager.OnPageChangeListener cardViewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int pos) {
            View view = cardImageAdapter.viewCollection.get(pos);
            view.bringToFront();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public CardViewPagerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        ((AngelmanApplication) context.getApplicationContext()).getAngelmanComponent().inject(this);

        subject = inflate(context, R.layout.card_viewpager_layout, this);
        categoryItemTextView = ((TextView) subject.findViewById(R.id.category_item_title));
        backButton = findViewById(R.id.back_button);
        cardViewPager = (CardViewPager) findViewById(R.id.view_pager);
        glideRequestManager = Glide.with(context);

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBackButtonListener.clickBackButton();
            }
        });
    }

    public void setCategoryData(CategoryModel categoryModel) {
        setBackground(ResourcesCompat.getDrawable(
                context.getResources(),
                ResourcesUtil.getCardViewLayoutBackgroundBy(categoryModel.color),
                context.getTheme()
        ));

        categoryItemTextView.setText(categoryModel.title);
        allCardListInSelectedCategory = cardRepository.getSingleCardListWithCategoryId(categoryModel.index);
        cardImageAdapter = new CardImageAdapter(context, allCardListInSelectedCategory, glideRequestManager);
        cardViewPager.setAdapter(cardImageAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(cardViewPager);

        cardViewPager.addOnPageChangeListener(cardViewPagerOnPageChangeListener);
    }

    public void setOnClickBackButtonListener(OnClickBackButtonListener onClickBackButtonListener) {
        this.onClickBackButtonListener = onClickBackButtonListener;
    }

    public interface OnClickBackButtonListener {
        void clickBackButton();
    }
}
