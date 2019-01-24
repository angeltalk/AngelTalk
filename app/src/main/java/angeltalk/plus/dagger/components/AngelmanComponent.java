package angeltalk.plus.dagger.components;

import javax.inject.Singleton;

import angeltalk.plus.dagger.modules.AngelmanModule;
import angeltalk.plus.data.sqlite.DatabaseHelper;
import angeltalk.plus.network.transfer.CardTransfer;
import angeltalk.plus.network.transfer.KaKaoTransfer;
import angeltalk.plus.network.transfer.MessageTransfer;
import angeltalk.plus.presentation.activity.CameraGallerySelectionActivity;
import angeltalk.plus.presentation.activity.CardListActivity;
import angeltalk.plus.presentation.activity.CardViewPagerActivity;
import angeltalk.plus.presentation.activity.CategoryMenuActivity;
import angeltalk.plus.presentation.activity.MakeCardActivity;
import angeltalk.plus.presentation.activity.MakeCardPreviewActivity;
import angeltalk.plus.presentation.activity.MakeCategoryActivity;
import angeltalk.plus.presentation.activity.OnboardingActivity;
import angeltalk.plus.presentation.activity.PhotoEditorActivity;
import angeltalk.plus.presentation.activity.ShareCardActivity;
import angeltalk.plus.presentation.custom.AddCardView;
import angeltalk.plus.presentation.custom.CardTitleLayout;
import angeltalk.plus.presentation.custom.CardViewPagerLayout;
import angeltalk.plus.presentation.custom.CategoryMenuLayout;
import angeltalk.plus.presentation.manager.ApplicationInitializer;
import angeltalk.plus.presentation.manager.NotificationActionManager;
import angeltalk.plus.presentation.receiver.NotificationActionReceiver;
import angeltalk.plus.presentation.service.ScreenReceiver;
import angeltalk.plus.presentation.service.ScreenService;
import dagger.Component;

@Singleton
@Component(modules = {AngelmanModule.class})
public interface AngelmanComponent {
    void inject(MakeCardActivity activity);
    void inject(CardViewPagerActivity activity);
    void inject(CategoryMenuActivity activity);
    void inject(MakeCategoryActivity activity);
    void inject(CategoryMenuLayout view);
    void inject(CardViewPagerLayout view);
    void inject(DatabaseHelper databaseHelper);
    void inject(CardTitleLayout cardTitleLayout);
    void inject(AddCardView addCardView);
    void inject(CameraGallerySelectionActivity cameraGallerySelectionActivity);
    void inject(OnboardingActivity onboardingActivity);
    void inject(NotificationActionManager notificationActionManager);
    void inject(ScreenReceiver screenReceiver);
    void inject(CardTransfer cardTransfer);
    void inject(MessageTransfer messageTransfer);
    void inject(KaKaoTransfer kaKaoTransfer);
    void inject(ApplicationInitializer applicationInitializer);
    void inject(CardListActivity cardListActivity);
    void inject(ShareCardActivity shareCardActivity);
    void inject(MakeCardPreviewActivity makeCardPreviewActivity);
    void inject(PhotoEditorActivity photoEditorActivity);
    void inject(NotificationActionReceiver notificationActionReceiver);
    void inject(ScreenService screenService);
}
