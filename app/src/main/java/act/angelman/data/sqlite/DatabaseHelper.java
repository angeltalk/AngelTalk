package act.angelman.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;

import act.angelman.domain.model.CardModel;
import act.angelman.domain.model.CategoryModel;
import act.angelman.presentation.util.ContentsUtil;
import lombok.Cleanup;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 13;

    private static final String DATABASE_NAME = "AngelmanDatabase";

    private static final String SQL_CREATE_CARD_LIST =
            "CREATE TABLE " + CardColumns.TABLE_NAME + "(" +
                    CardColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    CardColumns.CATEGORY_ID + " INTEGER," +
                    CardColumns.NAME + " TEXT," +
                    CardColumns.CONTENT_PATH + " TEXT," +
                    CardColumns.VOICE_PATH + " TEXT," +
                    CardColumns.FIRST_TIME + " TEXT," +
                    CardColumns.CARD_TYPE + " TEXT," +
                    CardColumns.THUMBNAIL_PATH + " TEXT," +
                    CardColumns.HIDE + " INTEGER," +
                    CardColumns.CARD_INDEX + " INTEGER)";

    private static final String SQL_CREATE_CATEGORY_LIST =
            "CREATE TABLE " + CategoryColumns.TABLE_NAME + "(" +
                    CategoryColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
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

            // New version
            if(oldVersion > 8){
                db.execSQL("drop table " + CardColumns.TABLE_NAME);
                db.execSQL("drop table " + CategoryColumns.TABLE_NAME);
                onCreate(db);
                return;
            }

            //Card Model Migration
            List<CategoryModel> categoryModelList = getOldVersionCategoryModel(db);
            List<CardModel> cardModelList = getOldVersionCardModel(db);

            db.execSQL("drop table " + CardColumns.TABLE_NAME);
            db.execSQL("drop table " + CategoryColumns.TABLE_NAME);
            createTables(db);

            for(CategoryModel categoryModel : categoryModelList) {
                createNewVersionCategoryModel(db, categoryModel);
            }

            for(CardModel cardModel : cardModelList){
                createNewVersionCardModel(db,cardModel);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<CardModel> getOldVersionCardModel(SQLiteDatabase db) {
        List<CardModel> cardModelList = Lists.newArrayList();

        @Cleanup
        Cursor c = db.query(CardColumns.TABLE_NAME, null, null, null, null, null, null);

        if (c.getCount() == 0) return null;

        if (c.moveToFirst()) {
            do {
                CardModel cardModel = new CardModel();
                cardModel.name = c.getString(c.getColumnIndex(CardColumns.NAME));
                cardModel.contentPath = c.getString(c.getColumnIndex("image_path"));
                cardModel.voicePath = c.getString(c.getColumnIndex(CardColumns.VOICE_PATH));
                cardModel.firstTime = c.getString(c.getColumnIndex(CardColumns.FIRST_TIME));
                cardModel.cardIndex = c.getInt(c.getColumnIndex(CardColumns.CARD_INDEX));
                cardModel.categoryId = c.getInt(c.getColumnIndex(CardColumns.CATEGORY_ID));
                cardModel.cardType = CardModel.CardType.PHOTO_CARD;
                cardModel.hide = false;

                if(cardModel.contentPath.contains("DCIM")) {
                    String oldFilePath = cardModel.contentPath;
                    String newFilePath = cardModel.contentPath.replaceAll("DCIM", "contents");

                    File file = new File(oldFilePath);
                    if(file.exists()) {
                        file.renameTo(new File(newFilePath));
                    }
                    cardModel.contentPath = newFilePath;
                } else {
                    //Asset to Content
                    String oldFilePath =  "file:///android_asset/"+"" + cardModel.contentPath;

                    cardModel.contentPath = ContentsUtil.getContentFolder() + File.separator + cardModel.contentPath;

                    File file = new File(oldFilePath);
                    if(file.exists()) {
                        file.renameTo(new File(cardModel.contentPath));
                    }
                }

                cardModelList.add(cardModel);
            } while (c.moveToNext());

            File oldContentFolder = new File(Environment.getExternalStorageDirectory() + File.separator + ContentsUtil.ANGELMAN_FOLDER + File.separator + "DCIM");
            oldContentFolder.delete();

        }
        return cardModelList;
    }

    public long createNewVersionCardModel(SQLiteDatabase db, CardModel cardModel) {
        ContentValues values = new ContentValues();
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

    public List<CategoryModel> getOldVersionCategoryModel(SQLiteDatabase db) {
        List<CategoryModel> list = Lists.newArrayList();

        String[] columns = {CategoryColumns.TITLE, CategoryColumns.ICON, CategoryColumns.INDEX, CategoryColumns.COLOR};

        @Cleanup
        Cursor c = db.query(true, CategoryColumns.TABLE_NAME, columns, null,null, null, null, null, null);
        c.moveToFirst();

        if(c.getCount() != 0) {
            do {
                list.add(CategoryModel.builder()
                        .title(c.getString(c.getColumnIndex(CategoryColumns.TITLE)))
                        .icon(c.getInt(c.getColumnIndex(CategoryColumns.ICON)))
                        .index(c.getInt(c.getColumnIndex(CategoryColumns.INDEX)))
                        .color(c.getInt(c.getColumnIndex(CategoryColumns.COLOR)))
                        .build());
            } while (c.moveToNext());
        }
        return list;
    }

    public long createNewVersionCategoryModel(SQLiteDatabase db, CategoryModel categoryModel) {

        ContentValues values = new ContentValues();
        values.put(CategoryColumns.INDEX, categoryModel.index);
        values.put(CategoryColumns.TITLE, categoryModel.title);
        values.put(CategoryColumns.ICON,  categoryModel.icon);
        values.put(CategoryColumns.COLOR, categoryModel.color);

        return db.insert(CategoryColumns.TABLE_NAME, null, values);
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
