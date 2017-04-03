package act.sds.samsung.angelman.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 10;

    private static final String DATABASE_NAME = "AngelmanDatabase";

    private static final String SQL_CREATE_CARD_LIST =
            "CREATE TABLE " + CardColumns.TABLE_NAME + "(" +
                    CardColumns._ID + " INTEGER_PRIMARY_KEY," +
                    CardColumns.CATEGORY_ID + " INTEGER," +
                    CardColumns.NAME + " TEXT," +
                    CardColumns.CONTENT_PATH + " TEXT," +
                    CardColumns.VOICE_PATH + " TEXT," +
                    CardColumns.FIRST_TIME + " TEXT," +
                    CardColumns.CARD_TYPE + " TEXT," +
                    CardColumns.THUMBNAIL_PATH + " TEXT," +
                    CardColumns.CARD_INDEX + " INTEGER)";

    private static final String SQL_CREATE_CATEGORY_LIST =
            "CREATE TABLE " + CategoryColumns.TABLE_NAME + "(" +
                    CategoryColumns._ID + " INTEGER_PRIMARY_KEY," +
                    CategoryColumns.TITLE + " TEXT," +
                    CategoryColumns.ICON + " INTEGER," +
                    CategoryColumns.COLOR + " INTEGER," +
                    CategoryColumns.INDEX + " INTEGER)";

    private static DatabaseHelper databaseHelper;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context){
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        new DefaultDataGenerator().insertDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("drop table " + CardColumns.TABLE_NAME);
            db.execSQL("drop table " + CategoryColumns.TABLE_NAME);
            onCreate(db);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("drop table " + CardColumns.TABLE_NAME);
            db.execSQL("drop table " + CategoryColumns.TABLE_NAME);
            onCreate(db);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CATEGORY_LIST);
        db.execSQL(SQL_CREATE_CARD_LIST);
    }
}
