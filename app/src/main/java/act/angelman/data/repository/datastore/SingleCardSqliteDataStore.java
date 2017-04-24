package act.angelman.data.repository.datastore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import act.angelman.data.sqlite.CardColumns;
import act.angelman.data.sqlite.DatabaseHelper;
import act.angelman.domain.model.CardModel;
import lombok.Cleanup;


public class SingleCardSqliteDataStore implements SingleCardDataStore {

    DatabaseHelper dbHelper;

    public SingleCardSqliteDataStore(@NonNull Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public long createSingleCardModel(CardModel cardModel) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        cardModel.cardIndex = getNewIndex(cardModel.categoryId);

        values.put(CardColumns.NAME, cardModel.name);
        values.put(CardColumns.CONTENT_PATH, cardModel.contentPath);
        values.put(CardColumns.VOICE_PATH, cardModel.voicePath);
        values.put(CardColumns.FIRST_TIME, cardModel.firstTime);
        values.put(CardColumns.CARD_INDEX, cardModel.cardIndex);
        values.put(CardColumns.CATEGORY_ID, cardModel.categoryId);
        values.put(CardColumns.CARD_TYPE, cardModel.cardType.getValue());
        values.put(CardColumns.THUMBNAIL_PATH, cardModel.thumbnailPath);
        values.put(CardColumns.HIDE, cardModel.hide ? 1 : 0);

        return db.insert(CardColumns.TABLE_NAME, null, values);
    }

    @Override
    public List<CardModel> getCardListWithCategoryId(int selectedCategoryId) {
        List<CardModel> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = "CATEGORY_ID = ?";
        String[] selectionArgs = {String.valueOf(selectedCategoryId)};
        String orderby = CardColumns.CARD_INDEX + " desc";

        @Cleanup
        Cursor c = db.query(CardColumns.TABLE_NAME, null, selection, selectionArgs, null, null, orderby);

        if (c.getCount() == 0) return null;

        if (c.moveToFirst()) {
            do {
                CardModel cardModel = new CardModel();
                cardModel._id = c.getString(c.getColumnIndex(CardColumns._ID));
                cardModel.name = c.getString(c.getColumnIndex(CardColumns.NAME));
                cardModel.contentPath = c.getString(c.getColumnIndex(CardColumns.CONTENT_PATH));
                cardModel.voicePath = c.getString(c.getColumnIndex(CardColumns.VOICE_PATH));
                cardModel.firstTime = c.getString(c.getColumnIndex(CardColumns.FIRST_TIME));
                cardModel.cardIndex = c.getInt(c.getColumnIndex(CardColumns.CARD_INDEX));
                cardModel.categoryId = c.getInt(c.getColumnIndex(CardColumns.CATEGORY_ID));
                cardModel.thumbnailPath = c.getString(c.getColumnIndex(CardColumns.THUMBNAIL_PATH));
                cardModel.cardType = CardModel.CardType.valueOf(c.getString(c.getColumnIndex(CardColumns.CARD_TYPE)));
                cardModel.hide = (c.getInt(c.getColumnIndex(CardColumns.HIDE)) != 0);

                list.add(cardModel);
            } while (c.moveToNext());
        }
        return list;
    }

    @Override
    public boolean updateCategoryCardIndex(List<CardModel> cardModelList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<CardModel> cardModels = Lists.newArrayList(cardModelList);
        Collections.reverse(cardModels);

        try {
            db.beginTransaction();
            if (removeSingleCardsInCategory(cardModels.get(0).categoryId)) {
                for (CardModel cardModel : cardModels) {
                    createSingleCardModel(cardModel);
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }

    @Override
    public CardModel getSingleCard(String cardId) {
        List<CardModel> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = "_ID = ?";
        String[] selectionArgs = {String.valueOf(cardId)};
        String orderby = CardColumns.CARD_INDEX + " desc";

        @Cleanup
        Cursor c = db.query(CardColumns.TABLE_NAME, null, selection, selectionArgs, null, null, orderby);

        if (c.getCount() == 0) return null;

        if (c.moveToFirst()) {
            do {
                CardModel cardModel = new CardModel();
                cardModel._id = c.getString(c.getColumnIndex(CardColumns._ID));
                cardModel.name = c.getString(c.getColumnIndex(CardColumns.NAME));
                cardModel.contentPath = c.getString(c.getColumnIndex(CardColumns.CONTENT_PATH));
                cardModel.voicePath = c.getString(c.getColumnIndex(CardColumns.VOICE_PATH));
                cardModel.firstTime = c.getString(c.getColumnIndex(CardColumns.FIRST_TIME));
                cardModel.cardIndex = c.getInt(c.getColumnIndex(CardColumns.CARD_INDEX));
                cardModel.categoryId = c.getInt(c.getColumnIndex(CardColumns.CATEGORY_ID));
                cardModel.thumbnailPath = c.getString(c.getColumnIndex(CardColumns.THUMBNAIL_PATH));
                cardModel.cardType = CardModel.CardType.valueOf(c.getString(c.getColumnIndex(CardColumns.CARD_TYPE)));
                cardModel.hide = (c.getInt(c.getColumnIndex(CardColumns.HIDE)) != 0);

                list.add(cardModel);
            } while (c.moveToNext());
        }
        return list.get(0);
    }

    @Override
    public boolean removeSingleCardModel(int categoryId, int cardIndex) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(CardColumns.TABLE_NAME, CardColumns.CATEGORY_ID + "=" + categoryId + " AND " + CardColumns.CARD_INDEX + "=" + cardIndex, null) > 0;
    }

    @Override
    public boolean removeSingleCardsInCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(CardColumns.TABLE_NAME, CardColumns.CATEGORY_ID + "=" + categoryId, null) > 0;
    }

    @Override
    public boolean updateSingleCardModelHide(int categoryId, int cardIndex, boolean hide) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardColumns.HIDE, hide ? 1 : 0);
        return db.update(CardColumns.TABLE_NAME, contentValues, CardColumns.CATEGORY_ID + "=" + categoryId + " AND " + CardColumns.CARD_INDEX + "=" + cardIndex, null) > 0 ? true : false;
    }

    @Override
    public boolean updateSingleCardModel(String cardId, ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(CardColumns.TABLE_NAME, contentValues, CardColumns._ID + "=" + cardId, null) > 0 ? true : false;
    }

    private int getNewIndex(int categoryId) {
        CardModel lastAddedSingleCard = getLastAddedSingleCard(categoryId);
        if (lastAddedSingleCard == null) return 1;

        return lastAddedSingleCard.cardIndex + 1;
    }

    private CardModel getLastAddedSingleCard(int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = "CATEGORY_ID = ?";
        String[] selectionArgs = {String.valueOf(categoryId)};
        String orderby = CardColumns.CARD_INDEX + " desc";

        @Cleanup
        Cursor c = db.query(CardColumns.TABLE_NAME, null, selection, selectionArgs, null, null, orderby, "1");

        if (c.getCount() == 0) return null;

        c.moveToFirst();

        CardModel cardModel = CardModel.builder()
                ._id(c.getString(c.getColumnIndex(CardColumns._ID)))
                .name(c.getString(c.getColumnIndex(CardColumns.NAME)))
                .contentPath(c.getString(c.getColumnIndex(CardColumns.CONTENT_PATH)))
                .firstTime(c.getString(c.getColumnIndex(CardColumns.FIRST_TIME)))
                .categoryId(c.getInt(c.getColumnIndex(CardColumns.CATEGORY_ID)))
                .cardIndex(c.getInt(c.getColumnIndex(CardColumns.CARD_INDEX)))
                .cardType(CardModel.CardType.valueOf(c.getString(c.getColumnIndex(CardColumns.CARD_TYPE))))
                .thumbnailPath(c.getString(c.getColumnIndex(CardColumns.THUMBNAIL_PATH)))
                .hide(c.getInt(c.getColumnIndex(CardColumns.HIDE)) != 0)
                .build();

        return cardModel;
    }
}
