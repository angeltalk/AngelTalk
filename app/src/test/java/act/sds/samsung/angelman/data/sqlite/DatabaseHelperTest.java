package act.sds.samsung.angelman.data.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import act.sds.samsung.angelman.BuildConfig;
import act.sds.samsung.angelman.TestAngelmanApplication;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.presentation.activity.CameraGallerySelectionActivity;
import act.sds.samsung.angelman.presentation.manager.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Robolectric.setupActivity;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DatabaseHelperTest {

    @Inject
    ApplicationManager applicationManager;

    private CameraGallerySelectionActivity subject;

    private static final String SQL_CREATE_SINGLECARD_LIST = "CREATE TABLE " + CardColumns.TABLE_NAME + "(" +
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

    private static final String SQL_CREATE_CATEGORY_LIST = "CREATE TABLE " + CategoryColumns.TABLE_NAME + "(" +
            CategoryColumns._ID + " INTEGER_PRIMARY_KEY," +
            CategoryColumns.TITLE + " TEXT," +
            CategoryColumns.ICON + " INTEGER," +
            CategoryColumns.COLOR + " INTEGER," +
            CategoryColumns.INDEX + " INTEGER)";

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        when(applicationManager.getCategoryModel()).thenReturn(getSetupCategoryModel());
        subject = setupActivity(CameraGallerySelectionActivity.class);
    }

    @Test
    public void givenSchemaExits_whenClassCreated_thenCreateDb() throws Exception {
        SQLiteDatabase mockDb = mock(SQLiteDatabase.class);

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(subject.getApplicationContext());
        dbHelper.onCreate(mockDb);
        verify(mockDb).execSQL(SQL_CREATE_SINGLECARD_LIST);

        verify(mockDb).execSQL(SQL_CREATE_CATEGORY_LIST);

        verify(mockDb, atLeastOnce()).insert(anyString(), anyString(), any(ContentValues.class));
    }

    @Test
    public void givenSchemaExits_whenVersionUpgraded_thenUpgradeDb() throws Exception {
        SQLiteDatabase mockDb = mock(SQLiteDatabase.class);

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(subject.getApplicationContext());
        dbHelper.onUpgrade(mockDb, 1, 2);

        verify(mockDb).execSQL("drop table " + CardColumns.TABLE_NAME);
        verify(mockDb).execSQL("drop table " + CategoryColumns.TABLE_NAME);
        verify(mockDb).execSQL(SQL_CREATE_SINGLECARD_LIST);
        verify(mockDb).execSQL(SQL_CREATE_CATEGORY_LIST);
        verify(mockDb, atLeastOnce()).insert(anyString(), anyString(), any(ContentValues.class));
    }

    @Test
    public void givenSchemaExits_whenVersionUpgraded_thenDowngradeDb() throws Exception {
        SQLiteDatabase mockDb = mock(SQLiteDatabase.class);

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(subject.getApplicationContext());
        dbHelper.onDowngrade(mockDb, 2, 1);

        verify(mockDb).execSQL("drop table " + CardColumns.TABLE_NAME);
        verify(mockDb).execSQL("drop table " + CategoryColumns.TABLE_NAME);
        verify(mockDb).execSQL(SQL_CREATE_SINGLECARD_LIST);
        verify(mockDb).execSQL(SQL_CREATE_CATEGORY_LIST);
        verify(mockDb, atLeastOnce()).insert(anyString(), anyString(), any(ContentValues.class));
    }

    private CategoryModel getSetupCategoryModel() {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.index = 0;
        categoryModel.title = "먹을 것";
        categoryModel.color = ResourcesUtil.RED;
        return categoryModel;

    }
}