package angeltalk.plus.presentation.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import angeltalk.plus.R;
import angeltalk.plus.UITest;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class CardViewTest extends UITest {

    private CardView subject;
    private Bitmap inputBitmap;
    private TextView cardImageTitle;
    private EditText cardTitleEdit;

    @Before
    public void setUp() throws Exception {
        subject = new CardView((Context) ApplicationProvider.getApplicationContext());
        inputBitmap = ((BitmapDrawable) getDrawable(R.drawable.ic_camera)).getBitmap();
        cardImageTitle = subject.findViewById(R.id.card_image_title);
        cardTitleEdit = subject.findViewById(R.id.card_image_title_edit);
    }

    @Test
    public void givenCameraMode_whenClickTheCameraButton_thenShowAddCardViewMessageInTextView() {
        subject.setImageBitmap(inputBitmap);
        ImageView cardImage = subject.findViewById(R.id.card_image);
        assertThat(((BitmapDrawable) cardImage.getDrawable()).getBitmap()).isEqualTo(inputBitmap);
    }

    @Test
    public void verifyChangeCardViewByStatus() {
        subject.changeCardViewStatus();
        assertThat(cardTitleEdit.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(cardImageTitle.getVisibility()).isEqualTo(View.GONE);

        subject.changeCardViewStatus();
        assertThat(cardTitleEdit.getVisibility()).isEqualTo(View.GONE);
        assertThat(cardImageTitle.getVisibility()).isEqualTo(View.VISIBLE);
    }
}