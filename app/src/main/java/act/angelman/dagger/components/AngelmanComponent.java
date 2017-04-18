package act.angelman.dagger.components;

import javax.inject.Singleton;

import act.angelman.dagger.modules.AngelmanModule;
import act.angelman.data.sqlite.DatabaseHelper;
import act.angelman.network.transfer.CardTransfer;
import act.angelman.network.transfer.KaKaoTransfer;
import act.angelman.network.transfer.SmsTransfer;
import act.angelman.presentation.activity.CameraGallerySelectionActivity;
import act.angelman.presentation.activity.CardListActivity;
import act.angelman.presentation.activity.CardViewPagerActivity;
import act.angelman.presentation.activity.CategoryMenuActivity;
import act.angelman.presentation.activity.MakeCardActivity;
import act.angelman.presentation.activity.MakeCategoryActivity;
import act.angelman.presentation.activity.OnboardingActivity;
import act.angelman.presentation.activity.ShareCardActivity;
import act.angelman.presentation.custom.AddCardView;
import act.angelman.presentation.custom.CardTitleLayout;
import act.angelman.presentation.custom.CardViewPagerLayout;
import act.angelman.presentation.custom.CategoryMenuLayout;
import act.angelman.presentation.service.ScreenReceiver;
import act.angelman.presentation.manager.ApplicationInitializer;
import act.angelman.presentation.manager.NotificationActionManager;
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
    void inject(SmsTransfer smsTransfer);
    void inject(KaKaoTransfer kaKaoTransfer);
    void inject(ApplicationInitializer applicationInitializer);
    void inject(CardListActivity cardListActivity);
    void inject(ShareCardActivity shareCardActivity);
}
