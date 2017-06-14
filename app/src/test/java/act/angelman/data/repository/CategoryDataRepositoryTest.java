package act.angelman.data.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import act.angelman.BuildConfig;
import act.angelman.data.repository.datastore.CategoryDataStore;
import act.angelman.domain.model.CategoryModel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk=22)
public class CategoryDataRepositoryTest {

    CategoryDataRepository subject;

    @Before
    public void setUp() throws Exception {
        subject = new CategoryDataRepository(RuntimeEnvironment.application.getApplicationContext());
        subject.dataStore = mock(CategoryDataStore.class);
    }

    @Test
    public void getCategoryAllList() throws Exception {
        subject.getCategoryAllList();
        verify(subject.dataStore).getCategoryAllList();
    }

    @Test
    public void getCategoryAllIconList() throws Exception {
        subject.getCategoryAllIconList();
        verify(subject.dataStore).getCategoryAllIconList();
    }

    @Test
    public void getCategoryAllBackgroundList() throws Exception {
        subject.getCategoryAllBackgroundList();
        verify(subject.dataStore).getCategoryAllBackgroundList();
    }

    @Test
    public void saveNewCategoryItemAndReturnId() throws Exception {
        CategoryModel model = CategoryModel.builder().index(1).title("PLAY").build();
        subject.saveNewCategoryItemAndReturnId(model);
        verify(subject.dataStore).saveNewCategoryItemAndReturnId(model);
    }

    @Test
    public void deleteCategory() throws Exception {
        subject.deleteCategory(1);
        verify(subject.dataStore).deleteCategory(1);
    }

}