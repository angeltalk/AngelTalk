package act.sds.samsung.angelman.data.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import act.sds.samsung.angelman.domain.model.CardModel;

import static act.sds.samsung.angelman.presentation.util.ResourceMapper.ColorType.BLUE;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.ColorType.GREEN;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.ColorType.ORANGE;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.ColorType.RED;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.ColorType.YELLOW;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.BUS;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.FOOD;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.FRIEND;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.PUZZLE;
import static act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType.SCHOOL;

public class DefaultDataCreator {

    private static final int LOCKSCREEN_VISIBLE = 1;
    private static final int LOCKSCREEN_INVISIBLE = 0;

    public void insertDefaultData(SQLiteDatabase db) {
        insertDefaultCategory(db);
        insertDefaultSingleCard(db);
    }

    private void insertDefaultCategory(SQLiteDatabase db) {
        insertCategoryData(db, 0, "음식", FOOD.ordinal(), RED.ordinal());
        insertCategoryData(db, 1, "놀이", PUZZLE.ordinal(), ORANGE.ordinal());
        insertCategoryData(db, 2, "탈 것", BUS.ordinal(), YELLOW.ordinal());
        insertCategoryData(db, 3, "가고 싶은 곳", SCHOOL.ordinal(), GREEN.ordinal());
        insertCategoryData(db, 4, "사람", FRIEND.ordinal(), BLUE.ordinal());
    }

    private void insertDefaultSingleCard(SQLiteDatabase db) {
        int index = 0;
        insertCategoryItemData(db,   0       , "물 먹고 싶어요"  , "water.mp4", "20161018_000002", CardModel.CardType.VIDEO_CARD, index++  , LOCKSCREEN_VISIBLE);
        insertCategoryItemData(db,   0       , "쥬스"          , "juice.png", "20161019_120018", CardModel.CardType.PHOTO_CARD, index++  , LOCKSCREEN_VISIBLE);
        insertCategoryItemData(db,   0       , "우유"          , "milk.png",  "20161019_120017", CardModel.CardType.PHOTO_CARD, index++  , LOCKSCREEN_VISIBLE);

        index = 0;
        insertCategoryItemData(db,   1       , "색칠 놀이 해요"  , "coloring.mp4", "20161019_120012", CardModel.CardType.VIDEO_CARD, index++, LOCKSCREEN_INVISIBLE);
        insertCategoryItemData(db,   1       , "블럭"          , "block.jpg", "20161019_120011", CardModel.CardType.PHOTO_CARD, index++, LOCKSCREEN_INVISIBLE);
        insertCategoryItemData(db,   1       , "크레파스"       , "crayon.jpg", "20161019_120011", CardModel.CardType.PHOTO_CARD, index++, LOCKSCREEN_INVISIBLE);
        insertCategoryItemData(db,   1       , "색종이"         , "coloredpaper.jpg", "20161019_120019", CardModel.CardType.PHOTO_CARD, index++, LOCKSCREEN_INVISIBLE);

        index = 0;
        insertCategoryItemData(db,   2       , "차 타요"       , "car.mp4", "20161019_120011", CardModel.CardType.VIDEO_CARD, index++, LOCKSCREEN_INVISIBLE);
        insertCategoryItemData(db,   2       , "비행기 타요"    , "airplane.mp4", "20161019_120011", CardModel.CardType.VIDEO_CARD, index++, LOCKSCREEN_INVISIBLE);
        insertCategoryItemData(db,   2       , "지하철 타요"    , "subway.mp4", "20161019_120011", CardModel.CardType.VIDEO_CARD, index++, LOCKSCREEN_INVISIBLE);
        insertCategoryItemData(db,   2       , "버스"         , "bus.jpg", "20161019_120011", CardModel.CardType.PHOTO_CARD, index++, LOCKSCREEN_INVISIBLE);

        index = 0;
        insertCategoryItemData(db,   3       , "손 씻고 싶어요" , "washinghand.mp4", "20161019_120012", CardModel.CardType.VIDEO_CARD, index++, LOCKSCREEN_INVISIBLE);
        insertCategoryItemData(db,   3       , "놀이공원 가요"  , "amusementpark.mp4", "20161019_120012", CardModel.CardType.VIDEO_CARD, index++, LOCKSCREEN_INVISIBLE);
        insertCategoryItemData(db,   3       , "그네 타러 가요" , "swing.mp4", "20161019_120012", CardModel.CardType.VIDEO_CARD, index++, LOCKSCREEN_INVISIBLE);
        insertCategoryItemData(db,   3       , "패스트푸드"    , "fastfood.jpg", "20161019_120012", CardModel.CardType.PHOTO_CARD, index++, LOCKSCREEN_INVISIBLE);
        insertCategoryItemData(db,   3       , "수영장"       , "swimmingpool.jpg", "20161019_120012", CardModel.CardType.PHOTO_CARD, index++, LOCKSCREEN_INVISIBLE);
        insertCategoryItemData(db,   3       , "마트"        , "mart.JPG", "20161019_120012", CardModel.CardType.PHOTO_CARD, index++, LOCKSCREEN_INVISIBLE);

        index = 0;
        insertCategoryItemData(db,   4       , "선생님"      ,"", "20161019_120013", CardModel.CardType.PHOTO_CARD, index++, LOCKSCREEN_INVISIBLE);
        insertCategoryItemData(db,   4       , "아빠"       ,"", "20161019_120013", CardModel.CardType.PHOTO_CARD, index++, LOCKSCREEN_INVISIBLE);
        insertCategoryItemData(db,   4       , "엄마"       ,"", "20161019_120012", CardModel.CardType.PHOTO_CARD, index++, LOCKSCREEN_INVISIBLE);
    }

    private void insertCategoryData(SQLiteDatabase db, int order, String name, int icon, int color) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(CategoryColumns.TITLE, name);
        contentValue.put(CategoryColumns.ICON, icon);
        contentValue.put(CategoryColumns.INDEX, order);
        contentValue.put(CategoryColumns.COLOR, color);
        db.insert(CategoryColumns.TABLE_NAME, "null", contentValue);
    }

    private void insertCategoryItemData(SQLiteDatabase db, int categoryIndex, String item, String imagePath, String firstTime, CardModel.CardType cardType, int index, int lockScreen){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardColumns.CATEGORY_ID, categoryIndex);
        contentValues.put(CardColumns.NAME, item);
        contentValues.put(CardColumns.CONTENT_PATH, imagePath);
        contentValues.put(CardColumns.FIRST_TIME, firstTime);
        contentValues.put(CardColumns.CARD_TYPE, cardType.getValue());
        contentValues.put(CardColumns.CARD_INDEX, index);
        db.insert(CardColumns.TABLE_NAME, "null", contentValues);
    }
}
