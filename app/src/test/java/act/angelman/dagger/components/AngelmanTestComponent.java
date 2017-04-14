package act.angelman.dagger.components;

import javax.inject.Singleton;

import act.angelman.dagger.modules.AngelmanModule;
import act.angelman.data.sqlite.DatabaseHelperTest;
import act.angelman.presentation.activity.CameraGallerySelectionActivityTest;
import act.angelman.presentation.activity.CardListActivityTest;
import act.angelman.presentation.activity.CardViewPagerActivityTest;
import act.angelman.presentation.activity.CategoryMenuActivityTest;
import act.angelman.presentation.activity.MakeCardActivityTest;
import act.angelman.presentation.activity.MakeCategoryActivityTest;
import act.angelman.presentation.activity.OnboardingActivityTest;
import act.angelman.presentation.activity.ShareCardActivityTest;
import act.angelman.presentation.activity.VideoActivityTest;
import act.angelman.presentation.adapter.CardImageAdapterTest;
import act.angelman.presentation.custom.AddCardViewTest;
import act.angelman.presentation.custom.CardViewPagerLayoutTest;
import act.angelman.presentation.custom.CategoryMenuLayoutTest;
import act.angelman.presentation.service.ScreenReceiverTest;
import dagger.Component;

@Singleton
@Component(modules = {AngelmanModule.class})
public interface AngelmanTestComponent extends AngelmanComponent {
    void inject(MakeCardActivityTest activity);
    void inject(CategoryMenuActivityTest activity);
    void inject(CardViewPagerActivityTest activity);
    void inject(CategoryMenuLayoutTest view);
    void inject(CardViewPagerLayoutTest view);
    void inject(MakeCategoryActivityTest activity);
    void inject(DatabaseHelperTest activity);
    void inject(AddCardViewTest addCardViewTest);
    void inject(CameraGallerySelectionActivityTest cameraGallerySelectionActivityTest);
    void inject(OnboardingActivityTest onboardingActivityTest);
    void inject(VideoActivityTest videoActivityTest);
    void inject(CardImageAdapterTest cardImageAdapterTest);
    void inject(ScreenReceiverTest screenReceiverTest);
    void inject(ShareCardActivityTest shareCardActivityTest);
    void inject(CardListActivityTest cardListActivityTest);
}
