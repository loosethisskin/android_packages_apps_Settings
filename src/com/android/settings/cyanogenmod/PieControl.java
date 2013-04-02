package com.android.settings.cyanogenmod;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.SeekBarDialogPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class PieControl extends SettingsPreferenceFragment
                        implements Preference.OnPreferenceChangeListener {

    private static final int DEFAULT_POSITION = 1 << 1; // this equals Position.BOTTOM.FLAG

    private static final String PIE_CONTROL = "pie_control_checkbox";
    private static final String PIE_SIZE = "pie_control_size";
    private static final String[] TRIGGER = {
        "pie_control_trigger_left",
        "pie_control_trigger_bottom",
        "pie_control_trigger_right",
        "pie_control_trigger_top"
    };

    private CheckBoxPreference mPieControl;
    private SeekBarDialogPreference mPieSize;
    private CheckBoxPreference[] mTrigger = new CheckBoxPreference[4];
    private Preference mPieColor;
    private Preference mPieSelectedColor;
    private Preference mPieOutlineColor;
    private SeekBarPreference mPieStart;
    private SeekBarPreference mPieDistance;

    private ContentObserver mPieTriggerObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            updatePieTriggers();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pie_control);

        PreferenceScreen prefSet = getPreferenceScreen();
        mPieControl = (CheckBoxPreference) prefSet.findPreference(PIE_CONTROL);
        mPieControl.setOnPreferenceChangeListener(this);
        mPieSize = (SeekBarDialogPreference) prefSet.findPreference(PIE_SIZE);
        mPieColor = prefSet.findPreference("pie_color");
        mPieSelectedColor = prefSet.findPreference("pie_selected_color");
        mPieOutlineColor = prefSet.findPreference("pie_outline_color");
        mPieStart = (SeekBarPreference) prefSet.findPreference("pie_start");
        mPieStart.setDefault(Settings.System.getInt(getActivity().getApplicationContext()
                .getContentResolver(), Settings.System.PIE_START, 3));
        mPieStart.setOnPreferenceChangeListener(this);
        mPieDistance = (SeekBarPreference) prefSet.findPreference("pie_distance");
        mPieDistance.setDefault(Settings.System.getInt(getActivity().getApplicationContext()
                .getContentResolver(), Settings.System.PIE_DISTANCE, 8));
        mPieDistance.setOnPreferenceChangeListener(this);

        for (int i = 0; i < TRIGGER.length; i++) {
            mTrigger[i] = (CheckBoxPreference) prefSet.findPreference(TRIGGER[i]);
            mTrigger[i].setOnPreferenceChangeListener(this);
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mPieColor) {
            ColorPickerDialog cp = new ColorPickerDialog(getActivity(),
                    mPieColorListener, Settings.System.getInt(getContentResolver(),
                    Settings.System.PIE_COLOR, 0xdd0099cc));
            cp.setDefaultColor(0xdd0099cc);
            cp.show();
            return true;
        } else if (preference == mPieSelectedColor) {
            ColorPickerDialog cp = new ColorPickerDialog(getActivity(),
                    mPieSelectedColorListener, Settings.System.getInt(getContentResolver(),
                    Settings.System.PIE_SELECTED_COLOR, 0xff33b5e5));
            cp.setDefaultColor(0xff33b5e5);
            cp.show();
            return true;
        } else if (preference == mPieOutlineColor) {
            ColorPickerDialog cp = new ColorPickerDialog(getActivity(),
                    mPieOutlineColorListener, Settings.System.getInt(getContentResolver(),
                    Settings.System.PIE_OUTLINE_COLOR, 0xdd0099cc));
            cp.setDefaultColor(0xdd0099cc);
            cp.show();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    ColorPickerDialog.OnColorChangedListener mPieColorListener =
        new ColorPickerDialog.OnColorChangedListener() {
            public void colorChanged(int color) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.PIE_COLOR, color);
            }
            public void colorUpdate(int color) {
            }
    };

    ColorPickerDialog.OnColorChangedListener mPieSelectedColorListener =
        new ColorPickerDialog.OnColorChangedListener() {
            public void colorChanged(int color) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.PIE_SELECTED_COLOR, color);
            }
            public void colorUpdate(int color) {
            }
    };

    ColorPickerDialog.OnColorChangedListener mPieOutlineColorListener =
        new ColorPickerDialog.OnColorChangedListener() {
            public void colorChanged(int color) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.PIE_OUTLINE_COLOR, color);
            }
            public void colorUpdate(int color) {
            }
    };

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPieControl) {
            boolean newState = (Boolean) newValue;

            Settings.System.putInt(getContentResolver(),
                    Settings.System.PIE_CONTROLS, newState ? 1 : 0);
            propagatePieControl(newState);
        } else if (preference == mPieStart) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(), Settings.System.PIE_START, value);
        } else if (preference == mPieDistance) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(), Settings.System.PIE_DISTANCE, value);
        } else {
            int triggerSlots = 0;
            for (int i = 0; i < mTrigger.length; i++) {
                boolean checked = preference == mTrigger[i]
                        ? (Boolean) newValue : mTrigger[i].isChecked();
                if (checked) {
                    triggerSlots |= 1 << i;
                }
            }
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PIE_POSITIONS, triggerSlots);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        mPieControl.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.PIE_CONTROLS, 0) == 1);
        propagatePieControl(mPieControl.isChecked());

        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.PIE_POSITIONS), true,
                mPieTriggerObserver);

        updatePieTriggers();
    }

    @Override
    public void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mPieTriggerObserver);
    }

    private void propagatePieControl(boolean value) {
        for (int i = 0; i < mTrigger.length; i++) {
            mTrigger[i].setEnabled(value);
        }
        mPieSize.setEnabled(value);
    }

    private void updatePieTriggers() {
        int triggerSlots = Settings.System.getInt(getContentResolver(),
                Settings.System.PIE_POSITIONS, DEFAULT_POSITION);

        for (int i = 0; i < mTrigger.length; i++) {
            if ((triggerSlots & (0x01 << i)) != 0) {
                mTrigger[i].setChecked(true);
            } else {
                mTrigger[i].setChecked(false);
            }
        }
    }

}
