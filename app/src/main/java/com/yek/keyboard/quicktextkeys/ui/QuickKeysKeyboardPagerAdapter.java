package com.yek.keyboard.quicktextkeys.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yek.keyboard.addons.DefaultAddOn;
import com.yek.keyboard.keyboards.AnyPopupKeyboard;
import com.yek.keyboard.keyboards.Keyboard;
import com.yek.keyboard.keyboards.PopupListKeyboard;
import com.yek.keyboard.keyboards.views.AnyKeyboardViewWithMiniKeyboard;
import com.yek.keyboard.keyboards.views.OnKeyboardActionListener;
import com.yek.keyboard.keyboards.views.QuickKeysKeyboardView;
import com.yek.keyboard.quicktextkeys.HistoryQuickTextKey;
import com.yek.keyboard.quicktextkeys.QuickTextKey;
import com.yek.keyboard.ui.ScrollViewWithDisable;
import com.yek.keyboard.ui.ViewPagerWithDisable;
import com.yek.keyboard.R;

import java.util.List;

/*package*/ class QuickKeysKeyboardPagerAdapter extends PagerAdapter {

    @NonNull
    private final Context mContext;
    @NonNull
    private final OnKeyboardActionListener mKeyboardActionListener;
    @NonNull
    private final LayoutInflater mLayoutInflater;
    @NonNull
    private final AnyPopupKeyboard[] mPopupKeyboards;
    @NonNull
    private final boolean[] mIsAutoFitKeyboards;
    @NonNull
    private final QuickTextKey[] mAddOns;
    private final DefaultAddOn mDefaultLocalAddOn;
    private final ViewPagerWithDisable mViewPager;

    public QuickKeysKeyboardPagerAdapter(@NonNull Context context, @NonNull ViewPagerWithDisable ownerPager, @NonNull List<QuickTextKey> keyAddOns, @NonNull OnKeyboardActionListener keyboardActionListener) {
        mViewPager = ownerPager;
        mDefaultLocalAddOn = new DefaultAddOn(context, context);
        mContext = context;
        mKeyboardActionListener = keyboardActionListener;
        mAddOns = keyAddOns.toArray(new QuickTextKey[keyAddOns.size()]);
        mPopupKeyboards = new AnyPopupKeyboard[mAddOns.length];
        mIsAutoFitKeyboards = new boolean[mAddOns.length];
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mPopupKeyboards.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View root = mLayoutInflater.inflate(R.layout.quick_text_popup_autorowkeyboard_view, container, false);
        ScrollViewWithDisable scrollViewWithDisable = (ScrollViewWithDisable) root.findViewById(R.id.scroll_root_for_quick_test_keyboard);
        container.addView(root);

        final QuickKeysKeyboardView keyboardView = (QuickKeysKeyboardView) root.findViewById(R.id.keys_container);
        keyboardView.setOnPopupShownListener(new PopupKeyboardShownHandler(mViewPager, scrollViewWithDisable));
        keyboardView.setOnKeyboardActionListener(mKeyboardActionListener);
        QuickTextKey addOn = mAddOns[position];
        AnyPopupKeyboard keyboard = mPopupKeyboards[position];
        if (keyboard == null) {
            if (addOn.isPopupKeyboardUsed()) {
                keyboard = new AnyPopupKeyboard(addOn, mContext, addOn.getPackageContext(), addOn.getPopupKeyboardResId(), keyboardView.getThemedKeyboardDimens(), addOn.getName());
            } else {
                keyboard = new PopupListKeyboard(mDefaultLocalAddOn, mContext, keyboardView.getThemedKeyboardDimens(), addOn.getPopupListNames(), addOn.getPopupListValues(), addOn.getName());
            }
            mPopupKeyboards[position] = keyboard;
            final int keyboardViewMaxWidth = keyboardView.getThemedKeyboardDimens().getKeyboardMaxWidth();
            mIsAutoFitKeyboards[position] = keyboard.getMinWidth() > keyboardViewMaxWidth || addOn instanceof HistoryQuickTextKey;
            if (mIsAutoFitKeyboards[position]) {
                //fixing up the keyboard, so it will fit nicely in the width
                int currentY = 0;
                int xSub = 0;
                for (Keyboard.Key key : keyboard.getKeys()) {
                    key.y = currentY;
                    key.x -= xSub;
                    if (key.x + key.width > keyboardViewMaxWidth) {
                        currentY += key.height;
                        xSub += key.x;
                        key.y = currentY;
                        key.x = 0;
                    }
                }
                keyboard.resetDimensions();
            }
        }
        keyboardView.setKeyboard(keyboard);
        return root;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        QuickTextKey key = mAddOns[position];
        return mContext.getResources().getString(R.string.quick_text_tab_title_template, key.getKeyOutputText(), key.getName());
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private static class PopupKeyboardShownHandler implements AnyKeyboardViewWithMiniKeyboard.OnPopupShownListener {
        private final ViewPagerWithDisable mViewPager;
        private final ScrollViewWithDisable mScrollViewWithDisable;

        public PopupKeyboardShownHandler(ViewPagerWithDisable viewPager, ScrollViewWithDisable scrollViewWithDisable) {
            mViewPager = viewPager;
            mScrollViewWithDisable = scrollViewWithDisable;
        }

        @Override
        public void onPopupKeyboardShowingChanged(boolean showing) {
            mViewPager.setEnabled(!showing);
            mScrollViewWithDisable.setEnabled(!showing);
        }
    }
}