/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.dialer.binary.aosp;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.contacts.common.extensions.PhoneDirectoryExtender;
import com.android.contacts.common.extensions.PhoneDirectoryExtenderFactory;
import com.android.dialer.binary.common.DialerApplication;
import com.android.dialer.inject.ContextModule;
import com.android.dialer.lookup.LookupCacheService;
import com.android.dialer.lookup.LookupProvider;
import com.android.dialer.lookup.LookupSettings;
import com.android.dialer.lookup.ReverseLookupService;
import com.android.dialer.phonenumbercache.CachedNumberLookupService;
import com.android.dialer.phonenumbercache.PhoneNumberCacheBindings;
import com.android.dialer.phonenumbercache.PhoneNumberCacheBindingsFactory;
import com.android.incallui.bindings.InCallUiBindings;
import com.android.incallui.bindings.InCallUiBindingsFactory;
import com.android.incallui.bindings.InCallUiBindingsStub;
import com.android.incallui.bindings.PhoneNumberService;

import java.util.List;

/**
 * The application class for the AOSP Dialer. This is a version of the Dialer app that has no
 * dependency on Google Play Services.
 */
public class AospDialerApplication extends DialerApplication implements
    PhoneNumberCacheBindingsFactory, PhoneDirectoryExtenderFactory, InCallUiBindingsFactory {

  /** Returns a new instance of the root component for the AOSP Dialer. */
  @Override
  @NonNull
  protected Object buildRootComponent() {
    return DaggerAospDialerRootComponent.builder().contextModule(new ContextModule(this)).build();
  }

  @Override
  public PhoneDirectoryExtender newPhoneDirectoryExtender() {
    return new PhoneDirectoryExtender() {
      @Override
      public boolean isEnabled(Context context) {
        return LookupSettings.isForwardLookupEnabled(AospDialerApplication.this)
            || LookupSettings.isPeopleLookupEnabled(AospDialerApplication.this);
      }

      @Override
      @Nullable
      public Uri getContentUri() {
        return LookupProvider.NEARBY_AND_PEOPLE_LOOKUP_URI;
      }
    };
  }

  @Override
  public InCallUiBindings newInCallUiBindings() {
    return new InCallUiBindingsStub() {
      @Override
      @Nullable
      public PhoneNumberService newPhoneNumberService(Context context) {
        return new ReverseLookupService(context);
      }
    };
  }

  @Override
  public PhoneNumberCacheBindings newPhoneNumberCacheBindings() {
    return new PhoneNumberCacheBindings() {
      @Override
      @Nullable
      public CachedNumberLookupService getCachedNumberLookupService() {
        return new LookupCacheService();
      }
    };
  }
}
