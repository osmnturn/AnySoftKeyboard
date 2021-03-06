package com.yek.keyboard.ime;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.anysoftkeyboard.api.KeyCodes;
import com.yek.keyboard.keyboards.Keyboard;
import com.yek.keyboard.keyboards.views.AnyKeyboardView;
import com.yek.keyboard.quicktextkeys.QuickTextKey;
import com.yek.keyboard.quicktextkeys.QuickTextKeyFactory;
import com.yek.keyboard.quicktextkeys.ui.QuickTextPagerView;
import com.yek.keyboard.quicktextkeys.ui.QuickTextViewFactory;
import com.yek.keyboard.R;

public abstract class AnySoftKeyboardWithQuickText extends AnySoftKeyboardClipboard {

    private boolean mDoNotFlipQuickTextKeyAndPopupFunctionality;
    private String mOverrideQuickTextText = null;

    @Override
    protected void onLoadSettingsRequired(SharedPreferences sharedPreferences) {
        super.onLoadSettingsRequired(sharedPreferences);
        mDoNotFlipQuickTextKeyAndPopupFunctionality = sharedPreferences.getBoolean(
                getString(R.string.settings_key_do_not_flip_quick_key_codes_functionality),
                getResources().getBoolean(R.bool.settings_default_do_not_flip_quick_keys_functionality));

        mOverrideQuickTextText = sharedPreferences.getString(getString(R.string.settings_key_emoticon_default_text), null);
    }

    protected void onQuickTextRequested(Keyboard.Key key) {
        if (mDoNotFlipQuickTextKeyAndPopupFunctionality) {
            outputCurrentQuickTextKey(key);
        } else {
            switchToQuickTextKeyboard();
        }
    }

    protected void onQuickTextKeyboardRequested(Keyboard.Key key) {
        if (mDoNotFlipQuickTextKeyAndPopupFunctionality) {
            switchToQuickTextKeyboard();
        } else {
            outputCurrentQuickTextKey(key);
        }
    }

    private void outputCurrentQuickTextKey(Keyboard.Key key) {
        QuickTextKey quickTextKey = QuickTextKeyFactory.getCurrentQuickTextKey(this);
        if (TextUtils.isEmpty(mOverrideQuickTextText))
            onText(key, quickTextKey.getKeyOutputText());
        else
            onText(key, mOverrideQuickTextText);
    }

    private void switchToQuickTextKeyboard() {
        abortCorrectionAndResetPredictionState(false);
        setCandidatesViewShown(false);

        cleanUpQuickTextKeyboard(false);
        View standardKeyboardView = (View) getInputView();
        final int height = standardKeyboardView.getHeight();
        standardKeyboardView.setVisibility(View.GONE);
        QuickTextPagerView quickTextsLayout = QuickTextViewFactory.createQuickTextView(getApplicationContext(), getInputViewContainer(), height);
        quickTextsLayout.setThemeValues(((AnyKeyboardView)getInputView()).getLabelTextSize(), ((AnyKeyboardView)getInputView()).getKeyTextColor(), ((AnyKeyboardView)getInputView()).getDrawableForKeyCode(KeyCodes.CANCEL), ((AnyKeyboardView)getInputView()).getDrawableForKeyCode(KeyCodes.DELETE), ((AnyKeyboardView)getInputView()).getDrawableForKeyCode(KeyCodes.SETTINGS));
        getInputViewContainer().addView(quickTextsLayout);
    }

    private boolean cleanUpQuickTextKeyboard(boolean reshowStandardKeyboard) {
        final ViewGroup inputViewContainer = getInputViewContainer();
        if (inputViewContainer == null) return false;

        QuickTextPagerView quickTextsLayout = (QuickTextPagerView) inputViewContainer.findViewById(R.id.quick_text_pager_root);
        if (quickTextsLayout != null) {
            inputViewContainer.removeView(quickTextsLayout);
            if (reshowStandardKeyboard) {
                View standardKeyboardView = (View) getInputView();
                standardKeyboardView.setVisibility(View.VISIBLE);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void hideWindow() {
        if (!cleanUpQuickTextKeyboard(true)) {
            super.hideWindow();
        }
    }
}
