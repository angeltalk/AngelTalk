package act.angelman.presentation.adapter;

import android.content.Context;
import android.media.MediaPlayer;
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
import java.util.List;

import act.angelman.R;
import act.angelman.domain.model.CardModel;
import act.angelman.presentation.custom.AddCardView;
import act.angelman.presentation.custom.CardView;
import act.angelman.presentation.custom.VideoCardTextureView;
import act.angelman.presentation.util.AngelManGlideTransform;
import act.angelman.presentation.manager.ApplicationManager;
import act.angelman.presentation.util.ContentsUtil;
import act.angelman.presentation.util.FontUtil;
import act.angelman.presentation.util.PlayUtil;
import act.angelman.presentation.util.ResourcesUtil;

public class CardImageAdapter extends PagerAdapter {

    private Context context;
    private final RequestManager glide;
    private boolean hasNewCardView;
    private List<CardModel> dataList;
    public SparseArray<View> viewCollection = new SparseArray<>();
    private PlayUtil playUtil;
    private boolean isNotLongClicked;
    private ApplicationManager applicationManager;
    private CardView lastSelectedCardView = null;
    private Handler speakHandler = new Handler();

    public CardImageAdapter(Context context, List<CardModel> dataList, RequestManager glide, ApplicationManager applicationManager) {
        this.context = context;
        this.dataList = dataList;
        this.glide = glide;

        hasNewCardView = false;

        playUtil = PlayUtil.getInstance();
        playUtil.initTts(context.getApplicationContext());

        this.applicationManager = applicationManager;
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
        int position = dataList.indexOf(cardView.dataModel);
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
            int categoryColor = applicationManager.getCategoryModel().color;

            plusIcon.setImageResource(ResourcesUtil.getPlusIconBy(categoryColor));
            return cardView;
        } else {
            final CardView cardView = new CardView(context);
            cardView.setDataModel(dataList.get(position));
            cardView.cardImage.setScaleType(ImageView.ScaleType.FIT_XY);

            CardModel singleSectionItems = dataList.get(position);

            cardView.cardTitle.setText(singleSectionItems.name);
            cardView.cardTitle.setTypeface(FontUtil.setFont(context, FontUtil.FONT_MEDIUM));
            View cardContainer = cardView.findViewById(R.id.card_container);

            if (singleSectionItems.cardType == CardModel.CardType.PHOTO_CARD) {
                cardView.findViewById(R.id.card_image).setVisibility(View.VISIBLE);
                cardView.findViewById(R.id.card_video).setVisibility(View.GONE);
                String imagePath = singleSectionItems.contentPath;
                glide.load(ContentsUtil.getContentFile(imagePath))
                        .bitmapTransform(new AngelManGlideTransform(context, 10, 0, AngelManGlideTransform.CornerType.TOP))
                        .override(280, 280)
                        .into(cardView.cardImage);
            } else if (singleSectionItems.cardType == CardModel.CardType.VIDEO_CARD) {
                glide.load(ContentsUtil.getContentFile(ContentsUtil.getThumbnailPath(singleSectionItems.contentPath)))
                        .bitmapTransform(new AngelManGlideTransform(context, 10, 0, AngelManGlideTransform.CornerType.TOP))
                        .override(280, 280)
                        .into(cardView.cardImage);
                VideoCardTextureView cardVideoView = ((VideoCardTextureView) cardView.findViewById(R.id.card_video));
                cardVideoView.setVisibility(View.VISIBLE);
                cardVideoView.setScaleType(VideoCardTextureView.ScaleType.CENTER_CROP);
                File video = ContentsUtil.getContentFile(singleSectionItems.contentPath);
                if (video != null) {
                    cardVideoView.setDataSource(video.getAbsolutePath());
                }
            }

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
        CardModel emptyModel = CardModel.builder().cardIndex(-1).build();
        dataList.add(0, emptyModel);
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
        lastSelectedCardView = cardView;
        if (cardView.mode == CardView.MODE_VIEW_CARD) {
            cardView.bringToFront();

            if (cardView.status == CardView.CardViewStatus.CARD_TITLE_SHOWN) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(500);
            }

            AnimationSet animSet = new AnimationSet(true);
            Animation zoomOutAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_out);
            animSet.addAnimation(zoomOutAnimation);
            cardView.startAnimation(animSet);

            if (cardView.dataModel.cardType == CardModel.CardType.VIDEO_CARD) {
                final VideoCardTextureView cardVideoView = ((VideoCardTextureView) cardView.findViewById(R.id.card_video));
                if (cardVideoView != null) {
                    cardVideoView.play(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            cardVideoView.resetPlayer();
                        }
                    });

                }
                startSpeakCardName(cardView, 3500); //Video card
            } else {
                startSpeakCardName(cardView, 500); //Text card
            }
        }
    }

    private void startSpeakCardName(final CardView cardView, int delayMillis) {
        //Handler handler = new Handler();
        speakHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (voiceFileExists(cardView)) {
                    playUtil.play(cardView.dataModel.voicePath);
                } else {
                    playUtil.ttsSpeak(cardView.cardTitle.getText().toString());
                }
            }
        }, delayMillis);
    }

    public void stopVideoView() {
        if (lastSelectedCardView != null) {
            VideoCardTextureView cardVideoView = ((VideoCardTextureView) lastSelectedCardView.findViewById(R.id.card_video));
            if (cardVideoView != null && cardVideoView.isPlaying()) {
                cardVideoView.stop();
                cardVideoView.seekTo(0);
            }
        }
    }

    public void releaseSpeakHandler() {
        playUtil.playStop();
        playUtil.ttsStop();
        speakHandler.removeCallbacksAndMessages(null);
    }

    private boolean voiceFileExists(CardView cardView) {
        return cardView.dataModel != null &&
                cardView.dataModel.voicePath != null &&
                (new File(cardView.dataModel.voicePath)).exists();
    }
}
