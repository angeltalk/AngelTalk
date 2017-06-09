package act.angelman.presentation.custom;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
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

import act.angelman.AngelmanApplication;
import act.angelman.R;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.model.CategoryModel;
import act.angelman.domain.repository.CardRepository;
import act.angelman.presentation.adapter.CardImageAdapter;
import act.angelman.presentation.util.PlayUtil;
import act.angelman.presentation.util.ResourcesUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class CardViewPagerLayout extends RelativeLayout {

    @Inject
    CardRepository cardRepository;

    @BindView(R.id.yes_no_pannel)
    RelativeLayout yesNoPannel;


    @BindView(R.id.yes_layout)
    LinearLayout yesCardLayout;

    @BindView(R.id.no_layout)
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
        ((AngelmanApplication) context.getApplicationContext()).getAngelmanComponent().inject(this);

        subject = inflate(context, R.layout.card_viewpager_layout, this);

        ButterKnife.bind(this);

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

    @OnClick(R.id.yes_no_btn)
    public void onClickYesNoButton(View v){
        Animation slide_up = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        yesNoPannel.startAnimation(slide_up);
        yesNoPannel.setVisibility(View.VISIBLE);
        yesNoPannel.setClickable(true);
    }


    @OnClick(R.id.yes_no_close_btn)
    public void onClickYesNoCloseButton(View v){
        Animation slide_down = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        yesNoPannel.startAnimation(slide_down);
        yesNoPannel.setVisibility(View.GONE);
    }

    @OnClick(R.id.yes_layout)
    public void onClickYesLayout(View v){
        Animation scaleUp = AnimationUtils.loadAnimation(context, R.anim.zoom_out);
        v.startAnimation(scaleUp);
        playUtil.ttsSpeak(getResources().getString(R.string.response_yes));

    }

    @OnClick(R.id.no_layout)
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
