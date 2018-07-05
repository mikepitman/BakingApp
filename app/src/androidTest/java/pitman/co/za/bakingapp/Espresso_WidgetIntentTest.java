package pitman.co.za.bakingapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

// https://github.com/googlesamples/android-testing/blob/master/ui/espresso/IntentsBasicSample/app/src/androidTest/java/com/example/android/testing/espresso/BasicSample/DialerActivityTest.java
// https://classroom.udacity.com/nanodegrees/nd801/parts/3be77470-96de-400a-bbfb-2ae4cc924d48/modules/6cb81da9-d083-4721-a31b-4f435de9fedd/lessons/f0084cc7-2cbc-4b8e-8644-375e8c927167/concepts/f0c53eb6-722d-4558-b317-d4205dc7822d

@RunWith(AndroidJUnit4.class)
public class Espresso_WidgetIntentTest {

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void verifySkeletonActivityIntent() {
        // Click in first recipe, to initiate intent for SkeletonActivity
        onView(withId(R.id.recipe_cards_rv)).perform(actionOnItemAtPosition(0, click()));
        intended(toPackage("pitman.co.za.bakingapp"));
    }

    @Test
    @Ignore
    public void verifyWidgetIntent() {
        // Click on first recipe, which should send broadcast intent to RecipeWidgetProvider extending AppWidgetProvider
        onView(withId(R.id.recipe_cards_rv)).perform(actionOnItemAtPosition(0, click()));
        intended(toPackage("pitman.co.za.bakingapp.widget"));
        /* Not catching this intent using espresso tools, guessing this may be the reason:
         * https://developer.android.com/guide/components/broadcasts
         * Note: Although intents are used for both sending broadcasts and starting activities with startActivity(Intent),
         * these actions are completely unrelated. Broadcast receivers can't see or capture intents used to start an activity;
         * likewise, when you broadcast an intent, you can't find or start an activity.
         */
    }
}
