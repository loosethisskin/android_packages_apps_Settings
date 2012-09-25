/*
 * Copyright (C) 2012 CyanogenMod
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
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class PowerMenu extends SettingsPreferenceFragment {
    private static final String TAG = "PowerMenu";

    private static final String KEY_REBOOT = "power_menu_reboot";
    private static final String KEY_SCREENSHOT = "power_menu_screenshot";
    private static final String KEY_PROFILES = "power_menu_profiles";
    private static final String KEY_AIRPLANE = "power_menu_airplane";
    private static final String KEY_SILENT = "power_menu_silent";
    private static final String KEY_SYSTEMBAR = "power_menu_systembar";
    private static final String KEY_MULTIUSER = "power_menu_multiuser";
    private static final String KEY_NAV = "power_menu_nav";

    private CheckBoxPreference mRebootPref;
    private CheckBoxPreference mScreenshotPref;
    private CheckBoxPreference mProfilesPref;
    private CheckBoxPreference mAirplanePref;
    private CheckBoxPreference mSilentPref;
    private CheckBoxPreference mSystembarPref;
    private CheckBoxPreference mMultiuserPref;
    private CheckBoxPreference mNavPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.power_menu_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mRebootPref = (CheckBoxPreference) prefSet.findPreference(KEY_REBOOT);
        mRebootPref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_REBOOT_ENABLED, 1) == 1));

        mScreenshotPref = (CheckBoxPreference) prefSet.findPreference(KEY_SCREENSHOT);
        mScreenshotPref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_SCREENSHOT_ENABLED, 0) == 1));

        mProfilesPref = (CheckBoxPreference) prefSet.findPreference(KEY_PROFILES);
        mProfilesPref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_PROFILES_ENABLED, 1) == 1));

        mAirplanePref = (CheckBoxPreference) prefSet.findPreference(KEY_AIRPLANE);
        mAirplanePref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_AIRPLANE_MODE_ENABLED, 1) == 1));

        mSilentPref = (CheckBoxPreference) prefSet.findPreference(KEY_SILENT);
        mSilentPref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_SILENT_MODE_ENABLED, 1) == 1));

        mSystembarPref = (CheckBoxPreference) prefSet.findPreference(KEY_SYSTEMBAR);
        mSystembarPref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_SYSTEMBAR_TOGGLE_ENABLED, 1) == 1));

        mMultiuserPref = (CheckBoxPreference) prefSet.findPreference(KEY_MULTIUSER);
        mMultiuserPref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_MULTIUSER_MODE_ENABLED, 1) == 1));

        mNavPref = (CheckBoxPreference) prefSet.findPreference(KEY_NAV);
        mNavPref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_NAV_CONTROLS_ENABLED, 0) == 1));

        // Only enable if System Profiles are also enabled
        boolean enabled = Settings.System.getInt(getContentResolver(),
                Settings.System.SYSTEM_PROFILES_ENABLED, 1) == 1;
        mProfilesPref.setEnabled(enabled);

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mScreenshotPref) {
            value = mScreenshotPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_SCREENSHOT_ENABLED,
                    value ? 1 : 0);
        } else if (preference == mRebootPref) {
            value = mRebootPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_REBOOT_ENABLED,
                    value ? 1 : 0);
        } else if (preference == mProfilesPref) {
            value = mProfilesPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_PROFILES_ENABLED,
                    value ? 1 : 0);
        } else if (preference == mAirplanePref) {
            value = mAirplanePref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_AIRPLANE_MODE_ENABLED,
                    value ? 1 : 0);
        } else if (preference == mSilentPref) {
            value = mSilentPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_SILENT_MODE_ENABLED,
                    value ? 1 : 0);
        } else if (preference == mSystembarPref) {
            value = mSystembarPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_SYSTEMBAR_TOGGLE_ENABLED,
                    value ? 1 : 0);
        } else if (preference == mMultiuserPref) {
            value = mMultiuserPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_MULTIUSER_MODE_ENABLED,
                    value ? 1 : 0);
        } else if (preference == mNavPref) {
            value = mNavPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_NAV_CONTROLS_ENABLED,
                    value ? 1 : 0);
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return true;
    }

}
