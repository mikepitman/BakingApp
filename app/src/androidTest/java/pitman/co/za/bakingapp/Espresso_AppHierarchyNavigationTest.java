package pitman.co.za.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
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
        onView(allOf(getElementByPosition(allOf(withId(R.id.nextStepButton)), 0), isClickable()));

        // Verify the recipe instructions are displayed.
        // Espresso trips over the use of a view ID to direct layout in a relativeLayout, so use a matcher to retrieve the specific instance
        onView(allOf(getElementByPosition(allOf(withId(R.id.recipe_instructions)), 1))).check(matches(withText("Recipe Introduction")));

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

    // Keep getting multiple matches for a view with only a single instance in the view file, so use a matcher to retrieve specific instance of a match
    // https://stackoverflow.com/questions/32387137/espresso-match-first-element-found-when-many-are-in-hierarchy
    private static Matcher<View> getElementByPosition(final Matcher<View> matcher, final int position) {
        return new BaseMatcher<View>() {
            int counter = 0;

            @Override
            public boolean matches(Object item) {
                if (matcher.matches(item)) {
                    if (counter == position) {
                        counter++;
                        return true;
                    }
                    counter++;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Element at position: " + position);
            }
        };
    }
}

