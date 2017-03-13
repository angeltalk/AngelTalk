package act.sds.samsung.angelman.presentation.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.RequestManager;

import java.io.File;
import java.util.ArrayList;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.presentation.activity.AbstractActivity;
import act.sds.samsung.angelman.presentation.custom.AddCardView;
import act.sds.samsung.angelman.presentation.custom.CardView;
import act.sds.samsung.angelman.presentation.util.AngelManGlideTransform;
import act.sds.samsung.angelman.presentation.util.FontUtil;
import act.sds.samsung.angelman.presentation.util.ImageUtil;
import act.sds.samsung.angelman.presentation.util.PlayUtil;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

import static act.sds.samsung.angelman.presentation.custom.CardView.MODE_VIEW_CARD;

public class CardImageAdapter extends PagerAdapter {
    private Context context;

    private final RequestManager glide;

    private boolean hasNewCardView;
    private ArrayList<CardModel> dataList;
    public SparseArray<View> viewCollection = new SparseArray<>();

    private PlayUtil playUtil;
    private ImageUtil imageUtil;
    private boolean isNotLongClicked;

    public CardImageAdapter(Context context, ArrayList<CardModel> dataList, RequestManager glide) {
        this.context = context;
        this.dataList = dataList;
        this.glide = glide;

        hasNewCardView = false;

        playUtil = PlayUtil.getInstance();
        playUtil.initTts(context.getApplicationContext());

        imageUtil = ImageUtil.getInstance();
    }

    public View getItemAt(int index) {
        return viewCollection.get(index);
    }

    @Override
    public int getCount() {
        if (dataList == null) return 0;
        else return dataList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        if(object instanceof AddCardView){
            return 0;
        }
        CardView cardView = (CardView) object;
        int position = dataList.indexOf(cardView.dataModel);
        return position == -1 ? POSITION_NONE : position;
    }

    @Override
    public View instantiateItem(final ViewGroup container, int position) {
        if (hasNewCardView && position == 0) {
            AddCardView cardView = new AddCardView(context);
            container.addView(cardView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            viewCollection.put(position, cardView);

            ImageView plusIcon = (ImageView)cardView.findViewById(R.id.plus_icon);

            @ResourcesUtil.BackgroundColors
            int categoryColor = ((AbstractActivity) context).getCategoryColor();

            plusIcon.setImageResource(ResourcesUtil.getPlusIconBy(categoryColor));

            return cardView;
        } else {
            final CardView cardView = new CardView(context);
            cardView.setDataModel(dataList.get(position));

            cardView.cardImage.setScaleType(ImageView.ScaleType.FIT_XY);

            CardModel singleSectionItems = dataList.get(position);
            String imgPath = singleSectionItems.imagePath;
            cardView.cardTitle.setText(singleSectionItems.name);
            cardView.cardTitle.setTypeface(FontUtil.setFont(context, FontUtil.FONT_MEDIUM));

            boolean isStorage = imgPath.contains("DCIM");

            if (isStorage) {
                glide
                        .load(new File(imgPath))
                        .override(280, 280)
                        .bitmapTransform(new AngelManGlideTransform(context, 10, 0, AngelManGlideTransform.CornerType.TOP))
                        .into(cardView.cardImage);
            } else {
                glide
                        .load(imageUtil.makeImagePathForAsset(imgPath))
                        .bitmapTransform(new AngelManGlideTransform(context, 10, 0, AngelManGlideTransform.CornerType.TOP))
                        .override(280, 280)
                        .into(cardView.cardImage);
            }

            View cardContainer = cardView.findViewById(R.id.card_container);
            cardContainer.setOnClickListener(onClickListener);
            cardContainer.setOnLongClickListener(onLongClickListener);

            isNotLongClicked = true;

            container.addView(cardView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            viewCollection.put(position, cardView);
            return cardView;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        viewCollection.remove(position);
        object = null;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void addNewCardViewAtFirst() {
        hasNewCardView = true;
        CardModel emptyModel = new CardModel();
        dataList.add(0, emptyModel);
    }

    private void startAnimationAndVibrator(CardView cardView) {
        if (cardView.mode == MODE_VIEW_CARD) {
            cardView.bringToFront();

            if (cardView.status == CardView.CardViewStatus.CARD_TITLE_SHOWN) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(500);
            }
            AnimationSet animSet = new AnimationSet(true);
            Animation zoomOutAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_out);
            animSet.addAnimation(zoomOutAnimation);
            cardView.startAnimation(animSet);

            startSpeakCardName(cardView);
        }
    }

    private void startSpeakCardName(final CardView cardView) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cardView.dataModel == null || cardView.dataModel.voicePath == null || cardView.dataModel.voicePath.length() < 1) {
                    playUtil.ttsSpeak(cardView.cardTitle.getText().toString());
                } else {
                    playUtil.play(cardView.dataModel.voicePath);
                }
            }
        }, 500);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getParent().getParent() instanceof CardView) {
                if (isNotLongClicked) {
                    CardView cardView = (CardView) v.getParent().getParent();
                    startAnimationAndVibrator(cardView);
                } else {
                    isNotLongClicked = true;
                }
            }
        }
    };

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (v.getParent().getParent() instanceof CardView) {
                isNotLongClicked = false;
                CardView cardView = (CardView) v.getParent().getParent();
                startAnimationAndVibrator(cardView);
                return false;
            }
            return true;
        }
    };
}
