package pitman.co.za.bakingapp;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.DatabaseUtils;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import android.test.RenamingDelegatingContext;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;

import java.io.File;

/**
 * Created by Michael on 2018/04/17.
 * Guidelines used in this class:
 * https://alvinalexander.com/java/jwarehouse/android/test-runner/src/android/test/ProviderTestCase2.java.shtml
 */

public class ProviderTestCase2<T extends ContentProvider> extends AndroidTestCase {

    Class<T> mProviderClass;
    String mProviderAuthority;

    private IsolatedContext mProviderContext;
    private MockContentResolver mResolver;

    // MockContext
    private class MockContext2 extends MockContext {

        @Override
        public Resources getResources() {
            return getContext().getResources();
        }

        @Override
        public File getDir(String name, int mode) {
            // name the directory so the directory will be separated from one created through the regular Context
            return getContext().getDir("mockcontext2_" + name, mode);
        }
    }

    // Constructor
    public ProviderTestCase2() {}

    public ProviderTestCase2(Class<T> providerClass, String providerAuthority) {
        mProviderClass = providerClass;
        mProviderAuthority = providerAuthority;
    }

    // Content provider set up for each test method
    private T mProvider;

    public T getProvider() {
        return mProvider;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mResolver = new MockContentResolver();
        final String filenamePrefix = "test.";
        RenamingDelegatingContext targetContextWrapper = new RenamingDelegatingContext(
                new MockContext2(), // The context that most methods are delegated to
                getContext(), // The context that file methods are delegated to
                filenamePrefix);
        mProviderContext = new IsolatedContext(mResolver, targetContextWrapper);

        mProvider = mProviderClass.newInstance();
        mProvider.attachInfo(mProviderContext, null);
        assertNotNull(mProviderClass);
        mResolver.addProvider(mProviderAuthority, getProvider());
    }

    public MockContentResolver getMockContentResolver() {
        return mResolver;
    }

    public IsolatedContext getMockContext() {
        return mProviderContext;
    }

    public static <T extends ContentProvider> ContentResolver newResolverWithContentProviderFromSql(
            Context targetContext, String filenamePrefix, Class<T> providerClass, String authority,
            String databaseName, int databaseVersion, String sql)
            throws IllegalAccessException, InstantiationException {
        MockContentResolver resolver = new MockContentResolver();
        RenamingDelegatingContext targetContextWrapper = new RenamingDelegatingContext(
                new MockContext(), // The context that most methods are delegated to
                targetContext, // The context that file methods are delegated to
                filenamePrefix);
        Context context = new IsolatedContext(resolver, targetContextWrapper);
        DatabaseUtils.createDbFromSqlStatements(context, databaseName, databaseVersion, sql);

        T provider = providerClass.newInstance();
        provider.attachInfo(context, null);
        resolver.addProvider(authority, provider);

        return resolver;
    }
}
