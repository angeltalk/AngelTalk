package act.angelman.presentation.custom;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import act.angelman.BuildConfig;
import act.angelman.R;
import act.angelman.UITest;

import static org.assertj.android.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CardViewTest extends UITest{

    private CardView subject;
    private Bitmap inputBitmap;
    private TextView cardImageTitle;
    private EditText cardTitleEdit;

    @Before
    public void setUp() throws Exception {
        subject = new CardView(RuntimeEnvironment.application);
        inputBitmap = ((BitmapDrawable) getDrawable(R.drawable.ic_camera)).getBitmap();
        cardImageTitle = (TextView) subject.findViewById(R.id.card_image_title);
        cardTitleEdit = (EditText) subject.findViewById(R.id.card_image_title_edit);
    }

    @Test
    public void givenCameraMode_whenClickTheCameraButton_thenShowAddCardViewMessageInTextView() {
        subject.setImageBitmap(inputBitmap);
        ImageView cardImage = (ImageView) subject.findViewById(R.id.card_image);
        assertThat(((BitmapDrawable) cardImage.getDrawable()).getBitmap()).isEqualTo(inputBitmap);
    }

    @Test
    public void verifyChangeCardViewByStatus() {
        subject.changeCardViewStatus();
        assertThat(cardTitleEdit).isVisible();
        assertThat(cardImageTitle).isGone();

        subject.changeCardViewStatus();
        assertThat(cardTitleEdit).isGone();
        assertThat(cardImageTitle).isVisible();
    }
}