package act.sds.samsung.angelman.data.repository.datastore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import act.sds.samsung.angelman.data.sqlite.DatabaseHelper;
import act.sds.samsung.angelman.data.sqlite.CardColumns;
import act.sds.samsung.angelman.domain.model.CardModel;
import lombok.Cleanup;


public class SingleCardSqliteDataStore implements  SingleCardDataStore {

    DatabaseHelper dbHelper;

    public SingleCardSqliteDataStore(@NonNull Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public ArrayList<CardModel> getAllCardList() {
        ArrayList<CardModel> list = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {CardColumns.NAME, CardColumns.CONTENT_PATH,CardColumns.THUMBNAIL_PATH, CardColumns.VOICE_PATH, CardColumns.FIRST_TIME, CardColumns.CARD_TYPE, CardColumns.HIDE};
        String orderby = CardColumns.CATEGORY_ID + " asc, " + CardColumns.CARD_INDEX + " desc";

        @Cleanup
        Cursor c = db.query(CardColumns.TABLE_NAME, columns, null,null, null, null, orderby);

        c.moveToFirst();
        do{
            CardModel cardModel = new CardModel();
            cardModel.name      = c.getString(c.getColumnIndex(CardColumns.NAME));
            cardModel.contentPath = c.getString(c.getColumnIndex(CardColumns.CONTENT_PATH));
            cardModel.voicePath = c.getString(c.getColumnIndex(CardColumns.VOICE_PATH));
            cardModel.firstTime = c.getString(c.getColumnIndex(CardColumns.FIRST_TIME));
            cardModel.thumbnailPath = c.getString(c.getColumnIndex(CardColumns.THUMBNAIL_PATH));
            cardModel.cardType = CardModel.CardType.valueOf(c.getString(c.getColumnIndex(CardColumns.CARD_TYPE)));
            cardModel.hide = (c.getInt(c.getColumnIndex(CardColumns.HIDE)) != 0);

            list.add(cardModel);

        } while(c.moveToNext());
        return list;
    }

    @Override
    public long createSingleCardModel(CardModel cardModel) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CardColumns.NAME, cardModel.name);
        values.put(CardColumns.CONTENT_PATH, cardModel.contentPath);
        values.put(CardColumns.VOICE_PATH, cardModel.voicePath);
        values.put(CardColumns.FIRST_TIME, cardModel.firstTime);
        values.put(CardColumns.CARD_INDEX, getNewIndex(cardModel.categoryId));
        values.put(CardColumns.CATEGORY_ID, cardModel.categoryId);
        values.put(CardColumns.CARD_TYPE, cardModel.cardType.getValue());
        values.put(CardColumns.THUMBNAIL_PATH, cardModel.thumbnailPath);
        values.put(CardColumns.HIDE, cardModel.hide ? 1 : 0);

        return db.insert(CardColumns.TABLE_NAME, null, values);
    }

    @Override
    public ArrayList<CardModel> getCardListWithCategoryId(int selectedCategoryId) {
        ArrayList<CardModel> list = new ArrayList<>();
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
                cardModel.name      = c.getString(c.getColumnIndex(CardColumns.NAME));
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
    public boolean removeSingleCardModel(int categoryId, int cardIndex) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(CardColumns.TABLE_NAME, CardColumns.CATEGORY_ID + "=" + categoryId + " AND " + CardColumns.CARD_INDEX + "=" + cardIndex , null) > 0;
    }

    @Override
    public boolean removeSingleCardsInCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(CardColumns.TABLE_NAME, CardColumns.CATEGORY_ID + "=" + categoryId , null) > 0;
    }

    @Override
    public boolean updateSingleCardModelHide(int categoryId, int cardIndex, boolean hide) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardColumns.HIDE, hide ? 1 : 0);
        return db.update(CardColumns.TABLE_NAME,contentValues, CardColumns.CATEGORY_ID + "=" + categoryId + " AND " + CardColumns.CARD_INDEX + "=" + cardIndex, null) > 0 ? true : false;
    }

    private int getNewIndex(int categoryId) {
        CardModel lastAddedSingleCard = getLastAddedSingleCard(categoryId);
        if(lastAddedSingleCard == null) return 1;

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
        CardModel cardModel = new CardModel(c.getString(c.getColumnIndex(CardColumns.NAME)),
                                  c.getString(c.getColumnIndex(CardColumns.CONTENT_PATH)),
                                  c.getString(c.getColumnIndex(CardColumns.FIRST_TIME)),
                                  c.getInt(c.getColumnIndex(CardColumns.CATEGORY_ID)),
                                  c.getInt(c.getColumnIndex(CardColumns.CARD_INDEX)),
                                  CardModel.CardType.valueOf(c.getString(c.getColumnIndex(CardColumns.CARD_TYPE))),
                                  c.getString(c.getColumnIndex(CardColumns.THUMBNAIL_PATH)),
                                  c.getInt(c.getColumnIndex(CardColumns.HIDE)) != 0);
        return cardModel;
    }
}
