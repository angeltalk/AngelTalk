package act.sds.samsung.angelman.presentation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.presentation.custom.FontTextView;
import act.sds.samsung.angelman.presentation.util.AngelManGlideTransform;
import act.sds.samsung.angelman.presentation.util.ContentsUtil;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

public class CardListRecyclerViewAdapter extends RecyclerView.Adapter<CardListRecyclerViewAdapter.CardListRecyclerViewHolder> {

    private final List<CardModel> cardModelList;
    private final RequestManager glide;
    private final Context context;
    private final int categoryModelColor;

    public CardListRecyclerViewAdapter(List<CardModel> cardModelList, int categoryModelColor, Context context) {
        this.cardModelList = cardModelList;
        this.context = context;
        this.categoryModelColor = categoryModelColor;
        this.glide = Glide.with(context);
    }

    @Override
    public CardListRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_list, parent, false);
        return new CardListRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardListRecyclerViewHolder holder, int position) {

        holder.showHideItemBar.setImageResource(ResourcesUtil.getShowHideItemBarBy(categoryModelColor));
        holder.showHideIcon.setImageResource(ResourcesUtil.getShowHideIconBy(categoryModelColor));

        CardModel cardModel = cardModelList.get(position);
        holder.cardName.setText(cardModel.name);
        String thumbnailPath = "";
        if(cardModel.cardType == CardModel.CardType.VIDEO_CARD) {
            thumbnailPath = cardModel.thumbnailPath;
        } else if(cardModel.cardType == CardModel.CardType.PHOTO_CARD) {
            thumbnailPath = cardModel.contentPath;
        }
        glide.load(ContentsUtil.getContentFile(ContentsUtil.getThumbnailPath(thumbnailPath)))
                .override(60, 60)
                .bitmapTransform(new AngelManGlideTransform(context
                        , 10, 0, AngelManGlideTransform.CornerType.ALL))
                .into(holder.cardThumbnail);
    }

    @Override
    public int getItemCount() {
        return cardModelList.size();
    }

    static class CardListRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView cardThumbnail;
        FontTextView cardName;
        ImageView showHideIcon;
        ImageView showHideItemBar;

        public CardListRecyclerViewHolder(View view) {
            super(view);
            this.showHideItemBar = ((ImageView) view.findViewById(R.id.show_hide_item_bar));
            this.cardThumbnail = ((ImageView) view.findViewById(R.id.card_thumbnail));
            this.cardName = ((FontTextView) view.findViewById(R.id.card_name));
            this.showHideIcon = ((ImageView) view.findViewById(R.id.show_hide_icon));
        }
    }
}
