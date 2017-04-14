package act.angelman.data.repository.datastore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import act.angelman.BuildConfig;
import act.angelman.data.sqlite.CardColumns;
import act.angelman.data.sqlite.DatabaseHelper;
import act.angelman.domain.model.CardModel;
import lombok.Cleanup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SingleCardSqliteDataStoreTest {
    private SQLiteDatabase mockDb;

    private String SQL_CREATE_SINGLECARD_LIST =
            "CREATE TABLE " + CardColumns.TABLE_NAME + "(" +
                    CardColumns._ID + " INTEGER_PRIMARY_KEY," +
                    CardColumns.CATEGORY_ID + " INTEGER," +
                    CardColumns.NAME + " TEXT," +
                    CardColumns.CONTENT_PATH + " TEXT," +
                    CardColumns.VOICE_PATH + " TEXT," +
                    CardColumns.FIRST_TIME + " TEXT," +
                    CardColumns.CARD_TYPE + " TEXT," +
                    CardColumns.THUMBNAIL_PATH + " TEXT," +
                    CardColumns.HIDE + " INTEGER," +
                    CardColumns.CARD_INDEX + " INTEGER)";
    private String[] columns = {CardColumns.NAME, CardColumns.CONTENT_PATH,CardColumns.THUMBNAIL_PATH, CardColumns.VOICE_PATH, CardColumns.FIRST_TIME, CardColumns.CARD_TYPE, CardColumns.HIDE};

    @Before
    public void setUp() throws Exception {
        mockDb = SQLiteDatabase.create(null);
    }

    @Test
    public void givenExistDataBase_whenGetAllSingleCardList_thenVerifySizeOfList() throws Exception {
        DatabaseHelper mockDbHelper = mock(DatabaseHelper.class);

        SingleCardSqliteDataStore dataStore = new SingleCardSqliteDataStore(RuntimeEnvironment.application);
        dataStore.dbHelper = mockDbHelper;

        when(mockDbHelper.getReadableDatabase()).thenReturn(mockDb);

        mockDb.execSQL(SQL_CREATE_SINGLECARD_LIST);
        insertData(mockDb);

        @Cleanup
        Cursor c = mockDb.query(CardColumns.TABLE_NAME, columns, null,null, null, null, CardColumns.CARD_INDEX + " desc");
        List<CardModel> list = dataStore.getCardListWithCategoryId(0);

        verify(mockDbHelper).getReadableDatabase();

        assertThat(c.getCount()).isEqualTo(list.size());
        assertThat(list.get(0).name).isEqualTo("우유");
    }

    @Test
    public void givenExistDataBase_whenCreateSingleCard_thenVerifyIncrementation() throws Exception {
        DatabaseHelper mockDbHelper = mock(DatabaseHelper.class);

        SingleCardSqliteDataStore dataStore = new SingleCardSqliteDataStore(RuntimeEnvironment.application);
        dataStore.dbHelper = mockDbHelper;

        when(mockDbHelper.getWritableDatabase()).thenReturn(mockDb);
        when(mockDbHelper.getReadableDatabase()).thenReturn(mockDb);

        mockDb.execSQL(SQL_CREATE_SINGLECARD_LIST);
        insertData(mockDb);

        @Cleanup
        Cursor c = mockDb.query(CardColumns.TABLE_NAME, columns, null,null, null, null, CardColumns.FIRST_TIME + " desc");

        int initialCount = c.getCount();

        CardModel cardModel = CardModel.builder().name("치킨").contentPath("chicken.png").firstTime("20161012_133600").cardType(CardModel.CardType.PHOTO_CARD).build();
        long result = dataStore.createSingleCardModel(cardModel);

        @Cleanup
        Cursor resultCursor = mockDb.query(CardColumns.TABLE_NAME, columns, null,null, null, null, CardColumns.CARD_INDEX + " desc");
        resultCursor.moveToFirst();
        assertThat(resultCursor.getString(resultCursor.getColumnIndex(CardColumns.NAME))).isEqualTo("치킨");
        assertThat(resultCursor.getCount()).isEqualTo(initialCount + 1);
        assertThat(result).isNotEqualTo(-1);
    }

    @Test
    public void givenExistDataBase_whenUpdateSingleCardModelHide_thenVerifyChangeHide() throws Exception {
        DatabaseHelper mockDbHelper = mock(DatabaseHelper.class);

        SingleCardSqliteDataStore dataStore = new SingleCardSqliteDataStore(RuntimeEnvironment.application);
        dataStore.dbHelper = mockDbHelper;

        when(mockDbHelper.getWritableDatabase()).thenReturn(mockDb);
        when(mockDbHelper.getReadableDatabase()).thenReturn(mockDb);

        mockDb.execSQL(SQL_CREATE_SINGLECARD_LIST);
        insertData(mockDb);

        for(CardModel cardModel  : dataStore.getCardListWithCategoryId(0)){
            if(cardModel.cardIndex == 1){
                assertThat(cardModel.hide).isFalse();
            }
        }

        // when
        dataStore.updateSingleCardModelHide(0, 1, true);

        // then
        for(CardModel cardModel  : dataStore.getCardListWithCategoryId(0)){
            if(cardModel.cardIndex == 1){
                assertThat(cardModel.hide).isTrue();
            }
        }

    }

    private void insertData(SQLiteDatabase db){
        int index = 0;
        insertCategoryItemData(db,   0       , "물 먹고 싶어요"  , "water.mp4", "water.jpg",  "20161018_000002", CardModel.CardType.VIDEO_CARD, index++);
        insertCategoryItemData(db,   0       , "쥬스"          , "juice.png",null, "20161019_120018", CardModel.CardType.PHOTO_CARD, index++);
        insertCategoryItemData(db,   0       , "우유"          , "milk.png", null, "20161019_120017", CardModel.CardType.PHOTO_CARD, index++);
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