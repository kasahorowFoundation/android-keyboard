package com.anysoftkeyboard.keyboards;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.anysoftkeyboard.AnySoftKeyboardRobolectricTestRunner;
import com.menny.android.anysoftkeyboard.AnyApplication;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AnySoftKeyboardRobolectricTestRunner.class)
public class KeyboardAddOnTest {

    private static final String KASAHOROW_AKAN_KEYBOARD_ID = "e99e252e-fc49-42dd-b763-9f78294cb0f0";

    @Test
    public void testGetKeyboardDefaultEnabled() throws Exception {
        List<KeyboardAddOnAndBuilder> enabledKeyboards =
                AnyApplication.getKeyboardFactory(getApplicationContext()).getEnabledAddOns();
        // checking that ASK English is enabled
        boolean askEnglishEnabled = false;
        for (KeyboardAddOnAndBuilder addOnAndBuilder : enabledKeyboards) {
            if (addOnAndBuilder.getId().equals(KASAHOROW_AKAN_KEYBOARD_ID)) {
                assertTrue(addOnAndBuilder.getKeyboardDefaultEnabled());
                assertEquals(
                        addOnAndBuilder.getPackageName(), getApplicationContext().getPackageName());
                askEnglishEnabled = true;
            }
        }
        assertTrue(askEnglishEnabled);
        // only one enabled keyboard
        Assert.assertEquals(1, enabledKeyboards.size());
    }

    @Test
    public void testGetEnabledDefaultFromAllKeyboards() throws Exception {
        List<KeyboardAddOnAndBuilder> allAvailableKeyboards =
                AnyApplication.getKeyboardFactory(getApplicationContext()).getAllAddOns();

        Map<String, Boolean> keyboardsEnabled = new HashMap<>();
        for (KeyboardAddOnAndBuilder addOnAndBuilder : allAvailableKeyboards) {
            keyboardsEnabled.put(
                    addOnAndBuilder.getId(), addOnAndBuilder.getKeyboardDefaultEnabled());
        }

        Assert.assertEquals(12, keyboardsEnabled.size());
        Assert.assertTrue(keyboardsEnabled.containsKey(KASAHOROW_AKAN_KEYBOARD_ID));
        Assert.assertTrue(keyboardsEnabled.get(KASAHOROW_AKAN_KEYBOARD_ID));
    }

    private KeyboardAddOnAndBuilder getKeyboardFromFactory(String id) {
        List<KeyboardAddOnAndBuilder> keyboards =
                AnyApplication.getKeyboardFactory(getApplicationContext()).getAllAddOns();

        for (KeyboardAddOnAndBuilder addOnAndBuilder : keyboards) {
            if (addOnAndBuilder.getId().equals(id)) {
                return addOnAndBuilder;
            }
        }

        return null;
    }

    @Test
    public void testGetKeyboardLocale() throws Exception {
        KeyboardAddOnAndBuilder askEnglish = getKeyboardFromFactory(KASAHOROW_AKAN_KEYBOARD_ID);
        assertNotNull(askEnglish);
        assertEquals(askEnglish.getKeyboardLocale(), "ak");
    }

    @Test
    public void testCreateKeyboard() throws Exception {}
}
