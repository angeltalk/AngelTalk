package act.angelman.presentation.activity;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CreateNewCard.class,
        DeleteExistingCategoriesAndCreateNewCategory.class,
        HideShowCardsAndChangeOrderOfCards.class,
        SendAndReceiveCard.class,
        DeleteCard.class,
        TurnKidsModeOnOffByUsingNotificationDrawer.class
})
public class UITestSuite {
}
