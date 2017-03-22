package act.sds.samsung.angelman.domain.model;

import org.apache.maven.artifact.ant.shaded.ReflectionUtils;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

public class CardModelTest {

    @Test
    public void getName() throws Exception {
        //Given
        CardModel cardModel = new CardModel();
        inject(cardModel, "name", "홍길동");

        //When & Then
        assertThat(cardModel.name.equals("홍길동")).isTrue();
    }

    @Test
    public void setName() throws Exception {
        CardModel cardModel = new CardModel();
        cardModel.name = "홍길동";

        Object objValue = getFieldValue(cardModel, "name");
        assertThat(objValue.equals("홍길동")).isTrue();
    }

    @Test
    public void getImagePath() throws Exception {
        //Given
        CardModel cardModel = new CardModel();
        inject(cardModel, "contentPath", "www.daum.net");

        //When & Then
        assertThat(cardModel.contentPath.equals("www.daum.net")).isTrue();
    }

    @Test
    public void setImagePath() throws Exception {
        CardModel cardModel = new CardModel();
        cardModel.contentPath = "www.google.com";

        Object objValue = getFieldValue(cardModel, "contentPath");

        assertThat(objValue.equals("www.google.com")).isTrue();
    }

    @Test
    public void getFirstTime() throws Exception {
        //Given
        CardModel cardModel = new CardModel();
        inject(cardModel, "firstTime", "20160928115012");

        //When & Then
        assertThat(cardModel.firstTime.equals("20160928115012")).isTrue();
    }

    @Test
    public void setFirstTime() throws Exception {
        CardModel cardModel = new CardModel();
        cardModel.firstTime = "20160928115012";

        Object objValue = getFieldValue(cardModel, "firstTime");

        assertThat(objValue.equals("20160928115012")).isTrue();
    }

    private static void inject(Object target, String fieldName, Object value) throws Exception{
        ReflectionUtils.setVariableValueInObject(target, fieldName, value);
    }


    private Object getFieldValue(CardModel cardModel, String name) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = cardModel.getClass();
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(cardModel);
    }
}