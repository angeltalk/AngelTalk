package act.sds.samsung.angelman.presentation.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
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
import java.util.List;

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

public class CardImageAdapter extends PagerAdapter {

    private Context context;
    private final RequestManager glide;
    private boolean hasNewCardView;
    private List<CardModel> dataList;
    public SparseArray<View> viewCollection = new SparseArray<>();
    private PlayUtil playUtil;
    private ImageUtil imageUtil;
    private boolean isNotLongClicked;

    public CardImageAdapter(Context context, List<CardModel> dataList, RequestManager glide) {
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
        if (dataList == null) {
            return 0;
        } else {
            return dataList.size();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof AddCardView) {
            return 0;
        }
        CardView cardView = (CardView) object;
        int position = dataList.indexOf(cardView.getDataModel());
        return position == -1 ? POSITION_NONE : position;
    }

    @Override
    public View instantiateItem(final ViewGroup container, int position) {
        if (hasNewCardView && position == 0) {
            AddCardView cardView = new AddCardView(context);
            container.addView(cardView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            viewCollection.put(position, cardView);
            ImageView plusIcon = (ImageView) cardView.findViewById(R.id.plus_icon);

            @ResourcesUtil.BackgroundColors
            int categoryColor = ((AbstractActivity) context).getCategoryColor();

            plusIcon.setImageResource(ResourcesUtil.getPlusIconBy(categoryColor));
            return cardView;
        } else {
            final CardView cardView = new CardView(context);
            cardView.setDataModel(dataList.get(position));
            cardView.getCardImage().setScaleType(ImageView.ScaleType.FIT_XY);

            CardModel singleSectionItems = dataList.get(position);
            String imagePath = singleSectionItems.imagePath;
            cardView.getCardTitleTextView().setText(singleSectionItems.name);
            cardView.getCardTitleTextView().setTypeface(FontUtil.setFont(context, FontUtil.FONT_MEDIUM));

            glide.load(getImageFile(imagePath))
                 .bitmapTransform(new AngelManGlideTransform(context, 10, 0, AngelManGlideTransform.CornerType.TOP))
                 .override(280, 280)
                 .into(cardView.getCardImage());

            View cardContainer = cardView.findViewById(R.id.card_container);
            cardContainer.setOnClickListener(cardContainerOnClickListener);
            cardContainer.setOnLongClickListener(cardContainerOnLongClickListener);

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

    @NonNull
    private File getImageFile(String imagePath) {
        File file;
        if (imagePath.contains("DCIM")) {
            file = new File(imagePath);
        } else {
            file = new File(imageUtil.makeImagePathForAsset(imagePath));
        }
        return file;
    }

    private View.OnClickListener cardContainerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getParent().getParent() instanceof CardView) {
                if (isNotLongClicked) {
                    CardView cardView = (CardView) v.getParent().getParent();
                    startCardSelectionEffect(cardView);
                } else {
                    isNotLongClicked = true;
                }
            }
        }
    };

    private View.OnLongClickListener cardContainerOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (v.getParent().getParent() instanceof CardView) {
                isNotLongClicked = false;
                CardView cardView = (CardView) v.getParent().getParent();
                startCardSelectionEffect(cardView);
                return false;
            } else {
                return true;
            }
        }
    };

    private void startCardSelectionEffect(CardView cardView) {
        if (cardView.getCardViewMode() == CardView.CardViewMode.CARD_VIEW_MODE) {
            cardView.bringToFront();

            if (cardView.getStatus() == CardView.CardViewStatus.CARD_TITLE_SHOWN) {
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
                if (cardView.getDataModel() == null || cardView.getDataModel().voicePath == null || cardView.getDataModel().voicePath.length() < 1) {
                    playUtil.ttsSpeak(cardView.getCardTitleTextView().getText().toString());
                } else {
                    playUtil.play(cardView.getDataModel().voicePath);
                }
            }
        }, 500);
    }
}
