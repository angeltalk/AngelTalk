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

public class ChangeOrderRecyclerViewAdapter extends RecyclerView.Adapter<ChangeOrderRecyclerViewAdapter.ChangeOrderRecyclerViewHolder> {

    private final RequestManager glide;
    private final Context context;
    private final int categoryModelColor;
    private List<CardModel> cardModelList;

    public ChangeOrderRecyclerViewAdapter(List<CardModel> cardModelList, int categoryModelColor, Context context) {
        this.cardModelList = cardModelList;
        this.context = context;
        this.categoryModelColor = categoryModelColor;
        this.glide = Glide.with(context);
    }

    @Override
    public ChangeOrderRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_list, parent, false);
        return new ChangeOrderRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChangeOrderRecyclerViewHolder holder, final int position) {
        CardModel cardModel = cardModelList.get(position);
        glide.load(ContentsUtil.getContentFile(getThumbnailPath(cardModel)))
                .override(60, 60)
                .bitmapTransform(new AngelManGlideTransform(context
                        , 10, 0, AngelManGlideTransform.CornerType.ALL))
                .into(holder.cardThumbnail);
        holder.cardName.setText(cardModel.name);
        setViewByHide(holder, cardModel.hide);

        holder.showHideIcon.setVisibility(View.GONE);
        holder.itemMoveIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return cardModelList.size();
    }

    private void setViewByHide(final ChangeOrderRecyclerViewHolder holder, boolean hide) {
        if(hide) {
            holder.showHideItemBar.setImageResource(ResourcesUtil.getShowHideItemBarBy(ResourcesUtil.HIDING));
            holder.itemMoveIcon.setImageResource(ResourcesUtil.getItemMoveIconBy(categoryModelColor));
            holder.cardName.setTextColor(context.getResources().getColor(R.color.black_4C));
            holder.cardThumbnail.setImageAlpha(60);
        } else {
            holder.showHideItemBar.setImageResource(ResourcesUtil.getShowHideItemBarBy(categoryModelColor));
            holder.itemMoveIcon.setImageResource(ResourcesUtil.getItemMoveIconBy(categoryModelColor));
            holder.cardName.setTextColor(context.getResources().getColor(R.color.black_00));
            holder.cardThumbnail.setImageAlpha(255);
        }
    }

    private String getThumbnailPath(CardModel cardModel) {
        String thumbnailPath = "";
        if(cardModel.cardType == CardModel.CardType.VIDEO_CARD) {
            thumbnailPath = cardModel.thumbnailPath;
        } else if(cardModel.cardType == CardModel.CardType.PHOTO_CARD) {
            thumbnailPath = cardModel.contentPath;
        }
        return thumbnailPath;
    }

    public void onItemSelected() {

    }

    public void setCardModelList(List<CardModel> cardModelList) {
        this.cardModelList = cardModelList;
    }

    public static class ChangeOrderRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView cardThumbnail;
        FontTextView cardName;
        ImageView showHideIcon;
        ImageView showHideItemBar;
        ImageView itemMoveIcon;

        public ChangeOrderRecyclerViewHolder(View view) {
            super(view);
            this.showHideItemBar = ((ImageView) view.findViewById(R.id.show_hide_item_bar));
            this.cardThumbnail = ((ImageView) view.findViewById(R.id.card_thumbnail));
            this.cardName = ((FontTextView) view.findViewById(R.id.card_name));
            this.showHideIcon = ((ImageView) view.findViewById(R.id.show_hide_icon));
            this.itemMoveIcon = ((ImageView) view.findViewById(R.id.item_move_icon));
        }
    }
}