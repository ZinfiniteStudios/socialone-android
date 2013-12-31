package com.socialone.android;

import com.google.inject.AbstractModule;
import com.socialone.android.utils.EncryptedPreferencesProvider;
import com.socialone.android.utils.EncryptedSharedPreferences;

/**
 * Generated from archetype
 */
public class RoboGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EncryptedSharedPreferences.class).toProvider(EncryptedPreferencesProvider.class);
    }
}
