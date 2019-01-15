package act.angelman.presentation.custom;

import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import act.angelman.BuildConfig;
import act.angelman.R;
import act.angelman.UITest;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=22)
public class CardListTabButtonTest extends UITest {
    private CardListTabButton subject;

    @Before
    public void setUp() throws Exception {
        subject = new CardListTabButton(RuntimeEnvironment.application, Robolectric.buildAttributeSet()
                .addAttribute(R.attr.selected, "true")
                .addAttribute(R.attr.buttonText, "@string/show_hide_text").build());
    }

    @Test
    public void whenLaunched_thenShowButtonViewSelected() throws Exception {
        assertThat(subject.buttonTextView.getText()).isEqualTo("보이기/숨기기");
        assertThat(subject.buttonTextView.getCurrentTextColor()).isEqualTo(subject.getResources().getColor(R.color.white));
        assertThat(subject.tabIndicator.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void isSelected() throws Exception {
        assertThat(subject.isSelected()).isTrue();
    }

    @Test
    public void givenSelected_whenSetUnselected_thenShowButtonViewUnselected() throws Exception {
        // when
        subject.setSelected(false);
        // then
        assertThat(subject.buttonTextView.getCurrentTextColor()).isEqualTo(subject.getResources().getColor(R.color.white_B2));
        assertThat(subject.tabIndicator.getVisibility()).isEqualTo(View.INVISIBLE);
    }
}