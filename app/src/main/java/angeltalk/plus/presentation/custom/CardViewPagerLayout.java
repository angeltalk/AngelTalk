package angeltalk.plus.presentation.custom;

import dagger.hilt.android.AndroidEntryPoint;


import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

import javax.inject.Inject;

import angeltalk.plus.AngelmanApplication;
import angeltalk.plus.R;
import angeltalk.plus.domain.model.CardModel;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.domain.repository.CardRepository;
import angeltalk.plus.presentation.adapter.CardImageAdapter;
import angeltalk.plus.presentation.util.PlayUtil;
import angeltalk.plus.presentation.util.ResourcesUtil;
@AndroidEntryPoint
public class CardViewPagerLayout extends RelativeLayout {

    @Inject
    CardRepository cardRepository;

    ConstraintLayout yesNoPannel;


    LinearLayout yesCardLayout;

    LinearLayout noCardLayout;


    private View subject;

    private Context context;

    private OnClickBackButtonListener onClickBackButtonListener;

    private CardImageAdapter cardImageAdapter;

    int currentCardIndex = 0;

    RequestManager glide;

    CardViewPager mViewPager;

    List<CardModel> allCardListInSelectedCategory;

    PlayUtil playUtil;

    public CardViewPagerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {

        subject = inflate(context, R.layout.card_viewpager_layout, this);
        bindViews();
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
        playUtil = PlayUtil.getInstance();

    }
    // TODO(phase-6): @OnClick wiring removed — wire setOnClickListener in bindViews()
    public void slideUpYesNoLayout(View v){
        if(cardImageAdapter != null) {
            cardImageAdapter.releaseSpeakHandler();
            cardImageAdapter.stopVideoView();
        }
        Animation slide_up = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        yesNoPannel.startAnimation(slide_up);
        yesNoPannel.setVisibility(View.VISIBLE);
        yesNoPannel.setClickable(true);
    }
    // TODO(phase-6): @OnClick wiring removed — wire setOnClickListener in bindViews()
    public void slideDownYesNoLayout(View v){
        Animation slide_down = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        yesNoPannel.startAnimation(slide_down);
        yesNoPannel.setVisibility(View.GONE);
    }
    // TODO(phase-6): @OnClick wiring removed — wire setOnClickListener in bindViews()
    public void onClickYesLayout(View v){
        Animation scaleUp = AnimationUtils.loadAnimation(context, R.anim.zoom_out);
        v.startAnimation(scaleUp);
        playUtil.ttsSpeak(getResources().getString(R.string.response_yes));

    }
    // TODO(phase-6): @OnClick wiring removed — wire setOnClickListener in bindViews()
    public void onClickNoLayout(View v){
        Animation scaleUp = AnimationUtils.loadAnimation(context, R.anim.zoom_out);
        v.startAnimation(scaleUp);
        playUtil.ttsSpeak(getResources().getString(R.string.response_no));
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

        mViewPager = (CardViewPager) findViewById(R.id.view_pager);

        allCardListInSelectedCategory = cardRepository.getSingleCardListWithCategoryId(categoryModel.index, false);

        cardImageAdapter = new CardImageAdapter(context, allCardListInSelectedCategory, glide);
        mViewPager.setAdapter(cardImageAdapter);
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
    private void bindViews() {
        yesNoPannel = findViewById(R.id.yes_no_pannel);
        yesCardLayout = findViewById(R.id.yes_layout);
        noCardLayout = findViewById(R.id.no_layout);
        findViewById(R.id.yes_no_btn).setOnClickListener(v -> slideUpYesNoLayout(v));
        findViewById(R.id.yes_no_close_btn).setOnClickListener(v -> slideDownYesNoLayout(v));
        findViewById(R.id.yes_layout).setOnClickListener(v -> onClickYesLayout(v));
        findViewById(R.id.no_layout).setOnClickListener(v -> onClickNoLayout(v));
    }
}
