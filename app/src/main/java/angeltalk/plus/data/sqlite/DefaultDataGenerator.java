package angeltalk.plus.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

import angeltalk.plus.R;
import angeltalk.plus.domain.model.CardModel;
import angeltalk.plus.presentation.util.ContentsUtil;

import static angeltalk.plus.presentation.util.ResourceMapper.ColorType.BLUE;
import static angeltalk.plus.presentation.util.ResourceMapper.ColorType.GREEN;
import static angeltalk.plus.presentation.util.ResourceMapper.ColorType.ORANGE;
import static angeltalk.plus.presentation.util.ResourceMapper.ColorType.RED;
import static angeltalk.plus.presentation.util.ResourceMapper.ColorType.YELLOW;
import static angeltalk.plus.presentation.util.ResourceMapper.IconType.BUS;
import static angeltalk.plus.presentation.util.ResourceMapper.IconType.FOOD;
import static angeltalk.plus.presentation.util.ResourceMapper.IconType.FRIEND;
import static angeltalk.plus.presentation.util.ResourceMapper.IconType.PUZZLE;
import static angeltalk.plus.presentation.util.ResourceMapper.IconType.SCHOOL;

public class DefaultDataGenerator {

    private Context context;

    public void insertDefaultData(Context context, SQLiteDatabase db) {
        this.context = context;
        insertDefaultCategory(db);
        insertDefaultSingleCard(context, db);
    }

    private void insertDefaultCategory(SQLiteDatabase db) {
        insertCategoryData(db, 0, context.getString(R.string.category_eat_drink), FOOD.ordinal(), RED.ordinal());
        insertCategoryData(db, 1, context.getString(R.string.category_play), PUZZLE.ordinal(), ORANGE.ordinal());
        insertCategoryData(db, 2, context.getString(R.string.category_ride), BUS.ordinal(), YELLOW.ordinal());
        insertCategoryData(db, 3, context.getString(R.string.category_go), SCHOOL.ordinal(), GREEN.ordinal());
        insertCategoryData(db, 4, context.getString(R.string.category_people), FRIEND.ordinal(), BLUE.ordinal());
    }

    private void insertDefaultSingleCard(Context context, SQLiteDatabase db) {
        String contentFolder = ContentsUtil.getContentFolder(context) + File.separator;

        int index = 0;
        insertCategoryItemData(db,   0       , context.getString(R.string.sample_card_drinkwater)  , contentFolder+"water.mp4",contentFolder+"water.jpg", "20161018_000002", CardModel.CardType.VIDEO_CARD, index++);
        insertCategoryItemData(db,   0       , context.getString(R.string.sample_card_juice)          , contentFolder+"juice.png", null,"20161019_120018", CardModel.CardType.PHOTO_CARD, index++);
        insertCategoryItemData(db,   0       , context.getString(R.string.sample_card_milk)          , contentFolder+"milk.png", null , "20161019_120017", CardModel.CardType.PHOTO_CARD, index++);

        index = 0;
        insertCategoryItemData(db,   1       , context.getString(R.string.sample_card_painting)  , contentFolder+"coloring.mp4",contentFolder+"coloring.jpg", "20161019_120012", CardModel.CardType.VIDEO_CARD, index++);
        insertCategoryItemData(db,   1       , context.getString(R.string.sample_card_swing) , contentFolder+"swing.mp4",contentFolder+"swing.jpg", "20161019_120012", CardModel.CardType.VIDEO_CARD, index++);
        insertCategoryItemData(db,   1       , context.getString(R.string.sample_card_block)          , contentFolder+"block.jpg", null, "20161019_120011", CardModel.CardType.PHOTO_CARD, index++);
        insertCategoryItemData(db,   1       , context.getString(R.string.sample_card_crayon)       , contentFolder+"crayon.jpg", null, "20161019_120011", CardModel.CardType.PHOTO_CARD, index++);
        insertCategoryItemData(db,   1       , context.getString(R.string.sample_card_coloredpaper)         , contentFolder+"coloredpaper.jpg", null, "20161019_120019", CardModel.CardType.PHOTO_CARD, index++);

        index = 0;
        insertCategoryItemData(db,   2       , context.getString(R.string.sample_card_airplane)    , contentFolder+"airplane.mp4", contentFolder+"airplane.jpg", "20161019_120011", CardModel.CardType.VIDEO_CARD, index++);
        insertCategoryItemData(db,   2       , context.getString(R.string.sample_card_bus)         , contentFolder+"bus.jpg",  null, "20161019_120011", CardModel.CardType.PHOTO_CARD, index++);
        insertCategoryItemData(db,   2       , context.getString(R.string.sample_card_car)       , contentFolder+"car.mp4", contentFolder+"car.jpg", "20161019_120011", CardModel.CardType.VIDEO_CARD, index++);

        index = 0;
        insertCategoryItemData(db,   3       , context.getString(R.string.sample_card_handwashing) , contentFolder+"washinghand.mp4", contentFolder+"washinghand.jpg" ,"20161019_120012", CardModel.CardType.VIDEO_CARD, index++);
        insertCategoryItemData(db,   3       , context.getString(R.string.sample_card_amusement_park)  , contentFolder+"amusementpark.mp4",contentFolder+"amusementpark.jpg",  "20161019_120012", CardModel.CardType.VIDEO_CARD, index++);
        insertCategoryItemData(db,   3       , context.getString(R.string.sample_card_fast_food)    , contentFolder+"fastfood.jpg", null, "20161019_120012", CardModel.CardType.PHOTO_CARD, index++);
        insertCategoryItemData(db,   3       , context.getString(R.string.sample_card_pool)       , contentFolder+"swimmingpool.jpg", null, "20161019_120012", CardModel.CardType.PHOTO_CARD, index++);
        insertCategoryItemData(db,   3       , context.getString(R.string.sample_card_mall)        , contentFolder+"mart.jpg",  null,"20161019_120012", CardModel.CardType.PHOTO_CARD, index++);

        index = 0;
        insertCategoryItemData(db,   4       , context.getString(R.string.sample_card_teacher)      ,contentFolder+"blank.jpg",  null,"20161019_120013", CardModel.CardType.PHOTO_CARD, index++);
        insertCategoryItemData(db,   4       , context.getString(R.string.sample_card_daddy)       ,contentFolder+"blank.jpg",  null,"20161019_120013", CardModel.CardType.PHOTO_CARD, index++);
        insertCategoryItemData(db,   4       , context.getString(R.string.sample_card_mommy)       ,contentFolder+"blank.jpg", null, "20161019_120012", CardModel.CardType.PHOTO_CARD, index++);
    }

    private void insertCategoryData(SQLiteDatabase db, int order, String name, int icon, int color) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(CategoryColumns.TITLE, name);
        contentValue.put(CategoryColumns.ICON, icon);
        contentValue.put(CategoryColumns.INDEX, order);
        contentValue.put(CategoryColumns.COLOR, color);
        db.insert(CategoryColumns.TABLE_NAME, "null", contentValue);
    }

    private void insertCategoryItemData(SQLiteDatabase db, int categoryIndex, String item, String imagePath,String thumbnailPath, String firstTime, CardModel.CardType cardType, int index){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardColumns.CATEGORY_ID, categoryIndex);
        contentValues.put(CardColumns.NAME, item);
        contentValues.put(CardColumns.CONTENT_PATH, imagePath);
        contentValues.put(CardColumns.THUMBNAIL_PATH, thumbnailPath);
        contentValues.put(CardColumns.FIRST_TIME, firstTime);
        contentValues.put(CardColumns.CARD_TYPE, cardType.getValue());
        contentValues.put(CardColumns.CARD_INDEX, index);
        contentValues.put(CardColumns.HIDE, 0);
        db.insert(CardColumns.TABLE_NAME, "null", contentValues);
    }
}
