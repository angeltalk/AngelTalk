package act.angelman.presentation.custom;

import android.app.Activity;
import android.view.DragEvent;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAbsListView;
import org.robolectric.shadows.ShadowDrawable;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import act.angelman.BuildConfig;
import act.angelman.R;
import act.angelman.TestAngelmanApplication;
import act.angelman.UITest;
import act.angelman.domain.model.CategoryModel;
import act.angelman.domain.repository.CategoryRepository;
import act.angelman.presentation.adapter.CategoryAdapter;
import act.angelman.presentation.shadow.ShadowKeyCharacterMap;

import static act.angelman.R.drawable.ic_food;
import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(AngelmanTestRunner.WithKorean.class)
@Config(constants = BuildConfig.class, shadows = ShadowKeyCharacterMap.class)
public class CategoryMenuLayoutTest extends UITest {

    @Inject
    CategoryRepository categoryRepository;

    private CategoryMenuLayout subject;
    private RelativeLayout container;

    @Before
    public void setUp() throws Exception {
        ((TestAngelmanApplication) RuntimeEnvironment.application).getAngelmanTestComponent().inject(this);
        when(categoryRepository.getCategoryAllList()).thenReturn(getCategoryList());
        subject = new CategoryMenuLayout(RuntimeEnvironment.application, null);
        container = spy(subject.lockContainer);
    }

    @Test
    public void whenLaunchedLayout_thenShowCurrentTimeAndDate() throws Exception {
        System.out.println(Arrays.toString(TimeZone.getAvailableIDs()));

        Activity act = setupActivity(Activity.class);
        act.setContentView(R.layout.category_menu_layout);

        String todayStr = "AM 09:00 01월 01일 Thu요일";

        TextClock ampm = (TextClock)act.findViewById(R.id.clock_ampm);
        TextClock time = (TextClock)act.findViewById(R.id.clock_time);
        TextClock date = (TextClock)act.findViewById(R.id.clock_date);

        String dateAndTimeNow = ampm.getText() + " " + time.getText() + " " + date.getText();

        assertThat(todayStr).isEqualTo(dateAndTimeNow);
    }

    @Test
    public void whenCalledSetLockAreaVisibleWithGoneMethod_thenChangeLockAreaVisibilityToGone() throws Exception {
        subject.setLockAreaVisibleWithGone();

        assertThat(subject.findViewById(R.id.lock_container)).isGone();
        assertThat(subject.findViewById(R.id.lock_guide)).isGone();

        ShadowDrawable shadowDrawable = shadowOf(((ImageView) subject.findViewById(R.id.lock_image)).getDrawable());
        assertThat(shadowDrawable.getCreatedFromResId()).isEqualTo(R.drawable.ic_lock_disabled);
    }

    @Test
    public void whenLaunchedApplication_thenShowCategoryListByOrder() throws Exception {
        GridView categoryList = (GridView) subject.findViewById(R.id.category_list);

        CategoryAdapter adapter = (CategoryAdapter) categoryList.getAdapter();
        assertThat(adapter.getItem(0).title).isEqualTo("먹을 것");
        assertThat(adapter.getItem(0).index).isEqualTo(0);
        assertThat(adapter.getItem(0).icon).isEqualTo(ic_food);
        assertThat(adapter.getItem(0).color).isEqualTo(R.color.background_red);
    }

    @Test
    public void whenClickCategoryItem_thenShowCardViewPagerView() throws Exception {
        GridView categoryList = (GridView) subject.findViewById(R.id.category_list);
        ShadowAbsListView shadowGridView = shadowOf(categoryList);
        CategoryMenuLayout.OnCategoryViewChangeListener mock = setMockChangeListener();
        shadowGridView.performItemClick(0);
        verify(mock).categoryClick(((CategoryModel) categoryList.getItemAtPosition(0)));
    }

    @Test
    public void whenLongClickLockButton_thenBlurredAndShowLockArea() throws Exception {
        ImageView lockButton = (ImageView) subject.findViewById(R.id.lock_image);
        assertThat(subject.findViewById(R.id.lock_container)).isGone();
        lockButton.performLongClick();
        assertThat(subject.findViewById(R.id.lock_container)).isVisible();
    }

    @Test
    public void whenLockButtonDraggedInsideLockArea_thenGoneBlurredAndLockArea() throws Exception {
        fakeDragEventOnArea(true);
        assertThat(container).isGone();
    }

    @Test
    public void whenLockButtonDraggedOutsideLockArea_thenUnLock() throws Exception {
        CategoryMenuLayout.OnCategoryViewChangeListener mock = setMockChangeListener();
        fakeDragEventOnArea(false);
        verify(mock).onUnLock();
    }

    private CategoryMenuLayout.OnCategoryViewChangeListener setMockChangeListener() {
        CategoryMenuLayout.OnCategoryViewChangeListener mock = mock(CategoryMenuLayout.OnCategoryViewChangeListener.class);
        subject.setOnCategoryViewChangeListener(mock);
        return mock;
    }

    private void fakeDragEventOnArea(boolean inOut) {


        DragEvent dragEvent = mock(DragEvent.class);
        when(dragEvent.getAction()).thenReturn(DragEvent.ACTION_DROP);

        when(container.getRight()).thenReturn(100);
        when(container.getBottom()).thenReturn(200);

        if(inOut){
            when(dragEvent.getX()).thenReturn(99f);
            when(dragEvent.getY()).thenReturn(199f);
        }else{
            when(dragEvent.getX()).thenReturn(10f);
            when(dragEvent.getY()).thenReturn(10f);
        }
        container.dispatchDragEvent(dragEvent);
    }

    private List<CategoryModel> getCategoryList() {
        return Lists.newArrayList( CategoryModel.builder().title("먹을 것").icon(ic_food).color(R.color.background_red).build() );
    }
}
