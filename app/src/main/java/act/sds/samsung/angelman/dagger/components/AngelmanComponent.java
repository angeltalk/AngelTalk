package act.sds.samsung.angelman.dagger.components;

import javax.inject.Singleton;

import act.sds.samsung.angelman.dagger.modules.AngelmanModule;
import act.sds.samsung.angelman.data.sqlite.AngelmanDbHelper;
import act.sds.samsung.angelman.presentation.activity.CameraGallerySelectionActivity;
import act.sds.samsung.angelman.presentation.activity.CardViewPagerActivity;
import act.sds.samsung.angelman.presentation.activity.CategoryMenuActivity;
import act.sds.samsung.angelman.presentation.activity.MakeCardActivity;
import act.sds.samsung.angelman.presentation.activity.NewCategoryActivity;
import act.sds.samsung.angelman.presentation.custom.AddCardView;
import act.sds.samsung.angelman.presentation.custom.CardCategoryLayout;
import act.sds.samsung.angelman.presentation.custom.CardViewPagerLayout;
import act.sds.samsung.angelman.presentation.custom.CategoryMenuLayout;
import dagger.Component;

@Singleton
@Component(modules = {AngelmanModule.class})
public interface AngelmanComponent {
    void inject(MakeCardActivity activity);
    void inject(CardViewPagerActivity activity);
    void inject(CategoryMenuActivity activity);
    void inject(NewCategoryActivity activity);
    void inject(CategoryMenuLayout view);
    void inject(CardViewPagerLayout view);
    void inject(AngelmanDbHelper angelmanDbHelper);
    void inject(CardCategoryLayout cardCategoryLayout);
    void inject(AddCardView addCardView);
    void inject(CameraGallerySelectionActivity cameraGallerySelectionActivity);
}
