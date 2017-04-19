package act.angelman.presentation.activity.v1;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        OnboardingAndCategoryMenuViewTest.class,
        CardViewPagerTest.class,
        MakeNewCardWithCameraTest.class,
        MoveToAnotherCategoryTest.class,
        MakeNewCategoryTest.class,
//        DeleteCategoryTest.class,
        SendVOCTest.class
})
public class E2ESuite {
}