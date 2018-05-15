package pitman.co.za.bakingapp;

//import android.content.ContentValues;
//import android.net.Uri;
//import android.test.mock.MockContentResolver;
//import org.mockito.junit.MockitoJUnitRunner;
//import android.util.Log;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import pitman.co.za.bakingapp.data.RecipesContract;
import pitman.co.za.bakingapp.data.RecipesProvider;

/**
 * Created by Michael on 2018/03/26.
 * Lots of different references used to put this all together, including but not necessarily limited to:
 * https://medium.com/@rohitsingh14101992/content-provider-testing-android-ecc136cb2cda
 * https://stackoverflow.com/questions/5286876/android-code-example-for-testing-with-the-providertestcase2
 * https://github.com/IanDarwin/Android-Cookbook-Examples/blob/master/ContentProviderTest/src/com/example/contentprovidersample/test/MyContentProviderTest.java
 */
//@RunWith(MockitoJUnitRunner.class)
public class ProviderUnitTest extends ProviderTestCase2<RecipesProvider> {

//    private static final String TAG = ProviderUnitTest.class.getSimpleName();
//    MockContentResolver mMockResolver;
//
//    public ProviderUnitTest() {
//        super(RecipesProvider.class, RecipesContract.CONTENT_AUTHORITY);
//    }
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//        Log.d(TAG, "setUp: ");
//        mMockResolver = getMockContentResolver();
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//        super.tearDown();
//        Log.d(TAG, "tearDown:");
//    }
//
//    @Test
//    public void test_insert() {
//        ContentValues values = recipeValues();
//        Uri generatedUri = RecipesContract.RecipeTable.buildRecipeUri(2L);
//        if (generatedUri != null) {
//            System.out.print(generatedUri);
//        } else {
//            System.out.print("URI is null");
//        }
//        Uri uri = mMockResolver.insert(generatedUri, values);
//        assertNotNull(uri);
//    }
//
//    public static ContentValues recipeValues() {
//        ContentValues v = new ContentValues(2);
//        v.put(RecipesContract.RecipeTable.COLUMN_RECIPE_ID, 2);
//        v.put(RecipesContract.RecipeTable.COLUMN_RECIPE_NAME, "Test Recipe");
//
//        assertNotNull(v);
//        return v;
//    }
}
