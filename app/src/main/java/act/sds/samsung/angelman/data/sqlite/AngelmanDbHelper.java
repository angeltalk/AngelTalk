package act.sds.samsung.angelman.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AngelmanDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 8;

    final static String DATABASE_NAME = "AngelmanDatabase";

    public static final String SQL_CREATE_CARD_LIST =
            "CREATE TABLE " + CardColumns.TABLE_NAME + "(" +
                    CardColumns._ID + " INTEGER PRIMARY KEY," +
                    CardColumns.CATEGORY_ID + " INTEGER," +
                    CardColumns.NAME + " TEXT," +
                    CardColumns.IMAGE_PATH + " TEXT," +
                    CardColumns.VOICE_PATH + " TEXT," +
                    CardColumns.FIRST_TIME + " TEXT," +
                    CardColumns.CARD_INDEX + " INTEGER)";

    public static final String SQL_CREATE_CATEGORY_LIST =
            "CREATE TABLE " + CategoryColumns.TABLE_NAME + "(" +
                    CategoryColumns._ID + " INTEGER PRIMARY KEY," +
                    CategoryColumns.TITLE + " TEXT," +
                    CategoryColumns.ICON + " INTEGER," +
                    CategoryColumns.COLOR + " INTEGER," +
                    CategoryColumns.INDEX + " INTEGER)";

    private static AngelmanDbHelper angelmanDbHelper;

    private AngelmanDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static AngelmanDbHelper getInstance(Context context){
        if(angelmanDbHelper == null){
            angelmanDbHelper = new AngelmanDbHelper(context);
        }
        return angelmanDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        new DefaultDataCreator().insertDefaultData(db);
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
