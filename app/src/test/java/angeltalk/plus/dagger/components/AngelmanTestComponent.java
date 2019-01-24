package angeltalk.plus.dagger.components;

import javax.inject.Singleton;

import angeltalk.plus.dagger.modules.AngelmanModule;
import angeltalk.plus.data.sqlite.DatabaseHelperTest;
import angeltalk.plus.presentation.activity.CameraGallerySelectionActivityTest;
import angeltalk.plus.presentation.activity.CardListActivityTest;
import angeltalk.plus.presentation.activity.CardViewPagerActivityTest;
import angeltalk.plus.presentation.activity.CardViewPagerActivityWithIntentTest;
import angeltalk.plus.presentation.activity.CategoryMenuActivityTest;
import angeltalk.plus.presentation.activity.MakeCardActivityTest;
import angeltalk.plus.presentation.activity.MakeCardPreviewActivityTest;
import angeltalk.plus.presentation.activity.MakeCategoryActivityTest;
import angeltalk.plus.presentation.activity.OnboardingActivityTest;
import angeltalk.plus.presentation.activity.PhotoEditorActivityTest;
import angeltalk.plus.presentation.activity.ShareCardActivityTest;
import angeltalk.plus.presentation.activity.VideoActivityTest;
import angeltalk.plus.presentation.adapter.CardImageAdapterTest;
import angeltalk.plus.presentation.custom.AddCardViewTest;
import angeltalk.plus.presentation.custom.CardViewPagerLayoutTest;
import angeltalk.plus.presentation.custom.CategoryMenuLayoutTest;
import angeltalk.plus.presentation.service.ScreenReceiverTest;
import angeltalk.plus.presentation.util.ResolutionUtilTest;
import dagger.Component;

@Singleton
@Component(modules = {AngelmanModule.class})
public interface AngelmanTestComponent extends AngelmanComponent {
    void inject(MakeCardActivityTest activity);
    void inject(CategoryMenuActivityTest activity);
    void inject(CardViewPagerActivityTest activity);
    void inject(CardViewPagerActivityWithIntentTest activity);
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
    void inject(MakeCardPreviewActivityTest makeCardPreviewActivityTest);
    void inject(PhotoEditorActivityTest photoEditorActivityTest);
    void inject(ResolutionUtilTest resolutionUtilTest);
}
