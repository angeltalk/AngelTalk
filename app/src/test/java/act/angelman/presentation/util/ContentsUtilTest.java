package act.angelman.presentation.util;

import android.graphics.Bitmap;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;

import act.angelman.BuildConfig;
import act.angelman.domain.model.CardModel;
import act.angelman.domain.model.CardTransferModel;
import act.angelman.presentation.shadow.ShadowThumbnailUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = ShadowThumbnailUtil.class)
public class ContentsUtilTest {

    @Test
    public void givenFileNameAndBitMapExists_whenCallImageSave_thenSaveImage() throws Exception {
        String fileName = ContentsUtil.getImagePath(RuntimeEnvironment.application.getApplicationContext());
        Bitmap fakeBitmap = Bitmap.createBitmap(1440, 2560, Bitmap.Config.ARGB_8888);//mock(Bitmap.class);
        Window window = mock(Window.class);
        WindowManager windowManager = mock(WindowManager.class);
        Display display = mock(Display.class);
        View view = mock(View.class);
        when(window.getWindowManager()).thenReturn(windowManager);
        when(windowManager.getDefaultDisplay()).thenReturn((display));
        when(window.getDecorView()).thenReturn(view);

        ContentsUtil.saveImage(fakeBitmap, fileName, 444, 112);

        assertThat(fakeBitmap.getHeight()).isEqualTo(2560);
        assertThat(fakeBitmap.getWidth()).isEqualTo(1440);

        File file = new File(fileName);
        assertThat(file.exists()).isTrue();
    }

    @Test
    public void givenFileFullPath_whenGetFileNameFromFullPath_thenReturnFileName() throws Exception {
        // given
        String fileFullPath = "/storage/emulated/0/angelman/DCIM/20170329_133245.jpg";
        // when then
        assertThat(ContentsUtil.getContentNameFromContentPath(fileFullPath)).isEqualTo("20170329_133245.jpg");
    }

    @Test
    public void givenVideoFile_whenCallSaveVideoThumbnail_thenMakeVideoThumbnailImage() throws Exception {
        // given
        FileUtil.copyFile(new File(ContentsUtil.getContentFolder(RuntimeEnvironment.application.getApplicationContext()) + File.separator + "airplane.mp4"), new File(ContentsUtil.getTempFolder(RuntimeEnvironment.application.getApplicationContext()) + File.separator + "airplane.mp4"));

        // when
        ContentsUtil.saveVideoThumbnail(ContentsUtil.getTempFolder(RuntimeEnvironment.application.getApplicationContext()) + File.separator + "airplane.mp4");

        // then
        assertThat(new File(ContentsUtil.getTempFolder(RuntimeEnvironment.application.getApplicationContext()) + File.separator + "airplane.jpg").exists()).isTrue();
    }

    @Test
    public void givenSharedPhotoCard_whenCallGetTempCardModel_thenMakeTempCardModel() throws Exception {
        // given
        CardTransferModel cardTransferModel = new CardTransferModel();
        cardTransferModel.name = "sharedPhotoCard";
        cardTransferModel.cardType = "PHOTO_CARD";

        String folderPath = ContentsUtil.getTempFolder(RuntimeEnvironment.application.getApplicationContext()) + File.separator;
        File content = new File(folderPath + "test.jpg");
        File voice = new File(folderPath + "test.3gdp");
        content.createNewFile();
        voice.createNewFile();

        // when
        CardModel cardModel = ContentsUtil.getTempCardModel(folderPath, cardTransferModel);

        // then
        assertThat(cardModel.name).isEqualTo("sharedPhotoCard");
        assertThat(cardModel.cardType).isEqualTo(CardModel.CardType.PHOTO_CARD);
        assertThat(cardModel.contentPath).isEqualTo(content.getAbsolutePath());
        assertThat(cardModel.voicePath).isEqualTo(voice.getAbsolutePath());
    }

    @Test
    public void givenSharedVideoCard_whenCallGetTempCardModel_thenMakeTempCardModel() throws Exception {
        // given
        CardTransferModel cardTransferModel = new CardTransferModel();
        cardTransferModel.name = "sharedVideoCard";
        cardTransferModel.cardType = "VIDEO_CARD";

        String folderPath = ContentsUtil.getTempFolder(RuntimeEnvironment.application.getApplicationContext()) + File.separator;
        File content = new File(folderPath + "test.mp4");
        File thumbnail = new File(folderPath + "test.jpg");
        File voice = new File(folderPath + "test.3gdp");
        content.createNewFile();
        thumbnail.createNewFile();
        voice.createNewFile();

        // when
        CardModel cardModel = ContentsUtil.getTempCardModel(folderPath, cardTransferModel);

        // then
        assertThat(cardModel.name).isEqualTo("sharedVideoCard");
        assertThat(cardModel.cardType).isEqualTo(CardModel.CardType.VIDEO_CARD);
        assertThat(cardModel.contentPath).isEqualTo(content.getAbsolutePath());
        assertThat(cardModel.thumbnailPath).isEqualTo(thumbnail.getAbsolutePath());
        assertThat(cardModel.voicePath).isEqualTo(voice.getAbsolutePath());
    }

    @Test
    public void givenPhotoCardAndTempFiles_whenCallCopySharedFiles_thenCopy() throws Exception {
        // given
        String folderPath = ContentsUtil.getTempFolder(RuntimeEnvironment.application.getApplicationContext()) + File.separator;
        new File(folderPath + "test.jpg").createNewFile();
        new File(folderPath + "test.3gdp").createNewFile();

        CardModel cardModel = CardModel.builder()
                .name("copyPhotoCard")
                .cardType(CardModel.CardType.PHOTO_CARD)
                .contentPath(ContentsUtil.getImagePath(RuntimeEnvironment.application.getApplicationContext()))
                .voicePath(ContentsUtil.getVoicePath(RuntimeEnvironment.application.getApplicationContext()))
                .build();

        // when
        ContentsUtil.copySharedFiles(RuntimeEnvironment.application.getApplicationContext(), cardModel);

        // then
        assertThat(cardModel.name).isEqualTo("copyPhotoCard");
        assertThat(cardModel.cardType).isEqualTo(CardModel.CardType.PHOTO_CARD);
        assertThat(new File(cardModel.contentPath).exists()).isTrue();
        assertThat(new File(cardModel.voicePath).exists()).isTrue();
    }

    @Test
    public void givenVideoCardAndTempFiles_whenCallCopySharedFiles_thenCopy() throws Exception {
        // given
        String folderPath = ContentsUtil.getTempFolder(RuntimeEnvironment.application.getApplicationContext()) + File.separator;
        new File(folderPath + "test.mp4").createNewFile();
        new File(folderPath + "test.jpg").createNewFile();
        new File(folderPath + "test.3gdp").createNewFile();

        String videoPath = ContentsUtil.getVideoPath(RuntimeEnvironment.application.getApplicationContext());
        CardModel cardModel = CardModel.builder()
                .name("copyVideoCard")
                .cardType(CardModel.CardType.VIDEO_CARD)
                .contentPath(videoPath)
                .thumbnailPath(ContentsUtil.getThumbnailPath(videoPath))
                .voicePath(ContentsUtil.getVoicePath(RuntimeEnvironment.application.getApplicationContext()))
                .build();

        // when
        ContentsUtil.copySharedFiles(RuntimeEnvironment.application.getApplicationContext(),cardModel);

        // then
        assertThat(cardModel.name).isEqualTo("copyVideoCard");
        assertThat(cardModel.cardType).isEqualTo(CardModel.CardType.VIDEO_CARD);
        assertThat(new File(cardModel.contentPath).exists()).isTrue();
        assertThat(new File(cardModel.thumbnailPath).exists()).isTrue();
        assertThat(new File(cardModel.voicePath).exists()).isTrue();
    }
}