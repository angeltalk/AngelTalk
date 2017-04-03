package act.sds.samsung.angelman.presentation.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.data.transfer.CardTransfer;
import act.sds.samsung.angelman.domain.model.CardModel;
import act.sds.samsung.angelman.domain.model.CardTransferModel;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.domain.repository.CardRepository;
import act.sds.samsung.angelman.domain.repository.CategoryRepository;
import act.sds.samsung.angelman.presentation.adapter.CategoryAdapter;
import act.sds.samsung.angelman.presentation.custom.CustomConfirmDialog;
import act.sds.samsung.angelman.presentation.listener.OnDownloadCompleteListener;
import act.sds.samsung.angelman.presentation.receiver.NotificationActionReceiver;
import act.sds.samsung.angelman.presentation.util.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.ContentsUtil;
import act.sds.samsung.angelman.presentation.util.FileUtil;
import act.sds.samsung.angelman.presentation.util.NotificationActionManager;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static act.sds.samsung.angelman.R.string.delete_category;

public class CategoryMenuActivity extends AbstractActivity {

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CardRepository cardRepository;

    @Inject
    CardTransfer cardTransfer;

    @Inject
    ApplicationManager applicationManager;

    @BindView(R.id.category_list)
    public GridView categoryGridView;

    @BindView(R.id.logo_angeltalk)
    public ImageView logoButton;

    @BindView(R.id.category_delete_button)
    public TextView categoryDeleteButton;

    @BindString(R.string.voc_web_url)
    public String vocWebUrl;

    @BindString(R.string.new_category)
    public String newCategoryString;

    @BindString(R.string.one_left_delete_category)
    public String oneLeftDeleteCategory;

    public enum CategoryMenuStatus {
        NONE, CATEGORY_DEFAULT, CATEGORY_DELETABLE
    }

    protected CategoryAdapter categoryAdapter;
    private CustomConfirmDialog dialog;
    private int selectedCategoryId = -1;
    private PopupWindow easterEggPopup;
    private GestureDetector logoGestureDetector;

    String receiveKey ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ButterKnife.bind(this);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);

        initEasterEggPopup();
        setCategoryGridView();
        launchNotification();

        if (getString(R.string.kakao_scheme).equals(getIntent().getScheme())) {
            Uri uri = getIntent().getData();
            receiveKey = uri.getQueryParameter("key");
            showDownloadConfirmDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCategoryGridView();
    }

    @Override
    public void onBackPressed() {
        if (categoryAdapter.getCategoryMenuStatus() == CategoryMenuStatus.CATEGORY_DELETABLE) {
            changeCategoryMenuStatus(CategoryMenuStatus.CATEGORY_DEFAULT);
        } else if (easterEggPopup != null) {
            easterEggPopup.dismiss();
            easterEggPopup = null;
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.send_voc)
    public void onClickSendVoc(View v) {
        Uri webpage = Uri.parse(vocWebUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @OnClick(R.id.category_delete_button)
    public void onClickCategoryDeleteButton(View v) {
        if (categoryAdapter.getCategoryMenuStatus().equals(CategoryMenuStatus.CATEGORY_DEFAULT)) {
            changeCategoryMenuStatus(CategoryMenuStatus.CATEGORY_DELETABLE);
        } else {
            changeCategoryMenuStatus(CategoryMenuStatus.CATEGORY_DEFAULT);
        }
    }

    @OnTouch(R.id.logo_angeltalk)
    public boolean onTouch(View v, MotionEvent event) {
        return logoGestureDetector.onTouchEvent(event);
    }

    private void initEasterEggPopup() {
        logoGestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                showEasterEggPopup();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });
    }

    private void setCategoryGridView() {
        List<CategoryModel> categoryAllList = categoryRepository.getCategoryAllList();
        categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryAllList, true);
        categoryGridView.setAdapter(categoryAdapter);
        categoryAdapter.setCategoryList(categoryAllList);
        setCategoryItemClickListener(categoryGridView, categoryAllList);
        changeCategoryMenuStatus(CategoryMenuStatus.CATEGORY_DEFAULT);
    }

    private void changeCategoryMenuStatus(CategoryMenuStatus categoryMenuStatus) {
        switch (categoryMenuStatus) {
            case CATEGORY_DEFAULT:
                categoryDeleteButton.setText(R.string.delete);
                break;
            case CATEGORY_DELETABLE:
                categoryDeleteButton.setText(R.string.complete);
                break;
            default:
                Log.e("error", "category is not set");
                break;
        }
        categoryAdapter.changeCategoryItemsStatus(categoryMenuStatus);
    }

    private void showEasterEggPopup() {
        View view = getLayoutInflater().inflate(R.layout.easter_egg, null);
        ImageView easterEggClose = (ImageView) view.findViewById(R.id.easter_egg_close);

        easterEggClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                easterEggPopup.dismiss();
            }
        });
        easterEggPopup = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        easterEggPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void setCategoryItemClickListener(final GridView gridView, final List<CategoryModel> categoryList) {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoryId = categoryAdapter.getItem(position).index;

                String titleString = ((TextView) view.findViewById(R.id.category_title)).getText().toString();
                if (categoryAdapter.getCategoryMenuStatus() == CategoryMenuStatus.CATEGORY_DELETABLE) {
                    showDeleteConfirmDialog(titleString);
                } else if (categoryAdapter.getCategoryMenuStatus() == CategoryMenuStatus.CATEGORY_DEFAULT) {
                    if (titleString.equals(newCategoryString)) {
                        moveToNewCategoryActivity();
                    } else {
                        CategoryModel categoryModel = categoryList.get(position);
                        moveToCategoryViewPagerActivity(categoryModel, null);
                    }
                }
            }
        });
    }

    private void showDeleteConfirmDialog(String categoryName) {
        String message;
        if (categoryAdapter.getCount() == 1) {
            message = oneLeftDeleteCategory;
        } else {
            message = String.format(getResources().getString(delete_category), categoryName);
        }
        dialog = new CustomConfirmDialog(this, message, deleteCategoryClickListener, cancelClickListener);
    }

    private void moveToNewCategoryActivity() {
        Intent intent = new Intent(this, NewCategoryActivity.class);
        startActivity(intent);
    }

    private void moveToCategoryViewPagerActivity(CategoryModel categoryModel , String intentKey) {
        Intent intent = new Intent(getApplicationContext(), CardViewPagerActivity.class);
        applicationManager.setCategoryModel(categoryModel);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(CardViewPagerActivity.CATEGORY_COLOR, categoryModel.color);
        intent.putExtra(intentKey, true);
        getApplicationContext().startActivity(intent);
    }

    private View.OnClickListener deleteCategoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<CardModel> singleCardList = cardRepository.getSingleCardListWithCategoryId(selectedCategoryId);

            for (CardModel cardModel : singleCardList) {
                if (cardModel.contentPath.contains(ContentsUtil.CONTENT_FOLDER)) {
                    FileUtil.removeFile(cardModel.contentPath);
                    FileUtil.removeFile(cardModel.voicePath);
                }
            }

            cardRepository.deleteSingleCardsWithCategory(selectedCategoryId);
            categoryRepository.deleteCategory(selectedCategoryId);
            categoryAdapter.removeItem(selectedCategoryId);

            dialog.dismiss();

            if (categoryAdapter.getCount() == 0) {
                changeCategoryMenuStatus(CategoryMenuStatus.CATEGORY_DEFAULT);
                moveToNewCategoryActivity();
            }
        }
    };

    private View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

    private void launchNotification() {
        NotificationActionManager notificationActionManager = new NotificationActionManager(this);
        notificationActionManager.generateNotification(new Intent(this, NotificationActionReceiver.class));
    }


    private void showDownloadConfirmDialog() {
        String message = getString(R.string.save_confirm_message);
        dialog = new CustomConfirmDialog(this, message, saveCardClickListener , cancelClickListener);
    }

    private View.OnClickListener saveCardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cardTransfer.downloadCard(receiveKey, new OnDownloadCompleteListener(){
                @Override
                public void onSuccess(CardTransferModel cardTransferModel, String filePath){
                    try {
                        FileUtil.unzip(filePath, ContentsUtil.getTempFolder());
                        CardModel cardModel = saveNewSharedCard(cardTransferModel);
                        ContentsUtil.copySharedFiles(cardModel);
                        FileUtil.removeFilesIn(ContentsUtil.getTempFolder());

                        CategoryModel categoryModel = categoryRepository.getCategoryAllList().get(0);
                        moveToCategoryViewPagerActivity(categoryModel, CardViewPagerActivity.INTENT_KEY_SHARE_CARD);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFail(){
                    FileUtil.removeFilesIn(ContentsUtil.getTempFolder());
                }
            });

            dialog.dismiss();
        }
    };

    private CardModel saveNewSharedCard(CardTransferModel cardTransferModel) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        CardModel.CardType cardType = CardModel.CardType.valueOf(cardTransferModel.cardType);

        String contentPath = cardType == CardModel.CardType.VIDEO_CARD ? ContentsUtil.getVideoPath() : ContentsUtil.getImagePath();

        CardModel cardModel = new CardModel(cardTransferModel.name,
                contentPath,
                ContentsUtil.getVoicePath(),
                dateFormat.format(date),
                0,
                cardType,
                cardType == CardModel.CardType.VIDEO_CARD  ? ContentsUtil.getThumbnailPath(contentPath) : null);

        cardRepository.createSingleCardModel(cardModel);

        return cardModel;
    }

}
