package angeltalk.plus.presentation.custom;


import android.view.View;
import android.app.Activity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.common.collect.Lists;

import org.junit.Ignore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAbsListView;
import org.robolectric.shadows.ShadowDrawable;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;


import angeltalk.plus.R;
import angeltalk.plus.UITest;
import angeltalk.plus.domain.model.CategoryModel;
import angeltalk.plus.domain.repository.CategoryRepository;
import angeltalk.plus.presentation.adapter.CategoryAdapter;
import angeltalk.plus.presentation.shadow.ShadowKeyCharacterMap;

import static angeltalk.plus.R.drawable.ic_food;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;
import org.junit.Rule;
import org.mockito.Mockito;

import angeltalk.plus.dagger.modules.AngelmanModule;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

@Ignore("phase-9: Hilt @AndroidEntryPoint view needs Activity context; rewrite with Robolectric-built host Activity")
@HiltAndroidTest
@UninstallModules(AngelmanModule.class)
@RunWith(RobolectricTestRunner.class)
@Config(shadows = ShadowKeyCharacterMap.class)
public class CategoryMenuLayoutTest extends UITest {


    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @BindValue
    angeltalk.plus.domain.repository.CardRepository __cardRepoMock = Mockito.mock(angeltalk.plus.domain.repository.CardRepository.class);
    @BindValue
    CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);

    private CategoryMenuLayout subject;
    private RelativeLayout container;

    @Before
    public void setUp() throws Exception {
        hiltRule.inject();
        when(categoryRepository.getCategoryAllList()).thenReturn(getCategoryList());
        subject = new CategoryMenuLayout(RuntimeEnvironment.application, null);
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
    public void whenCalledSetLockAreaVisibleWithGoneMethod_thenInvisibilityLongPressLockGuide() throws Exception {
        subject.setLockAreaVisibleWithGone();

        assertThat(subject.findViewById(R.id.lock_long_press_guide).getVisibility()).isEqualTo(View.GONE);

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
    public void whenLockButtonClick_thenShowLockButtonLongPressGuide() throws Exception {
        subject.setLockView(subject.getContext());
        CategoryMenuLayout.OnCategoryViewChangeListener mock = setMockChangeListener();

        ImageView lockButton = (ImageView) subject.findViewById(R.id.lock_image);
        TextView longPressGuide = subject.findViewById(R.id.lock_long_press_guide);

        lockButton.performClick();

        assertThat(longPressGuide.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(lockButton.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void whenLongClickLockButton_thenHideGuideAndCallOnCategoryViewChangeListener() throws Exception {
        subject.setLockView(subject.getContext());
        CategoryMenuLayout.OnCategoryViewChangeListener mock = setMockChangeListener();

        ImageView lockButton = (ImageView) subject.findViewById(R.id.lock_image);
        TextView longPressGuide = subject.findViewById(R.id.lock_long_press_guide);

        lockButton.performLongClick();

        assertThat(longPressGuide.getVisibility()).isEqualTo(View.GONE);
        verify(mock).onUnLock();
    }

    @Test
    public void whenLunchedWithNavigationBar_thenSetSmallerMargin() throws Exception {
        subject.setSmallerMarginLayout();

        DisplayMetrics dm = subject.getResources().getDisplayMetrics();
        ConstraintLayout.LayoutParams lp = ((ConstraintLayout.LayoutParams) subject.findViewById(R.id.clock_layout).getLayoutParams());
        assertThat(lp.topMargin).isEqualTo(Math.round(10 * dm.density));
        assertThat(lp.bottomMargin).isEqualTo(Math.round(2 * dm.density));
        assertThat(subject.findViewById(R.id.lock_image).getPaddingStart()).isEqualTo(Math.round(4*dm.density));
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
