package act.sds.samsung.angelman.data.repository.datastore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import act.sds.samsung.angelman.data.sqlite.DatabaseHelper;
import act.sds.samsung.angelman.data.sqlite.CategoryColumns;
import act.sds.samsung.angelman.domain.model.CategoryItemModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.presentation.util.ResourceMapper;
import act.sds.samsung.angelman.presentation.util.ResourceMapper.ColorState;
import act.sds.samsung.angelman.presentation.util.ResourceMapper.IconState;
import act.sds.samsung.angelman.presentation.util.ResourceMapper.IconType;
import lombok.Cleanup;


public class CategoryDataSqliteDataStore implements CategoryDataStore{

    private DatabaseHelper dbHelper;

    public CategoryDataSqliteDataStore(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public ArrayList<CategoryModel> getCategoryAllList() {
        ArrayList<CategoryModel> list = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {CategoryColumns.TITLE, CategoryColumns.ICON, CategoryColumns.INDEX, CategoryColumns.COLOR};

        @Cleanup
        Cursor c = db.query(true, CategoryColumns.TABLE_NAME, columns, null,null, null, null, CategoryColumns.INDEX, null);
        c.moveToFirst();

        if(c.getCount() != 0) {
            do {
                CategoryModel categoryModel = new CategoryModel();
                categoryModel.title = c.getString(c.getColumnIndex(CategoryColumns.TITLE));
                categoryModel.icon = c.getInt(c.getColumnIndex(CategoryColumns.ICON));
                categoryModel.index = c.getInt(c.getColumnIndex(CategoryColumns.INDEX));
                categoryModel.color = c.getInt(c.getColumnIndex(CategoryColumns.COLOR));
                list.add(categoryModel);
            } while (c.moveToNext());
        }
        return list;
    }

    @Override
    public boolean deleteCategory(int category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.delete(CategoryColumns.TABLE_NAME, CategoryColumns.INDEX + "=" + category , null) > 0;
    }

    @Override
    public ArrayList<CategoryItemModel> getCategoryAllIconList() {

        ArrayList<CategoryItemModel> list = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        @Cleanup
        Cursor c = db.rawQuery(
                "Select a.icon, ifnull(status, " + IconState.UNSELECT.ordinal() + ") as status from (" + getSelectIconListQuery() + ") a left outer join (select icon, " + IconState.USED.ordinal() + " as status from category)c on a.icon = c.icon", null);
        c.moveToFirst();

        do{
            CategoryItemModel categoryItemModel = new CategoryItemModel();
            categoryItemModel.type = c.getInt(c.getColumnIndex(CategoryColumns.ICON));
            categoryItemModel.status = c.getInt(c.getColumnIndex(CategoryColumns.STATUS));
            list.add(categoryItemModel);

        } while(c.moveToNext());

        return list;
    }

    @Override
    public ArrayList<CategoryItemModel> getCategoryAllBackgroundList() {
        ArrayList<CategoryItemModel> list = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        @Cleanup
        Cursor c = db.rawQuery("Select a.color, ifnull(status, " + ColorState.UNSELECT.ordinal() + ") as status from (" + getSelectBackgroudListQuery() + ") a left outer join (select color, " + ColorState.USED.ordinal() + " as status from category)c on a.color = c.color", null);
        c.moveToFirst();

        do{
            CategoryItemModel categoryItemModel = new CategoryItemModel();
            categoryItemModel.type = c.getInt(c.getColumnIndex(CategoryColumns.COLOR));
            categoryItemModel.status = c.getInt(c.getColumnIndex(CategoryColumns.STATUS));
            list.add(categoryItemModel);

        } while(c.moveToNext());

        return list;
    }

    @Override
    public int saveNewCategoryItemAndReturnId(CategoryModel model) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CategoryColumns.TITLE, model.title);
        values.put(CategoryColumns.ICON,  model.icon);
        values.put(CategoryColumns.COLOR, model.color);

        int newIndex = getNewIndex();
        values.put(CategoryColumns.INDEX, newIndex);

        if (db.insert(CategoryColumns.TABLE_NAME, null, values) < 0) {
            throw new InvalidParameterException("can't insert category");
        }

        return newIndex;
    }

    private int getNewIndex() {
        return getLastCategoryIndex() + 1;
    }

    private int getLastCategoryIndex() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String orderBy = CategoryColumns.INDEX + " desc";

        @Cleanup
        Cursor c = db.query(CategoryColumns.TABLE_NAME, null, null, null, null, null, orderBy, "1");

        if (c.getCount() == 0) {
            return 0;
        }

        c.moveToFirst();

        return c.getInt(c.getColumnIndex(CategoryColumns.INDEX));
    }

    public String getSelectIconListQuery() {
        String query = "SELECT 0 as icon";

        for(int i = 1; i < IconType.values().length; i++) {
            query += " union SELECT " + i + " as icon";
        }

        return query;
    }

    public String getSelectBackgroudListQuery() {
        String query = "SELECT 0 as color";

        for(int i = 1; i < ResourceMapper.ColorType.values().length; i++) {
            query += " union SELECT " + i + " as icon";
        }

        return query;
    }
}
