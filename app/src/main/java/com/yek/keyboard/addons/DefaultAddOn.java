package com.yek.keyboard.addons;

import android.content.Context;

import com.yek.keyboard.R;

/**
 * Empty add-on which is to be used to hold simple implementation for context mapping
 */
public class DefaultAddOn extends AddOnImpl {
    public DefaultAddOn(Context askContext, Context packageContext) {
        super(askContext, packageContext, "DEFAULT_ADD_ON", R.string.default_local_add_on_name, "", false, 0);
    }
}
