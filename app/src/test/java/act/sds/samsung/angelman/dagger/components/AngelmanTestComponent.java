package act.sds.samsung.angelman.dagger.components;

import javax.inject.Singleton;

import act.sds.samsung.angelman.dagger.modules.AngelmanModule;
import act.sds.samsung.angelman.data.sqlite.AngelmanDbHelperTest;
import act.sds.samsung.angelman.presentation.activity.CameraGallerySelectionActivityTest;
import act.sds.samsung.angelman.presentation.activity.CardViewPagerActivityTest;
import act.sds.samsung.angelman.presentation.activity.CategoryMenuActivityTest;
import act.sds.samsung.angelman.presentation.activity.MakeCardActivityTest;
import act.sds.samsung.angelman.presentation.activity.NewCategoryActivityTest;
import act.sds.samsung.angelman.presentation.activity.OnboardingActivityTest;
import act.sds.samsung.angelman.presentation.activity.VideoActivityTest;
import act.sds.samsung.angelman.presentation.adapter.CardImageAdapterTest;
import act.sds.samsung.angelman.presentation.custom.AddCardViewTest;
import act.sds.samsung.angelman.presentation.custom.CardViewPagerLayoutTest;
import act.sds.samsung.angelman.presentation.custom.CategoryMenuLayoutTest;
import act.sds.samsung.angelman.presentation.service.ScreenReceiverTest;
import dagger.Component;

@Singleton
@Component(modules = {AngelmanModule.class})
public interface AngelmanTestComponent extends AngelmanComponent {
    void inject(MakeCardActivityTest activity);
    void inject(CategoryMenuActivityTest activity);
    void inject(CardViewPagerActivityTest activity);
    void inject(CategoryMenuLayoutTest view);
    void inject(CardViewPagerLayoutTest view);
    void inject(NewCategoryActivityTest activity);
    void inject(AngelmanDbHelperTest activity);
    void inject(AddCardViewTest addCardViewTest);
    void inject(CameraGallerySelectionActivityTest cameraGallerySelectionActivityTest);
    void inject(OnboardingActivityTest onboardingActivityTest);
    void inject(VideoActivityTest videoActivityTest);
    void inject(CardImageAdapterTest cardImageAdapterTest);
    void inject(ScreenReceiverTest screenReceiverTest);


}
