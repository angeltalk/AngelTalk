package act.angelman.data.sqlite;

import android.provider.BaseColumns;

public abstract class CardColumns implements BaseColumns {
    public static final String TABLE_NAME  = "cardlist";
    public static final String NAME        = "name";
    public static final String CONTENT_PATH = "content_path";
    public static final String VOICE_PATH  = "voice_path";
    public static final String FIRST_TIME  = "first_time";
    public static final String CATEGORY_ID = "category_id";
    public static final String CARD_INDEX  = "card_index";
    public static final String CARD_TYPE = "card_type";
    public static final String THUMBNAIL_PATH = "thumbnail_path";
    public static final String HIDE ="hide";
}
