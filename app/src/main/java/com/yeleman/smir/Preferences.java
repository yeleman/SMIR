package com.yeleman.smir;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {

//   private CheckBoxPreference showSplash;

   @Override
   protected void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      addPreferencesFromResource(R.layout.preferences);
      }

}
