package angeltalk.plus.data.repository.datastore;

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

import angeltalk.plus.data.sqlite.CategoryColumns;
import angeltalk.plus.data.sqlite.DatabaseHelper;
import angeltalk.plus.domain.model.CategoryItemModel;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.presentation.util.ResourceMapper;
import lombok.Cleanup;

import static angeltalk.plus.presentation.util.ResourceMapper.ColorType.BLUE;
import static angeltalk.plus.presentation.util.ResourceMapper.ColorType.GREEN;
import static angeltalk.plus.presentation.util.ResourceMapper.ColorType.ORANGE;
import static angeltalk.plus.presentation.util.ResourceMapper.ColorType.PURPLE;
import static angeltalk.plus.presentation.util.ResourceMapper.ColorType.RED;
import static angeltalk.plus.presentation.util.ResourceMapper.ColorType.YELLOW;
import static angeltalk.plus.presentation.util.ResourceMapper.IconType.BUS;
import static angeltalk.plus.presentation.util.ResourceMapper.IconType.FOOD;
import static angeltalk.plus.presentation.util.ResourceMapper.IconType.FRIEND;
import static angeltalk.plus.presentation.util.ResourceMapper.IconType.PUZZLE;
import static angeltalk.plus.presentation.util.ResourceMapper.IconType.SCHOOL;
import static angeltalk.plus.presentation.util.ResourceMapper.IconType.TSHIRT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=22)
public class CategoryDataSqliteDataStoreTest {

    private SQLiteDatabase mockDb;
    private String SQL_CREATE_CATEGORY_LIST =
            "CREATE TABLE " + angeltalk.plus.data.sqlite.CategoryColumns.TABLE_NAME + "(" +
                    angeltalk.plus.data.sqlite.CategoryColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    angeltalk.plus.data.sqlite.CategoryColumns.TITLE + " TEXT," +
                    angeltalk.plus.data.sqlite.CategoryColumns.ICON + " INTEGER," +
                    angeltalk.plus.data.sqlite.CategoryColumns.COLOR + " INTEGER," +
                    angeltalk.plus.data.sqlite.CategoryColumns.INDEX + " INTEGER)";
    String[] columns = {CategoryColumns.TITLE, CategoryColumns.ICON, CategoryColumns.INDEX, CategoryColumns.COLOR};
    private CategoryDataSqliteDataStore dataStore;
    private DatabaseHelper mockDbHelper;

    @Before
    public void setUp() throws Exception {
        mockDb = SQLiteDatabase.create(null);
        mockDbHelper = mock(DatabaseHelper.class);

        dataStore = new CategoryDataSqliteDataStore(RuntimeEnvironment.application);
        dataStore.dbHelper = mockDbHelper;

        when(mockDbHelper.getReadableDatabase()).thenReturn(mockDb);
        when(mockDbHelper.getWritableDatabase()).thenReturn(mockDb);
    }

    @Test
    public void givenExistDataBase_whenGetAllCategoryList_thenVerifySizeOfList() throws Exception {

        // given
        mockDb.execSQL(SQL_CREATE_CATEGORY_LIST);
        insertDefaultCategory(mockDb);

        // when
        @Cleanup
        Cursor c = mockDb.query(true, CategoryColumns.TABLE_NAME, columns, null,null, null, null, CategoryColumns.INDEX, null);
        List<CategoryModel> list = dataStore.getCategoryAllList();

        // then
        assertThat(list.size()).isEqualTo(c.getCount()).isEqualTo(5);
    }

    @Test
    public void givenExistDataBase_whenDeleteCategory_thenVerifySizeOfList() throws Exception {

        // given
        mockDb.execSQL(SQL_CREATE_CATEGORY_LIST);
        insertDefaultCategory(mockDb);

        List<CategoryModel> listBefore = dataStore.getCategoryAllList();
        assertThat(listBefore.size()).isEqualTo(5);

        // when
        dataStore.deleteCategory(4);

        // then
        List<CategoryModel> listAfter = dataStore.getCategoryAllList();
        assertThat(listAfter.size()).isEqualTo(4);
    }

    @Test
    public void givenExistDataBase_whenGetCategoryAllIconList_thenVerifySizeOfList() throws Exception {

        // given
        mockDb.execSQL(SQL_CREATE_CATEGORY_LIST);
        insertDefaultCategory(mockDb);

        // when
        @Cleanup
        Cursor c = mockDb.rawQuery(
                "Select a.icon, ifnull(status, " + ResourceMapper.IconState.UNSELECT.ordinal()
                        + ") as status from (" + dataStore.getSelectIconListQuery()
                        + ") a left outer join (select icon, " + ResourceMapper.IconState.USED.ordinal()
                        + " as status from category)c on a.icon = c.icon", null);
        List<CategoryItemModel> list = dataStore.getCategoryAllIconList();
        // then
        assertThat(list.size()).isEqualTo(c.getCount()).isEqualTo(10);
    }

    @Test
    public void givenExistDataBase_whenGetCategoryAllBackgroundList_thenVerifySizeOfList() throws Exception {

        // given
        mockDb.execSQL(SQL_CREATE_CATEGORY_LIST);
        insertDefaultCategory(mockDb);

        // when
        @Cleanup
        Cursor c = mockDb.rawQuery("Select a.color, ifnull(status, " + ResourceMapper.ColorState.UNSELECT.ordinal()
                + ") as status from (" + dataStore.getSelectBackgroudListQuery()
                + ") a left outer join (select color, " + ResourceMapper.ColorState.USED.ordinal()
                + " as status from category)c on a.color = c.color", null);
        List<CategoryItemModel> list = dataStore.getCategoryAllBackgroundList();
        // then
        assertThat(list.size()).isEqualTo(c.getCount()).isEqualTo(6);
    }

    @Test
    public void givenExistDataBase_whenSaveNewCategoryItem_thenVerifyReturnedIdAndSizeOfList() throws Exception {

        // given
        mockDb.execSQL(SQL_CREATE_CATEGORY_LIST);
        insertDefaultCategory(mockDb);

        List<CategoryModel> list = dataStore.getCategoryAllList();
        assertThat(list.size()).isEqualTo(5);

        // when
        CategoryModel categoryModel = CategoryModel.builder().title("옷").icon(TSHIRT.ordinal()).color(PURPLE.ordinal()).build();
        int returnedIndex =  dataStore.saveNewCategoryItemAndReturnId(categoryModel);

        // then
        assertThat(returnedIndex).isEqualTo(5);

        list = dataStore.getCategoryAllList();
        assertThat(list.size()).isEqualTo(6);

    }

    private void insertDefaultCategory(SQLiteDatabase db) {
        insertCategoryData(db, 0, "음식", FOOD.ordinal(), RED.ordinal());
        insertCategoryData(db, 1, "놀이", PUZZLE.ordinal(), ORANGE.ordinal());
        insertCategoryData(db, 2, "탈 것", BUS.ordinal(), YELLOW.ordinal());
        insertCategoryData(db, 3, "가고 싶은 곳", SCHOOL.ordinal(), GREEN.ordinal());
        insertCategoryData(db, 4, "사람", FRIEND.ordinal(), BLUE.ordinal());
    }

    private void insertCategoryData(SQLiteDatabase db, int order, String name, int icon, int color) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(CategoryColumns.TITLE, name);
        contentValue.put(CategoryColumns.ICON, icon);
        contentValue.put(CategoryColumns.INDEX, order);
        contentValue.put(CategoryColumns.COLOR, color);
        db.insert(CategoryColumns.TABLE_NAME, "null", contentValue);
    }
}