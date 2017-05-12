package act.angelman.presentation.custom;


import android.content.Context;
import android.graphics.Typeface;
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

import act.angelman.AngelmanApplication;
import act.angelman.R;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.model.CategoryModel;
import act.angelman.domain.repository.CardRepository;
import act.angelman.presentation.adapter.CardImageAdapter;
import act.angelman.presentation.util.ResourcesUtil;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class CardViewPagerLayout extends RelativeLayout {

    @Inject
    CardRepository cardRepository;

    List<CardModel> allCardListInSelectedCategory;
    private View subject;
    CardViewPager mViewPager;
    private Context context;
    private OnClickBackButtonListener onClickBackButtonListener;
    private CardImageAdapter cardImageAdapter;
    int currentCardIndex = 0;

    RequestManager glide;

    public CardViewPagerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        ((AngelmanApplication) context.getApplicationContext()).getAngelmanComponent().inject(this);

        subject = inflate(context, R.layout.card_viewpager_layout, this);

        findViewById(R.id.back_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardImageAdapter != null) {
                    cardImageAdapter.releaseSpeakHandler();
                    cardImageAdapter.stopVideoView();
                }
                onClickBackButtonListener.clickBackButton();
            }
        });

        glide = Glide.with(context);

    }

    public void setCategoryData(CategoryModel categoryModel){
        setBackground(
                ResourcesCompat.getDrawable(
                        context.getResources(),
                        ResourcesUtil.getCardViewLayoutBackgroundBy(categoryModel.color),
                        context.getTheme()
                )
        );

        TextView categoryTitle = (TextView) subject.findViewById(R.id.category_item_title);
        categoryTitle.setText(categoryModel.title);
        categoryTitle.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_regular)));

        mViewPager = (CardViewPager) findViewById(R.id.view_pager);

        allCardListInSelectedCategory = cardRepository.getSingleCardListWithCategoryId(categoryModel.index, false);

        cardImageAdapter = new CardImageAdapter(context, allCardListInSelectedCategory, glide);
        mViewPager.setAdapter(cardImageAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                cardImageAdapter.releaseSpeakHandler();
                cardImageAdapter.stopVideoView();
                currentCardIndex = pos;
                View view = cardImageAdapter.viewCollection.get(pos);
                view.bringToFront();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void setOnClickBackButtonListener(OnClickBackButtonListener onClickBackButtonListener){
        this.onClickBackButtonListener = onClickBackButtonListener;
    }

    public interface OnClickBackButtonListener {
        void clickBackButton();
     }
}
