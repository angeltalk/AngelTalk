package angeltalk.plus.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import angeltalk.plus.domain.model.CardModel;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.presentation.manager.ApplicationConstants;
import angeltalk.plus.presentation.util.ContentsUtil;
import angeltalk.plus.presentation.util.FileUtil;
import lombok.Cleanup;

import static angeltalk.plus.presentation.util.ContentsUtil.getContentFolder;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 14;

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
    private Context context;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
        new DefaultDataGenerator().insertDefaultData(context,db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            if(oldVersion > 8){
                //New Version
                db.execSQL("drop table " + CardColumns.TABLE_NAME);
                db.execSQL("drop table " + CategoryColumns.TABLE_NAME);
                onCreate(db);
            } else {
                //Old Version
                SharedPreferences.Editor edit = context.getSharedPreferences(ApplicationConstants.PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
                edit.putBoolean(ApplicationConstants.NEW_INSTALL, false).apply();

                List<CategoryModel> categoryModelList = getOldVersionCategoryModel(db);
                List<CardModel> cardModelList = getOldVersionCardModel(db);

                db.execSQL("drop table " + CardColumns.TABLE_NAME);
                db.execSQL("drop table " + CategoryColumns.TABLE_NAME);
                createTables(db);

                for (CategoryModel categoryModel : categoryModelList) {
                    createNewVersionCategoryModel(db, categoryModel);
                }

                for (CardModel cardModel : cardModelList) {

                    if(cardModel.contentPath.contains("DCIM")) {
                        // made card
                        fileMigration(cardModel);
                    } else {
                        // exist asset
                        if(Strings.isNullOrEmpty(cardModel.contentPath)) {
                            cardModel.contentPath = "blank.jpg";
                        }
                        copyDefaultOldAssetImageToImageFolder(cardModel.contentPath, context);
                        cardModel.contentPath = ContentsUtil.getContentFolder(context) + File.separator + cardModel.contentPath;
                    }
                    createNewVersionCardModel(db, cardModel);
                }

                File oldContentFolder = new File(Environment.getExternalStorageDirectory() + File.separator + ContentsUtil.ANGELMAN_FOLDER + File.separator + "DCIM");
                oldContentFolder.delete();
                File oldVoiceFolder = new File(Environment.getExternalStorageDirectory() + File.separator + ContentsUtil.ANGELMAN_FOLDER + File.separator + "voice");
                oldVoiceFolder.delete();
                File oldRootFolder = new File(Environment.getExternalStorageDirectory() + File.separator + ContentsUtil.ANGELMAN_FOLDER);
                oldRootFolder.delete();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fileMigration(CardModel cardModel) {
        String oldFilePath = cardModel.contentPath;
        String newFilePath = ContentsUtil.getContentFolder(context) + File.separator + cardModel.contentPath.substring(cardModel.contentPath.lastIndexOf(File.separator)+1);
        String oldVoiceFilePath = cardModel.voicePath;
        String newVoiceFilePath = ContentsUtil.getVoiceFolder(context) + File.separator + cardModel.voicePath.substring(cardModel.voicePath.lastIndexOf(File.separator)+1);

        try {
            FileUtil.copyFile(new File(oldFilePath), new File(newFilePath));
            FileUtil.copyFile(new File(oldVoiceFilePath), new File(newVoiceFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new File(oldFilePath).delete();
        new File(oldVoiceFilePath).delete();

        cardModel.voicePath = newVoiceFilePath;
        cardModel.contentPath = newFilePath;
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

                cardModelList.add(cardModel);
            } while (c.moveToNext());

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

    private void copyDefaultOldAssetImageToImageFolder(String fileName ,Context context) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.open("oldContents" + File.separator + fileName);
            File outFile = new File(getContentFolder(context), fileName);
            out = new FileOutputStream(outFile);
            FileUtil.copyFile(in, out);
        } catch (IOException e) {
            Log.e("AngelmanApplication", "Failed to copy asset file: " + fileName, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("AngelmanApplication", "Failed to close image input file.", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e("AngelmanApplication", "Failed to close image output file.", e);
                }
            }
        }
    }

}
