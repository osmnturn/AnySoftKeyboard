package com.anysoftkeyboard.quicktextkeys.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yek.keyboard.anysoftkeyboard.keyboards.views.AnyKeyboardViewWithMiniKeyboard;
import com.yek.keyboard.anysoftkeyboard.keyboards.views.OnKeyboardActionListener;
import com.yek.keyboard.anysoftkeyboard.keyboards.views.QuickKeysKeyboardView;
import com.yek.keyboard.anysoftkeyboard.quicktextkeys.QuickTextKey;
import com.yek.keyboard.anysoftkeyboard.quicktextkeys.QuickTextKeyFactory;
import com.yek.keyboard.anysoftkeyboard.quicktextkeys.ui.QuickKeysKeyboardPagerAdapter;
import com.yek.keyboard.anysoftkeyboard.ui.ScrollViewWithDisable;
import com.yek.keyboard.anysoftkeyboard.ui.ViewPagerWithDisable;
import com.yek.keyboard.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowView;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class QuickKeysKeyboardPagerAdapterTest {

    private ViewPagerWithDisable mViewPager;
    private QuickKeysKeyboardPagerAdapter mUnderTest;
    private List<QuickTextKey> mOrderedEnabledQuickKeys;
    private OnKeyboardActionListener mKeyboardListener;

    @Before
    public void setup() {
        mViewPager = Mockito.mock(ViewPagerWithDisable.class);
        mOrderedEnabledQuickKeys = QuickTextKeyFactory.getOrderedEnabledQuickKeys(RuntimeEnvironment.application);
        mKeyboardListener = Mockito.mock(OnKeyboardActionListener.class);
        mUnderTest = new QuickKeysKeyboardPagerAdapter(RuntimeEnvironment.application, mViewPager, mOrderedEnabledQuickKeys, mKeyboardListener);
    }

    @Test
    public void testGetCount() throws Exception {
        Assert.assertEquals(mOrderedEnabledQuickKeys.size(), mUnderTest.getCount());
    }

    @Test
    public void testDestroyItem() {
        ViewGroup container = Mockito.mock(ViewGroup.class);
        View child = Mockito.mock(View.class);
        mUnderTest.destroyItem(container, 0, child);
        Mockito.verify(container).removeView(child);
    }

    @Test
    public void testInstantiateItem() throws Exception {
        ViewGroup container = new LinearLayout(RuntimeEnvironment.application);
        Object instance0 = mUnderTest.instantiateItem(container, 0);
        Assert.assertNotNull(instance0);
        Assert.assertTrue(instance0 instanceof ScrollViewWithDisable);
        Assert.assertEquals(1, container.getChildCount());
        Assert.assertSame(instance0, container.getChildAt(0));

        final QuickKeysKeyboardView keyboardView0 = (QuickKeysKeyboardView) ((View) instance0).findViewById(R.id.keys_container);
        Assert.assertNotNull(keyboardView0);

        Object instance1 = mUnderTest.instantiateItem(container, 1);
        Assert.assertNotNull(instance1);
        Assert.assertNotSame(instance0, instance1);
        final QuickKeysKeyboardView keyboardView1 = (QuickKeysKeyboardView) ((View) instance1).findViewById(R.id.keys_container);
        Assert.assertNotNull(keyboardView1);

        Assert.assertNotEquals(keyboardView0.getKeyboard().getKeyboardAddOn().getId(), keyboardView1.getKeyboard().getKeyboardAddOn().getId());

        Object instance0Again = mUnderTest.instantiateItem(container, 0);
        Assert.assertNotNull(instance0Again);
        Assert.assertNotSame(instance0, instance0Again);
        final QuickKeysKeyboardView keyboardView0Again = (QuickKeysKeyboardView) ((View) instance0Again).findViewById(R.id.keys_container);
        Assert.assertNotNull(keyboardView0Again);
        Assert.assertSame(keyboardView0.getKeyboard(), keyboardView0Again.getKeyboard());
    }

    @Test
    @Config(shadows = ShadowAnyKeyboardViewWithMiniKeyboard.class)
    public void testPopupListenerDisable() throws Exception {
        ViewGroup container = new LinearLayout(RuntimeEnvironment.application);
        Object instance0 = mUnderTest.instantiateItem(container, 0);
        final QuickKeysKeyboardView keyboardView0 = (QuickKeysKeyboardView) ((View) instance0).findViewById(R.id.keys_container);

        ShadowAnyKeyboardViewWithMiniKeyboard shadow = (ShadowAnyKeyboardViewWithMiniKeyboard) ShadowExtractor.extract(keyboardView0);
        Assert.assertNotNull(shadow.mPopupShownListener);

        Mockito.verify(mViewPager, Mockito.never()).setEnabled(Mockito.anyBoolean());

        shadow.mPopupShownListener.onPopupKeyboardShowingChanged(true);

        Mockito.verify(mViewPager).setEnabled(false);
        Mockito.verifyNoMoreInteractions(mViewPager);

        Mockito.reset(mViewPager);

        shadow.mPopupShownListener.onPopupKeyboardShowingChanged(false);

        Mockito.verify(mViewPager).setEnabled(true);
        Mockito.verifyNoMoreInteractions(mViewPager);
    }

    @Implements(AnyKeyboardViewWithMiniKeyboard.class)
    public static class ShadowAnyKeyboardViewWithMiniKeyboard extends ShadowView {

        private AnyKeyboardViewWithMiniKeyboard.OnPopupShownListener mPopupShownListener;

        public ShadowAnyKeyboardViewWithMiniKeyboard() {
        }

        @Implementation
        public void setOnPopupShownListener(AnyKeyboardViewWithMiniKeyboard.OnPopupShownListener listener) {
            mPopupShownListener = listener;
        }
    }
}