package act.angelman.presentation.manager;

public class ApplicationConstants {
    public static final String VIDEO_FRAGMENT_TAG = "VIDEO_FRAGMENT_TAG";
    public static final String INTENT_KEY_SHARE_CARD = "fromShareCardActivity";
    public static final String CATEGORY_COLOR = "categoryColor";
    public static final String INTENT_KEY_NEW_CARD = "isNewCard";
    public static final String IMAGE_PATH_EXTRA = "imagePath";
    public static final String FIRST_LAUNCH = "firstLaunch";
    public static final String NEW_INSTALL = "newInstall";
    public static final String PRIVATE_PREFERENCE_NAME = "act.angelman";
    public static final String INTENT_KEY_REFRESH_CARD = "isRefreshCard";
    public static final String INTENT_KEY_LIST_BACK = "listBack";
    public static final String INTENT_KEY_CARD_EDITED = "cardEdited";
    public static final String EDIT_CARD_ID = "editCardId";
    public static final String EDIT_TYPE = "editType";
    public static final String INTENT_KEY_MULTI_DOWNLOAD = "isMultiDownload";
    public static final String INTENT_KEY_MULTI_DOWNLOAD_DATA = "multiDownloadData";
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    public static final int CAMERA_PERMISSION_FOR_PHOTO_REQUEST_CODE = 2;
    public static final int CAMERA_PERMISSION_FOR_VIDEO_REQUEST_CODE = 3;



    public enum SHARE_MESSENGER_TYPE{ KAKAOTALK, MESSAGE ,WHATSAPP}
    public enum CardEditType {
        CONTENT("CONTENT"),
        NAME("NAME"),
        VOICE("VOICE");
        private String value;
        CardEditType(String type){this.value = type;}
        public String value() {return value;}
    }
}
