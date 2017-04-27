package act.angelman.data.repository.datastore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;

import java.security.InvalidParameterException;
import java.util.List;

import act.angelman.data.sqlite.CategoryColumns;
import act.angelman.data.sqlite.DatabaseHelper;
import act.angelman.domain.model.CategoryItemModel;
import act.angelman.domain.model.CategoryModel;
import act.angelman.presentation.util.ResourceMapper;
import act.angelman.presentation.util.ResourceMapper.ColorState;
import act.angelman.presentation.util.ResourceMapper.IconState;
import act.angelman.presentation.util.ResourceMapper.IconType;
import lombok.Cleanup;


public class CategoryDataSqliteDataStore implements CategoryDataStore{

    DatabaseHelper dbHelper;

    public CategoryDataSqliteDataStore(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public List<CategoryModel> getCategoryAllList() {
        List<CategoryModel> list = Lists.newArrayList();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {CategoryColumns.TITLE, CategoryColumns.ICON, CategoryColumns.INDEX, CategoryColumns.COLOR};

        @Cleanup
        Cursor c = db.query(true, CategoryColumns.TABLE_NAME, columns, null,null, null, null, CategoryColumns.INDEX, null);
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

    @Override
    public boolean deleteCategory(int category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.delete(CategoryColumns.TABLE_NAME, CategoryColumns.INDEX + "=" + category , null) > 0;
    }

    @Override
    public List<CategoryItemModel> getCategoryAllIconList() {

        List<CategoryItemModel> list = Lists.newArrayList();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        @Cleanup
        Cursor c = db.rawQuery(
                "Select a.icon, ifnull(status, " + IconState.UNSELECT.ordinal() + ") as status from (" + getSelectIconListQuery() + ") a left outer join (select icon, " + IconState.USED.ordinal() + " as status from category)c on a.icon = c.icon", null);
        c.moveToFirst();

        do{
            list.add(CategoryItemModel.builder()
                    .type(c.getInt(c.getColumnIndex(CategoryColumns.ICON)))
                    .status(c.getInt(c.getColumnIndex(CategoryColumns.STATUS)))
                    .build());
        } while(c.moveToNext());

        return list;
    }

    @Override
    public List<CategoryItemModel> getCategoryAllBackgroundList() {
        List<CategoryItemModel> list = Lists.newArrayList();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        @Cleanup
        Cursor c = db.rawQuery("Select a.color, ifnull(status, " + ColorState.UNSELECT.ordinal() + ") as status from (" + getSelectBackgroudListQuery() + ") a left outer join (select color, " + ColorState.USED.ordinal() + " as status from category)c on a.color = c.color", null);
        c.moveToFirst();

        do{
            list.add(
                    CategoryItemModel.builder()
                            .type(c.getInt(c.getColumnIndex(CategoryColumns.COLOR)))
                            .status(c.getInt(c.getColumnIndex(CategoryColumns.STATUS)))
                            .build());

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
