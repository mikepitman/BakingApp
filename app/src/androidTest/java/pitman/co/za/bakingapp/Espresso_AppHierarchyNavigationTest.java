package pitman.co.za.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;

@RunWith(AndroidJUnit4.class)
public class Espresso_AppHierarchyNavigationTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivity_TestRecyclerView() {
        // Start with listing of recipes in MainActivity
        onData(is("Brownies")).inAdapterView(withId(R.id.recipe_cards_rv));
    }

    @Test
    public void navigateRecipeMenu_RecipeSkeleton_RecipeMenu() {

        // Click on first menu item, to view recipe skeleton
        onView(withId(R.id.recipe_cards_rv)).perform(actionOnItemAtPosition(0, click()));

        onData(anything())
                .inAdapterView(withId(R.id.recipe_skeleton_list))
                .atPosition(0)
                .check(matches(withText("Ingredients")));

        // Press backButton to return to MainActivity
        Espresso.pressBack();
        onData(is("Brownies")).inAdapterView(withId(R.id.recipe_cards_rv));
    }

    @Test
    public void navigateRecipeMenu_RecipeSkeleton_Ingredients_BackToRecipeMenu() {

        // Click on first menu item, to view recipe skeleton
        onView(withId(R.id.recipe_cards_rv)).perform(actionOnItemAtPosition(0, click()));

        onData(anything())
                .inAdapterView(withId(R.id.recipe_skeleton_list))
                .atPosition(0)
                .check(matches(withText("Ingredients")));

        // Click on first item in list (always 'Ingredients')
        onData(anything())
                .inAdapterView(withId(R.id.recipe_skeleton_list))
                .atPosition(0)
                .perform(click());

        // Verify ingredients list is displayed
        onData(is("semisweet chocolate chips")).inAdapterView(withId(R.id.ingredients_list));

        // Return to recipe skeleton
        Espresso.pressBack();
        onData(anything())
                .inAdapterView(withId(R.id.recipe_skeleton_list))
                .atPosition(0)
                .check(matches(withText("Ingredients")));

        // Press backButton to return to Recipe menu
        Espresso.pressBack();
        onData(is("Brownies")).inAdapterView(withId(R.id.recipe_cards_rv));
    }

    @Test
    public void navigateRecipeMenu_RecipeSkeleton_RecipeStep_BackToRecipeMenu() {

        // Click on first menu item, to view recipe skeleton
        onView(withId(R.id.recipe_cards_rv)).perform(actionOnItemAtPosition(0, click()));

        onData(anything())
                .inAdapterView(withId(R.id.recipe_skeleton_list))
                .atPosition(1)
                .check(matches(withText("Recipe Introduction")));

        // Click on second item in skeletonView, to view recipeStep
        onData(anything())
                .inAdapterView(withId(R.id.recipe_skeleton_list))
                .atPosition(1)
                .perform(click());

        // Verify RecipeStep is displayed
        onView(withText("Recipe Introduction")).check(matches(isDisplayed()));

        onView(withId(R.id.recipe_instructions)).check(matches(withText("Recipe Introduction")));
//        onView(withId(R.id.nextStepButton)).check(matches(withText()));

        // Return to recipe skeleton
        Espresso.pressBack();
        onData(anything())
                .inAdapterView(withId(R.id.recipe_skeleton_list))
                .atPosition(1)
                .check(matches(withText("Recipe Introduction")));

        // Press backButton to return to Recipe menu
        Espresso.pressBack();
        onData(is("Brownies")).inAdapterView(withId(R.id.recipe_cards_rv));
    }
}

