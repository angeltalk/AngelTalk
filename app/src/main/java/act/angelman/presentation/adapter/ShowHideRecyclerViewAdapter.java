package act.angelman.presentation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

import act.angelman.R;
import act.angelman.domain.model.CardModel;
import act.angelman.presentation.listener.OnDataChangeListener;
import act.angelman.presentation.util.AngelManGlideTransform;
import act.angelman.presentation.util.ContentsUtil;

public class ShowHideRecyclerViewAdapter extends RecyclerView.Adapter<ShowHideRecyclerViewAdapter.CardListRecyclerViewHolder> {

    private final RequestManager glide;
    private final Context context;
    private List<CardModel> cardModelList;
    private OnDataChangeListener dataChangeListener;

    public ShowHideRecyclerViewAdapter(List<CardModel> cardModelList, Context context, OnDataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
        this.cardModelList = cardModelList;
        this.context = context;
        this.glide = Glide.with(context);
    }

    @Override
    public CardListRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_list, parent, false);
        return new CardListRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardListRecyclerViewHolder holder, final int position) {
        CardModel cardModel = cardModelList.get(position);
        glide.load(ContentsUtil.getContentFile(getThumbnailPath(cardModel)))
                .override(60, 60)
                .bitmapTransform(new AngelManGlideTransform(context
                        , 10, 0, AngelManGlideTransform.CornerType.ALL))
                .into(holder.cardThumbnail);
        holder.cardName.setText(cardModel.name);
        setViewByHide(holder, cardModel.hide);
        holder.itemMoveIcon.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean show = !cardModelList.get(position).hide;
                dataChangeListener.onHideChange(position, show);
                setViewByHide(holder, show);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardModelList.size();
    }

    private void setViewByHide(final CardListRecyclerViewHolder holder, boolean hide) {
        if(hide) {
            holder.hideItemBar.setVisibility(View.VISIBLE);
            holder.hideIcon.setVisibility(View.VISIBLE);
            holder.showHideItemBar.setVisibility(View.GONE);
            holder.showHideIcon.setVisibility(View.GONE);

            holder.cardName.setTextColor(context.getResources().getColor(R.color.black_4C));
            holder.cardThumbnail.setImageAlpha(60);
        } else {
            holder.hideItemBar.setVisibility(View.GONE);
            holder.hideIcon.setVisibility(View.GONE);
            holder.showHideItemBar.setVisibility(View.VISIBLE);
            holder.showHideIcon.setVisibility(View.VISIBLE);

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

    static class CardListRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView cardThumbnail;
        TextView cardName;
        ImageView showHideIcon;
        ImageView hideIcon;
        ImageView showHideItemBar;
        ImageView hideItemBar;
        ImageView itemMoveIcon;

        public CardListRecyclerViewHolder(View view) {
            super(view);
            this.showHideItemBar = ((ImageView) view.findViewById(R.id.show_hide_item_bar));
            this.cardThumbnail = ((ImageView) view.findViewById(R.id.card_thumbnail));
            this.cardName = ((TextView) view.findViewById(R.id.card_name));
            this.showHideIcon = ((ImageView) view.findViewById(R.id.show_hide_icon));
            this.hideIcon = ((ImageView) view.findViewById(R.id.hide_icon));
            this.itemMoveIcon = ((ImageView) view.findViewById(R.id.item_move_icon));
            this.hideItemBar = ((ImageView) view.findViewById(R.id.hide_item_bar));
        }
    }
}
